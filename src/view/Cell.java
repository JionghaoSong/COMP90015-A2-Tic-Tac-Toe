package view;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:22
 */

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Cell extends JPanel {
	private final int X;
	private final int Y;
	private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
	private final Color BORDER_COLOR = new Color(168, 227, 6);
	private final int BORDER_WIDTH = 1;
	private final int WIN_BORDER_WIDTH = 3;
	private Image image;
	private GameProtocol gameProtocol;
	private String figure;
	private String opponentFigure;

	public String getOpponentFigure() {
		return opponentFigure;
	}

	public void setOpponentFigure(String opponentFigure) {
		this.opponentFigure = opponentFigure;
	}

	public Cell(int x, int y, GameProtocol gameProtocol, String figure) {
		this.X = x;
		this.Y = y;
		this.figure = figure;
		if (this.figure == Constants.CIRCLE_DIR) {
			this.opponentFigure = Constants.CROSS_DIR;
		} else {
			this.opponentFigure = Constants.CIRCLE_DIR;
		}
		this.gameProtocol = gameProtocol;
		super.setBackground(this.BACKGROUND_COLOR);
		super.setBorder(BorderFactory.createLineBorder(this.BORDER_COLOR, this.BORDER_WIDTH));
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					// Send the coordinates of the clicked cell to the game protocol
					gameProtocol.sendCoordinate(x, y);
					try { Thread.sleep(500); } catch (InterruptedException err) {}
					if (gameProtocol.isCanPut()) {
						System.err.println("Cell <put move (" + X + ", " + Y + ")>");
						// Load the current player's image if it's allowed to place a move
						image = ImageIO.read(new File(figure));
					}
					repaint();
				} catch (IOException err) {
					err.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// Change the border color when the mouse enters the cell
				setBorder(BorderFactory.createLineBorder(Color.ORANGE, BORDER_WIDTH));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// Restore the original border color when the mouse exits the cell
				setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_WIDTH));
			}
		});
	}

	public void putOpponentMove() throws IOException {
		System.err.println("Cell <put opponent (" + X + ", " + Y + ")>");
		// Load the image of the opponent's move
		if(gameProtocol.isAuto()){
			opponentFigure = gameProtocol.getChoice();
			gameProtocol.setAuto(false);
		}
		File file = new File(opponentFigure);
		this.image = ImageIO.read(file);
		// Highlight the cell with a red border
		super.setBorder(BorderFactory.createLineBorder(Color.RED, BORDER_WIDTH + 1));
		super.updateUI();
		try { Thread.sleep(2000); } catch (InterruptedException err) {}
		// Restore the original border color after a delay
		setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_WIDTH));
		super.updateUI();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draw the image in the cell
		g.drawImage(image, 0, 0, null);
	}
}