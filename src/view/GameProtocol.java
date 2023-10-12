package view;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:22
 */


import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GameProtocol {
	private DataInputStream in;
	private DataOutputStream out;
	private Thread listener;
	private String msg;
	private boolean canPut;
	private boolean quit = false;
	private boolean endQuit;
	private boolean isWin;
	private boolean opponentIsWin;
	private boolean tie;
	private boolean opponentDisconnect;
	private boolean timeUp;
	private int xCoord;
	private int yCoord;
	private int time = -1;
	private String timePic = "";
	private boolean timeEnd;
	private boolean auto;
	private String choice;
	LoginPanel loginPanel;
	
	public GameProtocol(Socket socket, LoginPanel loginPanel) throws IOException {
		this.loginPanel = loginPanel;
		this.xCoord = -1;
		this.yCoord = -1;
		this.isWin = false;
		this.opponentIsWin = false;
		this.timeUp = false;
		this.opponentDisconnect = false;
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
		System.out.println(in.readUTF());
		System.out.println(in.readUTF());
	}

	public void setEndQuit(boolean endQuit) {
		this.endQuit = endQuit;
	}

	public String getChoice() {
		return choice;
	}

	public void sendTime(int message) throws IOException {
		System.err.println("GameProtocol <send message>");
		this.out.writeUTF("time");
		this.out.writeInt(message);
	}

	public void sendChoice(int type, String message) throws IOException {
		System.err.println("GameProtocol <send choice>");
		this.out.writeUTF("choice");
		this.out.writeInt(type);
		this.out.writeUTF(message);
	}

	public void sendStartTime() throws IOException {
		System.err.println("GameProtocol <send start>");
		this.out.writeUTF("startTime");
	}

	public void sendMessage(String message) throws IOException {
		System.err.println("GameProtocol <send message>");
		this.out.writeUTF("message");
		this.out.writeUTF(message);
	}
	
	public void sendCoordinate(int x, int y) throws IOException {
		this.out.writeUTF("timeReset");
		System.err.println("GameProtocol <send coordinate>");
		this.out.writeUTF("coordinate");
		this.out.writeInt(x);
		this.out.writeInt(y);

	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public boolean isTie() {
		return tie;
	}

	public void setTie(boolean tie) {
		this.tie = tie;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(boolean timeEnd) {
		this.timeEnd = timeEnd;
	}

	public boolean getIsWin() {
		return this.isWin;
	}
	
	public boolean getOpponentIsWin() {
		return this.opponentIsWin;
	}
	
	public boolean isOpponentDisconnect() {
		return this.opponentDisconnect;
	}
	
	public String getMsg() {
		String result = msg;
		msg = null;
		return result;
	}
	
	public boolean isCanPut() {
		if (this.canPut) {
			this.canPut = false;
			return true;
		}
		return false;
	}
	
	public boolean isTimeUp() {
		return this.timeUp;
	}
	
	public int getX() {
		int result = this.xCoord;
		this.xCoord = -1;
		return result;
	}
	
	public int getY() {
		int result = this.yCoord;
		this.yCoord = -1;
		return result;
	}

	public void start() throws InterruptedException {
		listener = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (;;) {
						timePic = "";
						String mode = in.readUTF();
						System.out.println("listener: " + mode);
						if("quit".equals(mode)){
							quit = true;
							out.writeUTF("break");
							break;
						}
						if ("time".equals(mode)) {
							System.err.println("GameProtocol <get time>");
							time = in.readInt();
						}
						if ("auto".equals(mode)) {
							System.err.println("GameProtocol <get auto>");
							auto = true;
							choice = in.readUTF();
						}
						if ("message".equals(mode)) {
							System.err.println("GameProtocol <get message>");
							msg = in.readUTF();
						} else if ("coordinate".equals(mode)) {
							System.err.println("GameProtocol <get coordinate>");
							xCoord = in.readInt();
							yCoord = in.readInt();
							opponentIsWin = in.readBoolean();
							if (opponentIsWin) {
								System.err.println("GameProtocol <opponent win>");
								out.writeUTF("break");
								break;
							}
						} else if ("answerToCanPut".equals(mode)) {
							System.err.println("GameProtocol <get answer to can put>");
							canPut = in.readBoolean();
							if (canPut) {
								isWin = in.readBoolean();
								if (isWin) {
									System.err.println("GameProtocol <win>");
									break;
								}
								System.err.println("GameProtocol <can't to put>");
							}
						} else if ("timeIsOver".equals(mode)) {
							timeUp = true;
							break;
						}else if ("opponent disconnected".equals(mode)) {
							out.writeUTF("break");
							opponentDisconnect = true;
							//JOptionPane.showMessageDialog(null, "Opponent disconnected. You Win!", "", JOptionPane.INFORMATION_MESSAGE);
							break;
						} else if ("tie".equals(mode)) {
							tie = true;
							break;
						}
					}
					System.err.println("GameProtocol <stop>");
				} catch (IOException e) {
					System.out.println(e.getMessage());
					System.err.println("GameProtocol <IOException start()>");
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Server crushed", "", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		listener.start();
		listener.join();
	}
	
	public void stop() {
		listener.interrupt();
	}
}