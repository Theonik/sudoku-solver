package su.kininarima.sudoku.main;

import java.io.*;
import java.util.Scanner;
/**
 * This class handles IO for .sud files, it is instantiated with a target file and can either save or load a .sud file
 * @author Theodore Nikopoulos-Exintaris thn2@aber.ac.uk
 *
 */
public class FileIO {
	File currentFile;
	public FileIO(File f) {
		currentFile = f;
	}
	/** Loads a Sudoku file, and returns a SudokuBoard object. **/
	public SudokuBoard loadSudoku() throws FileNotFoundException, 
	ArrayIndexOutOfBoundsException {
		SudokuBoard boardLoader = new SudokuBoard();
		Scanner infile =new Scanner(new InputStreamReader
				(new FileInputStream(currentFile)));
		for (byte yCor = 0; yCor<9 ; yCor++){
			String line = infile.nextLine();
			for (byte xCor = 0 ; xCor < line.length() ; xCor++){
				char currentChar = line.charAt(xCor);
				if ((int)(currentChar)-48 > 0 && (int)(currentChar)-48 < 58) {
					boardLoader.setBlock(xCor, yCor, (byte)(currentChar-48));
				}
			}
		}
		infile.close();
		return boardLoader;
	}

	/** Saves Sudoku Board sb into the file provided in the constructor **/
	public void saveSudoku(SudokuBoard sb) throws FileNotFoundException {
		SudokuBoard boardSaver = sb;
		PrintWriter outfile = new PrintWriter(new OutputStreamWriter
				(new FileOutputStream(currentFile)));
		for (byte y = 0; y<9 ;y++){
			StringBuffer lineBuffer = new StringBuffer(9);			
			for (byte x = 0; x<9; x++){
				if (boardSaver.getBlock(x, y) == 0){
					lineBuffer.append(' ');
				}
				else {
					lineBuffer.append(boardSaver.getBlock(x, y));				
				}
			}
			outfile.println(lineBuffer);
		}
		outfile.close();
	}
}
