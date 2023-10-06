package model;

import java.io.*;
import java.net.*;

/*LoginProtocol类的功能如下：该类负责处理用户登录和注册的验证逻辑，并通过创建GameStartProtocol对象提供游戏协议的通信功能。

		用户登录验证：isCanLogin()方法用于验证用户登录。根据提供的昵称和密码，在用户列表中查找匹配的用户。如果找到匹配用户且该用户不在线，则将用户设置为在线状态，并返回登录成功的标志。
					否则，返回登录失败的标志和相应的错误消息。
		用户注册验证：isCanReg()方法用于验证用户注册。检查用户列表中是否已存在具有相同昵称的用户。如果存在相同昵称的用户，则返回注册失败的标志和相应的错误消息。
					否则，将用户添加到用户列表中，并将用户设置为在线状态，返回注册成功的标志。
		处理请求：run()方法作为线程的运行方法，用于接收和处理客户端发送的请求。根据接收到的请求类型（登录或注册），调用相应的验证方法进行处理。
				如果验证成功，则创建一个GameStartProtocol对象，与客户端进行游戏协议通信。*/


	public class LoginProtocol extends Thread {
		private DataInputStream in;
		private DataOutputStream out;
		private UserList userList;
		private GameList gameList;
		private Socket socket;
		private User user;
		private GameStartProtocol gameStartProtocol;

		public LoginProtocol(Socket socket, UserList userList, GameList gameList) throws IOException {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			this.user = null;
			this.userList = userList;
			this.gameList = gameList;
			this.socket = socket;
		}

		// Check if user can login with the provided nickname and password
		public boolean isCanLogin(String nickname, String password) throws IOException {
			this.user = this.userList.isContains(new User(nickname, password));
			if (this.user != null) {
				if (!this.user.isOnline()) {
					System.err.println("LoginProtocol <login: " + user.getNickname() + ">");
					this.out.writeBoolean(true);
					this.user.setOnline(true);
					return true;
					//this.userList.save(Constants.USER_LIST_DIR);
				} else {
					this.user = null;
					this.out.writeBoolean(false);
					this.out.writeUTF("User already logged in");
					return false;
				}
			} else {
				this.user = null;
				this.out.writeBoolean(false);
				this.out.writeUTF("User does not exist");
				return false;
			}
		}

		// Check if user can register with the provided nickname and password
		public boolean isCanReg(String nickname, String password) throws IOException {
			if (this.userList.isContainsNickname(nickname)) {
				this.out.writeBoolean(false);
				this.out.writeUTF("User with the same nickname already exists");
				this.user = null;
				return false;
			} else {
				System.err.println("LoginProtocol <registration: " + this.user.getNickname() + ">");
				this.userList.add(this.user);
				this.userList.save(Constants.USER_LIST_DIR);
				this.user.setOnline(true);
				this.out.writeBoolean(true);
				return true;
			}
		}

		@Override
		public void run() {
			try {
				for (;;) {
					// Wait for login or registration request
					System.err.println("LoginProtocol <get request>");
					String mode = this.in.readUTF();
					String nickname = this.in.readUTF();
					String password = this.in.readUTF();
					System.out.println(mode);
					this.user = new User(nickname, password);

					if ("login".equals(mode) && this.isCanLogin(nickname, password) ||
							"registration".equals(mode) && this.isCanReg(nickname, password)) {

						System.err.println("LoginProtocol <game start protocol>");
						this.gameStartProtocol = new GameStartProtocol(this.socket, this.gameList, this.user, this.userList);
						this.gameStartProtocol.getRequest();
					}
				}
			} catch (IOException e) {
				System.err.println("LoginProtocol <IOException>");
				e.printStackTrace();
				this.user.setOnline(false);
				in = null;
				out = null;
				socket = null;
			} catch (InterruptedException e) {
				System.err.println("LoginProtocol <InterruptedException>");
				this.user.setOnline(false);
				e.printStackTrace();
			}
		}
	}