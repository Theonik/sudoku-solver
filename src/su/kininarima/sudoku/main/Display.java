package su.kininarima.sudoku.main;


/**
 * This class converts the data stored in the SudokuBoard into sensible string data.
 * 
 * @author Theodore Nikopoulos-Exintaris thn2@aber.ac.uk
 *
 */
public class Display {
	private final String newLine = System.getProperty("line.separator"); //OS agnostic linebreak
	private SudokuBoard suBoard;


	public Display(SudokuBoard sb) {
		suBoard = sb;
	}

/** This method produces a string version of the Board to display in GUI or in a terminal window **/
	public String getText() {
		StringBuffer cellBuffer = new StringBuffer();
		for (byte yCor = 0; yCor < 9; yCor++) {
			if (yCor == 3||yCor == 6) {
				cellBuffer.append("=================================================");
				cellBuffer.append(newLine);
			}
			else {
				cellBuffer.append("----------------+---------------+----------------");
				cellBuffer.append(newLine);
			}
			cellBuffer.append('|');
			for (byte xCor = 0 ; xCor < 9; xCor++) {
				if (xCor == 3||xCor == 6) {
					cellBuffer.append('|');
				}
				if (suBoard.isEmpty(xCor, yCor)) {
					cellBuffer.append("     ");
				}
				else {
					cellBuffer.append("  ");
					cellBuffer.append(suBoard.getBlock(xCor, yCor));
					cellBuffer.append("  ");
				}
			}
			cellBuffer.append("|");
			cellBuffer.append(newLine);
		}
		cellBuffer.append("-------------------------------------------------");
		return cellBuffer.toString();
	}

}
