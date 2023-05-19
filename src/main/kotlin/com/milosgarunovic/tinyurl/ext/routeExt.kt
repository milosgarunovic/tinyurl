package com.milosgarunovic.tinyurl.ext

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.time.withTimeout
import java.time.Duration

fun Route.routeTimeout(duration: Duration, callback: Route.() -> Unit): Route {
    // With createChild, we create a child node for this received Route
    val routeWithTimeout = this.createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })

    // Intercepts calls from this route at the features step
    routeWithTimeout.intercept(ApplicationCallPipeline.Plugins) {
        withTimeout(duration) {
            proceed() // With proceed we can define code to be executed before and after the call
        }
    }

    // Configure this route with the block provided by the user
    callback(routeWithTimeout)

    return routeWithTimeout
}