# vertx-idle-timeout-reproducer
Reproduce an issue with the HttpClient idle time out, where writes resets the timeout.

Run the test with:

@ ./gradlew test

This will trigger a time out after 4s of the test while it should complete after 500ms.

Removing the periodic write on the WebSocket will let the idle timeout to fire after 500ms and the test will complete.