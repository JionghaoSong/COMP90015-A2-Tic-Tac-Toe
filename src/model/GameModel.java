package model;

import java.io.*;
import java.net.*;
import java.util.*;

/*这段代码是一个Java类GameModel，它表示一个游戏模型。
以下是该代码的主要功能：

		定义了游戏模型的属性，如游戏ID、创建者、连接者、是否轮到创建者行动、是否轮到连接者行动等。
		创建了与创建者和连接者之间的套接字连接，以便进行通信。
		使用二维数组来表示游戏场地。
		包含了等待对手加入游戏的逻辑，创建了一个线程来等待对手连接。
		包含了计算玩家等级的逻辑，根据游戏结果更新玩家的等级。
		提供了获取创建者套接字、游戏模型、连接者套接字和游戏ID的方法。
		实现了equals方法，用于比较两个游戏模型是否相等。
		提供了连接到游戏的方法，用于将连接者添加到游戏模型中。
		重写了toString方法，用于返回游戏模型的字符串表示形式。*/

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
	protected boolean getWaitingResult() throws InterruptedException {
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
							ratingRecount();
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

	protected void ratingRecount() {
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
		}

		// Ensure ratings are not negative
		newCreatorRating = Math.max(0, newCreatorRating);
		newConnectorRating = Math.max(0, newConnectorRating);

		this.creator.setRating(newCreatorRating);
		this.connector.setRating(newConnectorRating);
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