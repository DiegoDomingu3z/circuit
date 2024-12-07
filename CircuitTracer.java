import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail, Diego Dominguez
 */
public class CircuitTracer {

	/** Launch the program. 
	 * 
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		new CircuitTracer(args); //create this with args
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private void printUsage() {
		//TODO: print out clear usage instructions when there are problems with
        System.out.println("Usage: java CircuitTracer -s|-q -c|-g filename");
		System.out.println("-s for stack or -q for queue");
		System.out.println("-c for console or -g for GUI");
    }
	
	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	public CircuitTracer(String[] args) {
		//TODO: parse and validate command line args - first validation provided
		if (args.length != 3) {
			printUsage();
			return; //exit the constructor immediately
		}
		//TODO: initialize the Storage to use either a stack or queue
		String storage = args[0];
		// check the storage type
        if (!storage.equals("-s") && !storage.equals("-q")) {
            printUsage();
        }
		// check the output type
        String outputType = args[1];
        if (!outputType.equals("-c") && !outputType.equals("-g")) {
            printUsage();
        }
        String userInputFile = args[2];
        File inputFile = new File(userInputFile);
		Storage<TraceState> stateStore = null;
		// logic to check what the user wants, gui or stack
		switch (storage) {
			case "-s":
				stateStore = Storage.getStackInstance();
				break;
			case "-q":
				stateStore = Storage.getQueueInstance();
				break;
			default:
				printUsage();
				return;
		}
		//TODO: read in the CircuitBoard from the given file
		readBoard(inputFile, stateStore, outputType);
	}


	//TODO: run the search for best paths
	// Method to load the circuit board from the input file
    private void readBoard(File inputFile, Storage<TraceState> stateStore, String outputType) {
		try {
			// CircuitBoard object
			CircuitBoard board = new CircuitBoard(inputFile.getAbsolutePath());
			ArrayList<TraceState> bestPaths = new ArrayList<TraceState>();
			// to stateStore for each open position adjacent to the starting component
			int x = board.getStartingPoint().x;
			int y = board.getStartingPoint().y;

			// check the board right
			if (board.isOpen(x + 1, y)) {
				stateStore.store(new TraceState(board, x + 1, y));
			}
			// left
			if (board.isOpen(x - 1, y)) {
				stateStore.store(new TraceState(board, x - 1, y));
			}
			// down 
			if (board.isOpen(x, y - 1)) {
				stateStore.store(new TraceState(board, x, y - 1));
			}
			// up
			if (board.isOpen(x, y + 1)) {
				stateStore.store(new TraceState(board, x, y + 1));
			}

			// run while stateStore has something
			while (!stateStore.isEmpty()) {
				TraceState currentState = stateStore.retrieve();
				if (currentState.isSolution()) {
					 // best path is empty or eqaul in size
					if (bestPaths.isEmpty() || currentState.pathLength() == bestPaths.get(0).pathLength()) {
						bestPaths.add(currentState);

						// add the currentState to best path if its shorter
					} else if (currentState.pathLength() < bestPaths.get(0).pathLength()) {
						bestPaths.clear();
						bestPaths.add(currentState);
					}
				}
				else {
					x = currentState.getRow();
					y = currentState.getCol();
					// check right
					if (currentState.isOpen(x + 1, y)) {
						stateStore.store(new TraceState(currentState, x + 1, y));
					}
					// left
					if (currentState.isOpen(x - 1, y)) {
						stateStore.store(new TraceState(currentState, x - 1, y));
					}
					// up
					if (currentState.isOpen(x, y + 1)) {
						stateStore.store(new TraceState(currentState, x, y + 1));
					}
					// down
					if (currentState.isOpen(x, y - 1)) {
						stateStore.store(new TraceState(currentState, x, y - 1));
					}
				}
			}

			// printing result based on outType decision by the user
			switch (outputType) {
				case "-c":
					for (TraceState path : bestPaths) {
						System.out.println(path.getBoard().toString());
					}
					break;
				case "-g":
					System.out.println("sorry but GUI is not supported");
					break;
				default:
					printUsage();
					return;
			}
		} catch (FileNotFoundException e) {
			System.out.println(e + "FILE NOT FOUND");
			return;
		} catch (InvalidFileFormatException e) {
			System.out.println(e + "not correct format");
			return;
		}
	}
} // class CircuitTracer
