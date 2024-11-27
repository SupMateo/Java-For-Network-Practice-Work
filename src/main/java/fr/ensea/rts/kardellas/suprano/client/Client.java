package fr.ensea.rts.kardellas.suprano.client;

import java.io.IOException;

public abstract class Client {
    protected String serverAddress;
    protected int serverPort;
    protected boolean running;
    public Client(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }
    public boolean isRunning() {
        return running;
    }

    public void stop(){
        running=false;
    }

    public abstract void start(boolean GUI) throws Exception;

    public abstract void sendMessage(String command) throws Exception;
}