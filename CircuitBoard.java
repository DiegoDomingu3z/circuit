import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Represents a 2D circuit board as read from an input file.
 *  
 * @author mvail, Diego Dominguez
 */
public class CircuitBoard {
	/** current contents of the board */
	private char[][] board;
	/** location of row,col for '1' */
	private Point startingPoint;
	/** location of row,col for '2' */
	private Point endingPoint;

	//constants you may find useful
	private final int ROWS; //initialized in constructor
	private final int COLS; //initialized in constructor
	private final char OPEN = 'O';	//capital 'o', an open position
	private final char CLOSED = 'X';//a blocked position
	private final char TRACE = 'T';	//part of the trace connecting 1 to 2
	private final char START = '1';	//the starting component
	private final char END = '2';	//the ending component
	private final String ALLOWED_CHARS = "OXT12"; //useful for validating with indexOf

	/** Construct a CircuitBoard from a given board input file, where the first
	 * line contains the number of rows and columns as ints and each subsequent
	 * line is one row of characters representing the contents of that position.
	 * Valid characters are as follows:
	 *  'O' an open position
	 *  'X' an occupied, unavailable position
	 *  '1' first of two components needing to be connected
	 *  '2' second of two components needing to be connected
	 *  'T' is not expected in input files - represents part of the trace
	 *   connecting components 1 and 2 in the solution
	 * 
	 * @param filename
	 * 		file containing a grid of characters
	 * @throws FileNotFoundException if Scanner cannot open or read the file
	 * @throws InvalidFileFormatException for any file formatting or content issue
	 */
	public CircuitBoard(String filename) throws FileNotFoundException {
		// read file
		Scanner fileScan = new Scanner(new File(filename));

		// check first line if it has something
		if (!fileScan.hasNextLine()) {
			fileScan.close();
			throw new InvalidFileFormatException("File is empty");
		}
		// check if the specified dimensions are valid or not
		String[] firstLine = fileScan.nextLine().trim().split("\\s+");
		if (firstLine.length != 2) {
			fileScan.close();
			throw new InvalidFileFormatException("Invalid row and column specification");
		}
		try {
			ROWS = Integer.parseInt(firstLine[0]);
			COLS = Integer.parseInt(firstLine[1]);
		} catch (NumberFormatException e) {
			fileScan.close();
			throw new InvalidFileFormatException("Invalid format");
		}
		// making arr for board
		board = new char[ROWS][COLS];
		int primary = 0; 
		int secondary = 0; 
		// loop through rows
		for (int i = 0; i < ROWS; i++) {
			if (!fileScan.hasNextLine()) {
				fileScan.close();
				throw new InvalidFileFormatException("rows is not the same, should be: " + ROWS);
			}
			String line = fileScan.nextLine().replaceAll("\\s+", "");
			if (line.length() != COLS) {
				fileScan.close();
				throw new InvalidFileFormatException("column on row" + i + " is not invalid, should be: " + COLS);
			}
			// loop characters
			for (int c = 0; c < COLS; c++) {
				char value = line.charAt(c);

				if (ALLOWED_CHARS.indexOf(value) == -1) {
					fileScan.close();
					throw new InvalidFileFormatException("Invalid character '" + value + "' at (" + i + ", " + c + ")");
				}
				// add to board arr
				board[i][c] = value;
				// check starting point
				if (value == START) {
					if (primary > 0) { 
						fileScan.close();
						throw new InvalidFileFormatException("more than one starting point");
					}
					startingPoint = new Point(i, c);
					// add to primary count (1)
					primary++;
				} else if (value == END) {
					if (secondary > 0) { 
						fileScan.close();
						throw new InvalidFileFormatException("more then one end");
					}
					endingPoint = new Point(i, c);
					// add to primary count (1)
					secondary++;
				}
			}
		}
	if (fileScan.hasNextLine()) {
		fileScan.close();
		throw new InvalidFileFormatException("Too many rows. Expected " + ROWS);
	}
	if (primary != 1) {
		throw new InvalidFileFormatException("Invalid number of '1's: " + primary);
	}
	if (secondary != 1) {
		throw new InvalidFileFormatException("Invalid number of '2's: " + secondary);
	}
	fileScan.close();
	}
	
	/** Copy constructor - duplicates original board
	 * 
	 * @param original board to copy
	 */
	public CircuitBoard(CircuitBoard original) {
		board = original.getBoard();
		startingPoint = new Point(original.startingPoint);
		endingPoint = new Point(original.endingPoint);
		ROWS = original.numRows();
		COLS = original.numCols();
	}

	/** Utility method for copy constructor
	 * @return copy of board array */
	private char[][] getBoard() {
		char[][] copy = new char[board.length][board[0].length];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				copy[row][col] = board[row][col];
			}
		}
		return copy;
	}
	
	/** Return the char at board position x,y
	 * @param row row coordinate
	 * @param col col coordinate
	 * @return char at row, col
	 */
	public char charAt(int row, int col) {
		return board[row][col];
	}
	
	/** Return whether given board position is open
	 * @param row
	 * @param col
	 * @return true if position at (row, col) is open 
	 */
	public boolean isOpen(int row, int col) {
		if (row < 0 || row >= board.length || col < 0 || col >= board[row].length) {
			return false;
		}
		return board[row][col] == OPEN;
	}
	
	/** Set given position to be a 'T'
	 * @param row
	 * @param col
	 * @throws OccupiedPositionException if given position is not open
	 */
	public void makeTrace(int row, int col) {
		if (isOpen(row, col)) {
			board[row][col] = TRACE;
		} else {
			throw new OccupiedPositionException("row " + row + ", col " + col + "contains '" + board[row][col] + "'");
		}
	}
	
	/** @return starting Point(row,col) */
	public Point getStartingPoint() {
		return new Point(startingPoint);
	}
	
	/** @return ending Point(row,col) */
	public Point getEndingPoint() {
		return new Point(endingPoint);
	}
	
	/** @return number of rows in this CircuitBoard */
	public int numRows() {
		return ROWS;
	}
	
	/** @return number of columns in this CircuitBoard */
	public int numCols() {
		return COLS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				str.append(board[row][col] + " ");
			}
			str.append("\n");
		}
		return str.toString();
	}
	
}// class CircuitBoard
