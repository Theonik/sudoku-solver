package su.kininarima.sudoku.test;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import su.kininarima.sudoku.main.SudokuBoard;

public class BoardTest {

	static SudokuBoard testBoard = new SudokuBoard();
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testBoard.setBlock((byte)0, (byte)0, (byte)9);
		testBoard.setBlock((byte)1, (byte)1, (byte)8);
		testBoard.setBlock((byte)0, (byte)3, (byte)7);
		testBoard.setBlock((byte)3, (byte)0, (byte)6);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSetBlock() {
		assertEquals("Block 1,1 should have a value of 0", 0, testBoard.getBlock((byte)0,(byte)1));
		assertEquals("Block 1,1 should have a value of 8", 8, testBoard.getBlock((byte)1,(byte)1));
	}
	
	@Test
	public void testPencilmarkToString () {
		assertEquals("Block 0,1 should have a value of '9'", "9", testBoard.getPencilmarkToString((byte)0,(byte)1,(byte)9));
		assertEquals("Block 1,1 should have a value of ' '", " ", testBoard.getPencilmarkToString((byte)1,(byte)1,(byte)7));
	}
	
	@Test
	public void testIsEmpty() {
		assertEquals("Should return true", true, testBoard.isEmpty((byte)0, (byte)1));
		assertEquals("Should return false", false, testBoard.isEmpty((byte)0, (byte)0));
	}
	
}
