package view;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;

public class Constants {
	public static final int FRAME_WIDTH 		= 550;
	public static final int FRAME_HEIGHT	 	= 350;
	public static final int FIELD_HEIGHT 		= 3;
	public static final int FIELD_WIDTH 		= 3;
	public static final int PORT		  		= 8000;
	public static final String DEFAULT_IP 		= "127.0.0.1";
	public static 		String DIR 				= "";
	public static final String CIRCLE_DIR 		= DIR + "src/client_data/circle.jpg";
	public static final String CROSS_DIR 		= DIR + "src/client_data/cross.jpg";
	public static final String BACKGROUND_DIR	= DIR + "src/client_data/background.jpg";
	public static final String LOG_FILE_DIR		= DIR + "./client_log.txt";
	public static final String NOT_FIGURE		= "";
	public static final int SIGN_OUT			= 0;
	public static final int NEW_GAME			= 1;
	public static final int CONNECT_TO_GAME		= 2;
	public static final int NOTHING				= 3;
	public static final int STOP_WAITING	 	= 7;
	public static final int OPPONENT_FOUND 		= 9;
	public static final int GET_RATING_LIST		= 10;
	public static final String EXIT				= "exit";
	public static Image BACKGROUND_IMAGE;
	
	public static void init() throws IOException {
		BACKGROUND_IMAGE = ImageIO.read(new File(Constants.BACKGROUND_DIR));
	}
}
