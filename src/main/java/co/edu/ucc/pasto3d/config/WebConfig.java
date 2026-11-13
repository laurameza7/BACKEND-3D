package co.edu.ucc.pasto3d.config;

import co.edu.ucc.pasto3d.seguridad.AutenticacionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Rutas que exigen sesión (usuario registrado o invitado). */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutenticacionInterceptor autenticacion;

    public WebConfig(AutenticacionInterceptor autenticacion) { this.autenticacion = autenticacion; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacion)
                .addPathPatterns("/api/asistente/**", "/api/auth/yo");
    }
}
