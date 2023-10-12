package model;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:20
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class GameProtocol {
    private UserListener creatorListener;
    private UserListener connectorListener;
    private int[][] field;
    private int freeCells;
    private int time;
    private boolean isCreatorMove;
    private boolean isConnectorMove;
    private boolean tie;
    private boolean creatorWin;
    private boolean connectorWin;
    private boolean startTime;
    private Thread thread;
    private String creatorChoice;
    private String connnectorChoice;

    public GameProtocol(User creator, Socket creatorSocket, User connector, Socket connectorSocket) throws IOException, InterruptedException {

        DataInputStream creatorIn = new DataInputStream(creatorSocket.getInputStream());
        DataOutputStream creatorOut = new DataOutputStream(creatorSocket.getOutputStream());
        DataInputStream connectorIn = new DataInputStream(connectorSocket.getInputStream());
        DataOutputStream connectorOut = new DataOutputStream(connectorSocket.getOutputStream());

        // Set initial values for game state
        isCreatorMove = true;
        isConnectorMove = false;
        tie = false;
        creatorWin = false;
        connectorWin = false;

        new Random();

        // Create UserListener objects to handle input/output for each player
        creatorListener = new UserListener(creatorIn, creatorOut, connectorIn, connectorOut, 1, isCreatorMove, creator);
        connectorListener = new UserListener(connectorIn, connectorOut, creatorIn, creatorOut, 2, isConnectorMove, connector);

        // Initialize game board and remaining free cells
        field = new int[3][3];
        freeCells = 9;
        time = 20;

        // Start the UserListener threads
        creatorListener.start();
        connectorListener.start();

        // Create and start the timer thread to track game time
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (; ; ) {
                        Thread.sleep(1000);
                        if(creatorWin || connectorWin){
                            break;
                        }
                        if(!startTime){
                            continue;
                        }
                        time--;
                        if (time <= 0) {
                            System.out.println("timer");
//                            creatorOut.writeUTF("timeIsOver");
//                            connectorOut.writeUTF("timeIsOver");
//                            creatorListener.interrupt();
//                            connectorListener.interrupt();
//                            break;
                            int x = -1;
                            int y = -1;
                            for (int x1 = 0; x1 < 3; x1++) {
                                boolean flag = false;
                                for (int y1 = 0; y1 < 3; y1++) {
                                    if(field[x1][y1] == 0){
                                        x = x1;
                                        y = y1;
                                        System.out.println(x);
                                        System.out.println(y);
                                        flag=true;
                                        break;
                                    }
                                }
                                if(flag){
                                    break;
                                }
                            }
                            if(isCreatorMove){
                                creatorOut.writeUTF("auto");       // Send the mode "coordinate" to the opponent
                                creatorOut.writeUTF(creatorChoice);       // Send the mode "coordinate" to the opponent
                                connectorOut.writeUTF("auto");       // Send the mode "coordinate" to the opponent
                                connectorOut.writeUTF(creatorChoice);       // Send the mode "coordinate" to the opponent
                            }

                            if(isConnectorMove){
                                creatorOut.writeUTF("auto");       // Send the mode "coordinate" to the opponent
                                creatorOut.writeUTF(connnectorChoice);       // Send the mode "coordinate" to the opponent
                                connectorOut.writeUTF("auto");       // Send the mode "coordinate" to the opponent
                                connectorOut.writeUTF(connnectorChoice);       // Send the mode "coordinate" to the opponent
                            }

                            if (isCreatorMove) {              // If the player is the creator and it's their turn
                                creatorOut.writeUTF("answerToCanPut");            // Notify the player to send the answer to "CanPut" question
                                if (field[x][y] == 0) {                      // Check if the cell is empty
                                    field[x][y] = 1;                         // Set the cell value to 1 (representing the creator)
                                    freeCells--;                             // Decrement the count of free cells
                                    isCreatorMove = false;                   // Switch the turn to the connector
                                    isConnectorMove = true;
                                    creatorOut.writeBoolean(true);            // Notify the player that the move is valid
                                    boolean isWin = winCheck(1, x, y);   // Check if the move resulted in a win
                                    if (isWin) {
                                        creatorWin = true;                    // Set the creator as the winner
                                    }
                                    creatorOut.writeBoolean(isWin);

                                    creatorOut.writeUTF("coordinate");       // Send the mode "coordinate" to the opponent
                                    creatorOut.writeInt(x);
                                    creatorOut.writeInt(y);
                                    creatorOut.writeBoolean(isWin);          // Send if the move resulted in a win to the opponent

                                    connectorOut.writeUTF("coordinate");       // Send the mode "coordinate" to the opponent
                                    connectorOut.writeInt(x);
                                    connectorOut.writeInt(y);
                                    connectorOut.writeBoolean(isWin);          // Send if the move resulted in a win to the opponent

                                    if (creatorWin || connectorWin) {
                                        break;                                // Break the loop if the game has been won
                                    }
                                } else {
                                    creatorOut.writeBoolean(false);            // Notify the player that the move is invalid
                                }
                            } else if (isConnectorMove) {     // If the player is the connector and it's their turn
                                creatorOut.writeUTF("answerToCanPut");            // Notify the player to send the answer to "CanPut" question
                                if (field[x][y] == 0) {                      // Check if the cell is empty
                                    field[x][y] = 2;                         // Set the cell value to 2 (representing the connector)
                                    freeCells--;                             // Decrement the count of free cells
                                    isConnectorMove = false;                 // Switch the turn to the creator
                                    isCreatorMove = true;
                                    creatorOut.writeBoolean(true);            // Notify the player that the move is valid
                                    boolean isWin = winCheck(2, x, y);   // Check if the move resulted in a win
                                    if (isWin) {
                                        connectorWin = true;                  // Set the connector as the winner
                                    }
                                    creatorOut.writeBoolean(isWin);            // Notify the player if they won or not

                                    creatorOut.writeUTF("coordinate");       // Send the mode "coordinate" to the opponent
                                    creatorOut.writeInt(x);
                                    creatorOut.writeInt(y);
                                    creatorOut.writeBoolean(isWin);          // Send if the move resulted in a win to the opponent

                                    connectorOut.writeUTF("coordinate");       // Send the"coordinate" mode to the opponent
                                    connectorOut.writeInt(x);
                                    connectorOut.writeInt(y);
                                    connectorOut.writeBoolean(isWin);          // Send if the move resulted in a win to the opponent
                                    if (creatorWin || connectorWin) {
                                        break;                                // Break the loop if the game has been won
                                    }
                                } else {
                                    creatorOut.writeBoolean(false);            // Notify the player that the move is invalid
                                }
                            } else {
                                creatorOut.writeUTF("answerToCanPut");
                                creatorOut.writeBoolean(false);                // Notify the player that it's not their turn
                            }
                            time = 20;
                        } else if (creatorWin || connectorWin) {
                            creatorListener.interrupt();
                            connectorListener.interrupt();
                            break;
                        } else if (freeCells == 0) {
                            tie = true;
                            creatorOut.writeUTF("tie");
                            connectorOut.writeUTF("tie");
                            creatorListener.interrupt();
                            connectorListener.interrupt();
                            break;
                        } else if (!creator.isOnline()) {
                            connectorOut.writeUTF("opponent disconnected");
                            //creatorListener.interrupt();
                            //connectorListener.interrupt();
                            break;
                        } else if (!connector.isOnline()) {
                            creatorOut.writeUTF("opponent disconnected");
                            //creatorListener.interrupt();
                            //connectorListener.interrupt();
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        System.err.println("GameProtocol <stop>");
    }

    public boolean isCreatorWin() {
        return creatorWin;
    }

    public boolean isConnectorWin() {
        return connectorWin;
    }

    public boolean isTie() {
        return tie;
    }

    private class UserListener extends Thread {
        private DataInputStream inPlayer;       // Input stream for the player
        private DataOutputStream outPlayer;     // Output stream for the player
        private DataInputStream inOpponent;     // Input stream for the opponent
        private DataOutputStream outOpponent;   // Output stream for the opponent
        private int number;                     // Player number (1 or 2)
        private User user;                      // User object representing the player

        public UserListener(DataInputStream inPlayer,
                            DataOutputStream outPlayer,
                            DataInputStream inOpponent,
                            DataOutputStream outOpponent,
                            int number,
                            boolean isMove,
                            User user) throws IOException {
            this.user = user;
            this.inPlayer = inPlayer;
            this.outPlayer = outPlayer;
            this.inOpponent = inOpponent;
            this.outOpponent = outOpponent;
            this.number = number;
            this.outPlayer.writeUTF("START GAME");                   // Notify the player to start the game
            this.outOpponent.writeUTF("OPPONENT IS READY");          // Notify the opponent that the player is ready
            //outPlayer.writeBoolean(isMove);
        }

        @Override
        public void run() {
            try {
                for (; ; ) {
                    String mode = inPlayer.readUTF();                   // Read the mode of operation from the player
                    System.out.println(mode);
                    if ("break".equals(mode)) {
                        break;                                          // Break the loop if "break" mode is received
                    }
                    if ("time".equals(mode)) {
                        int message = inPlayer.readInt();            // Read the message from the player
                        outOpponent.writeUTF(mode);                     // Send the mode to the opponent
                        outOpponent.writeInt(message);                   // Send the message to the opponent
                    }
                    if ("timeReset".equals(mode)) {
                        if(number == 1 && isCreatorMove || number == 2 && isConnectorMove){
                            time=20;
                            outOpponent.writeUTF("time");
                            outOpponent.writeInt(time);
                            outPlayer.writeUTF("time");
                            outPlayer.writeInt(time);
                        }
                    }
                    if ("startTime".equals(mode)) {
                        startTime = true;
                    }
                    if ("choice".equals(mode)) {
                        int type = inPlayer.readInt();
                        String choice = inPlayer.readUTF();
                        if(type == view.Constants.OPPONENT_FOUND){
                            creatorChoice = choice;
                        }else {
                            connnectorChoice = choice;
                        }
                    }
                    if ("message".equals(mode)) {
                        String message = inPlayer.readUTF();            // Read the message from the player
                        outOpponent.writeUTF(mode);                     // Send the mode to the opponent
                        outOpponent.writeUTF(message);                   // Send the message to the opponent
                    } else if ("coordinate".equals(mode)) {
                        int x = inPlayer.readInt();
                        int y = inPlayer.readInt();
                        System.err.println("GameProtocol <" + number + " move (" + x + "," + y + ")");

                        if (number == 1 && isCreatorMove) {              // If the player is the creator and it's their turn
                            if(x == -100 && y == -100){
                                outPlayer.writeUTF("quit");
                                outOpponent.writeUTF("quit");
                                connectorWin = true;
                                if (creatorWin || connectorWin) {
                                    break;                                // Break the loop if the game has been won
                                }
                            }

                            outPlayer.writeUTF("answerToCanPut");            // Notify the player to send the answer to "CanPut" question
                            if (field[x][y] == 0) {                      // Check if the cell is empty
                                field[x][y] = 1;                         // Set the cell value to 1 (representing the creator)
                                freeCells--;                             // Decrement the count of free cells
                                isCreatorMove = false;                   // Switch the turn to the connector
                                isConnectorMove = true;
                                outPlayer.writeBoolean(true);            // Notify the player that the move is valid
                                boolean isWin = winCheck(number, x, y);   // Check if the move resulted in a win
                                if (isWin) {
                                    creatorWin = true;                    // Set the creator as the winner
                                }
                                outPlayer.writeBoolean(isWin);            // Notify the player if they won or not
                                outOpponent.writeUTF("coordinate");       // Send the mode "coordinate" to the opponent
                                outOpponent.writeInt(x);
                                outOpponent.writeInt(y);
                                outOpponent.writeBoolean(isWin);          // Send if the move resulted in a win to the opponent
                                if (creatorWin || connectorWin) {
                                    break;                                // Break the loop if the game has been won
                                }
                            } else {
                                outPlayer.writeBoolean(false);            // Notify the player that the move is invalid
                            }
                        } else if (number == 2 && isConnectorMove) {     // If the player is the connector and it's their turn
                            if(x == -100 && y == -100){
                                outPlayer.writeUTF("quit");
                                outOpponent.writeUTF("quit");

                                creatorWin = true;
                                if (creatorWin || connectorWin) {
                                    break;                                // Break the loop if the game has been won
                                }
                            }
                            outPlayer.writeUTF("answerToCanPut");            // Notify the player to send the answer to "CanPut" question
                            if (field[x][y] == 0) {                      // Check if the cell is empty
                                field[x][y] = 2;                         // Set the cell value to 2 (representing the connector)
                                freeCells--;                             // Decrement the count of free cells
                                isConnectorMove = false;                 // Switch the turn to the creator
                                isCreatorMove = true;
                                outPlayer.writeBoolean(true);            // Notify the player that the move is valid
                                boolean isWin = winCheck(number, x, y);   // Check if the move resulted in a win
                                if (isWin) {
                                    connectorWin = true;                  // Set the connector as the winner
                                }
                                outPlayer.writeBoolean(isWin);            // Notify the player if they won or not
                                outOpponent.writeUTF("coordinate");       // Send the"coordinate" mode to the opponent
                                outOpponent.writeInt(x);
                                outOpponent.writeInt(y);
                                outOpponent.writeBoolean(isWin);          // Send if the move resulted in a win to the opponent
                                if (creatorWin || connectorWin) {
                                    break;                                // Break the loop if the game has been won
                                }
                            } else {
                                outPlayer.writeBoolean(false);            // Notify the player that the move is invalid
                            }
                        } else {
                            outPlayer.writeUTF("answerToCanPut");
                            outPlayer.writeBoolean(false);                // Notify the player that it's not their turn
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("GameProtocol <IOException> " + user.getNickname());
                user.setOnline(false);
                e.printStackTrace();
            }
        }
    }

    private boolean winCheck(int num, int x, int y) {
        int max = 3;                         // Maximum length of winning sequence
        int up = 0;                          // Counter for cells in the upward direction
        int down = 0;                        // Counter for cells in the downward direction
        int left = 0;                        // Counter for cells in the left direction
        int right = 0;                       // Counter for cells in the right direction
        int up_left = 0;                     // Counter for cells in the upward-left direction
        int up_right = 0;                    // Counter for cells in the upward-right direction
        int down_left = 0;                   // Counter for cells in the downward-left direction
        int down_right = 0;                  // Counter for cells in the downward-right direction

        // Count cells in the upward direction
        for (int i = 1; i < max; i++) {
            if (y - i >= 0 && field[x][y - i] == num)
                up++;
            else
                break;
        }

        // Count cells in the downward direction
        for (int i = 1; i < max; i++) {
            if (y + i < Constants.FIELD_HEIGHT && field[x][y + i] == num)
                down++;
            else
                break;
        }

        // Count cells in the left direction
        for (int i = 1; i < max; i++) {
            if (x - i >= 0 && field[x - i][y] == num)
                left++;
            else
                break;
        }

        // Count cells in the right direction
        for (int i = 1; i < max; i++) {
            if (x + i < Constants.FIELD_WIDTH && field[x + i][y] == num)
                right++;
            else
                break;
        }

        // Count cells in the upward-left direction
        for (int i = 1; i < max; i++) {
            if (x - i >= 0 && y - i >= 0 && field[x - i][y - i] == num)
                up_left++;
            else
                break;
        }

        // Count cells in the upward-right direction
        for (int i = 1; i < max; i++) {
            if (y - i >= 0 && x + i < Constants.FIELD_WIDTH && field[x + i][y - i] == num)
                up_right++;
            else
                break;
        }

        // Count cells in the downward-left direction
        for (int i = 1; i < max; i++) {
            if (x - i >= 0 && y + i < Constants.FIELD_HEIGHT && field[x - i][y + i] == num)
                down_left++;
            else
                break;
        }

        // Count cells in the downward-right direction
        for (int i = 1; i < max; i++) {
            if (x + i < Constants.FIELD_WIDTH && y + i < Constants.FIELD_HEIGHT && field[x + i][y + i] == num)
                down_right++;
            else
                break;
        }

        // Check if any of the directions have a winning sequence
        if (up + down + 1 >= max)
            return true;
        if (left + right + 1 >= max)
            return true;
        if (up_left + down_right + 1 >= max)
            return true;
        if (up_right + down_left + 1 >= max)
            return true;

        return false;                        // No winning sequence found
    }
}