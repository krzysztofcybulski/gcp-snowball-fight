/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hello

import com.fasterxml.jackson.annotation.JsonProperty
import ratpack.jackson.Jackson.fromJson
import ratpack.server.RatpackServer

fun main() {
    val algorithm = Algorithm()
    RatpackServer.start { server ->
        server
            .serverConfig { config ->
                config.port(8080)
            }
            .handlers { chain ->
                chain.path { c ->
                    c.byMethod {
                        it.get { ctx -> ctx.render("Let the battle begin with Ratpack!") }
                            .post { ctx ->
                                ctx.parse(fromJson(ArenaUpdate::class.java))
                                    .map(ArenaUpdate::arena)
                                    .map(algorithm::decide)
                                    .mapError { "T" }
                                    .then(ctx::render)
                            }
                    }
                }
            }
    }
}

data class Links(@JsonProperty("self") val self: Self)

data class Self(@JsonProperty("href") val href: String)

data class PlayerState(
    @JsonProperty("x") val x: Int,
    @JsonProperty("y") val y: Int,
    @JsonProperty("direction") val direction: String,
    @JsonProperty("wasHit") val wasHit: Boolean,
    @JsonProperty("score") val score: Int
)

data class Arena(@JsonProperty("dims") val dims: List<Int>, @JsonProperty("state") val state: Map<String, PlayerState>)

data class ArenaUpdate(@JsonProperty("_links") val links: Links, @JsonProperty("arena") val arena: Arena)