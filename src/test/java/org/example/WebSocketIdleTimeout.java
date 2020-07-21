package org.example;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import io.vertx.junit5.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class WebSocketIdleTimeout {
    private static final String URI = "/ws/test";
    private static final int PORT = 20456;
    private final Buffer webSocketPingBody = Buffer.buffer("ping");

    /**
     * The tests fails after a 4 seconds timeout. However it should succeed after 500ms when the Idle timeout is
     * triggered and the close handler completes the test. If nothing is sent to the Mock WebSocket server it does
     * indeed timeout after 500ms. But sending data seams to prevent the timeout which is inline with (from what I
     * understand) the behaviour of the underlying Netty IdleStateHandler usage. It is initialized with all instead
     * of write IdleStateEvent timeout. According to the Netty dock:
     * allIdleTimeSeconds - an IdleStateEvent whose state is IdleState.ALL_IDLE will be triggered when neither
     * read nor write was performed for the specified period of time. Specify 0 to disable.
     */
    @Test
    @Timeout(value = 4, timeUnit = TimeUnit.SECONDS)
    void testWebSocketClientNoPing(Vertx vertx, VertxTestContext testContext) {
        // Deploy the Server mock
        vertx.deployVerticle(new ServerWebSocketMock(), h -> {

            HttpClientOptions options = new HttpClientOptions()
                    .setIdleTimeout(500)
                    .setIdleTimeoutUnit(TimeUnit.MILLISECONDS)
                    .setSsl(true)
                    .setTrustAll(true);

            MultiMap headers = MultiMap.caseInsensitiveMultiMap();
            WebSocketConnectOptions wsOptions = new WebSocketConnectOptions()
                    .setHost("localhost")
                    .setPort(PORT)
                    .setHeaders(headers)
                    .setSsl(true)
                    .setURI(URI);
            vertx.createHttpClient(options)
                    .webSocket(wsOptions, ar -> {
                        if (ar.succeeded()) {
                            var webSocket = ar.result();
                            webSocket.closeHandler(v -> {
                                System.out.println("Closed handler ran");
                                testContext.completeNow();
                            });
                            webSocket.pongHandler(v -> {
                                System.out.println("Got pong");
                            });
                            webSocket.exceptionHandler(v -> {
                                System.out.println("Exception handler ran");
                                testContext.completeNow();
                            });
                            //vertx.setPeriodic(100, ph -> webSocket.writePing(webSocketPingBody));
                            vertx.setPeriodic(100, bh -> webSocket.writeBinaryMessage(webSocketPingBody));
                        }
                        else {
                            testContext.failNow(ar.cause());
                        }
                    });

        });
    }
}
