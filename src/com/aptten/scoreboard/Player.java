package com.aptten.scoreboard;

/**
 * The Player class represents a single player in the list of players and scores.  It holds the name
 * and score.
 *
 * @author  Brett Haines
 * @version 1.0 December 10, 2014
 */
public class Player {

    /** Displayed name of the player */
	private String name;

    /** Player's current score */
	private int score;

    /** Creates a Player object with name setName and score setScore.
     *
     * @param setName
     * @param setScore
     */
	public Player(String setName, int setScore) {
		name = setName;
		score = setScore;
	}

	public void setName(String newName) {
        name = newName;
    }

	public String getName() {
        return name;
    }

	public void setScore(int newScore) {
        score = newScore;
    }

	public int getScore() {
        return score;
    }

	public void increment(int amount) {
		score += amount;
	}

	public void decrement(int amount) {
		score -= amount;
	}
	
}