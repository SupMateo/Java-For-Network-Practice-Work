import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer {
    private int port;
    private DatagramSocket socket;
    private final int bufferSize = 1024;

    public boolean state = false;



    public UDPServer(){
        this.port = 8080;
    }

    public UDPServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws SocketException {
        if (args.length == 0) {
            UDPServer server = new UDPServer();
            server.launch();
            while(true){
                DatagramPacket packet = server.readDatagram();
                server.displayPacket(packet);
            }
        }else{
            UDPServer server = new UDPServer(Integer.parseInt(args[0]));
            System.out.println(server);
            server.launch();
            System.out.println(server);
            while(true){
                DatagramPacket packet = server.readDatagram();
                server.displayPacket(packet);
            }
        }
    }

    public void launch() throws SocketException {
        try{
            socket = new DatagramSocket(this.port);
            System.out.println("Server launched on port " + this.port);
            this.state = true;
        } catch (SocketException e){
            throw new SocketException(e);
        }
    }

    @Override
    public String toString(){
        if (this.state){
            return "The server is listening on port " + this.port;
        }else{
            return "The server is off";
        }
    }

    private DatagramPacket readDatagram(){
        try{
            byte[] buffer = new byte[bufferSize];
            DatagramPacket packet = new DatagramPacket(buffer ,buffer.length);
            socket.receive(packet);
            return packet;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayPacket(DatagramPacket packet){
        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();
        String data = new String(packet.getData() ,0 , packet.getLength());
        if (data.length() > bufferSize){
            data = data.substring(0, bufferSize);
        }
        System.out.println(clientAddress + ":"+ clientPort+" > " + socket.getInetAddress() + ":"+this.port +
                " / Data : " + data);
    }
}
