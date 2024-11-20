package fr.ensea.rts.kardellas.suprano.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.Console;
import java.net.DatagramSocket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UDPClientTest {
    private static final String TEST_ADDRESS = "localhost";
    private static final int TEST_PORT = 12345;

    @Mock
    private Console mockConsole;


    private UDPClient udpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a testable subclass that allows mocking console
        udpClient = spy(new UDPClient(TEST_ADDRESS, TEST_PORT) {
            @Override
            protected Console getConsole() {
                return mockConsole;
            }
        });
    }

    @Test
    void testConstructor() {
        assertEquals(TEST_ADDRESS, udpClient.serverAddress);
        assertEquals(TEST_PORT, udpClient.serverPort);
    }

    @Test
    void testMainMethodWithValidArguments() {
        assertDoesNotThrow(() -> {
            UDPClient.main(new String[]{TEST_ADDRESS, String.valueOf(TEST_PORT)});
        });
    }

    @Test
    void testMainMethodWithInvalidArguments() {
        String[] invalidArgs = {"localhost"};
        assertDoesNotThrow(() -> {
            UDPClient.main(invalidArgs);
        });
    }

    @Test
    void testStartMethodSendMessage() throws Exception {
        DatagramSocket spySocket = spy(new DatagramSocket());

        when(mockConsole.readLine())
                .thenReturn("Hello, UDP!")
                .thenReturn("exit");

        UDPClient testClient = spy(new UDPClient(TEST_ADDRESS, TEST_PORT) {
            @Override
            protected Console getConsole() {
                return mockConsole;
            }
        });

        testClient.start();
        verify(mockConsole, times(2)).readLine();
    }

    @Test
    void testStartMethodWithNoConsole() {
        // Create a client that returns null console
        UDPClient testClient = spy(new UDPClient(TEST_ADDRESS, TEST_PORT) {
            @Override
            protected Console getConsole() {
                return null;
            }
        });
        assertDoesNotThrow(testClient::start);
    }
}