package view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChatTitlePanel extends JPanel {
	private JLabel jLabel;

	public ChatTitlePanel(GameProtocol gameProtocol) {
		super.setBounds(440, 20, 300, 50);
		this.jLabel = new JLabel("Player Chat");
		this.jLabel.setBounds(10, 20, 300, 40);
		Font font = new Font(null, Font.BOLD, 16);
		this.jLabel.setFont(font);
		super.setLayout(null);
		super.add(this.jLabel);
		if(gameProtocol != null){
			try {
				gameProtocol.sendStartTime();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}