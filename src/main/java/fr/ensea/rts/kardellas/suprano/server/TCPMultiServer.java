package fr.ensea.rts.kardellas.suprano.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiServer extends Server {
    private ServerSocket serverSocket;
    private ConnectionThreadFactory connectionThreadFactory;
    private volatile boolean running; // Flag to control the server's state

    // Default constructor
    public TCPMultiServer() {
        super();
        this.connectionThreadFactory = new ConnectionThreadFactory(); // Default factory
    }

    // Constructor with port
    public TCPMultiServer(int port) {
        super(port);
        this.connectionThreadFactory = new ConnectionThreadFactory(); // Default factory
    }

    // Allow injection of a custom factory
    public TCPMultiServer(int port, ConnectionThreadFactory connectionThreadFactory) {
        super(port);
        this.connectionThreadFactory = connectionThreadFactory;
    }

    @Override
    public void launch() {
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("Server launched on IP " + InetAddress.getLocalHost().getHostAddress() + " and on port " + this.port);
            this.state = true;
            this.running = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to launch server", e);
        }
    }

    @Override
    public void handleConnection() {
        System.out.println("Server is ready to accept multiple connections...");
        while (running) { // Check the running flag to continue the loop
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                // Use the injected factory to create a new thread for each client connection
                ConnectionThread connectionThread = connectionThreadFactory.createConnectionThread(clientSocket);
                connectionThread.start();

            } catch (IOException e) {
                if (running) { // Log errors only if the server was running
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
        System.out.println("Server stopped accepting connections.");
    }

    public void stop() {
        this.running = false; // Stop the loop
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Close the ServerSocket
                System.out.println("Server socket closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing the server socket: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TCPMultiServer server = (args.length == 0) ? new TCPMultiServer() : new TCPMultiServer(Integer.parseInt(args[0]));
        server.launch();


        Thread serverThread = new Thread(server::handleConnection);
        serverThread.start();

        try {
            Thread.sleep(30000); // Wait for 30 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.stop();
    }

    // Factory class to generate ConnectionThread instances
    public static class ConnectionThreadFactory {
        public ConnectionThread createConnectionThread(Socket clientSocket) {
            return new ConnectionThread(clientSocket);
        }
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}
