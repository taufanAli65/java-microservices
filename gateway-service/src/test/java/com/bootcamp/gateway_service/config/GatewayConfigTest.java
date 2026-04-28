package com.bootcamp.gateway_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayConfigTest {
    @Test
    void exposesUserPokemonAndTradeRoutes() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(WebFluxProperties.class);
        context.registerBean(PathRoutePredicateFactory.class, () -> new PathRoutePredicateFactory(context.getBean(WebFluxProperties.class)));
        context.registerBean(RewritePathGatewayFilterFactory.class);
        context.refresh();

        try {
            RouteLocator routeLocator = new GatewayConfig().routes(new RouteLocatorBuilder(context));
            List<Route> routes = routeLocator.getRoutes().collectList().block();
            Map<String, Route> routesById = routes.stream()
                    .collect(Collectors.toMap(Route::getId, Function.identity()));

            assertEquals(URI.create("lb://user-service"), routesById.get("user-service").getUri());
            assertEquals(URI.create("lb://pokemon-service"), routesById.get("pokemon-service").getUri());
            assertEquals(URI.create("lb://trade-service"), routesById.get("trade-service").getUri());

            assertTrue(routesById.get("user-service").getPredicate().toString().contains("/gateway/user-service/**"));
            assertTrue(routesById.get("pokemon-service").getPredicate().toString().contains("/gateway/pokemon-service/**"));
            assertTrue(routesById.get("trade-service").getPredicate().toString().contains("/gateway/trade-service/**"));
        } finally {
            context.close();
        }
    }
}
