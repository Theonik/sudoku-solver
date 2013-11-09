package su.kininarima.sudoku.main;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

public class Solver extends Thread{
	private SudokuBoard suBoard;
	private boolean isEffective; // Flag that determines if there is a point to doing another pass
	private boolean halt; // allows for the solution to be paused
	private static final byte possibilities = 9; //number of possible values in a cell
	private static final byte rows = 9; //number of rows in a board
	private static final byte columns = 9; //number of columns in a board
	private static final byte subgrids = 9; //number of subgrids in a board
	private static final short maxAttempts = 250; //maximum allowed attempts to solve a puzzle
	private short attempts; //number of attempts
	private long timeElapsed; //time to solve in nanoseconds
	public Solver(SudokuBoard sb) {
		suBoard =sb;
	}

	/** Returns the time taken to in the last execution of the solver in nanoseconds **/
	public long getTimeElapsed() {
		return timeElapsed;
	}
	
	public short getAttempts() {
		return attempts;
	}

	/** Sets halt to allow the thread to be terminated **/
	public void setHalt(boolean h) {
		halt = h;
	}

	/** Eliminates pencilmarks for every empty cell based on the occupied cells that are sudoku neighbours. **/
	private void singleElimination(){
		ArrayBlockingQueue<Cell> emptyCells = suBoard.getAllEmptyCells();
		while (!emptyCells.isEmpty()) {
			Cell current = emptyCells.poll();
			ArrayBlockingQueue<Cell> neighbours = suBoard.getNeighbourFullCells(current.getXPos(), current.getYPos());
			while (!neighbours.isEmpty()){				
				if (current.eliminatePossibility(neighbours.poll().getValue())){ // Tries to eliminate a value from the cell. If at least 1 possibility has been eliminated in this pass the flag is set to true.
					isEffective = true;
				}
			}
		}
	}

	/** Implementation of single position **/
	private void singlePosition() {
		byte counter;
		Cell candidate = null;
		ArrayBlockingQueue<Cell> emptyCollection;
		for (byte value =  1; value <= possibilities ; value++) { 
			//go through each possible value until the maximum value is reached
			for (byte row = 0; row > rows; row++) { 
				//for each of those values go though every column of the cell
				counter = 0;
				emptyCollection = suBoard.getHorizontalCells(row);
				while(!emptyCollection.isEmpty()){
					Cell cCell = emptyCollection.poll();
					if (cCell.canBe(value)) { 
						candidate = cCell; //store a cell containing that value 
						counter++; //increment the occurrences of that value.
					}
				}
				if (counter==1) { 
					//if only one cell was found change the value of the current cell to the value being evaluated
					//suBoard.setBlock(candidate.getXPos(), candidate.getYPos(), value);
					for (byte pMark =1; pMark<=possibilities; pMark++) {
						if (pMark != value) {
							candidate.eliminatePossibility(pMark);
						}
					}
				} 
			}
			for (byte column = 0; column < columns; column++) { //repeat for each column
				counter = 0;
				emptyCollection = suBoard.getVerticalCells(column);
				while(!emptyCollection.isEmpty()){
					Cell cCell = emptyCollection.poll();
					if (cCell.canBe(value)) {
						candidate = cCell;
						counter++;
					}
				}
				if (counter==1) {
					//suBoard.setBlock(candidate.getXPos(), candidate.getYPos(), value);
					for (byte pMark =1; pMark<=possibilities; pMark++) {
						if (pMark != value) {
							candidate.eliminatePossibility(pMark);
						}
					}
				}
			}
			for (byte subgrid = 1; subgrid <= subgrids; subgrid++) { //repeat for each subgrid
				counter = 0;
				emptyCollection = suBoard.getSubgridCells(subgrid);
				while(!emptyCollection.isEmpty()){
					Cell cCell = emptyCollection.poll();
					if (cCell.canBe(value)) {
						candidate = cCell;
						counter++;
					}
				}
				if (counter==1) {
					//suBoard.setBlock(candidate.getXPos(), candidate.getYPos(), value);
					for (byte pMark =1; pMark<=possibilities; pMark++) {
						if (pMark != value) {
							candidate.eliminatePossibility(pMark);
						}
					}
				} 
			} 
		}
	}


