package fr.ensea.rts.kardellas.suprano.server;

public abstract class Server {
    protected final int port;
    protected boolean state = false;

    protected static int BUFFER_SIZE = 1024;

    public Server(int port) {
        this.port = port;
    }

    public Server() {
        this.port = 4444;
    }

    public abstract void launch() throws Exception;

    public abstract void handleConnection();

    @Override
    public String toString() {
        if (this.state) {
            return "The server is listening on port " + this.port;
        } else {
            return "The server is off";
        }
    }
}
