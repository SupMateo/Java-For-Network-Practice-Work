# Java For Network - Practice Work
#### Coded by Amin Kardellas and Matéo Suprano from ENSEA / RTS 2024 - 2025

## Overview

This project implements two server types—`UDPServer` and `TCPServer`—both extending an abstract class `Server`. The servers handle client connections by receiving data and responding with an echo of the data. The project also includes a `UDPClient` for interacting with the `UDPServer`, and a `TCPClient` for interacting with the `TCPServer`.

The servers support:
- Server launching on a specified port.
- Handling client connections.
- Echoing received data back to the client.

## Project Structure

### Classes

1. **`Server` (Abstract Class)**:
    - Base class for both `UDPServer` and `TCPServer`.
    - Common attributes:
        - `port`: The port number the server listens on.
        - `state`: Boolean indicating whether the server is running.
    - Methods:
        - `launch()`: Abstract method to launch the server.
        - `handleConnection()`: Abstract method to handle client connections.
        - `toString()`: Returns a string describing the server's state (running or stopped).

2. **`UDPServer` (Concrete Class)**:
    - Implements a server that communicates over the UDP protocol.
    - Key functionality:
        - Launches a UDP server on the specified port using a `DatagramSocket`.
        - Receives data from clients and echoes it back.
    - Methods:
        - `launch()`: Launches the server on the specified port.
        - `handleConnection()`: Receives packets from a client, prints the data, and echoes it back.
    - Usage:
        - The server can be started via the `main()` method, either with a specified port or using the default port (`4444`).

3. **`TCPServer` (Concrete Class)**:
    - Implements a server that communicates over the TCP protocol.
    - Key functionality:
        - Launches a TCP server on the specified port using a `ServerSocket`.
        - Accepts client connections, reads data, and echoes it back.
    - Methods:
        - `launch()`: Starts the TCP server on the given port.
        - `handleConnection()`: Accepts a client connection, reads incoming data, and echoes it back.
    - Usage:
        - The server can be started via the `main()` method, either with a specified port or using the default port (`4444`).
     
4. **`UDPClient` (Concrete Class)**:
    - A client that sends data to the `UDPServer`.
    - Key functionality:
        - Interacts with the `UDPServer` by sending data packets over UDP.
        - Takes user input from the console and sends it to the server.
        - The user can type `'exit'` to stop sending data and close the client.
    - Usage:
        - Run with the following command:
          ```bash
          java UDPClient <hostname> <port>
          ```
        - Example:
          ```bash
          java UDPClient localhost 5555
          ```

## Usage

### UDP Server

To run the UDP server:

1. Compile the `UDPServer.java` file:
   ```bash
   javac UDPServer.java
   ```

. Run the server with an optional port number argument:
  ```bash
   java UDPServer [port]
   ```
If no port is specified, the server defaults to port `4444`.
The server will print information about received data and echo the same data back to the client.

### TCP Server
To run the TCP server:

1. Compile the `TCPServer.java` file:
  ```bash
   javac TCPServer.java
   ```
2. Run the server with an optional port number argument:
  ```bash
   java TCPServer [port]
   ```

If no port is specified, the server defaults to port `4444`.
The server will accept client connections, print the received data, and echo it back to the client.


