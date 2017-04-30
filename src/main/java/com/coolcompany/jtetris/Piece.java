package com.coolcompany.jtetris;

import java.util.*;

public class Piece {
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next; // "next" rotation

	static private Piece[] pieces;    // singleton static array of first rotations
	static private boolean piecesInitialized = false;

	/**
	 * Defines a new piece given a TPoint[] array of its body.
	 * Makes its own copy of the array and the TPoints inside it.
	 */
	public Piece(TPoint[] points) {
		body = points.clone();
		width = getWidth(points);
		skirt = getSkirt(points);
		height = getHeight(points);
		pieces = getPieces();
	}

	/**
	 * Return skirt of the piece
	 */
	private int[] getSkirt(TPoint[] points) {
		int width = getWidth(points);
		int[] sk = new int[width];
		for (int i = 0; i < width; i++)
			sk[i] = 5;   // more than the highest possible point in piece

		for (int i = 0; i < points.length; i++)
			if (points[i].y < sk[points[i].x])
				sk[points[i].x] = points[i].y;
		return sk;
	}


	/**
	 * Return width of the piece
	 */
	private int getWidth(TPoint[] points) {
		int width = 0;
		for (int i = 0; i < points.length; i++)
			if (points[i].x > width)
				width = points[i].x;
		return width + 1;
	}

	/**
	 * Return height of the piece
	 */
	private int getHeight(TPoint[] points) {
		int height = 0;
		for (int i = 0; i < points.length; i++)
			if (points[i].y > height)
				height = points[i].y;
		return height + 1;
	}

	/**
	 * Return next rotation of the given body as piece
	 */
	private Piece nextRotation(TPoint[] points) {
		TPoint[] list = new TPoint[points.length];
		for (int i = 0; i < points.length; i++)
			list[i] = (new TPoint(height - 1 - points[i].y, points[i].x));

		return new Piece(list);
	}


	/**
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	/**
	 * Returns the width of the piece measured in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the piece measured in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns a pointer to the piece's body. The caller
	 * should not modify this array.
	 */
	public TPoint[] getBody() {
		return body;
	}

	/**
	 * Returns a pointer to the piece's skirt. For each x value
	 * across the piece, the skirt gives the lowest y value in the body.
	 * This is useful for computing where the piece will land.
	 * The caller should not modify this array.
	 */
	public int[] getSkirt() {
		return skirt;
	}


	/**
	 * Returns a new piece that is 90 degrees counter-clockwise
	 * rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		return nextRotation(body);
	}

	/**
	 * Returns a pre-computed piece that is 90 degrees counter-clockwise
	 * rotated from the receiver.	 Fast because the piece is pre-computed.
	 * This only works on pieces set up by makeFastRotations(), and otherwise
	 * just returns null.
	 */
	public Piece fastRotation() {
		return next;
	}


	/**
	 * Returns true if two pieces are the same --
	 * their bodies contain the same points.
	 * Interestingly, this is not the same as having exactly the
	 * same body arrays, since the points may not be
	 * in the same order in the bodies. Used internally to detect
	 * if two rotations are effectively the same.
	 */
	public boolean equals(Object obj) {
		if (obj == this) return true;

		if (!(obj instanceof Piece)) return false;
		Piece other = (Piece) obj;

		return bodyEquals(other.getBody(), this.body);
	}

	/*
	 * Return true if two given bodies are equal
	 * else return false
	 */
	private boolean bodyEquals(TPoint[] body1, TPoint[] body2) {
		for (int i = 0; i < body1.length; i++)
			if (!contains(body2, body1[i]))
				return false;
		return true;
	}

	/*
	 * Return true if the given body contains the given point
	 * else return false
	 */
	private boolean contains(TPoint[] body, TPoint p) {
		for (int i = 0; i < body.length; i++)
			if (body[i].equals(p))
				return true;
		return false;
	}


	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR = "0 0	0 1	 0 2  0 3";
	public static final String L1_STR = "0 0	0 1	 0 2  1 0";
	public static final String L2_STR = "0 0	1 0 1 1	 1 2";
	public static final String S1_STR = "0 0	1 0	 1 1  2 1";
	public static final String S2_STR = "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR = "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR = "0 0  1 0  1 1  2 0";

	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1 = 1;
	public static final int L2 = 2;
	public static final int S1 = 3;
	public static final int S2 = 4;
	public static final int SQUARE = 5;
	public static final int PYRAMID = 6;

	/**
	 * Returns an array containing the first rotation of
	 * each of the 7 standard tetris pieces in the order
	 * STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
	 * The next (counterclockwise) rotation can be obtained
	 * from each piece with the {@link #fastRotation()} message.
	 * In this way, the client can iterate through all the rotations
	 * until eventually getting back to the first rotation.
	 * (provided code)
	 */
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (!Piece.piecesInitialized) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.piecesInitialized = true;
			Piece.pieces = new Piece[]{
					makeFastRotations(new Piece(STICK_STR)),
					makeFastRotations(new Piece(L1_STR)),
					makeFastRotations(new Piece(L2_STR)),
					makeFastRotations(new Piece(S1_STR)),
					makeFastRotations(new Piece(S2_STR)),
					makeFastRotations(new Piece(SQUARE_STR)),
					makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}


		return Piece.pieces;
	}


	/**
	 * Given the "first" root rotation of a piece, computes all
	 * the other rotations and links them all together
	 * in a circular list. The list loops back to the root as soon
	 * as possible. Returns the root piece. fastRotation() relies on the
	 * pointer structure setup here.
	 */
	private static Piece makeFastRotations(Piece root) {
		Piece temp = root;
		while (true) {
			temp.next = temp.computeNextRotation();
			if (temp.next.equals(root)) {
				temp.next = root;
				break;
			}
			temp = temp.next;
		}

		return root;
	}


	/**
	 * Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 * the points into a TPoint[] array.
	 */
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while (tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());

				points.add(new TPoint(x, y));
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}

		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}

	@Override
	public String toString() {
		String res = "[";
		for (int i = 0; i < body.length; i++)
			res += body[i].toString();
		res += "]";
		return res;
	}
}
