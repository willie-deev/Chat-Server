package dev.willie.chatserver.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt>{
	private final String audience;

	public AudienceValidator(String audience) {
		this.audience = audience;
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt jwt) {
		if (!jwt.getAudience().contains(audience)) {
			return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Required audience not found", null));
		}

		return OAuth2TokenValidatorResult.success();
	}
}
