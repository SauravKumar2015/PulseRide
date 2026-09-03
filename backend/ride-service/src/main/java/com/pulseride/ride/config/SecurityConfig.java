package com.pulseride.ride.config;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean SecretKey jwtSecretKey(@Value("${jwt.secret}") String secret) { return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"); }
 @Bean JwtDecoder jwtDecoder(SecretKey key, @Value("${jwt.issuer}") String issuer) { var d = NimbusJwtDecoder.withSecretKey(key).build(); d.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer)); return d; }
 @Bean JwtAuthenticationConverter jwtAuthenticationConverter() { var c = new JwtAuthenticationConverter(); c.setJwtGrantedAuthoritiesConverter(jwt -> java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + jwt.getClaimAsString("role")))); return c; }
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter c) throws Exception { return http.csrf(x -> x.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a -> a.anyRequest().authenticated()).oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(c))).build(); }
}
