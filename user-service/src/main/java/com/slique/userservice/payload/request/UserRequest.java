package com.slique.userservice.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserRequest {
	@JsonProperty("username")
	private String username;
	
	@JsonProperty("enabled")
	private boolean enabled;
	
	@JsonProperty("emailVerified")
	private boolean emailVerified;
	
	@JsonProperty("firstName")
	private String firstName;
	
	@JsonProperty("lastName")
	private String lastName;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("credentials")
	private List<Credential> credentials = new ArrayList<>();
}
