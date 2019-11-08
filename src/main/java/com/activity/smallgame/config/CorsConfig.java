package com.activity.smallgame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * @author: Mr.dong
 * @create: 2019-08-28 17:24
 **/
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 是否支持Cookie跨域
        config.setAllowCredentials(true);
        // 设置原始域,这里设置所有域，可以设置例如：www.zyjp.info
        config.setAllowedOrigins(Arrays.asList("*"));
        // 设置允许的Header
        config.setAllowedHeaders(Arrays.asList("*"));
        // 设置允许的请求方法
        config.setAllowedMethods(Arrays.asList("*"));
        // 设置缓存时间，在这个时间段里对于相同的跨域请求，就不再检查了,这里设置300秒
        config.setMaxAge(300L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
