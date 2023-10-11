package view;

import javax.swing.*;
import java.awt.*;

public class CenterPanel extends JPanel {
	private JLabel jLabel;

	public CenterPanel(LoginPanel loginPanel, boolean win, boolean tie) {
		super.setBounds(200, 18, 300, 50);
		if(tie){
			String content = "tie turn";
			this.jLabel = new JLabel(content);
		}else {
			String content = "you " + (win ? "win," : "lose,") + "opponent " + (!win ? "win!" : "lose!");
			this.jLabel = new JLabel(content);
		}

		this.jLabel.setBounds(10, 18, 300, 40);
		Font font = new Font(null, Font.BOLD, 20);
		this.jLabel.setFont(font);
		this.jLabel.setForeground(Color.RED);
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