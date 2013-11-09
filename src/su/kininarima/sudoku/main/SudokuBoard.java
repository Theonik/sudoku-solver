package su.kininarima.sudoku.main;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * This class stores and gives information about the sudoku board.
 * @author Theodore Nikopoulos-Exintaris
 *
 */
public class SudokuBoard {
	private static final byte ROWS = 9; //Row constant
	private static final byte COLUMNS = 9; //Column constant
	private static final byte SUBGRIDS = 9; //Subgrid constant
	private Cell[][] board;
	private byte emptyCount; //Stores the count of 'empty' cells

	public SudokuBoard() {
		board = new Cell[ROWS][COLUMNS];
		emptyCount = ROWS*COLUMNS;
		for (byte y=0;y<COLUMNS;y++){
			for (byte x=0;x<COLUMNS;x++){
				board[x][y] = new Cell(x,y);
			}
		}
	}

	/** Replaces a Cell x,y in the board with a new Cell with value v **/
	public void setBlock(byte x, byte y, byte v){
		board[x][y] = new Cell(x, y, v);
	}

	/** Gets the value of a specific Cell. Returns 0 if empty **/
	public byte getBlock(byte x, byte y){
		return board[x][y].getValue();
	}
	
	/** Returns a pencilmark to string if present for a Cell, returns " " if not present **/
	public String getPencilmarkToString(byte x, byte y, byte n){
		return board[x][y].getPencilmarkToString(n);
	}

	/**returns true if a cell is empty**/
	public boolean isEmpty(byte x, byte y){
		return board[x][y].isEmpty();
	}

	/** Gets a byte array of possibilities from a cell**/
	public byte[] getPossibilities(int x, int y){
		return board[x][y].getPencimarks();
	}

	/** Returns the number of possibilities for a cell **/
	public byte getPossibilityCount(byte x, byte y){
		return board[x][y].getCounter();
	}

	/** Eliminates a single possibility for a block **/
	public void eliminatePossibility(byte x, byte y, byte p) {
		board[x][y].eliminatePossibility(p);
	}
	
	/** Returns the number of cells that still need to be completed **/
	public byte getEmptyCount() {
		return emptyCount;
	}
	
	/** Returns true if the board is full **/
	public boolean isSolved() {
		if (emptyCount==0){
			return true;
		}
		return false;
	}

	/** Returns a queue containing all the empty cells in the board row first. **/
	public ArrayBlockingQueue<Cell> getAllEmptyCells(){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(emptyCount);
		byte c = 0;
		for (byte column = 0; column<COLUMNS;column++) {
			for (byte row = 0; row<ROWS; row++) {
				if (board[row][column].isEmpty()) {
					collector.add(board[row][column]);
					c++;
				}
			}
		}
		emptyCount = c;
		return collector;
	}

	/** Returns a queue containing the full cells that are Sudoku neighbours of a given cell **/
	public ArrayBlockingQueue<Cell> getNeighbourFullCells(byte x, byte y){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(81);
		for (byte i = 0; i<ROWS;i++) {
			if (!board[i][y].isEmpty()&&(i!=x)) {
				collector.add(board[i][y]);
			}
		}
		for (byte i = 0; i<COLUMNS;i++) {
			if (!board[x][i].isEmpty()&&(i!=y)) {
				collector.add(board[x][i]);
			}
		}
		for (byte i = 0; i<COLUMNS; i++){
			for (byte j = 0; j<ROWS; j++ ){
				if (!board[j][i].isEmpty()&&(j!=x&&i!=y)&&(board[x][y].getSubgrid()==board[j][i].getSubgrid())) {
					collector.add(board[j][i]);
				}
			}
		}
		return collector;
	}

	/** Returns a queue containing the empty cells that are Sudoku neighbours of a given cell **/
	public ArrayBlockingQueue<Cell> getNeighbourEmptyCells(byte x, byte y){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(emptyCount);
		for (byte i = 0; i<ROWS;i++) {
			if (board[i][y].isEmpty()&&(i!=x)) {
				collector.add(board[i][y]);
			}
		}
		for (byte i = 0; i<COLUMNS;i++) {
			if (board[x][i].isEmpty()&&(i!=y)) {
				collector.add(board[x][i]);
			}
		}
		for (byte i = 0; i<COLUMNS; i++){ //adds all cells in the same subgrid that aren't already on the grid at the end of the grid
			for (byte j = 0; j<ROWS; j++ ){
				if (board[j][i].isEmpty()&&(j!=x&&i!=y)&&(board[x][y].getSubgrid()==board[j][i].getSubgrid())) {
					collector.add(board[j][i]);
				}
			}
		}
		return collector;
	}
	
	/** Gets a queue with all the empty cells on the same line as a cell **/
	public ArrayBlockingQueue<Cell> getSameHorizontalEmptyCells(byte x, byte y){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(ROWS);
		for (byte i = 0; i<ROWS;i++) {
			if (board[i][y].isEmpty()&&(i!=x)) {
				collector.add(board[i][y]);
			}
		}
		return collector;
	}
	/** Gets a queue with all the cells on a line **/
	public ArrayBlockingQueue<Cell> getHorizontalCells(byte y){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(ROWS);
		for (byte i = 0; i<ROWS;i++) {
			//if (board[i][y].isEmpty()) {
				collector.add(board[i][y]);
			//}
		}
		return collector;
	}
	
	/** Gets a queue with all the empty cells on the same column as a cell **/
	public ArrayBlockingQueue<Cell> getSameVerticalEmptyCells(byte x, byte y){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(COLUMNS);
		for (byte i = 0; i<COLUMNS;i++) {
			if (board[x][i].isEmpty()&&(i!=y)) {
				collector.add(board[x][i]);
			}
		}
		return collector;
	}
	
	/** Gets a queue with all the empty cells on a column **/
	public ArrayBlockingQueue<Cell> getVerticalCells(byte x){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(COLUMNS);
		for (byte i = 0; i<COLUMNS;i++) {
			//if (board[x][i].isEmpty()) {
				collector.add(board[x][i]);
			//}
		}
		return collector;
	}
	
	/** @return returns a queue with all the empty cells on the same subgrid as a cell **/
	public ArrayBlockingQueue<Cell> getSameSubgridEmptyCells(byte x, byte y){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(SUBGRIDS);
		for (byte i = 0; i<COLUMNS; i++){ //adds all cells in the same subgrid that aren't already on the grid at the end of the grid
			for (byte j = 0; j<ROWS; j++ ){
				if (board[j][i].isEmpty()&&(j!=x&&i!=y)&&(board[x][y].getSubgrid()==board[j][i].getSubgrid())) {
					collector.add(board[j][i]);
				}
			}
		}
		return collector;
	}
	
	/** @return returns a queue of all the cells in a given subgrid @Param subgrid that is saught**/ 
	public ArrayBlockingQueue<Cell> getSubgridCells(byte subgrid){
		ArrayBlockingQueue<Cell> collector = new ArrayBlockingQueue<Cell>(SUBGRIDS);
		for (byte i = 0; i<COLUMNS; i++){ //adds all cells in the same subgrid that aren't already on the grid at the end of the grid
			for (byte j = 0; j<ROWS; j++ ){
				if (board[j][i].getSubgrid() == subgrid) {
					collector.add(board[j][i]);
				}
			}
		}
		return collector;
	}
}
