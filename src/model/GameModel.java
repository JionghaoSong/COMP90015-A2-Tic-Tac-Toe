package model;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:20
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class GameModel extends Thread {
	private static int id = 1;
	private int gameID;
	private User creator;
	private User connector;
	private boolean isCreatorMove;
	private boolean isConnectorMove;
	private Socket creatorSocket;
	private Socket connectorSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private int[][] field;
	private boolean isInterruptWaiting;
	private Thread opponentWaiting;
	private boolean isEnded;
	private GameProtocol gameProtocol;

	public GameModel(User creator, Socket creatorSocket) throws IOException, InterruptedException {
		this.gameID = id++;
		this.field = new int[Constants.FIELD_WIDTH][Constants.FIELD_HEIGHT];
		this.isEnded = false;

		this.isCreatorMove = false;
		this.isConnectorMove = false;

		if (creator != null && creatorSocket != null) {
			this.creator = creator;
			this.creatorSocket = creatorSocket;
			this.in = new DataInputStream(creatorSocket.getInputStream());
			this.out = new DataOutputStream(creatorSocket.getOutputStream());
		}
	}

	/**
	 * Waits for the result of opponent waiting.
	 * @return true if the opponent is connected, false if the waiting is interrupted or cancelled.
	 * @throws InterruptedException if the thread is interrupted while waiting.
	 */
	protected boolean getWaitingResult(UserList userList) throws InterruptedException {
		this.isInterruptWaiting = false;

		Thread interruptWaiting = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Read the boolean value from input stream to determine if waiting is interrupted
					isInterruptWaiting = in.readBoolean();
				} catch (IOException e) {
					// Close the waiting window and set creator's online status to false
					creator.setOnline(false);
					isInterruptWaiting = true;
				}
			}
		});

		opponentWaiting = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.err.println("GameModel <opponent waiting>");
					for (;;) {
						Thread.sleep(100);
						if (canStart()) {
							System.err.println("GameModel <opponent connected>");
							// Send a boolean value indicating that the opponent is connected
							out.writeBoolean(true);
							gameProtocol = new GameProtocol(creator, creatorSocket, connector, connectorSocket);
							ratingRecount(creator, connector, userList);
							System.err.println("GameModel <end>");
							if (!creator.isOnline() || !connector.isOnline()) {
								isInterruptWaiting = true;
							}
							break;
						}
						if (isInterruptWaiting) {
							/* Confirm the cancellation */
							System.err.println("GameModel <stop opponent waiting>");
							// Send a boolean value indicating that the waiting is cancelled
							out.writeBoolean(false);
							break;
						}
					}
					isEnded = true;
				} catch (IOException e) {
					System.err.println("GameModel <IOException opponent waiting>");
					e.printStackTrace();
					isInterruptWaiting = true;
					isEnded = true;
				} catch (InterruptedException e) {
					System.err.println("GameModel <InterruptedException opponent waiting>");
					e.printStackTrace();
					isInterruptWaiting = true;
					isEnded = true;
				}
			}
		});

		interruptWaiting.start();
		opponentWaiting.start();
		opponentWaiting.join();
		return !this.isInterruptWaiting;
	}

	protected void ratingRecount(User creator, User connector, UserList userList) {
		// Recalculate ratings based on game outcome
		int newCreatorRating = 0;
		int newConnectorRating = 0;
		if (gameProtocol.isCreatorWin()) {
			// Creator wins
			newCreatorRating = this.creator.getRating() + 5;
			newConnectorRating = this.connector.getRating() - 5;
		} else if (gameProtocol.isConnectorWin()) {
			// Connector wins
			newCreatorRating = this.creator.getRating() - 5;
			newConnectorRating = this.connector.getRating() + 5;
		} else if (gameProtocol.isTie()) {
			// Tie game
			newCreatorRating = this.creator.getRating() + 2;
			newConnectorRating = this.connector.getRating() + 2;
		}else if (!creator.isOnline()) {
			// creator disconnect
			newCreatorRating = this.creator.getRating() - 5;
			newConnectorRating = this.connector.getRating() + 5;
		}else if (!connector.isOnline()) {
			// connector disconnect
			newCreatorRating = this.creator.getRating() + 5;
			newConnectorRating = this.connector.getRating() - 5;
		}

		// Ensure ratings are not negative
		newCreatorRating = Math.max(0, newCreatorRating);
		newConnectorRating = Math.max(0, newConnectorRating);

		this.creator.setRating(newCreatorRating);
		this.connector.setRating(newConnectorRating);
		try {
			User user = userList.find(creator.getNickname());
			User user1 = userList.find(connector.getNickname());
			userList.remove(user);
			userList.remove(user1);
			userList.add(creator);
			userList.add(connector);
			userList.save(Constants.USER_LIST_DIR);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Get the socket of the creator.
	 * @return the socket of the creator.
	 */
	public Socket getCreatorSocket() {
		return this.creatorSocket;
	}

	/**
	 * Get the current game model.
	 * @return the game model.
	 */
	public GameModel getGameModel() {
		return this;
	}

	/**
	 * Check if the game can start.
	 * @return true if the connector is not null and waiting is not interrupted, false otherwise.
	 */
	public boolean canStart() {
		if (this.connector != null && !this.isInterruptWaiting) {
			return true;
		}
		return false;
	}

	/**
	 * Get the socket of the connector.
	 * @return the socket of the connector.
	 */
	public Socket getConnectorSocket() {
		return this.connectorSocket;
	}

	/**
	 * Get the game ID.
	 * @return the game ID.
	 */
	public int getGameID() {
		return this.gameID;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GameModel) {
			if (((GameModel) o).gameID == this.gameID) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Connect to the game as a connector.
	 * @param connector the connector user.
	 * @param connectorSocket the socket of the connector.
	 */
	public void connectToGame(User connector, Socket connectorSocket) {
		if (connector != null && connectorSocket != null) {
			this.connectorSocket = connectorSocket;
			this.connector = connector;
			for (;;) {
				try { Thread.sleep(100); } catch (InterruptedException e) {}
				if (this.isEnded) {
					break;
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.gameID + ": ");
		sb.append(this.creator.getNickname());
		sb.append(" vs ");
		if (this.connector == null) {
			sb.append("<free>");
		} else {
			sb.append(this.connector.getNickname());
		}
		return sb.toString();
	}
}