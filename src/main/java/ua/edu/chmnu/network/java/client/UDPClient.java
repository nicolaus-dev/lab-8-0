package ua.edu.chmnu.network.java.client;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9555;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your message: ");
            String message = scanner.nextLine();

            byte[] sendBuffer = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            System.out.println("Message sent to server.");

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            clientSocket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            System.err.println("Error in client: " + e.getMessage());
        }
    }
}