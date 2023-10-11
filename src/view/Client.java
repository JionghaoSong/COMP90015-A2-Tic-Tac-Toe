package view;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:22
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client {
	private boolean isConnected;
	private Socket clientSocket;
	private String ip;
	private int port;

	public Client() throws IOException {
		isConnected = false;
		ip = Constants.DEFAULT_IP;
		port = Constants.PORT;

		for (;;) {
			try {
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(2, 2));

				JTextField ipField = new JTextField(ip);
				JTextField portField = new JTextField(Integer.toString(port));

				panel.add(new JLabel("Enter server IP:"));
				panel.add(ipField);
				panel.add(new JLabel("Enter server port:"));
				panel.add(portField);

				int result = JOptionPane.showConfirmDialog(null, panel, "Connect to Server",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (result == JOptionPane.OK_OPTION) {
					ip = ipField.getText();
					port = Integer.parseInt(portField.getText());

					System.err.println("Client <try to connect to server>");
					InetAddress inetAddress = InetAddress.getByName(ip);
					if (inetAddress.isReachable(1000)) {
						clientSocket = new Socket(inetAddress, port);
						if (clientSocket != null) {
							System.err.println("Client <connected>");
							isConnected = true;
							break;
						}
					}
				} else {
					System.exit(1);
				}
			} catch (ConnectException e) {
				System.err.println("Client <not connected>");
				int parameter = JOptionPane.showConfirmDialog(null,
						"Connection error. Do you want to try again?",
						"",
						JOptionPane.YES_NO_OPTION);
				if (parameter == JOptionPane.NO_OPTION) {
					System.exit(1);
				}
			}
		}
	}

	public Socket getSocket() {
		return clientSocket;
	}

//	public boolean isConnected() {
//		return isConnected;
//	}
//	public String getIp() {
//		return ip;
//	}
//
//	public int getPort() {
//		return port;
//	}
}