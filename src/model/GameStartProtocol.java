package model;

import java.io.*;
import java.net.*;

/*		建立游戏协议：该类用于处理与游戏相关的协议通信。
		处理请求：getRequest()方法用于接收和处理客户端发送的请求。根据接收到的请求类型，调用相应的方法进行处理。
		开始新游戏：newGame()方法用于开始一个新游戏。它创建一个GameModel对象，并将其添加到游戏列表中。然后向客户端发送一个布尔值表示游戏是否成功开始。
		连接到游戏：connectToGame()方法用于连接到现有游戏。它向客户端发送当前可用的游戏列表，并接收客户端发送的选择。如果选择有效，则将用户连接到选定的游戏，并从游戏列表中移除该游戏。
		注销：signOut()方法用于注销用户，将用户设置为离线状态。
		获取评级：getRating()方法用于获取用户评级。它从用户列表中获取评级列表，并将其发送给客户端。*/

public class GameStartProtocol {
	private DataInputStream in;
	private DataOutputStream out;
	private GameList gameList;
	private UserList userList;
	private User user;
	private Socket socket;

	public GameStartProtocol(Socket socket, GameList gameList, User user, UserList userList) throws IOException {
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
		this.gameList = gameList;
		this.userList = userList;
		this.user = user;
		this.socket = socket;
	}

	// Receive and process requests
	protected void getRequest() throws IOException, InterruptedException {
		for (;;) {
			System.err.println("GameStartProtocol <" + this.user.getNickname() + " get request>");
			System.out.print("getRequest = ");
			String mode = this.in.readUTF();
			System.out.println(mode);
			if ("new game".equals(mode)) {
				this.newGame(); // Start a new game
			} else if ("connect to game".equals(mode)) {
				this.connectToGame(); // Connect to an existing game
			} else if ("sign out".equals(mode)) {
				this.signOut(); // Sign out from the game
				break;
			} else if ("get rating".equals(mode)) {
				this.getRating(); // Get the user rating
			} else {
				System.out.println(mode);
			}
		}
	}

	// Start a new game
	protected void newGame() throws IOException, InterruptedException {
		System.err.println("GameStartProtocol <new game>");
		GameModel gameModel = new GameModel(this.user, this.socket);
		this.gameList.add(gameModel);
		this.out.writeBoolean(true);
		boolean isStarted = gameModel.getWaitingResult();
		System.out.println("isStarted = " + isStarted);
		if (!isStarted) {
			gameList.remove(gameModel);
			gameModel = null;
		}
	}

	// Connect to an existing game
	protected void connectToGame() throws IOException {
		System.err.println("GameStartProtocol <connect to game>");
		out.writeInt(this.gameList.getCount());
		for (int i = 0; i < this.gameList.getCount(); i++) {
			this.out.writeUTF(gameList.toString(i));
		}
		boolean isChoice = in.readBoolean();
		if (isChoice) {
			int id = this.in.readInt();
			System.err.println("GameStartProtocol <game id: " + id + ">");
			GameModel gameModel2 = gameList.getGameModel(id);
			if (gameModel2 != null) {
				System.err.println("GameStartProtocol <game started>");
				this.out.writeBoolean(true);
				gameModel2.connectToGame(this.user, this.socket);
				this.gameList.remove(gameModel2);
			} else {
				System.err.println("GameStartProtocol <impossible to connect>");
				this.out.writeBoolean(false);
			}
		}
	}

	// Sign out from the game
	protected void signOut() throws IOException {
		System.err.println("GameStartProtocol <sign out>");
		this.user.setOnline(false);
	}

	// Get the user rating
	protected void getRating() throws IOException {
		System.err.println("GameStartProtocol <get rating>");
		String ratingList = this.userList.getRatingList();
		this.out.writeUTF(ratingList);
	}
}