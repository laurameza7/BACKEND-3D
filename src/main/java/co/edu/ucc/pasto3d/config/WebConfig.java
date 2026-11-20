package co.edu.ucc.pasto3d.config;

import co.edu.ucc.pasto3d.security.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Routes that require a session (registered user or guest). */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authentication;

    public WebConfig(AuthenticationInterceptor authentication) { this.authentication = authentication; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authentication)
                .addPathPatterns("/api/assistant/**", "/api/auth/me");
    }
}
