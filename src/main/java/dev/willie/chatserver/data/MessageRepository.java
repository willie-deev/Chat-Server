package dev.willie.chatserver.data;

import dev.willie.chatserver.impl.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>{

}
