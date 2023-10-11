package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LeftPanel extends JPanel {
    private JButton sendButton;
	private JLabel timeLabel;
	private int time;
	private Thread timer;
	private GameProtocol gameProtocol;

	public LeftPanel(GameProtocol gameProtocol, String choice, int menuParametr) {
		super.setBounds(1, 1, 100, 360);
		this.gameProtocol = gameProtocol;
		this.sendButton = new JButton("QUIT");
		this.sendButton.setBounds(10, 300, 90, 40);
		this.sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("QUIT");
				if(gameProtocol.isTimeEnd()){
					JOptionPane.showMessageDialog(null, "The game is over!", "", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				gameProtocol.setQuit(true);
				try {
					gameProtocol.sendCoordinate(-100, -100);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		});
		
		Font font = new Font(null, Font.BOLD, 20);
		this.time = 20;
		this.timeLabel = new JLabel();
		this.timeLabel.setBounds(10, 10, 80, 60);
		this.timeLabel.setFont(font);
		this.timeLabel.setText("20");
		this.timer = new Thread(new Runnable() {
			int i = 0;
			@Override
			public void run() {
				updateUI();
				try {
					gameProtocol.sendChoice(menuParametr, choice);
				} catch (IOException e) {
					System.out.println(e);
				}
				if (menuParametr == Constants.CONNECT_TO_GAME){
					try {
						gameProtocol.sendTime(time);
					} catch (IOException e) {
						System.out.println(e);
					}
				}else {
					for (;;){
						try { Thread.sleep(100); } catch (InterruptedException e) {}
						if(gameProtocol.getTime() != -1){
							break;
						}
					}
					time = gameProtocol.getTime();
					gameProtocol.setTime(-1);
				}
				for (i = 0; i < 21; i++) {
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
					if(gameProtocol.isTimeEnd()){
						break;
					}
					if(gameProtocol.getTime() != -1){
						time = gameProtocol.getTime();
						gameProtocol.setTime(-1);
						i=0;
					}
					if(time < 10){
						timeLabel.setText("0" + time--);
					}else {
						timeLabel.setText("" + time--);
					}
					if(time <= 0){
						time = 20;
						i = 0 ;
					}
				}
			}
		});
		this.timer.start();
        super.setLayout(null);
		super.add(this.timeLabel);
		super.add(this.sendButton);
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}