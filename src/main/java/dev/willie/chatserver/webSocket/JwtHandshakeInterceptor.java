package dev.willie.chatserver.webSocket;

import dev.willie.chatserver.security.AudienceValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor{
	private final JwtDecoder jwtDecoder;
	public JwtHandshakeInterceptor(JwtDecoder jwtDecoder){
		this.jwtDecoder = jwtDecoder;
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attrs) throws Exception {
		List<String> headers = request.getHeaders().getOrEmpty("Authorization");
		if (headers.isEmpty() || !headers.get(0).startsWith("Bearer ")) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}

		String token = headers.get(0).substring(7);
		try {
			Jwt jwt = jwtDecoder.decode(token);   // signature, exp, issuer, and audience all validated :contentReference[oaicite:3]{index=3}
			Authentication auth = new JwtAuthenticationToken(jwt);
			attrs.put("auth", auth);              // stash for ChannelInterceptor :contentReference[oaicite:4]{index=4}
			return true;
		} catch (JwtException ex) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception){

	}
}
