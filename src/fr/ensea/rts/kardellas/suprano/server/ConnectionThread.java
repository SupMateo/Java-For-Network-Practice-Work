package fr.ensea.rts.kardellas.suprano.server;
import java.io.*;
import java.net.*;

public class ConnectionThread extends Thread {
    private Socket clientSocket;

    public ConnectionThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outputStream = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
        ) {
            String data;
            while (true) {
                StringBuilder receivedData = new StringBuilder();
                char[] buffer = new char[Server.BUFFER_SIZE];
                int charsRead;

                while ((charsRead = reader.read(buffer)) != -1) {
                    receivedData.append(buffer, 0, charsRead);

                    if (charsRead < buffer.length) {
                        break;
                    }
                }

                if (receivedData.toString().isEmpty() || receivedData.toString().equals("null")) {
                    System.out.println("Client closed the connection.");
                    clientSocket.close();
                    break;
                }

                data = receivedData.toString();
                System.out.println("Received from " + clientSocket.getInetAddress().getHostAddress() + ":"+clientSocket.getPort()+" : " + data);

                writer.println("Echo: " + data);
            }
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
