package com.bridgelabz.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {
	public CustomFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
        	System.out.println(request.getHeaders().get("token"));	
            if (!request.getHeaders().containsKey("token")) {
            	System.out.println("token is not present in Header");
                return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            };

            return chain.filter(exchange.mutate().request(request).build());
        };
	}

	public static class Config {
		// Put the configuration properties
	}
	
 
     private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus)  {
         ServerHttpResponse response = exchange.getResponse();
         response.setStatusCode(httpStatus);
 
         return response.setComplete();
     }}