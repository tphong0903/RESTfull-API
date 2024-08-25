package org.example.restfulapi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.UrlBasedViewResolverRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//Cach 1 CORS
//public class AppConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//       registry.addMapping("/**")
//               .allowCredentials(true)
//               .allowedOrigins("http://localhost:5173")
//               .allowedMethods("*")
//               .allowedHeaders("*");
//
//    }
//
//}

public  class AppConfig {
//    Cach 2 CORS
//    @Bean
//    public WebMvcConfigurer corsFilter(){
//        return new WebMvcConfigurer() {
//            public void addCorsMappings(CorsRegistry registry) {
//               registry.addMapping("/**")
//                       .allowCredentials(true)
//                       .allowedOrigins("http://localhost:5173")
//                       .allowedMethods("*")
//                       .allowedHeaders("*");
//            }
//        };
//    }
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addAllowedOrigin("http://localhost:5173");// URL nay dc phep goi API
        source.registerCorsConfiguration("/**",config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
