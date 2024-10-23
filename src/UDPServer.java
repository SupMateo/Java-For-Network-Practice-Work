import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDPServer {
    private int port;
    private DatagramSocket socket;

    public UDPServer(){
        this.port = 8080;
    }

    public UDPServer(int port) {
        this.port = port;
    }

    public void launch() throws SocketException {
        try{
            socket = new DatagramSocket(this.port);
            System.out.println("Server launched on port " + this.port);
        } catch (SocketException e){
            throw new SocketException(e);
        }
    }

    private void readDatagram(){
        try{
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer ,buffer.length);
            socket.receive(packet);
            InetAddress clientAddress = packet . getAddress () ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
