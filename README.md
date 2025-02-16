### **Project Documentation: Distributed Tic-Tac-Toe System**

#### **1. Project Overview**
This project is a **Distributed Tic-Tac-Toe System** developed in Java. It allows multiple players to connect and play the game in real time while communicating through a built-in chat feature. The system is designed with a **client-server architecture**, where the **server manages player connections and game logic**, while the **clients provide a graphical user interface (GUI) for interaction**.

The implementation supports either **Java Sockets** or **Remote Method Invocation (RMI)** for network communication.

---

#### **2. System Components**
- **Client (`client.jar`)**:
  - Provides a GUI for players to interact with the game.
  - Displays the Tic-Tac-Toe board and updates the game state in real-time.
  - Allows players to send chat messages to their opponent.
  - Handles user inputs, including making moves and quitting the game.

- **Server (`server.jar`)**:
  - Manages all connected clients and assigns matches.
  - Updates game states and sends them to players after every move.
  - Determines game results (win, draw, or loss) and notifies clients.
  - Handles player disconnections and game session management.

- **Log Files (`client_log.txt` & `server_log.txt`)**:
  - Store runtime logs for debugging and tracking system events.

---

#### **3. How to Run the System**

##### **Prerequisites**
- Install **Java 8+**.
- Ensure the required **JAR files** (`server.jar` and `client.jar`) are in the same directory.

##### **Step 1: Start the Server**
Run the following command in the terminal or command prompt:
```sh
java -jar server.jar <ip_address> <port>
```
Example:
```sh
java -jar server.jar 127.0.0.1 5000
```
This will start the server on the specified IP and port.

##### **Step 2: Start the Clients**
Each player should run the client application using:
```sh
java -jar client.jar <username> <server_ip> <server_port>
```
Example:
```sh
java -jar client.jar player1 127.0.0.1 5000
java -jar client.jar player2 127.0.0.1 5000
```
- `username`: Unique name for the player.
- `server_ip`: The IP address where the server is running.
- `server_port`: The port number the server is listening on.

##### **Step 3: Play the Game**
- Once two players are connected, the game will begin.
- Players take turns making moves by clicking on the GUI board.
- The game will continue until a player wins or the match ends in a draw.
- Players can communicate through the chat window on the side.

##### **Step 4: Ending the Game**
- A message will be displayed when the game concludes.
- Players can choose to find a new match or exit the game using the quit option.

---

#### **4. Additional Features**
- **Timeout System**: If a player does not make a move within 20 seconds, a random move is made automatically.
- **Fault Tolerance**: If a client crashes, the game pauses for 30 seconds, allowing a reconnection before declaring a draw.
- **Ranking System**: Players gain or lose points based on their performance, affecting their ranking.
