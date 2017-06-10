// Board.java
package com.coolcompany.jtetris;

import java.util.Arrays;

/**
 * CS108 Tetris Board.
 * Represents a Tetris board -- essentially a 2-d grid
 * of booleans. Supports tetris pieces and row clearing.
 * Has an "undo" feature that allows clients to add and remove pieces efficiently.
 * Does not do any drawing or have any idea of pixels. Instead,
 * just represents the abstract 2-d board.
 */
public class Board implements BoardAPI {

	static final int PLACE_OK = 0;
	static final int PLACE_ROW_FILLED = 1;
	static final int PLACE_OUT_BOUNDS = 2;
	static final int PLACE_BAD = 3;
	boolean committed;
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = false;
	private int[] colomns;
	private int[] rows;
	private int maxHeight;


	// Here a few trivial methods are provided:
	private boolean[][] backup;
	private int[] backupColomns;
	private int[] backupRows;
	private int backupHeight;


	/**
	 * Creates an empty board of the given width and height
	 * measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		backup = new boolean[width][height];
		committed = true;
		colomns = new int[width];
		backupColomns = new int[width];
		rows = new int[height];
		backupRows = new int[height];
		maxHeight = 0;
		backupHeight = 0;
	}

	/**
	 * Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the max column height present in the board.
	 * For an empty board this is 0.
	 */
	public int getMaxHeight() {
		return this.maxHeight;
	}

