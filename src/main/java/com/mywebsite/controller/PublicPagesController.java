package com.mywebsite.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class PublicPagesController {

    static Logger log = LoggerFactory.getLogger(PublicPagesController.class.getName());

    @Value("${project.bundles.home}")
    private String bundleJs;

    @RequestMapping("/")
    public String index(Model model) {

        if(!"/js/home_bundle.js".equals(bundleJs)) {
            model.addAttribute("overrideBundle", bundleJs);
        }

        return "index";
    }
}
