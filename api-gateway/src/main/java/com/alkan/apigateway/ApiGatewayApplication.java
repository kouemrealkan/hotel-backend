package com.alkan.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.function.Function;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ApiGatewayApplication {

    public static final String[] microServices = {
            "definition-service",
            "hotel-service"
    };

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();
        Arrays.stream(microServices).forEach(s -> {
            routes.route(s, routeFunction(s));
        });
        return routes.build();
    }

    private Function<PredicateSpec, Buildable<Route>> routeFunction(String service) {
        return routeFunction(service, 1, service);
    }

    private Function<PredicateSpec, Buildable<Route>> routeFunction(String path, int stripPrefix, String uri) {
        return r -> r
                .path("/" + path + "/*/**")
                .filters(f -> f
                        .stripPrefix(stripPrefix)
                        .retry(config -> config.setRetries(1).setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                )
                .uri("lb://" + uri);
    }

    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("fallback");
    }
}
