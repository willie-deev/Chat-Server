package dev.willie.chatserver.webSocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public class StompAuthChannelInterceptor implements ChannelInterceptor {
	private final JwtDecoder jwtDecoder;

	public StompAuthChannelInterceptor(JwtDecoder jwtDecoder) {
		this.jwtDecoder = jwtDecoder;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			List<String> authHeaders = accessor.getNativeHeader("Authorization");
			if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
				throw new IllegalArgumentException("Missing or invalid Authorization header");
			}
			String token = authHeaders.get(0).substring(7);
			try {
				Jwt jwt = jwtDecoder.decode(token);
				Authentication auth = new JwtAuthenticationToken(jwt);
				SecurityContextHolder.getContext().setAuthentication(auth);
				accessor.setUser(auth);
			} catch (JwtException ex) {
				throw new IllegalArgumentException("Invalid JWT token");
			}
		}
		return message;
	}
}