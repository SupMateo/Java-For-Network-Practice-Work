package fr.ensea.rts.kardellas.suprano.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import fr.ensea.rts.kardellas.suprano.client.Client;
import fr.ensea.rts.kardellas.suprano.client.TCPClient;
import fr.ensea.rts.kardellas.suprano.client.UDPClient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class MainClient extends Application {
    private TextArea console;
    private TextField commandInput;
    private TextField textBoxIP;
    private TextField textBoxPort;
    private ComboBox<String> dropdownProtocol;

    private Client currentClient;

    @Override
    public void start(Stage stage) {
        // Redirect System.out to TextArea
        redirectSystemOutputToTextArea();

        textBoxIP = new TextField();
        textBoxIP.setPromptText("IP");
        textBoxIP.setLayoutX(10);
        textBoxIP.setLayoutY(10);

        console = new TextArea();
        console.setEditable(false);
        console.setLayoutX(10);
        console.setLayoutY(50);
        console.setPrefWidth(465);
        console.setPrefHeight(150);

        textBoxPort = new TextField();
        textBoxPort.setPromptText("Port");
        textBoxPort.setLayoutX(165);
        textBoxPort.setLayoutY(10);

        dropdownProtocol = new ComboBox<>();
        dropdownProtocol.getItems().addAll("TCP", "UDP");
        dropdownProtocol.setPromptText("Protocol");
        dropdownProtocol.setLayoutX(320);
        dropdownProtocol.setLayoutY(10);

        Button connectButton = new Button("Connect");
        connectButton.setLayoutX(415);
        connectButton.setLayoutY(10);
        connectButton.setOnAction(e -> connectToServer());

        Button disconnectButton = new Button("Disconnect");
        disconnectButton.setLayoutX(415);
        disconnectButton.setLayoutY(40);
        disconnectButton.setOnAction(e -> disconnectFromServer());

        commandInput = new TextField();
        commandInput.setPromptText("Enter command");
        commandInput.setLayoutX(10);
        commandInput.setLayoutY(210);
        commandInput.setPrefWidth(465);
        commandInput.setOnAction(e -> sendCommand());

        Pane root = new Pane();
        root.setStyle("-fx-background-color: #6f6f6f;");
        root.getChildren().addAll(textBoxIP, textBoxPort, dropdownProtocol,
                connectButton, disconnectButton,
                console, commandInput);

        Scene scene = new Scene(root, 485, 245);
        scene.setFill(javafx.scene.paint.Color.DARKGRAY);
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();
    }

    private void redirectSystemOutputToTextArea() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                Platform.runLater(() -> {
                    console.appendText(String.valueOf((char) b));
                });
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    private void connectToServer() {
        String ip = textBoxIP.getText();
        String portStr = textBoxPort.getText();
        String protocol = dropdownProtocol.getValue();

        if (ip.isEmpty() || portStr.isEmpty() || protocol == null) {
            console.appendText("Please fill in all connection details.\n");
            return;
        }

        try {
            int port = Integer.parseInt(portStr);

            if (currentClient != null && currentClient.isRunning()) {
                currentClient.stop();
            }

            if ("TCP".equals(protocol)) {
                currentClient = new TCPClient(ip, port);
            } else if ("UDP".equals(protocol)) {
                currentClient = new UDPClient(ip, port);
            }

            new Thread(() -> {
                try {
                    Platform.runLater(() -> console.appendText("Connected to server.\n"));
                    currentClient.start(true);
                } catch (Exception e) {
                    Platform.runLater(() -> console.appendText("Connection error: " + e.getMessage() + "\n"));
                }
            }).start();
        } catch (NumberFormatException e) {
            console.appendText("Invalid port number: " + portStr + "\n");
        }
    }

    private void disconnectFromServer() {
        if (currentClient != null && currentClient.isRunning()) {
            currentClient = null;
            console.appendText("Disconnected from server.\n");
            currentClient.stop();
        } else {
            console.appendText("No active connection.\n");
        }
    }

    private void sendCommand() {
        String command = commandInput.getText();
        if (currentClient != null && currentClient.isRunning()) {
            try {
                currentClient.sendMessage(command);
                console.appendText("message envoyé");
                commandInput.clear();
            } catch (Exception e) {
                console.appendText("Failed to send message: " + e.getMessage() + "\n");
            }
        } else {
            console.appendText("Not connected. Please connect first.\n");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}