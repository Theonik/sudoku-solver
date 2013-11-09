package su.kininarima.sudoku.main;
/** This class represents a single Cell in the SudokuBoard data-structure. It stores useful information about that Cell
 * that is used to solve puzzles
 * @author Theodore Nikopoulos-Exintaris thn2@aber.ac.uk
 *
 */
public class Cell {
	private boolean[] pencilmarks; //Stores possible values for a cell
	private static final boolean defaultValue = true; //default pencilmark value
	private static final byte possibilities = 9; //stores the number of possibilities for this cell
	private static final byte subgridSize = 3; //stores the default size for square subgrids
	private byte counter; //stores the number of pencilmarks left to examine
	private byte value; //stores the value of the cell if it is known
	private final byte xPos, yPos, subgrid; // stores the x-position, y-position, and subgrid adress of a cell repectively
	
	/** Constructor for empty Cell **/
	public Cell(byte x, byte y) {
		counter = possibilities;
		value = 0;
		xPos = x;
		yPos = y;
		subgrid = findNormalSubgrid();
		pencilmarks = new boolean[possibilities+1];
		for(int i =0; i<=possibilities; i++){
			pencilmarks[i] = defaultValue;
		}

	}

	/** Constructor for cell with set value **/
	public Cell( byte x, byte y, byte v){
		counter = 1;
		value = v;
		xPos = x;
		yPos = y;
		subgrid = findNormalSubgrid();
		pencilmarks = new boolean[possibilities+1];
		for(byte i = 0; i<=possibilities; i++){
			pencilmarks[i] = false;
		}
		pencilmarks[v] = true;
	}

	/** Returns the pencilmarks as a byte array of correct length in order **/
	public byte[] getPencimarks(){
		byte[] p = new byte[counter];
		if (!pencilmarks[0]){
			p[0]= value;
			return p;
		}
		byte j = 0;
		for (byte i=1; i<=possibilities; i++){
			if (pencilmarks[i]) {
				p[j] = i;
				j++;
			}
		}
		return p;
	}
	
	/** Returns a pencilmark as a string if the pencilmark is present otherwise returns space **/
	public String getPencilmarkToString(byte n) {
		if (pencilmarks[n]) {
			return Byte.toString(n);
		}
		return " ";
	}
	
	/** Returns true if a cell is possible to be a value **/
	public boolean canBe(byte p) {
		return pencilmarks[p];
	}

	/** Eliminates a single possibility for the value of the cell. If there is only one possible answer it sets the field as solved.
	 * Returns true if a possibility was actually eliminated. **/
	public boolean eliminatePossibility(byte p){
		if (pencilmarks[p]){
			pencilmarks[p] = false;
			if(counter == 2) {
				pencilmarks[0] = false;
				for (byte i = 1;i<=possibilities;i++){
					if(pencilmarks[i]){
						value = i;
						break;
					}
				}
			}
			counter--;
			return true;
		}
			return false;
	}

	/** Gets the counter for possible values for this cell **/
	public byte getCounter() {
		return counter;
	}

	/** Returns true if the cell is empty **/
	public boolean isEmpty() {
		return pencilmarks[0];
	}

	/** Returns the value stored in the cell **/
	public byte getValue() {
		return value;
	}

	/** Returns the x position of the cell **/
	public byte getXPos(){
		return xPos;
	}

	/** Returns the y position of the cell **/
	public byte getYPos(){
		return yPos;
	}
	
	/** Computes the subgrid a cell is in based on its address **/
	private byte findNormalSubgrid(){
		byte hPos, modifier, grid;		
        hPos = (byte)((xPos / subgridSize)+1);
        modifier = (byte)(yPos / subgridSize);
		grid = (byte)((hPos) + (modifier*subgridSize));
		return grid;
	}
	
	/** Returns the subgrid a cell is in **/
	public byte getSubgrid(){
		return subgrid;
	}
}

