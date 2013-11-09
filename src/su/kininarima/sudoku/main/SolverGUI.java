

package su.kininarima.sudoku.main;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


/** This class is used to create a GUI for the application
 * 
 * @author Theodore Nikopoulos-Exintaris thn2@aber.ac.uk
 * 
 **/
public class SolverGUI extends javax.swing.JFrame implements Runnable {
	private static final long serialVersionUID = -1364680393308556648L;
	private final String newLine = System.getProperty("line.separator"); //OS agnostic linebreak
	private JFileChooser fileChooser; //swing stuff starts here
    private JMenu fileMenu, solveMenu;
    private JMenuBar jMenuBar1;
    private JPopupMenu.Separator jSeparator1;
    private JMenuItem loadButton, saveButton, startButton, stopButton, exitButton;
    private JPanel sudokuContainer;
    private JTextArea textArea;
	private FileNameExtensionFilter lFilter; // swing stuff ends here
	private boolean isSolving = false; //flag used by the pause button
    private SudokuBoard suBoard; //stores a pointer to the sudoku board
    private Display sudokuRender; //used to get the text for the sudoku display
    private Solver solverThread;  //thread where the sudoku is solved
    private Thread updater; //thread that updates GUI elements
    private File loadedFile; //stores the current file
    
    /** Constructor for the class **/
    public SolverGUI() {
    	suBoard = new SudokuBoard();
    	sudokuRender = new Display(suBoard);
        initComponents();
    }

    /** This method is called from within the constructor to initialize the form. **/  
    private void initComponents() {

        fileChooser = new JFileChooser();
        sudokuContainer = new JPanel();
        jMenuBar1 = new JMenuBar();
        fileMenu = new JMenu();
        saveButton = new JMenuItem();
        loadButton = new JMenuItem();
        jSeparator1 = new JPopupMenu.Separator();
        exitButton = new JMenuItem();
        solveMenu = new JMenu();
        startButton = new JMenuItem();
        stopButton = new JMenuItem();
	    lFilter = new FileNameExtensionFilter(
	            "Sudoku Files (*.txt;*.sud)", "txt", "sud");
	    textArea = new JTextArea(sudokuRender.getText());
        sudokuContainer.add(textArea);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); //taken from http://stackoverflow.com/q/11020978
        textArea.setEditable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sudoku Solver Ver. GUI");
        setName("solverFrame");  
        add(sudokuContainer, BorderLayout.CENTER);

        fileMenu.setText("File");
        saveButton.setEnabled(false);
        saveButton.setText("Save Sudoku");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        fileMenu.add(saveButton);

        loadButton.setText("Load Sudoku");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        fileMenu.add(loadButton);
        fileMenu.add(jSeparator1);

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        fileMenu.add(exitButton);

        jMenuBar1.add(fileMenu);

        solveMenu.setText("Solve");

        startButton.setText("Start Solution");
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        solveMenu.add(startButton);

        stopButton.setText("Reload Puzzle");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        solveMenu.add(stopButton);

        jMenuBar1.add(solveMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }

/** Handles presses of the save menu item **/
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	fileChooser.setFileFilter(lFilter);
    	int returnVal = fileChooser.showSaveDialog(SolverGUI.this);

    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		loadedFile = fileChooser.getSelectedFile();
    		FileIO saver = new FileIO(loadedFile);
    		this.setTitle("Sudoku Solver Ver. GUI - " + loadedFile.getName());
    		try {
				saver.saveSudoku(suBoard);
			} 
    		catch (FileNotFoundException e) {
				
			}
    	}
    }

    /** Handles presses of the load menu item **/
    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	fileChooser.setFileFilter(lFilter);
    	int returnVal = fileChooser.showOpenDialog(SolverGUI.this);
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		loadedFile = fileChooser.getSelectedFile();
    		FileIO loader = new FileIO(loadedFile);
    		try {
    			suBoard = loader.loadSudoku();
    			saveButton.setEnabled(true);
    			startButton.setEnabled(true);
    			stopButton.setEnabled(true);
    			this.setTitle("Sudoku Solver Ver. GUI - " + loadedFile.getName());
    			textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    			sudokuRender = new Display(suBoard);
    			textArea.setText(sudokuRender.getText());
    		} 
    		catch (FileNotFoundException e) {
    			JOptionPane.showMessageDialog(this,
    					"The file " + loadedFile.getName() + " could not be read." + newLine +
    					"Check that the file location and permissions are set correctly.",
    					"ERROR: File Not Found",
    					JOptionPane.ERROR_MESSAGE);
    		}
    		catch (ArrayIndexOutOfBoundsException ee) {
    			JOptionPane.showMessageDialog(this,
    					"The file " + loadedFile.getName() + " could not be read." + newLine +
    					"Please consult with the origin of the file to ",
    					"ERROR: File Corruption Error",
    					JOptionPane.ERROR_MESSAGE);
    		}
    	}
    }

    /** Handles presses of the exit menu item **/
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)); //ensures the threads die with the window.
    }

    /** Handles presses of the start menu item **/
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	if (!isSolving) {
    		solverThread = new Solver(suBoard);
    		updater = new Thread(this);
    		solverThread.setHalt(false);
    		loadButton.setEnabled(false);
    		saveButton.setEnabled(false);
    		stopButton.setEnabled(false);
    		updater = new Thread(this);
    		startButton.setText("Pause/Display Solution");
    		isSolving = true;
    		solverThread.start();
    		updater.start();
    	}
    	else {
    		pauseSolver();
    	}
    }
    private void pauseSolver() {
		solverThread.setHalt(true);
		loadButton.setEnabled(true);
		saveButton.setEnabled(true);
		stopButton.setEnabled(true);
		textArea.setText(sudokuRender.getText());
		startButton.setText("Start Solution");
		isSolving = false;
    }

    /** Handles presses of the reload menu item **/
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	FileIO loader = new FileIO(loadedFile);
		try {
			suBoard = loader.loadSudoku();
			textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			sudokuRender = new Display(suBoard);
			startButton.setEnabled(true);
			textArea.setText(sudokuRender.getText());
		} 
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this,
					"The file " + loadedFile.getName() + " could not be read." + newLine +
					"Check that the file location and permissions are set correctly.",
					"ERROR: File Not Found",
					JOptionPane.ERROR_MESSAGE);
		}
		catch (ArrayIndexOutOfBoundsException ee) {
			JOptionPane.showMessageDialog(this,
					"The file " + loadedFile.getName() + " could not be read." + newLine +
					"Please consult with the origin of the file to ",
					"ERROR: File Corruption Error",
					JOptionPane.ERROR_MESSAGE);
		}
    }

    /** Thread to manage the GUI **/
	@Override
	public void run() {
		while (solverThread.isAlive()&& this.isVisible()) {
		}
		pauseSolver();
		startButton.setEnabled(false);
		if (suBoard.isSolved()) {
       	JOptionPane.showMessageDialog(this, 
       		    "The Puzzle " + loadedFile.getName() + " was solved succesfully in " + solverThread.getTimeElapsed()*1E-6 + "ms.",
       		    "Success!",
       		    JOptionPane.PLAIN_MESSAGE);;
		}
		else {
       	JOptionPane.showMessageDialog(this, 
       		    "The Puzzle " + loadedFile.getName() + " could not be solved after " + solverThread.getAttempts() + " attempts.",
       		    "Deep regret.",
       		    JOptionPane.PLAIN_MESSAGE);;
		}
	}

}
