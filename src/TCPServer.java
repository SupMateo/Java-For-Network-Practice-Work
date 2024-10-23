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
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection accepted from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String data;
            StringBuilder receivedData = new StringBuilder();
            char[] buffer = new char[1024];
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                receivedData.append(buffer, 0, charsRead);
                if (charsRead < buffer.length) break;
            }

            data = receivedData.toString();
            System.out.println("Received: " + data);

            OutputStream outputStream = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println("Echo: " + data);

            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error handling client connection", e);
        }
    }

    public static void main(String[] args) {
        TCPServer server = (args.length == 0) ? new TCPServer() : new TCPServer(Integer.parseInt(args[0]));
        server.launch();
        while (true) {
            server.handleConnection();
        }
    }
}
