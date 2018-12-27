package com.mywebsite.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60*60*24)
@Profile({"development", "heroku"})
public class RedisConfig {

    @Bean
    @Profile("heroku")
    public RedisConnectionFactory redisConnectionFactory() throws URISyntaxException {
        String redisURL = System.getenv("REDIS_URL");
        URI uri = new URI(redisURL);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(uri.getHost(), uri.getPort());
        config.setPassword(RedisPassword.of(uri.getUserInfo().split(":", 2)[1]));
        return new JedisConnectionFactory(config);
    }

    @Bean
    @Profile("heroku")
    public static ConfigureRedisAction configureRedisAction() {
        //see: http://docs.spring.io/spring-session/docs/current/reference/html5/#api-redisoperationssessionrepository-sessiondestroyedevent
        return ConfigureRedisAction.NO_OP;
    }
}