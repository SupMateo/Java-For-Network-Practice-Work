package fr.ensea.rts.kardellas.suprano.server;

import java.io.*;
import java.net.*;

public class TCPServer extends Server {
    private ServerSocket serverSocket;

    public TCPServer() {
        super();
    }

    public TCPServer(int port) {
        super(port);
    }

    @Override
    public void launch() {
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("Server launched on IP " + InetAddress.getLocalHost().getHostAddress() + " and on port " + this.port);
            this.state = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to launch server", e);
        }
    }

    @Override
    public void handleConnection() {
        try {
            System.out.println("Waiting for a client connection...");
            Socket clientSocket = serverSocket.accept();  // This is where it blocks in real code
            System.out.println("Connection accepted from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

            // Call createConnectionThread to avoid direct instantiation in the test
            ConnectionThread connectionThread = createConnectionThread(clientSocket);
            connectionThread.start();
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                throw new RuntimeException("Error handling client connection", e);
            }
        }
    }

    // New method to allow mocking the creation of ConnectionThread
    protected ConnectionThread createConnectionThread(Socket clientSocket) {
        return new ConnectionThread(clientSocket);
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            this.state = false;
            System.out.println("Server stopped.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to stop the server", e);
        }
    }

    public static void main(String[] args) {
        TCPServer server = (args.length == 0) ? new TCPServer() : new TCPServer(Integer.parseInt(args[0]));
        server.launch();

        // Add a shutdown hook to stop the server on application exit
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        while (server.state) {
            server.handleConnection();
        }
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }
}
