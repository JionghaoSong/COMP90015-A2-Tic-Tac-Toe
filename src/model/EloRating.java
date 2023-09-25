package model;

public class EloRating {
	private static final int K_MORE_2400 = 10; // Constant representing the K-factor for players with a rating above 2400
	private static final int K_LESS_2400 = 20; // Constant representing the K-factor for players with a rating below or equal to 2400
	private static final int K_FIRST_30_GAMES = 30; // Constant representing the K-factor for the first 30 games
	public static final double WIN = 1; // Constant representing a win in a game
	public static final double LOSS = 0; // Constant representing a loss in a game
	public static final double TIE = 0.5; // Constant representing a tie in a game

	public static int toCount(int ratingA, int ratingB, double gameResult, int gamesCountA) {
		int k;
		if (gamesCountA < 30) {
			k = K_FIRST_30_GAMES;
		} else if (ratingA > 2400) {
			k = K_MORE_2400;
		} else {
			k = K_LESS_2400;
		}
		return ratingA + (int)( k * (gameResult - e(ratingA, ratingB)) );
	}

	private static double e(int ratingA, int ratingB) {
		double degree = ((double)( ratingB - ratingA)) / 400;
		return 1 / (1 + Math.pow(10, degree));
	}
}