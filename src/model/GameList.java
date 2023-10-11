package model;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Jionghao Song 1428049
 * @date 2023/10/11 22:20
 */


//Game list functionality to easily add, remove, fetch and display game model objects.
public class GameList {
	private ArrayList<GameModel> list;

	public GameList() {
		this.list = new ArrayList<GameModel>(); // Create an ArrayList to store GameModel objects
	}

	public boolean add(GameModel gameModel) {
		return this.list.add(gameModel); // Add a GameModel object to the list
	}

	public boolean remove(GameModel gameModel) {
		return this.list.remove(gameModel); // Remove a GameModel object from the list
	}

	public int getCount() {
		return this.list.size(); // Get the number of GameModel objects in the list
	}

	public GameModel getGameModel(int id) {
		Iterator iterator = this.list.iterator(); // Create an iterator to traverse the list
		if (list.size() == 0) {
			return null; // If the list is empty, return null
		}
		while (iterator.hasNext()) {
			GameModel gameModel = (GameModel) iterator.next(); // Get the next GameModel object from the iterator
			if (gameModel.getGameID() == id) {
				return gameModel; // If the GameModel object's ID matches the given ID, return the GameModel object
			}
		}
		return null; // If no matching GameModel object is found, return null
	}

	public String toString(int index) {
		return this.list.get(index).toString(); // Get the string representation of the GameModel object at the given index
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator iterator = this.list.iterator(); // Create an iterator to traverse the list
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString() + "\n"); // Append the string representation of each GameModel object to the StringBuilder
		}
		return sb.toString(); // Return the final string representation of all GameModel objects in the list
	}
}
