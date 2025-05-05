package dev.willie.chatserver.restfulAPI.request;

import dev.willie.chatserver.config.ConfigHandler;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Component
public class LogtoApiRequest{
	private final WebClient.Builder webClientBuilder;
	public static String accessToken;
	public LogtoApiRequest(WebClient.Builder webClientBuilder){
		this.webClientBuilder = webClientBuilder;
	}
	public void requestLogtoM2mAccessToken(){
		WebClient webClient = webClientBuilder.build();
		String authString = ConfigHandler.APP_ID + ":" + ConfigHandler.APP_SECRET;
		String authBase64 = Base64.getEncoder().encodeToString(authString.getBytes());
		String response = webClient.post()
				.uri(ConfigHandler.LOGTO_OIDC_TOKEN_ENDPOINT)
				.header("Authorization", "Basic " + authBase64)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData("grant_type", "client_credentials")
						.with("resource", ConfigHandler.LOGTO_API_ENDPOINT)
						.with("scope", "all"))
				.retrieve()
				.bodyToMono(String.class)
				.block();
//		System.out.println(response);
		try{
			JSONObject jsonObject = new JSONObject(response);
			LogtoApiRequest.accessToken = (String)jsonObject.get("access_token");
//			System.out.println(LogtoApiRequest.accessToken);
		}catch(Exception ignored){

		}
	}
}
