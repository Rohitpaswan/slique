package com.example.paymentservice.config;


import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {
	@Value("${razorpay.key.id}")
	private String razorpayApiKey;
	
	@Value("${razorpay.key.secret}")
	private String razorpayApiSecret;
	
	@Bean
	public RazorpayClient razorpayClient() throws Exception {
		return new RazorpayClient(razorpayApiKey, razorpayApiSecret);
	}
}
