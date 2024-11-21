package fr.ensea.rts.kardellas.suprano.server;
import java.io.*;
import java.net.*;

public class ConnectionThread extends Thread {
    private Socket clientSocket;

    public ConnectionThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    protected BufferedReader createBufferedReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    protected PrintWriter createPrintWriter(OutputStream outputStream) {
        return new PrintWriter(outputStream, true);
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader reader = createBufferedReader(inputStream);
                OutputStream outputStream = clientSocket.getOutputStream();
                PrintWriter writer = createPrintWriter(outputStream);
        ) {
            StringBuilder receivedData = new StringBuilder();
            char[] buffer = new char[Server.BUFFER_SIZE];
            int charsRead = reader.read(buffer);

            if (charsRead == -1 ||
                    (charsRead > 0 && (new String(buffer, 0, charsRead).trim().isEmpty() ||
                            new String(buffer, 0, charsRead).trim().equals("null")))) {
                System.out.println("Client closed the connection.");
                clientSocket.close();
                return;
            }

            receivedData.append(buffer, 0, charsRead);
            String data = receivedData.toString().trim();

            System.out.println("Received from " +
                    clientSocket.getInetAddress().getHostAddress() +
                    ":" + clientSocket.getPort() + " : " + data);

            writer.println("Echo: " + data);
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}