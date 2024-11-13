package fr.ensea.rts.kardellas.suprano.client;

public abstract class Client {
    protected String serverAddress;
    protected int serverPort;

    public Client(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    public abstract void start();
}
