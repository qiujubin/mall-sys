package com.gec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000", "http://127.0.0.1:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        String uploadPath;
        
        // 如果当前目录已经是backend，直接使用；否则添加backend路径
        if (projectRoot.endsWith("backend")) {
            uploadPath = projectRoot + "/target/classes/static/uploads/";
        } else {
            uploadPath = projectRoot + "/backend/target/classes/static/uploads/";
        }
        
        System.out.println("=== WebConfig 静态资源配置调试 ===");
        System.out.println("项目根目录: " + projectRoot);
        System.out.println("上传路径: " + uploadPath);
        System.out.println("上传路径是否存在: " + java.nio.file.Files.exists(java.nio.file.Paths.get(uploadPath)));
        
        // 配置静态资源访问路径 - 使用 file: 方式指向编译后的目录
        registry.addResourceHandler("/api/static/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(0); // 禁用缓存，便于调试
        
        // 同时配置不带 /api 前缀的路径
        registry.addResourceHandler("/static/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(0);
        
        System.out.println("静态资源处理器配置完成");
        System.out.println("=== WebConfig 调试结束 ===");
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:3000");
        configuration.addAllowedOriginPattern("http://127.0.0.1:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
