package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SelfSignedCertificate;

public class ServerWebSocketMock extends AbstractVerticle {
    private static final String URI =  "/ws/test";
    private static final int PORT = 20456;

    @Override
    public void start(Promise<Void> startPromise) {
        SelfSignedCertificate certificate = SelfSignedCertificate.create("localhost");
        HttpServerOptions options = new HttpServerOptions()
                .setPort(PORT)
                //.setIdleTimeout(500)
                //.setIdleTimeoutUnit(TimeUnit.MILLISECONDS)
                .setSsl(true)
                .setKeyCertOptions(certificate.keyCertOptions())
                .setTrustOptions(certificate.trustOptions());

        var server = vertx.createHttpServer(options)
                .websocketHandler(webSocket -> {
                    if (webSocket.path().equals(URI)) {
                        webSocket.closeHandler(h -> {

                        });
                    }
                    else {
                        webSocket.reject(404);
                    }
                })
                .listen(PORT, "localhost", h -> {
                    if (h.succeeded()) {
                        startPromise.complete();
                    }
                    else {
                        startPromise.fail(h.cause());
                    }
                });


    }
}