	/** Checks and eliminates naked triples to naked sextuples. **/
	private void superPairs() {
		final byte lowerBound = 2; //highest number to be ignored
		final byte upperBound = 7; //lowest number to be ignored
		ArrayBlockingQueue<Cell> emptyCells = suBoard.getAllEmptyCells();
		while (!emptyCells.isEmpty()) { //go through all the empty cells in the Board
			Cell current = emptyCells.poll();
			if (current.getCounter()>lowerBound&&current.getCounter()<upperBound) { //boundary condition to ignore some Cells
				Cell[] pairCandidates = new Cell[current.getCounter()];
				byte pairCount = 0; //initialise data for rows
				ArrayBlockingQueue<Cell> workCells = 
						suBoard.getSameHorizontalEmptyCells(current.getXPos(), current.getYPos());
				ArrayBlockingQueue<Cell> deleteCandidates = 
						suBoard.getSameHorizontalEmptyCells(current.getXPos(), current.getYPos());
				for (byte i=0; i<3; i++) { //repeat 3 times once for rows, columns, or subgrid.
					while (!workCells.isEmpty()) { //go through the neighboring cells
						Cell candidate = workCells.poll();
						if (candidate.getCounter()<=current.getCounter()){
							byte[] currentPencilmarks = current.getPencimarks();
							byte[] candidatePencilmarks = candidate.getPencimarks();
							if (arrayContains(currentPencilmarks, candidatePencilmarks)){ 
								pairCandidates[pairCount] = candidate;
								pairCount++; //collect and count all cells that can be part of the chain
							}
						}
					}
					if ((pairCount+1) == current.getCounter()) { //condition for the chain being valid
						while (!deleteCandidates.isEmpty()) { //go though the data again this time eliminating pencilmarks
							Cell deleteCandidate = deleteCandidates.poll();
							if (!arrayContainsSingle(pairCandidates, deleteCandidate)&&deleteCandidate != current) {
								//skip any cell that is part of the chain
								for (byte pMark = 0; pMark<=pairCount; pMark ++) {
									deleteCandidate.eliminatePossibility(current.getPencimarks()[pMark]);
								}
							}
						}
					}
					if (i==0) { //get data for columns
						pairCandidates = new Cell[current.getCounter()];
						pairCount = 0;
						workCells = 
								suBoard.getSameVerticalEmptyCells(current.getXPos(), current.getYPos());
						deleteCandidates = 
								suBoard.getSameVerticalEmptyCells(current.getXPos(), current.getYPos());
					}
					else if (i==1) { //get data for subgrid
						pairCandidates = new Cell[current.getCounter()];
						pairCount = 0;
						workCells = 
								suBoard.getSameSubgridEmptyCells(current.getXPos(), current.getYPos());
						deleteCandidates = 
								suBoard.getSameSubgridEmptyCells(current.getXPos(), current.getYPos());
					}
				}
			}
		}
	}

	/** Implementation of the naked pair algorithm **/
	private void nakedPairs() {
		ArrayBlockingQueue<Cell> emptyCells = suBoard.getAllEmptyCells();
		while (!emptyCells.isEmpty()) {
			Cell current = emptyCells.poll();
			if (current.getCounter() == 2){
				ArrayBlockingQueue<Cell> workCells = 
						suBoard.getSameHorizontalEmptyCells(current.getXPos(),
						current.getYPos()); //get all emptycells that are not it and are not on the same line.
				ArrayBlockingQueue<Cell> deathRow = 
						suBoard.getSameHorizontalEmptyCells(current.getXPos(),
						current.getYPos()); //get the same cells for the case a pair is found
				for (byte i=0; i<3; i++) { //repeat 3 times once for rows once for columns and once for subgrids.
					cell:
						while (!workCells.isEmpty()) {
							Cell candidate = workCells.poll();
							boolean areEqual = Arrays.equals(current.getPencimarks(),
									candidate.getPencimarks()); //determines if the two arrays have the same values
							if (areEqual) {
								while (!deathRow.isEmpty()) {
									Cell accused = deathRow.poll();
									if ((accused!=current)&&(accused!=candidate)) { 
										//Removes pencilmarks associated with the pair if not part of the pair
										for (byte pMark = 0; pMark<2; pMark++) {
											accused.eliminatePossibility(current.getPencimarks()[pMark]);
										}
									}
								}
								break cell; //There cannot be more another counterpart on that line so we move on.
							}
						}
				if (i==0) { //replace the data with rows
					workCells = suBoard.getSameVerticalEmptyCells(current.getXPos(),
							current.getYPos());
					deathRow = suBoard.getSameVerticalEmptyCells(current.getXPos(),
							current.getYPos());
				}
				else if (i==1) { // replace the data with subgrids
					workCells = suBoard.getSameSubgridEmptyCells(current.getXPos(),
							current.getYPos());
					deathRow = suBoard.getSameSubgridEmptyCells(current.getXPos(),
							current.getYPos());
				}
				}
			}
		}
	}

	/** Compares 2 arrays and returns true if all elements of the inner array are contained in the larger array**/
	private boolean arrayContains(byte[] outer,byte[] inner) {
		for (int i = 0; i < inner.length; i++) {
			if (!arrayContainsSingle(outer, inner[i])) {
				return false;
			}
		}
		return true;
	}

	/** Searches for a single element in an array of Cell and @Returns returns true if the element is part of the array. **/
	private boolean arrayContainsSingle(Cell[] array, Cell inner) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == inner){
				return true;
			}
		}
		return false;
	}

	/** Searches for a single element in an array of byte and returns true if it's there **/
	private boolean arrayContainsSingle(byte[] array, byte inner) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == (inner)){
				return true;
			}
		}
		return false;
	}

	/** This run method manages solving the puzzles **/
	@Override
	public void run() {
		isEffective = true;
		attempts = 0;
		long startTime = System.nanoTime();
		solve:
			while (!suBoard.isSolved()&&!halt&&attempts<maxAttempts) { 
				while (isEffective) { //checks if the state of the board has changed
					isEffective = false;
					singleElimination();
					if (suBoard.isSolved()) {
						break solve;
					}
				}
				singlePosition();
				singleElimination();
				if (suBoard.isSolved()) {
					break solve;
				}
				nakedPairs();
				singleElimination();
				if (suBoard.isSolved()) {
					break solve;
				}
				superPairs();
				isEffective = true;
				attempts++;
			}
		long endTime = System.nanoTime();
		timeElapsed = endTime-startTime; //calculate time taken to solve
	}
}
