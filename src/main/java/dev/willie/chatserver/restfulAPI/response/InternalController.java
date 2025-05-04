package dev.willie.chatserver.restfulAPI.response;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class InternalController{
	@GetMapping("/api/internal/profile")
	public String protectedProfile(HttpServletRequest request) {
		return "successed";
	}
}
