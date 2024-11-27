package fr.ensea.rts.kardellas.suprano.server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class TCPMultiServer extends Server {
    private ServerSocket serverSocket;
    public TCPMultiServer() {
        super();
    }
    public TCPMultiServer(int port) {
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
        System.out.println("Server is ready to accept multiple connections...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                // Create and start a new thread for each client connection
                ConnectionThread connectionThread = new ConnectionThread(clientSocket);
                connectionThread.start();
            } catch (IOException e) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        TCPMultiServer server = (args.length == 0) ? new TCPMultiServer() : new TCPMultiServer(Integer.parseInt(args[0]));
        server.launch();
        server.handleConnection();
    }

    public static class ConnectionThreadFactory {
        public ConnectionThread createConnectionThread(Socket clientSocket) {
            return new ConnectionThread(clientSocket);
        }
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}



