package dev.willie.chatserver;

import dev.willie.chatserver.config.ConfigHandler;
import dev.willie.chatserver.restfulAPI.request.LogtoApiRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static java.lang.Thread.sleep;

@SpringBootApplication
public class ChatServerApplication{

	public static void main(String[] args){
		ConfigHandler.loadConfigs();
		SpringApplication.run(ChatServerApplication.class, args);
	}
	@Bean
	public CommandLineRunner requestLogtoM2mAccessToken(LogtoApiRequest logtoApiRequest) {
		return args -> {
			// This will run on application startup
			Thread thread = new Thread(() ->{
				while(true){
					logtoApiRequest.requestLogtoM2mAccessToken();
					try{
						Thread.sleep(1000 * 2400);
					}catch(InterruptedException e){
						throw new RuntimeException(e);
					}
				}
			});
			thread.start();
		};
	}
}
