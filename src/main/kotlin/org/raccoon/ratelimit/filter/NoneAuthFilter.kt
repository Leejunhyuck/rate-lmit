package org.raccoon.ratelimit.filter

import org.raccoon.ratelimit.service.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

@Component
class NoneAuthFilter : AbstractGatewayFilterFactory<NoneAuthFilter.Config>(Config::class.java) {
    @Autowired
    private val redisService: RedisService? = null
    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange?, chain: GatewayFilterChain -> chain.filter(exchange) }
    }

    class Config
}