package com.coolcompany.jtetris;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoardTest {
	private Board b;
	private Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;

	// This shows how to build things in setUp() to re-use
	// across tests.

	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	@Before
	public void setUp() throws Exception {
		b = new Board(3, 6);

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		b.place(pyr1, 0, 0);
	}

	// Check the basic width/height/max after the one placement
	@Test
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
		assertEquals(3, b.getWidth());
		assertEquals(6, b.getHeight());
	}

	// Place sRotated into the board, then check some measures
	@Test
	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}


	private Board board;

	@Test
	public void testUndo() {
		board = new Board(5, 5);
		int res = board.place(pyr1, 3, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		board.undo();
		assertEquals(0, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(3));
		assertEquals(0, board.getColumnHeight(4));
		assertEquals(0, board.getRowWidth(0));
		assertEquals(0, board.getRowWidth(1));
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		board.undo();
		assertEquals(0, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(3));
		assertEquals(0, board.getColumnHeight(4));
		assertEquals(0, board.getRowWidth(0));
		assertEquals(0, board.getRowWidth(1));
	}


	@Test
	public void test1() {
		board = new Board(4, 8);
		int res = board.place(pyr1, 0, 0);
		assertEquals(Board.PLACE_OK, res);
		board.commit();
		assertEquals(2, board.getMaxHeight());
		res = board.place(pyr2, 2, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		assertEquals(4, board.getRowWidth(0));
		int numRows = board.clearRows();
		assertEquals(1, numRows);
		board.commit();
		assertEquals(3, board.getRowWidth(0));
		assertEquals(1, board.getRowWidth(1));
		assertEquals(2, board.getMaxHeight());
		assertEquals(2, board.getColumnHeight(3));
		assertEquals(1, board.getColumnHeight(2));
		assertEquals(1, board.getColumnHeight(1));
		assertEquals(0, board.getColumnHeight(0));
		res = board.place(pyr4, 0, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		numRows = board.clearRows();
		assertEquals(1, numRows);
		board.commit();
		assertEquals(3, board.getRowWidth(0));
		assertEquals(1, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(2, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(2));
		res = board.place(pyr3, 0, 0);
		assertEquals(Board.PLACE_BAD, res);
		board.undo();
		assertEquals(3, board.getRowWidth(0));
		assertEquals(1, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(2, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(2));
		res = board.place(pyr3, 1, 1);
		assertEquals(Board.PLACE_OK, res);
		assertEquals(3, board.getMaxHeight());
		assertEquals(3, board.getColumnHeight(1));
		assertEquals(3, board.getColumnHeight(2));
		assertEquals(3, board.getColumnHeight(3));
		assertEquals(3, board.getRowWidth(0));
		assertEquals(2, board.getRowWidth(1));
		assertEquals(3, board.getRowWidth(2));
		board.undo();
		assertEquals(3, board.getRowWidth(0));
		assertEquals(1, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(2, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(2));
		res = board.place(pyr3, 1, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		assertEquals(4, board.getRowWidth(0));
		assertEquals(4, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(2, board.getMaxHeight());
		assertEquals(2, board.getColumnHeight(2));
		board.clearRows();
		assertEquals(0, board.getRowWidth(0));
		assertEquals(0, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(0, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(0));
		assertEquals(0, board.getColumnHeight(1));
		assertEquals(0, board.getColumnHeight(2));
		assertEquals(0, board.getColumnHeight(3));
	}

	@Test
	public void test2() {
		board = new Board(5, 10);
		board.place(pyr2, 0, 0);
		board.commit();
		board.place(new Piece(Piece.STICK_STR), 2, 0);
		board.commit();
		int res = board.place(new Piece(Piece.SQUARE_STR), 3, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		assertEquals(4, board.getMaxHeight());
		board.clearRows();
		assertEquals(3, board.getMaxHeight());
		board.commit();
		res = board.place(new Piece(Piece.L2_STR).computeNextRotation().computeNextRotation(), 0, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		board.clearRows();
		board.commit();
		res = board.place(new Piece(Piece.SQUARE_STR), 3, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		int numRows = board.clearRows();
		assertEquals(2, numRows);
		assertEquals(0, board.getRowWidth(0));
		assertEquals(0, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(0, board.getRowWidth(3));
		assertEquals(0, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(0));
		assertEquals(0, board.getColumnHeight(1));
		assertEquals(0, board.getColumnHeight(2));
		assertEquals(0, board.getColumnHeight(3));
		assertEquals(0, board.getColumnHeight(4));
	}

	@Test
	public void test3() {
		board = new Board(4, 8);
		board.place(new Piece(Piece.L2_STR), 1, 0);
		board.commit();
		int res = board.place(pyr4, 1, 2);
		assertEquals(Board.PLACE_OK, res);
		board.commit();
		int h = board.dropHeight(pyr2, 2);
		assertEquals(3, h);
		h = board.dropHeight(s, 0);
		assertEquals(5, h);
		Piece Lrr = new Piece(Piece.L1_STR).computeNextRotation().computeNextRotation();
		h = board.dropHeight(Lrr, 2);
		assertEquals(2, h);
		board.place(Lrr, 2, 2);
		board.commit();
		res = board.place(new Piece(Piece.STICK_STR), 0, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		board.clearRows();
		assertEquals(3, board.getMaxHeight());
		assertEquals(2, board.getRowWidth(1));
		board.commit();
		board.commit();
		res = board.place(pyr4, 0, 2);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		board.clearRows();
		assertEquals(4, board.getMaxHeight());
		board.commit();
		assertEquals(0, board.dropHeight(Lrr, 2));
		res = board.place(Lrr, 2, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		board.clearRows();
		assertEquals(2, board.getMaxHeight());
		assertEquals(3, board.getRowWidth(0));
		board.commit();
		board.undo();
		assertEquals(2, board.getMaxHeight());
		assertEquals(3, board.getRowWidth(0));
		res = board.place(Lrr.computeNextRotation(), 1, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		assertEquals(2, board.clearRows());
		assertEquals(0, board.getRowWidth(0));
		assertEquals(0, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(0, board.getMaxHeight());
		assertEquals(0, board.getColumnHeight(0));
		assertEquals(0, board.getColumnHeight(1));
		assertEquals(0, board.getColumnHeight(2));
		assertEquals(0, board.getColumnHeight(3));
		board.undo();
		assertEquals(3, board.getRowWidth(0));
		assertEquals(1, board.getRowWidth(1));
		assertEquals(0, board.getRowWidth(2));
		assertEquals(2, board.getMaxHeight());
		assertEquals(2, board.getColumnHeight(0));
		assertEquals(0, board.getColumnHeight(1));
		assertEquals(1, board.getColumnHeight(2));
		assertEquals(1, board.getColumnHeight(3));
	}


	@Test
	public void test4() {
		board = new Board(10, 24);
		Piece p = new Piece("0 0  1 0  1 1  2 1");
		int res = board.place(p, 3, 22);
		assertEquals(0, res);
		board.undo();
		res = board.place(p, 3, 21);
		assertEquals(0, res);
		board.undo();
		res = board.place(p, 3, 20);
		assertEquals(0, res);
		board.undo();
		res = board.place(p, 3, 19);
		assertEquals(0, res);
		board.undo();
		res = board.place(p, 3, 18);
		assertEquals(0, res);
		board.undo();
		res = board.place(p, 3, 17);
		assertEquals(0, res);
		board.undo();

	}

}
