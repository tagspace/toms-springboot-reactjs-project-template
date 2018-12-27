package com.mywebsite.configuration.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class RequestLoggerInterceptor implements HandlerInterceptor {

    static Logger log = LoggerFactory.getLogger(RequestLoggerInterceptor.class.getName());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        return;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(!(handler instanceof HandlerMethod)) {
            //doesn't hit a controller, so don't log (e.g. static resources like images, etc.)
            return;
        }

        Enumeration<String> attNames = request.getAttributeNames();
        Map<String, Object> attributes = new HashMap<>();
        while (attNames.hasMoreElements()) {
            String att = attNames.nextElement();
            Object value = request.getAttribute(att);
            attributes.put(att, value);
        }

        //build props
        LinkedHashMap<String, String> props = new LinkedHashMap<>();
        props.put("status", ex != null ? "500" : response.getStatus() + "");
        props.put("user", currentUser());
        props.put("method", request.getMethod());
        props.put("path", request.getServletPath());
        //spring security will catch urls if we're not logged in, and store the request as a session attribute for when they've logged in
        String savedRequest = getSavedRequest(request);
        if (savedRequest != null) {
            props.put("savedRequest", savedRequest);
        }
        props.put("handler", extractHandler((HandlerMethod) handler));
        String originalPath = originalPath(request);
        if (originalPath != null) {
            props.put("OriginalPath", originalPath);
        }
        if (ex != null) {
            props.put("Exception", String.format("%s[%s]", ex.getClass().getSimpleName(), ex.getMessage()));
        }
        switch (response.getStatus()) {
            case HttpServletResponse.SC_MOVED_PERMANENTLY:
            case HttpServletResponse.SC_MOVED_TEMPORARILY:
                props.put("ResponseHeader:Location", response.getHeader("Location"));
                break;
        }
        props.put("RequestParams", buildParams(request));//make this the last prop (as can be quite long)
        String payload = extractPayload(request);
        if (payload != null) {
            props.put("Payload", payload);
        }

        //log it out
        String msg = String.join(", ", props.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
        log.info(String.format("[%s] %s", sessionId(request), msg));
    }

    private String getSavedRequest(HttpServletRequest req) {
        HttpSession session = req.getSession(false);//don't create session if one hasn't already been created
        if(session == null) {
            return null;
        }
        DefaultSavedRequest savedRequest = (DefaultSavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if(savedRequest == null) {
            return null;
        }
        return savedRequest.getRequestURI();
    }

    private String sessionId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);//don't create a session if one hasn't already been created
        if (session != null) {
            return session.getId();
        }
        return null;
    }

    private String extractPayload(HttpServletRequest request) {
        String payload = null;
        /**
         * Note: this wrapper is added by the CommonsRequestLoggingFilter
         */
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper cachedRequest = (ContentCachingRequestWrapper) request;
            byte[] buf = cachedRequest.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 2048);
                try {
                    payload = new String(buf, 0, length, cachedRequest.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    payload = "[unknown]";
                }
            } else {
                payload = "[empty]";
            }
            //remove passwords!
            payload = String.join("&", Arrays.asList(payload.split("&")).stream()
                    .filter(s -> !s.contains("password"))
                    .filter(s -> !s.contains("ssh_key"))
                    .collect(Collectors.toList()));
        }

        return payload;
    }

    private String originalPath(HttpServletRequest request) {
        String requestUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        if (requestUri == null) {
            return null;
        }
        Object servletPath = request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH);
        Object queryString = request.getAttribute(RequestDispatcher.FORWARD_QUERY_STRING);
        return requestUri + (queryString == null ? "" : "?" + queryString);
    }

    private String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return "Unknown";

    }

    private String extractHandler(HandlerMethod handler) {
        Method method = handler.getMethod();
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    private String buildParams(HttpServletRequest request) {
        List<String> params = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            //remove passwords!
            if(entry.getKey().contains("password")) {
                continue;
            }
            if(entry.getKey().contains("ssh_key")) {
                continue;
            }
            params.add(entry.getKey() + "=" + request.getParameter(entry.getKey()));
        }
        params.sort((a, b) -> a.length() - b.length());
        return String.format("{%s}", String.join(",", params));
    }
}