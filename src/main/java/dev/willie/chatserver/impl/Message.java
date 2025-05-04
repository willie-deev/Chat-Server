package dev.willie.chatserver.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message{
	@Id
	private Long id;
	private Long sendTime;
	private String senderId;
	private String content;

	protected Message(){};

	public Message(Long id, Long sendTime, String senderId, String content){
		this.id = id;
		this.sendTime = sendTime;
		this.senderId = senderId;
		this.content = content;
	}

	public String getContent(){
		return this.content;
	}

	public Long getSendTime(){
		return this.sendTime;
	}

	public String getSenderId(){
		return this.senderId;
	}

	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return id;
	}
}
