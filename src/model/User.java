package model;

public class User {
	private String nickname;
	private String password;
	private int rating;
	private int gamesCount;
	private boolean online;
	private boolean quit = false;
	private boolean answer;

	public User(String nickname, String password) {
		this.rating = 0;
		this.gamesCount = 0;
		this.online = false;
		this.nickname = nickname;
		this.password = password;
	}

	public User(String nickname, int rating, boolean answer) {
		this.rating = rating;
		this.nickname = nickname;
		this.answer = answer;
	}
	
	public User(String nickname, String password, int rating, int gamesCount) {
		this.online = false;
		this.rating = rating;
		this.nickname = nickname;
		this.password = password;
		this.gamesCount = gamesCount;
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public boolean isAnswer() {
		return answer;
	}

	public void setAnswer(boolean answer) {
		this.answer = answer;
	}

	public int getGamesCount() {
		return this.gamesCount;
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public int getRating() {
		return this.rating;
	}
	
	public void setRating(int value) {
		this.rating = value;
		this.gamesCount++;
	}
	
	public void setOnline(boolean value) {
		this.online = value;
	}
	
	public boolean isOnline() {
		return this.online;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User user = (User) o;
			if (user.nickname.equals(this.nickname) &&
				user.password.equals(this.password)) {
				return true;
			}
			return false;
		}
		return false;
	}

//	override the toString() method to return a string representation of the user object, including nickname, password, rating and number of games played.
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.nickname + "\n");
		sb.append(this.password + "\n");
		sb.append(this.rating + "\n");
		sb.append(this.gamesCount + "\n");
		return sb.toString();
	}
}