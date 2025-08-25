package jp.aevic.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * セキュリティ設定
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * @param http HttpSecurity設定
     * @return SecurityFilterChain
     * @throws Exception HttpSecurityの提供するメソッドでスローする可能性がある
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //CSRFを無効にする。
                .csrf(AbstractHttpConfigurer::disable)
                //CORSを無効にする。
                .cors(AbstractHttpConfigurer::disable)
                //リクエスト権限設定
                .authorizeHttpRequests(customizer -> customizer
                        //全てのリクエストを許可する設定
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}