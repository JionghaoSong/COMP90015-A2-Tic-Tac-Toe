package model;

import java.io.*;
import java.net.*;
import java.util.*;

/*
这段代码实现了一个游戏列表的功能。它包含一个 GameList 类，
用于管理游戏模型（GameModel）对象的集合：

		构造函数 GameList()：创建一个空的游戏列表，使用 ArrayList 来存储游戏模型对象。
		add(GameModel gameModel) 方法：将游戏模型对象添加到列表中。
		remove(GameModel gameModel) 方法：从列表中移除指定的游戏模型对象。
		getCount() 方法：获取列表中游戏模型对象的数量。
		getGameModel(int id) 方法：根据给定的 ID 获取相应的游戏模型对象。
		toString(int index) 方法：获取指定索引处游戏模型对象的字符串表示形式。
		toString() 方法：获取整个游戏列表中所有游戏模型对象的字符串表示形式。
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
