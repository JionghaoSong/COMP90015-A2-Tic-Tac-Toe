package model;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:20
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class Server {
	private ServerSocket serverSocket;
	private ConnectListener connectListener;
	private UserList userList;
	private GameList gameList;
	private JFrame frame;
	private JButton stopServerButton;
	private String ip;
	private int port;

	public Server() {
		this.gameList = new GameList();
		this.frame = new JFrame("Server");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setBounds(500, 200, 230, 150);
		this.frame.setLayout(null);
		this.frame.setResizable(false);

		this.stopServerButton = new JButton("Stop server");
		this.stopServerButton.setBounds(30, 20, 130, 30);
		this.stopServerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stop();
					frame.setVisible(false);
					System.exit(1);
				} catch (IOException err) {
					err.printStackTrace();
				}
			}
		});

		this.frame.getContentPane().add(this.stopServerButton);
	}

	public boolean loadUserList(String filename) throws FileNotFoundException {
		this.userList = new UserList();
		boolean ref = this.userList.load(filename);
		return ref;
	}

	public void start(String ip, int port) throws IOException, InterruptedException {
		this.serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
		this.connectListener = new ConnectListener(serverSocket, userList, gameList);
		System.err.println("Server <start connect listener>");
		this.connectListener.start();
		this.frame.setVisible(true);
		this.connectListener.join();
	}

	public void stop() throws IOException {
		this.connectListener.stopWork();
	}

	public static void main(String[] args) {
		try {
			System.setErr(new PrintStream(new File(Constants.LOG_FILE_DIR)));
			System.err.println("Server <start>");
			Server server = new Server();
			System.err.println("Server <load user list from: " + Constants.USER_LIST_DIR + ">");
			server.loadUserList(Constants.USER_LIST_DIR);

			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(2, 2));

			JTextField ipField = new JTextField(Constants.DEFAULT_IP);
			JTextField portField = new JTextField(Integer.toString(Constants.PORT));

			panel.add(new JLabel("Enter server IP:"));
			panel.add(ipField);
			panel.add(new JLabel("Enter server port:"));
			panel.add(portField);

			int result = JOptionPane.showConfirmDialog(null, panel, "Server Configuration",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				String ip = ipField.getText();
				int port = Integer.parseInt(portField.getText());
				server.start(ip, port);
			} else {
				System.exit(1);
			}

			System.err.println("Server <stop>");
		} catch (IOException e) {
			System.err.println("Server <IOException>");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("Server <InterruptedException>");
			e.printStackTrace();
		}
	}
}