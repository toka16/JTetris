package tetris;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;

	@Before
	public void setUp() throws Exception {
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
	}
	
	// Here are some sample tests to get you started
	
	@Test
	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}
	
	
	// Test the skirt returned by a few pieces
	@Test
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
		
		Piece l = new Piece(Piece.STICK_STR);
		assertTrue(Arrays.equals(new int[] {0}, l.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, l.computeNextRotation().getSkirt()));
		assertTrue(pyr1.equals(pyr4.computeNextRotation()));
		assertTrue(Arrays.equals(pyr1.getSkirt(), pyr4.computeNextRotation().getSkirt()));
		assertTrue(pyr1.equals(pyr1));
		assertFalse(pyr1.equals(new Object()));
	}
	
	@Test
	public void testS(){
		Piece s1 = new Piece(Piece.S1_STR);
		Piece s2 = new Piece(Piece.S2_STR);
		assertEquals(3, s1.getWidth());
		assertEquals(3, s2.getWidth());
		assertEquals(2, s1.getHeight());
		assertEquals(2, s2.getHeight());
		assertEquals(2, s1.computeNextRotation().getWidth());
		assertEquals(3, s1.computeNextRotation().getHeight());
		assertEquals(2, s2.computeNextRotation().getWidth());
		assertEquals(3, s2.computeNextRotation().getHeight());
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 0}, s2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, s1.computeNextRotation().getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 1}, s2.computeNextRotation().getSkirt()));
		
		Piece s1r = new Piece("0 1  0 2  1 0  1 1");
		assertTrue(s1r.equals(s1.computeNextRotation()));
		
		Piece s1FromPieces = Piece.getPieces()[Piece.S1];
		assertTrue(s1.equals(s1FromPieces));
		assertTrue(s1r.equals(s1FromPieces.fastRotation()));
	}
	
	@Test
	public void testL(){
		Piece l1 = new Piece(Piece.L1_STR);
		Piece l2 = new Piece(Piece.L2_STR);
		assertEquals(2, l1.getWidth());
		assertEquals(2, l2.getWidth());
		assertEquals(3, l1.getHeight());
		assertEquals(3, l2.getHeight());
		assertEquals(3, l1.computeNextRotation().getWidth());
		assertEquals(2, l1.computeNextRotation().getHeight());
		assertEquals(3, l2.computeNextRotation().getWidth());
		assertEquals(2, l2.computeNextRotation().getHeight());

		assertTrue(Arrays.equals(new int[] {0, 0}, l1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0}, l2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, l1.computeNextRotation().getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 1, 0}, l2.computeNextRotation().getSkirt()));
		
		Piece l1FromPieces = Piece.getPieces()[Piece.L1];
		assertTrue(l1.equals(l1FromPieces));
		assertTrue(l1FromPieces.fastRotation().equals(l1.computeNextRotation()));
	}
	
	
	@Test
	public void testStick(){
		Piece stick = new Piece(Piece.STICK_STR);
		assertEquals(1, stick.getWidth());
		assertEquals(4, stick.getHeight());
		assertTrue(stick.equals(stick.computeNextRotation().computeNextRotation()));
		assertTrue(stick.equals(Piece.getPieces()[Piece.STICK]));
		assertTrue(stick.computeNextRotation().equals(Piece.getPieces()[Piece.STICK].fastRotation()));
	}
	
	@Test
	public void testSquare(){
		Piece s = new Piece(Piece.SQUARE_STR);
		assertEquals(2, s.getWidth());
		assertEquals(2, s.getHeight());
		assertTrue(s.equals(s.computeNextRotation()));
		assertTrue(s.equals(Piece.getPieces()[Piece.SQUARE].fastRotation()));
		assertTrue(Arrays.equals(new int[] {0, 0}, s.getSkirt()));
	}
	
	@Test
	public void testPyramid(){
		Piece p = new Piece(Piece.PYRAMID_STR);
		assertTrue(p.equals(Piece.getPieces()[Piece.PYRAMID]));
		Piece pr = p.computeNextRotation();
		assertEquals(2, pr.getWidth());
		assertEquals(3, pr.getHeight());
		assertTrue(Arrays.equals(new int[] {1, 0}, pr.getSkirt()));
		assertTrue(pr.equals(p.computeNextRotation()));
		assertTrue(pr.equals(Piece.getPieces()[Piece.PYRAMID].computeNextRotation()));
	}
	
}
