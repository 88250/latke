package org.b3log.latke.http;

import org.b3log.latke.Latkes;

public class Server extends BaseServer {

    public static void main(final String[] args) throws Exception {
        Latkes.setScanPath("org.b3log");
        final Server server = new Server();
        server.start(8080);
    }
}
