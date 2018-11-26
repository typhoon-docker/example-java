import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


public class JavaServer {

    private static final int PORT = 8066;
    private HttpServer _server;

    public JavaServer() { }

    public void run() {
        startServer();
    }

    public void close() {
        _server.stop(0);
    }

    public void startServer() {
        try {
            // Create the server
            _server = HttpServer.create(new InetSocketAddress(PORT), 0);
            _server.createContext("/", new RootHandler());
            _server.setExecutor(null);  // creates a default executor

            // Start the server
            _server.start();
            System.out.println("JavaServer HTTP server started on port " + PORT);

        } catch (IOException e) {
            System.out.println("Could not start the JavaServer Http Server: " + e);
        }
    }

    static class RootHandler implements HttpHandler {

        RootHandler() { }

        public void handle(HttpExchange t) throws IOException {
            System.out.println("RootHandler called");

            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            String label;

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("git rev-parse --short HEAD");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                label = reader.readLine();
            }

            String response = String.format("Java server is up on %s, with label %s", hostname, label);
            final byte[] rawResponseBody = response.getBytes(StandardCharsets.UTF_8);

            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/plain");

            t.sendResponseHeaders(200, rawResponseBody.length);
            t.getResponseBody().write(rawResponseBody);
            t.close();

            System.out.println("RootHandler answered");
        }
    }
}
