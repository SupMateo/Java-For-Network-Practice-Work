import java.net.DatagramSocket;

public class UDPServer {
    private int port;
    private DatagramSocket socket;

    public UDPServer(){
        this.port = 8080;
    }

    public UDPServer(int port){
        this.port = port;
    }

    public void launch(){
        
    }
}
