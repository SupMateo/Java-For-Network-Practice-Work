package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TCPServerTest {

    private TCPServer server;
    private ServerSocket mockServerSocket;
    private Socket mockClientSocket;

    @BeforeEach
    public void setUp() {
        server = spy(new TCPServer(0) {
            @Override
            public void launch() {
                ServerSocket mockSocket = mock(ServerSocket.class);
                this.setServerSocket(mockSocket);
                this.state = true;
            }
        });

        mockServerSocket = mock(ServerSocket.class);
        mockClientSocket = mock(Socket.class);

        try {
            doReturn(mockServerSocket).when(server).getServerSocket();
            when(mockServerSocket.accept()).thenReturn(mockClientSocket);
        } catch (IOException e) {
            fail("Test setup failed", e);
        }
    }


    @Test
    public void testLaunch() {
        server.launch();
        assertTrue(server.state, "Server should be in running state after launch");
        assertNotNull(server.getServerSocket(), "Server socket should be created");
    }

    @Test
    void testHandleConnection() throws IOException {

        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        Socket mockClientSocket = Mockito.mock(Socket.class);
        ConnectionThread mockConnectionThread = Mockito.mock(ConnectionThread.class);

        when(mockServerSocket.accept()).thenReturn(mockClientSocket);

        TCPServer server = new TCPServer() {
            @Override
            protected ConnectionThread createConnectionThread(Socket clientSocket) {
                assertSame(mockClientSocket, clientSocket);
                return mockConnectionThread;
            }
        };

        server.setServerSocket(mockServerSocket);
        server.handleConnection();

        Mockito.verify(mockServerSocket, Mockito.times(1)).accept();
        Mockito.verify(mockConnectionThread, Mockito.times(1)).start();
    }

    @Test
    void testHandleConnectionWhenServerSocketIsClosed() throws IOException {

        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        when(mockServerSocket.isClosed()).thenReturn(true);

        TCPServer server = new TCPServer();
        server.setServerSocket(mockServerSocket);
        server.handleConnection();

        Mockito.verify(mockServerSocket, Mockito.never()).accept();
    }


    @Test
    void testStop() throws IOException {
        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        TCPServer server = new TCPServer();
        server.setServerSocket(mockServerSocket);

        server.stop();
        Mockito.verify(mockServerSocket).close();
        assertTrue(mockServerSocket.isClosed());
    }


    @Test
    void testHandleConnectionWithIOException() throws IOException {
        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        when(mockServerSocket.accept()).thenThrow(new IOException("Erreur réseau"));

        TCPServer server = new TCPServer();
        server.setServerSocket(mockServerSocket);

        assertThrows(RuntimeException.class, server::handleConnection);
    }



    @Test
    public void testCreateConnectionThread() {
        server.launch();
        ConnectionThread thread = server.createConnectionThread(mockClientSocket);

        assertNotNull(thread, "Should create a non-null ConnectionThread");
        assertEquals(mockClientSocket, thread.getClientSocket(),
                "Created thread should have the correct client socket");
    }

    @Test
    public void testDefaultConstructor() {
        TCPServer defaultServer = new TCPServer();
        assertNotNull(defaultServer, "Default constructor should create a non-null instance");
        assertEquals(0, defaultServer.getPort(), "Default constructor should set port to 0");
    }


    @Test
    public void testGetSetServerSocket() {
        TCPServer server = Mockito.spy(new TCPServer());
        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        doReturn(mockServerSocket).when(server).getServerSocket();

        server.setServerSocket(mockServerSocket);
        assertEquals(mockServerSocket, server.getServerSocket());
    }

    @Test
    public void testMainLoop() throws IOException, InterruptedException {
        server.launch();

        try (MockedConstruction<ConnectionThread> mockThreadConstruction =
                     Mockito.mockConstruction(ConnectionThread.class)) {

            // Simuler deux connexions clients
            doReturn(mockClientSocket, mockClientSocket)
                    .when(server.getServerSocket()).accept();

            // Exécuter deux itérations de la boucle principale
            server.state = true;
            new Thread(() -> {
                server.handleConnection();
                server.handleConnection();
                server.state = false;
            }).start();

            Thread.sleep(500);

            assertEquals(2, mockThreadConstruction.constructed().size(),
                    "Should create two ConnectionThread instances for two connections");
        }
    }

    @Test
    public void testStopWithException() {
        server.launch();

        try {
            doThrow(new IOException("Simulated close error")).when(mockServerSocket).close();
        } catch (IOException e) {
            fail("Setup failed", e);
        }

        assertThrows(RuntimeException.class, server::stop, "stop() should throw a RuntimeException");
        assertFalse(server.state, "Server should not be running after stop()");
    }


}