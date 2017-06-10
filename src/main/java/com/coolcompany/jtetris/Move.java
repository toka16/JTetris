package com.coolcompany.jtetris;

/**
 * Move is used as a struct to store a single Move
 * ("static" here means it does not have a pointer to an
 * enclosing Brain object, it's just in the Brain namespace.)
 */
class Move {

	int x;

	int y;

	Piece piece;

	double score;    // lower scores are better

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Piece getPiece() {
		return piece;
	}

	public double getScore() {
		return score;
	}
}
