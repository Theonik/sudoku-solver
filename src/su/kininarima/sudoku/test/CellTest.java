package su.kininarima.sudoku.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import su.kininarima.sudoku.main.Cell;

public class CellTest {

	static Cell emptyCell;
	static Cell loadedCell;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		emptyCell = new Cell((byte)0,(byte)0);
		loadedCell = new Cell((byte)3,(byte)3,(byte)9);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPencilmarks() {
		byte[] cc = {9};
		assertArrayEquals("Should return an array with a single element 9.", cc, loadedCell.getPencimarks());
		byte[] c = {1,2,3,4,5,6,7,8,9};
		assertArrayEquals("Should return an array of numbers 1-9.", c, emptyCell.getPencimarks());
	}
	
	@Test
	public void testEliminatePossibility() {
		emptyCell.eliminatePossibility((byte)9);
		byte[] c = {1,2,3,4,5,6,7,8};
		assertArrayEquals("Should return an array of numbers 1-8.", c, emptyCell.getPencimarks());
		assertEquals("Counter should be down by 1.", 8, emptyCell.getCounter());
		emptyCell.eliminatePossibility((byte)9);
		assertEquals("Counter should be unchanged.", 8, emptyCell.getCounter());
		emptyCell.eliminatePossibility((byte)1);
		byte[] cc = {2,3,4,5,6,7,8};
		assertArrayEquals("Should return an array of numbers 2-8.", cc, emptyCell.getPencimarks());
		assertEquals("Counter should be down by 1.", 7, emptyCell.getCounter());
		assertEquals("isEmpty should return true", true, emptyCell.isEmpty());
		for (byte i = 3; i<9; i++){
			emptyCell.eliminatePossibility(i);
		}
		byte[] ccc = {2};
		assertEquals("Value should be equal to 2.", (byte)2, emptyCell.getValue());
		assertArrayEquals("Should return an array of single number 2.", ccc, emptyCell.getPencimarks());
		assertEquals("The counter should be 1.", (byte)1, emptyCell.getCounter());
		assertEquals("isEmpty should return false", false, emptyCell.isEmpty());
	}
	
	@Test
	public void testEmptyFlag(){
		assertEquals("Empty cell should return true for isEmpty.", true, emptyCell.isEmpty());
		assertEquals("Empty cell should return false for isEmpty.", false, loadedCell.isEmpty());
	}
	
	@Test
	public void testAdressing(){
		assertEquals("emptyCell must be in subgrid 1", (byte)1, emptyCell.getSubgrid());
		assertEquals("loadedCell must be in subgrid 5", (byte)5, loadedCell.getSubgrid());
		emptyCell = new Cell((byte)8,(byte)8);
		assertEquals("emptyCell must be in subgrid 9", (byte)9, emptyCell.getSubgrid());
	}

}
