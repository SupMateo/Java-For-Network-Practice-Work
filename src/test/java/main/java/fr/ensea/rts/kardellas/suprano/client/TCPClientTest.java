package fr.ensea.rts.kardellas.suprano.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TCPClientTest {
    private TCPClient tcpClient;

    @Mock
    private Socket mockSocket;

    @Mock
    private PrintWriter mockOut;

    @Mock
    private BufferedReader mockIn;

    @Mock
    private BufferedReader mockStdIn;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tcpClient = spy(new TCPClient("localhost", 8080));
    }

    @Test
    void testConstructor() {
        assertEquals("localhost", tcpClient.serverAddress);
        assertEquals(8080, tcpClient.serverPort);
    }

    @Test
    void testToHex() {
        String input = "Hello";
        String expectedHex = "48 65 6c 6c 6f";
        assertEquals(expectedHex, tcpClient.toHex(input));
    }

    @Test
    void testToHexEmptyString() {
        assertEquals("", tcpClient.toHex(""));
    }

    @Test
    void testToHexWithSpecialCharacters() {
        String input = "A1!@#";
        String expectedHex = "41 31 21 40 23";
        assertEquals(expectedHex, tcpClient.toHex(input));
    }

    @Test
    void testStart_SuccessfulCommunication() throws IOException {
        // Mocks
        doReturn(mockSocket).when(tcpClient).createSocket();
        doReturn(mockOut).when(tcpClient).createPrintWriter(mockSocket);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream("Server Response".getBytes());
        doReturn(mockInputStream).when(mockSocket).getInputStream();

        doReturn(mockIn).when(tcpClient).createBufferedReader(mockInputStream);
        doReturn(mockStdIn).when(tcpClient).createStdinReader();

        // Simulate user input and server response
        when(mockStdIn.readLine())
                .thenReturn("Hello Server")
                .thenReturn(null);  // Simulate exit
        when(mockIn.readLine()).thenReturn("Server Response");

        assertDoesNotThrow(() -> tcpClient.start());

        verify(mockOut).println("Hello Server");
        verify(mockIn).readLine();
    }

    @Test
    void testStart_IOException() throws IOException {
        doThrow(new IOException("Connection failed")).when(tcpClient).createSocket();

        // Capture system error output
        java.io.ByteArrayOutputStream errContent = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(errContent));
        tcpClient.start();
        assertTrue(errContent.toString().contains("IOException: Connection failed"));
        System.setErr(System.err);
    }
}