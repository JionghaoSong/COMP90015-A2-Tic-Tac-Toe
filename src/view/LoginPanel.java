package view;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:22
 */


import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.net.*;

public class LoginPanel extends JPanel {
	private String nickname;
	private int rating;
	private String password;
	private JLabel messageLabel;
	private boolean isLogin;
	private boolean isRegistration;
	private Image image;
	private LoginProtocol loginProtocol;
	private User user;
	
	public LoginPanel(Socket socket) throws IOException {
		super.setBounds(2, 2, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
		super.setLayout(null);
		
		nickname = null;
		password = null;
		isLogin = false;
		isRegistration = false;
		loginProtocol = new LoginProtocol(socket);
		image = ImageIO.read(new File(Constants.BACKGROUND_DIR));
		
		Font font = new Font(null, Font.BOLD, 30);
		Font labelFont = new Font(null, Font.PLAIN, 20);
		
		messageLabel = new JLabel();
		messageLabel.setFont(font);
		messageLabel.setText("Ultimate Tic Tac Toe Experience");
		messageLabel.setForeground(new Color(0, 153, 153));
		messageLabel.setBounds(35, 25, 500, 50);
		
		JLabel nicknameLabel = new JLabel("Username:");
		nicknameLabel.setBounds(130, 90, 120, 20);
		nicknameLabel.setFont(labelFont);

		JTextField nicknameTextField = new JTextField();
		nicknameTextField.setBounds(250, 90, 130, 20);

//		JLabel nicknameNote = new JLabel("(3-8 letters)");
//		nicknameNote.setForeground(new Color(153, 153, 255));
//		nicknameNote.setBounds(260, 90, 220, 20);
		
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(130, 125, 120, 20);
		passwordLabel.setFont(labelFont);
		
		JPasswordField passwordTextField = new JPasswordField();
		passwordTextField.setBounds(250, 125, 130, 20);
//		passwordTextField.setText("3-8 letters");
		
//		JLabel passwordNote = new JLabel("(3-8 letters)");
//		passwordNote.setForeground(new Color(105, 153, 255));
//		passwordNote.setBounds(260, 125, 220, 20);
		
		JButton loginButton = new JButton("Sign in");
		loginButton.setBounds(270, 165, 110, 25);

		JButton registrationButton = new JButton("Sign up");
		registrationButton.setBounds(130, 165, 110, 25);
		registrationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.err.println("LoginPanel <registration button pressed>");
					nickname = nicknameTextField.getText();
					nicknameTextField.setText("");
					password = passwordTextField.getText();
					passwordTextField.setText("");
					if (!inputIsCorrect(nickname, password)) {
						System.err.println("LoginPanel <incorrect input>");
						JOptionPane.showMessageDialog(null, "Input 3-8 letters", "WARNING", JOptionPane.WARNING_MESSAGE);
					} else {
						System.err.println("LoginPanel <request to server>");
						 user = loginProtocol.send("registration", nickname, password);
						isRegistration = user.isAnswer();
						if (!isRegistration) {
							System.err.println("LoginPanel <imposible registration>");
							//JOptionPane.showMessageDialog(null, "User with a same nickname already exists", "WARNING", JOptionPane.WARNING_MESSAGE);
						} else {
							System.err.println("LoginPanel <successful registration>");
						}
					}
				} catch (IOException err) {
					System.err.println("LoginPanel <regButton.IOException>");
					err.printStackTrace();
				}
			}
		});

		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.err.println("LoginPanel <login button pressed>");
					nickname = nicknameTextField.getText();
					//System.out.println(nickname);
					nicknameTextField.setText("");
					password = passwordTextField.getText();
					//System.out.println(password);
					passwordTextField.setText("");
					if (!inputIsCorrect(nickname, password)) {
						System.err.println("LoginPanel <incorrect input>");
						JOptionPane.showMessageDialog(null, "Login or password incorrect", "WARNING", JOptionPane.WARNING_MESSAGE);
					} else {
						user = loginProtocol.send("login", nickname, password);
						isLogin = user.isAnswer();
						System.err.println("LoginPanel <request to server = " + isLogin + ">");
					}
				} catch (IOException err) {
					System.err.println("LoginPanel <logButton.IOException>");
					err.printStackTrace();
				}
			}
		});
		
		super.add(messageLabel);
		super.add(nicknameLabel);
		super.add(nicknameTextField);
//		super.add(nicknameNote);
		super.add(passwordLabel);
		super.add(passwordTextField);
//		super.add(passwordNote);
		super.add(loginButton);
		super.add(registrationButton);
	}
	
	public boolean inputIsCorrect(String nickname, String password) {
		if (nickname.length() <= 2 || nickname.length() > 8 || password.length() <= 2 || password.length() > 8) {
			return false;
		}
		if (nickname.contains(" ")) {
			return false;
		}
		return true;
	}
	
	public String[] getLoginParametres() {
		String[] parametres = new String[3];
		for(;;) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			if (isLogin || isRegistration) {
				parametres[0] = nickname;
				parametres[1] = password;
				if (isLogin) {
					parametres[2] = "sign in";
				}
				if (isRegistration) {
					parametres[2] = "registration";
				}
				isLogin = false;
				isRegistration = false;
				return parametres;
			}
		}
	}
	
	@Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(Constants.BACKGROUND_IMAGE, 0, 0, this);
    }


	public User getUser() {
		return user;
	}
}