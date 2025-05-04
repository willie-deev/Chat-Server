package dev.willie.chatserver.impl;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User{
	@Id
	private String id;
	private String email;

	protected User(){}

	public User(String id, String email){
		this.id = id;
		this.email = email;
	}

	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return id;
	}

	public String getEmail(){
		return this.email;
	}
}
