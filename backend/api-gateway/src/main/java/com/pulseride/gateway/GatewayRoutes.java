package com.pulseride.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {
    @Bean
    RouteLocator routes(RouteLocatorBuilder builder,
	    @Value("${services.driver-url:http://localhost:8083}") String driverUrl) {
	return builder.routes()
		.route("auth-service", route -> route.path("/pulse-ride/auth/**")
			.filters(filter -> filter.stripPrefix(1)).uri("lb://AUTH-SERVICE"))
		.route("user-service", route -> route.path("/pulse-ride/users/**")
			.filters(filter -> filter.stripPrefix(1)).uri("lb://USER-SERVICE"))
		.route("ride-service", route -> route.path("/pulse-ride/rides/**")
			.filters(filter -> filter.stripPrefix(1)).uri("lb://RIDE-SERVICE"))
		.route("driver-service", route -> route.path("/pulse-ride/drivers/**")
			.filters(filter -> filter.stripPrefix(1)).uri(driverUrl))
		.route("tracking-service", route -> route.path("/pulse-ride/tracking/**")
			.filters(filter -> filter.stripPrefix(1)).uri("lb://TRACKING-SERVICE"))
		.route("pricing-service", route -> route.path("/pulse-ride/pricing/**")
			.filters(filter -> filter.stripPrefix(1)).uri("lb://PRICING-SERVICE"))
		.route("payment-service", route -> route.path("/pulse-ride/payments/**")
			.filters(filter -> filter.stripPrefix(1)).uri("lb://PAYMENT-SERVICE"))
		.route("admin-service", route -> route.path("/pulse-ride/admin/**")
			.filters(filter -> filter.stripPrefix(1)).uri("lb://ADMIN-SERVICE"))
		.build();
    }
}
