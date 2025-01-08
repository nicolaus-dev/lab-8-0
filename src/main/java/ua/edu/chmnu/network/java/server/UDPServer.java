package ua.edu.chmnu.network.java.server;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class UDPServer {
    private static final int PORT = 9555;
    private static final String CLIENTS_FILE = "clients.txt";

    private Set<String> allowedClients;

    public UDPServer() {
        allowedClients = loadAllowedClients();
    }

    public void start() {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is running on port " + PORT + "...");

            while (true) {

                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                String clientIP = clientAddress.getHostAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Received: \"" + message + "\" from " + clientAddress);

                if (allowedClients.contains(clientIP)) {
                    System.out.println("Client " + clientIP + " is in the allowed list.");
                    sendToAllClients(serverSocket, message);
                } else {
                    System.out.println("Client " + clientIP + " is not in the allowed list.");
                    String response = "Access denied.";
                    byte[] sendBuffer = response.getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                    serverSocket.send(sendPacket);
                }
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        }
    }

    private void sendToAllClients(DatagramSocket serverSocket, String message) {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            String serverIP = serverAddress.getHostAddress();

            for (String clientIP : allowedClients) {

                if (clientIP.equals(serverIP)) {
                    continue;
                }

                try {
                    InetAddress clientAddress = InetAddress.getByName(clientIP);
                    String response = "Broadcast: " + message;
                    byte[] sendBuffer = response.getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, PORT);
                    serverSocket.send(sendPacket);

                    System.out.println("Message sent to " + clientIP);
                } catch (IOException e) {
                    System.err.println("Error sending to " + clientIP + ": " + e.getMessage());
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Error getting server IP: " + e.getMessage());
        }
    }

    private Set<String> loadAllowedClients() {
        Set<String> clients = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CLIENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                clients.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Failed to load allowed clients: " + e.getMessage());
        }
        return clients;
    }

    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.start();
    }
}