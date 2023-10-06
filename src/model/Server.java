package model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


/*这段代码是一个简单的服务器实现，具有以下功能：

		启动服务器：start(int port)方法用于启动服务器。它创建一个ServerSocket实例并监听指定的端口。
					然后创建一个ConnectListener线程，并将ServerSocket、UserList和GameList作为参数传递给它。最后，启动ConnectListener线程并显示服务器的窗口界面。

		停止服务器：stop()方法用于停止服务器。它调用ConnectListener的stopWork()方法，以停止ConnectListener线程的工作。

		加载用户列表：loadUserList(String filename)方法用于从文件中加载用户列表。
					它创建一个UserList实例，并调用UserList的load()方法来加载指定文件中的用户数据。

		获取用户列表：getUserList()方法返回当前服务器中的用户列表。

		主函数：main(String[] args)是程序的入口点。它首先将错误输出重定向到日志文件，并创建一个Server实例。
				然后加载用户列表文件并启动服务器。最后，当服务器停止时，程序退出。*/

public class Server {
	private ServerSocket serverSocket;
	private ConnectListener connectListener;
	private UserList userList;
	private GameList gameList;
	private JFrame frame;
	private JButton stopServerButton;
	
	public Server() {
		this.gameList = new GameList();
		this.frame = new JFrame("Server");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setBounds(500, 200, 230, 100);
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
				} catch (IOException err) { err.printStackTrace(); }
			}
		});
		
		this.frame.getContentPane().add(this.stopServerButton);
	}
	
	public boolean loadUserList(String filename) throws FileNotFoundException {
		this.userList = new UserList();
		boolean ref = this.userList.load(filename);
		return ref;
	}
	
	public void start(int port) throws IOException, InterruptedException {
		this.serverSocket = new ServerSocket(port);
		this.connectListener = new ConnectListener(serverSocket, userList, gameList);
		System.err.println("Server <start connect listener>");
		this.connectListener.start();
		this.frame.setVisible(true);
		this.connectListener.join();
	}
	
	public void stop() throws IOException {
		this.connectListener.stopWork();
	}
	
	public UserList getUserList() {
		return this.userList;
	}
	
	public static void main(String[] args) {
		try {
			System.setErr(new PrintStream(new File(Constants.LOG_FILE_DIR)));
			System.err.println("Server <start>");
			Server server = new Server();
			System.err.println("Server <load user list from: " + Constants.USER_LIST_DIR + ">");
			server.loadUserList(Constants.USER_LIST_DIR);
			server.start(Constants.PORT);
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