package dev.willie.chatserver.restfulAPI.response;

import dev.willie.chatserver.config.ConfigHandler;
import dev.willie.chatserver.data.UserRepository;
import dev.willie.chatserver.impl.User;
import dev.willie.chatserver.restfulAPI.request.LogtoApiRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class ProtectedController{
	private final WebClient.Builder webClientBuilder;
	private final UserRepository userRepository;
	public ProtectedController(WebClient.Builder webClientBuilder, UserRepository userRepository) {
		this.webClientBuilder = webClientBuilder;
		this.userRepository = userRepository;
	}
	@GetMapping("/api/addProfile")
	public String protectedProfile(@AuthenticationPrincipal Jwt jwt){
		String userId = (String)jwt.getClaims().get("sub");
//		System.out.println("userId: "+ userId);
		if(userRepository.existsById(userId)){
			return "succeed: user id existed";
		}
		WebClient webClient = webClientBuilder.build();
		String response = webClient.get()
				.uri(ConfigHandler.LOGTO_API_ENDPOINT + "/users/" + jwt.getSubject())
				.header("Authorization", "Bearer " + LogtoApiRequest.accessToken)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		try{
			JSONObject jsonObject = new JSONObject(response);
			String email = (String)jsonObject.get("primaryEmail");

			User user = new User(userId, email);
			userRepository.save(user);
//			User testuser = userRepository.findById(userId).orElseThrow(
//					() -> new EntityNotFoundException("User not found with id: " + userId)
//			);
//			System.out.println("testuser.getEmail(): " + testuser.getEmail());
//
//			User findUserByEmail = userRepository.findUserByEmail(email).get(0);
//			System.out.println("findUserByEmail: " + findUserByEmail.getId());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return "succeed: added new user id";
	}

	@GetMapping("/api/.wellknown/config.json")
	public String publicConfig() {
		return "Public config.";
	}
}