	/**
	 * Checks the board for internal consistency -- used
	 * for debugging.
	 */
	public void sanityCheck() {
		if (DEBUG) {
			int[] tempCols = new int[this.width];
			int[] tempRows = new int[this.height];
			int tempHeight = 0;
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					if (getGrid(i, j))
						tempRows[j]++;
				}
				tempCols[i] = getHeight(i);
				if (tempCols[i] > tempHeight)
					tempHeight = tempCols[i];
			}
			if (!Arrays.equals(tempRows, this.rows)) {
				throw new RuntimeException("mistake in rows widthes");
			}
			if (!Arrays.equals(tempCols, this.colomns)) {
				throw new RuntimeException("mistake in colomns heights");
			}
			if (tempHeight != this.maxHeight) {
				throw new RuntimeException("mistake in max height");
			}
		}
	}

	/**
	 * Given a piece and an x, returns the y
	 * value where the piece would come to rest
	 * if it were dropped straight down at that x.
	 * <p>
	 * <p>
	 * Implementation: use the skirt and the col heights
	 * to compute this fast -- O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		int height = 0;
		for (int i = 0; i < skirt.length; i++) {
			if (x + i >= this.width) break;
			if ((this.colomns[x + i] - skirt[i]) > height)
				height = this.colomns[x + i] - skirt[i];
		}
		return height;
	}

	/**
	 * Returns the height of the given column --
	 * i.e. the y value of the highest block + 1.
	 * The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
		return this.colomns[x];
	}

	/**
	 * Returns the number of filled blocks in
	 * the given row.
	 */
	public int getRowWidth(int y) {
		return this.rows[y];
	}

	/**
	 * Returns true if the given block is filled in the board.
	 * Blocks outside of the valid width/height area
	 * always return true.
	 */
	public boolean getGrid(int x, int y) {
		if (x < 0 || x >= this.width || y < 0 || y >= this.height)
			return true;
		return this.grid[x][y];
	}

	/**
	 * Attempts to add the body of a piece to the board.
	 * Copies the piece blocks into the board grid.
	 * Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 * for a regular placement that causes at least one row to be filled.
	 * <p>
	 * <p>Error cases:
	 * A placement may fail in two ways. First, if part of the piece may falls out
	 * of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 * Or the placement may collide with existing blocks in the grid
	 * in which case PLACE_BAD is returned.
	 * In both error cases, the board may be left in an invalid
	 * state. The client can use undo(), to recover the valid, pre-place state.
	 */
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!this.committed) throw new RuntimeException("place commit problem");

		int result = PLACE_OK;

		if (x < 0 || x + piece.getWidth() > this.width || y < 0)
			result = PLACE_OUT_BOUNDS;

		if (result == PLACE_OK) {
			TPoint[] b = piece.getBody();
			for (int i = 0; i < b.length; i++) {
				if (getGrid(b[i].x + x, b[i].y + y)) {
					result = PLACE_BAD;
					break;
				}
				this.grid[b[i].x + x][b[i].y + y] = true;
				if (this.colomns[b[i].x + x] < b[i].y + y + 1)
					this.colomns[b[i].x + x] = b[i].y + y + 1;
				if (b[i].y + y > this.maxHeight - 1)
					this.maxHeight = b[i].y + y + 1;
				this.rows[b[i].y + y]++;
				if (this.rows[b[i].y + y] == this.width)
					result = PLACE_ROW_FILLED;
			}
		}
		this.committed = false;
		if (DEBUG)
			if (result == PLACE_OK || result == PLACE_ROW_FILLED)
				sanityCheck();
		return result;
	}


	/**
	 * Deletes rows that are filled all the way across, moving
	 * things above down. Returns the number of rows cleared.
	 */
	public int clearRows() {
		int rowsCleared = 0;
		for (int i = 0; i < this.rows.length; i++) {
			if (this.rows[i] == this.width) {
				clearRowY(i);
				rowsCleared++;
				i--;
			}
		}
		if (DEBUG)
			sanityCheck();
		this.committed = false;
		return rowsCleared;
	}


	// Clear given row and slide upper rows down
	private void clearRowY(int y) {
		this.maxHeight = 0;
		for (int i = y; i < this.height - 1; i++) {
			for (int x = 0; x < this.width; x++) {
				this.grid[x][i] = this.grid[x][i + 1];
			}
			this.rows[i] = this.rows[i + 1];
		}
		for (int x = 0; x < this.width; x++) {
			this.grid[x][this.height - 1] = false;
		}
		this.rows[this.height - 1] = 0;
		for (int i = 0; i < this.width; i++) {
			this.colomns[i] = getHeight(i);
			if (this.colomns[i] > this.maxHeight) {
				this.maxHeight = this.colomns[i];
			}
		}
	}

	private int getHeight(int x) {
		for (int i = this.height - 1; i >= 0; i--)
			if (getGrid(x, i))
				return i + 1;
		return 0;
	}


	/**
	 * Reverts the board to its state before up to one place
	 * and one clearRows();
	 * If the conditions for undo() are not met, such as
	 * calling undo() twice in a row, then the second undo() does nothing.
	 * See the overview docs.
	 */
	public void undo() {
		if (!this.committed) {
			this.committed = true;
			for (int i = 0; i < this.width; i++) {
				System.arraycopy(this.backup[i], 0, this.grid[i], 0, this.height);
			}
			System.arraycopy(this.backupColomns, 0, this.colomns, 0, this.width);
			System.arraycopy(this.backupRows, 0, this.rows, 0, this.height);
			this.maxHeight = this.backupHeight;
		}
	}


	/**
	 * Puts the board in the committed state.
	 */
	public void commit() {
		if (!this.committed) {
			this.committed = true;
			for (int i = 0; i < this.width; i++) {
				System.arraycopy(this.grid[i], 0, this.backup[i], 0, this.height);
			}
			System.arraycopy(this.colomns, 0, this.backupColomns, 0, this.width);
			System.arraycopy(this.rows, 0, this.backupRows, 0, this.height);
			this.backupHeight = this.maxHeight;
		}
	}


	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = this.height - 1; y >= 0; y--) {
			buff.append('|');
			for (int x = 0; x < this.width; x++) {
				if (getGrid(x, y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x = 0; x < this.width + 2; x++) buff.append('-');
		return (buff.toString());
	}
}


