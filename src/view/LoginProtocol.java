package view;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:22
 */


import model.User;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class LoginProtocol {
	private DataInputStream in;
	private DataOutputStream out;
	
	public LoginProtocol(Socket socket) throws IOException {
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
	}
	
	public User send(String mode, String nickname, String password) throws IOException {
		out.writeUTF(mode);
		out.writeUTF(nickname);
		out.writeUTF(password);
		return getAnswer();
	}
	
	private User getAnswer() throws IOException {
		boolean answer = in.readBoolean();
		if (answer == false) {
			System.err.println("LoginProtocol <error to login>");
			String cause = in.readUTF();
			JOptionPane.showMessageDialog(null, cause, "WARNING", JOptionPane.WARNING_MESSAGE);
			return new User("", 0, answer);
		}else {
			String nickname = in.readUTF();
			int rating = in.readInt();
			return new User(nickname, rating, answer);
		}
	}
}