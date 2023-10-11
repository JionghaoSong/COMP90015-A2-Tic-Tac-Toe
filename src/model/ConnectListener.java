package model;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:20
 */

import java.net.*;
import java.io.*;

public class ConnectListener extends Thread {
	private boolean serverIsWork; // Flag to indicate if the server is running or not
	private ServerSocket serverSocket; // Socket to listen for incoming connections
	private Socket newClientSocket; // Socket to handle a new client connection
	private UserList userList; // List of users
	private GameList gameList; // List of games

//	It initializes the member variables with the provided parameters
	public ConnectListener(ServerSocket serverSocket, UserList userList, GameList gameList) {
		this.userList = userList;
		this.gameList = gameList;
		this.serverIsWork = true;
		this.serverSocket = serverSocket;
	}

//	The run() method is overridden from the Thread class.
//	It is the entry point for the thread and contains the logic for monitoring incoming client connections.
//	It runs in a loop until the serverIsWork flag is set to false.
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

//	The startUser() method is responsible for starting a new thread to handle a specific client connection.
	public void startUser(Socket clientSocket) throws IOException {
		LoginProtocol loginProtocol = new LoginProtocol(clientSocket, this.userList, this.gameList);
		loginProtocol.start(); // Start a new thread to handle the login protocol for the client
	}

//	The stopWork() method is used to stop the server and clean up resources.
	public void stopWork() throws IOException {
		serverIsWork = false; // Stop the server
		serverSocket.close(); // Close the server socket
		//this.interrupt();
	}
}