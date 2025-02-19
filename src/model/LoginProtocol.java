package model;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:20
 */

import java.io.*;
import java.net.*;

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
					this.out.writeUTF(this.user.getNickname());
					this.out.writeInt(this.user.getRating());
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
				this.out.writeUTF(this.user.getNickname());
				this.out.writeInt(this.user.getRating());
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