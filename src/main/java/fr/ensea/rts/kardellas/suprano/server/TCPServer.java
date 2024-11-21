package fr.ensea.rts.kardellas.suprano.server;

import java.io.*;
import java.net.*;

public class TCPServer extends Server {
    private ServerSocket serverSocket;

    public TCPServer() {
        super(8080); // Par défaut, on utilise le port 8080
    }

    public TCPServer(int port) {
        super(port);
    }

    @Override
    public void launch() {
        try {
            if (serverSocket == null || serverSocket.isClosed()) {
                serverSocket = new ServerSocket(this.port);
                System.out.println("Server launched on IP " + InetAddress.getLocalHost().getHostAddress() + " and on port " + this.port);
                this.state = true;
            } else {
                throw new IllegalStateException("Server is already running.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to launch server", e);
        }
    }

    @Override
    public void handleConnection() {
        try {
            System.out.println("Waiting for a client connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection accepted from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

            ConnectionThread connectionThread = createConnectionThread(clientSocket);
            connectionThread.start();
        } catch (IOException e) {
            if (serverSocket == null || serverSocket.isClosed()) {
                System.out.println("Server socket is closed, stopping server.");
            } else {
                throw new RuntimeException("Error handling client connection", e);
            }
        }
    }


    protected ConnectionThread createConnectionThread(Socket clientSocket) {
        return new ConnectionThread(clientSocket);
    }

    public void stop() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                this.state = false;
            } catch (IOException e) {
                throw new RuntimeException("Error while closing server socket", e);
            }
        }
    }

    public void runMainLoop() {
        while (state) {
            handleConnection();
        }
    }

    public static void main(String[] args) {
        TCPServer server = (args.length == 0) ? new TCPServer() : new TCPServer(Integer.parseInt(args[0]));
        server.launch();

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        server.runMainLoop();
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public int getPort() {
        return this.port;
    }
}

