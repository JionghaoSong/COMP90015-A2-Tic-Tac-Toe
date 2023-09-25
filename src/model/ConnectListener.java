package model;

import java.net.*;
import java.io.*;

public class ConnectListener extends Thread {
	private boolean serverIsWork; // Flag to indicate if the server is running or not
	private ServerSocket serverSocket; // Socket to listen for incoming connections
	private Socket newClientSocket; // Socket to handle a new client connection
	private UserList userList; // List of users
	private GameList gameList; // List of games

	public ConnectListener(ServerSocket serverSocket, UserList userList, GameList gameList) {
		this.userList = userList;
		this.gameList = gameList;
		this.serverIsWork = true;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		try {
			while (serverIsWork) {
				//try { Thread.sleep(100); } catch (InterruptedException e) {}
				System.err.println("ConnectListener <waiting...>");
				this.newClientSocket = serverSocket.accept(); // Accept incoming client connection
				this.startUser(newClientSocket); // Start a new thread to handle the client connection
				System.err.println("ConnectListener <client connected>");
			}
		} catch (IOException e) {
			System.err.println("Server <IOException>");
			e.printStackTrace();
		}
	}

	public void startUser(Socket clientSocket) throws IOException {
		LoginProtocol loginProtocol = new LoginProtocol(clientSocket, this.userList, this.gameList);
		loginProtocol.start(); // Start a new thread to handle the login protocol for the client
	}

	public void stopWork() throws IOException {
		serverIsWork = false; // Stop the server
		serverSocket.close(); // Close the server socket
		//this.interrupt();
	}
}