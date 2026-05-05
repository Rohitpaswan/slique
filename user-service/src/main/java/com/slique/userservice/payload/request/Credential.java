package com.slique.userservice.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Credential {
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("value")
	private String value;
	
	@JsonProperty("temporary")
	private boolean temporary;
}
