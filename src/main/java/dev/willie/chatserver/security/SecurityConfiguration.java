package dev.willie.chatserver.security;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.SecurityContext;
import dev.willie.chatserver.config.ConfigHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{
	@Value("${logto.audience}")
	private String audience;

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuer;

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	private String jwksUri;

	@Bean
	public JwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri)
				// Logto uses the ES384 algorithm to sign the JWTs by default.
				.jwsAlgorithm(SignatureAlgorithm.ES384)
				// The decoder should support the token type: Access Token + JWT.
				.jwtProcessorCustomizer(customizer -> customizer.setJWSTypeVerifier(
						new DefaultJOSEObjectTypeVerifier<SecurityContext>(new JOSEObjectType("at+jwt"))))
				.build();

		jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
				new AudienceValidator(audience),
				new JwtIssuerValidator(issuer),
				new JwtTimestampValidator()));

		return jwtDecoder;
	}

	@Bean
	public DefaultSecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")
				.cors(Customizer.withDefaults())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(Customizer.withDefaults()))
				.authorizeHttpRequests(auth -> auth
						// Allow all requests to the public APIs.
						.requestMatchers("/api/.wellknown/**").permitAll()
						.requestMatchers("/api/internal/**").access((authentication, requestAuthorizationContext) ->{
							return internalAccessAllowed(requestAuthorizationContext) ?
									new AuthorizationDecision(true) :
									new AuthorizationDecision(false);
						})
						// Require jwt token validation for the protected APIs.
						.anyRequest().authenticated());

		return http.build();
	}
	protected boolean internalAccessAllowed(RequestAuthorizationContext requestAuthorizationContext){
		String ip = requestAuthorizationContext.getRequest().getRemoteAddr();
		if(!ConfigHandler.WEB_SERVER_ADDRESS.equals(ip)){
			return false;
		}
		String token = requestAuthorizationContext.getRequest().getHeader("SERVER_COMMUNICATION_TOKEN");
		if(token == null){
			return false;
		}
		if(!token.equals(ConfigHandler.SERVER_COMMUNICATION_TOKEN)){
			return false;
		}
		return true;
	}
}
