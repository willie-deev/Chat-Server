package dev.willie.chatserver.restfulAPI.response;

import dev.willie.chatserver.config.ConfigHandler;
import dev.willie.chatserver.data.MessageRepository;
import dev.willie.chatserver.data.UserRepository;
import dev.willie.chatserver.impl.Message;
import dev.willie.chatserver.impl.User;
import dev.willie.chatserver.restfulAPI.request.LogtoApiRequest;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class ChatController{
	private final WebClient.Builder webClientBuilder;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	public ChatController(WebClient.Builder webClientBuilder, UserRepository userRepository, MessageRepository messageRepository) {
		this.webClientBuilder = webClientBuilder;
		this.userRepository = userRepository;
		this.messageRepository = messageRepository;
	}

	@GetMapping("/api/addProfile")
	public String addProfile(@AuthenticationPrincipal Jwt jwt){
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
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return "succeed: added new user id";
	}

	@PostMapping("/api/sendMessage")
	public String sendMessage(@AuthenticationPrincipal Jwt jwt, @RequestBody String body){
		String userId = (String)jwt.getClaims().get("sub");
		try{
			JSONObject bodyJson = new JSONObject(body);
			Message message = new Message(messageRepository.count(), System.currentTimeMillis(), userId, (String)bodyJson.get("content"));
			messageRepository.save(message);
		}catch(Exception ignored){
			return "json parse failed";
		}
		return "succeed";
	}

	@GetMapping("/api/getMessages")
	public String getMessages(@AuthenticationPrincipal Jwt jwt){
		List<Message> messages = messageRepository.findAll();
		Map<String, Map<String, String>> messageMap = new HashMap<>();
		messages.forEach(message -> {
			Map<String, String> tmp = new HashMap<>();
			User user = userRepository.findById(message.getSenderId()).orElseThrow();
			tmp.put("user_email", user.getEmail());
			tmp.put("content", message.getContent());
			messageMap.put(message.getSendTime().toString(), tmp);
		});
		JSONObject json = new JSONObject(messageMap);
		return json.toString();
	}

	@GetMapping("/api/.wellknown/config.json")
	public String publicConfig() {
		return "Public config.";
	}
}
