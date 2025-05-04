package dev.willie.chatserver.data;

import dev.willie.chatserver.impl.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String>{
	List<User> findUserByEmail(String email);
}
