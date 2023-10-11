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

public class TopPanel extends JPanel {
	private JLabel jLabel;
	private GameProtocol gameProtocol;

	public TopPanel(GameProtocol gameProtocol, LoginPanel loginPanel, String figure) {
		super.setBounds(200, 1, 300, 40);
		this.gameProtocol = gameProtocol;
		this.jLabel = new JLabel("Rank#" + loginPanel.getUser().getRating() + " " + loginPanel.getUser().getNickname() + "'s turn (" + (figure.contains("circle") ? "O":"X") + ")");
		this.jLabel.setBounds(10, 1, 300, 40);
		Font font = new Font(null, Font.BOLD, 18);
		this.jLabel.setFont(font);
		super.setLayout(null);
		super.add(this.jLabel);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public JLabel getjLabel() {
		return jLabel;
	}

	public void setjLabel(JLabel jLabel) {
		this.jLabel = jLabel;
	}
}