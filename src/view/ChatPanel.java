package view;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:22
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ChatPanel extends JPanel {
    private JButton sendButton;
    private JTextArea dialogArea;
    private JTextArea inputArea;
	private JLabel jLabel;
	private JLabel timeLabel;
	private int time;
	private Thread timer;
	private StringBuilder dialog;
    private String message;
	private GameProtocol gameProtocol;
	private Thread chat;
	
	public ChatPanel(GameProtocol gameProtocol, LoginPanel loginPanel) {
		super.setBounds(410, 80, 200, 300);
		
		this.dialog = new StringBuilder();
		
		this.gameProtocol = gameProtocol;
		
		this.dialogArea = new JTextArea();
		this.dialogArea.setEditable(false);
		this.dialogArea.setLineWrap(true);
        this.dialogArea.setWrapStyleWord(true);
		
		JScrollPane dialogScrollPane = new JScrollPane(this.dialogArea);
		dialogScrollPane.setBounds(2, 2, 196, 210);
		dialogScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		dialogScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.inputArea = new JTextArea();
		this.inputArea.setLineWrap(true);
        this.inputArea.setWrapStyleWord(true);

		JScrollPane inputScrollPane = new JScrollPane(this.inputArea);
		inputScrollPane.setBounds(2, 215, 196, 50);
		dialogScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		dialogScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		this.sendButton = new JButton("Send");
		this.sendButton.setBounds(118, 270, 80, 25);
		this.sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					message = inputArea.getText();
					if (message.length() > 0) {
						System.err.println("ChatPanel <send message>");
						gameProtocol.sendMessage("Rank#"+loginPanel.getUser().getRating() + " " + loginPanel.getUser().getNickname() + ": " + message);
						inputArea.setText("");
						dialog.append("Rank#"+loginPanel.getUser().getRating() + " " + loginPanel.getUser().getNickname() + ": " + message + "\n");
						dialogArea.setText(dialog.toString());
					}
				} catch (IOException err) {
					err.printStackTrace();
				}
			}
		});

		new Font(null, Font.BOLD, 20);

        super.setLayout(null);
		super.add(dialogScrollPane);
		super.add(inputScrollPane);
		//super.add(this.timeLabel);
		super.add(this.sendButton);
	}
	
	public void chatStart() {
		chat = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					try { Thread.sleep(100); } catch (InterruptedException e) {}
					String msg = gameProtocol.getMsg();
					if (msg != null) {
						System.err.println("ChatPanel <get message>");
						dialog.append(msg + "\n");
						dialogArea.setText(dialog.toString());
					}
				}
			}
		});
		chat.start();
	}
	
	public void chatStop() {
		chat.interrupt();
//		timer.interrupt();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}