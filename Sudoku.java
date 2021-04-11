import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileSystemView;

import acm.graphics.GCanvas;
import acm.graphics.GRect;
import acm.gui.TableConstraints;
import acm.gui.TableLayout;
import acm.gui.TablePanel;

/**
 * This class is designed to emulate the game Sudoku and provide a fun and
 * interactive experience. Enjoy!
 * 
 * @author Shikhar Dixit
 * @version 5/01/2019
 */
public class Sudoku extends TablePanel implements ActionListener {

	private static final int BAR_WIDTH = 3;

	public static final int DEFAULT_ROWS = 9;

	public static final int DEFAULT_COLUMNS = 9;

	public static final int DEFAULT_CELL_WIDTH = 1;

	private static int[][] data;

	private JTextField[] texts = new JTextField[81];

	static Sudoku sudokuGame;

	/**
	 * @see http://www.zentut.com/java-swing/how-to-create-popup-menu-in-java-swing/
	 */
	public Sudoku() {

		super(DEFAULT_ROWS * 2 - 1, DEFAULT_COLUMNS * 2 - 1, 0, 0);
		int count = 0;
		for (int r = 0; r < DEFAULT_ROWS; r++) {
			for (int c = 0; c < DEFAULT_COLUMNS; c++) {
				texts[count] = addNumberCell(r, c);
				if (c < DEFAULT_COLUMNS - 1) {
					addVerticalSeparator(c % 3 == 2);
				}
				count++;
			}
			if (r < DEFAULT_ROWS - 1) {
				addSeparatorRow(r);
			}
		}

		JMenu file = new JMenu("File");
		JMenuItem openTextFile = new JMenuItem("Open Puzzle");
		openTextFile.addActionListener(this);
		file.add(openTextFile);
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(this);
		file.add(save);
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(this);
		file.add(quit);

		JMenu game = new JMenu("Game");
		JMenuItem choosePuzzle = new JMenuItem("Choose Puzzle");
		game.add(choosePuzzle);
		choosePuzzle.addActionListener(this);
		JMenu chooseDifficulty = new JMenu("Choose Difficulty");
		game.add(chooseDifficulty);
		JMenuItem easy = new JMenuItem("Easy");
		chooseDifficulty.add(easy);
		easy.addActionListener(this);
		JMenuItem medium = new JMenuItem("Medium");
		chooseDifficulty.add(medium);
		medium.addActionListener(this);
		JMenuItem difficult = new JMenuItem("Difficult");
		chooseDifficulty.add(difficult);
		difficult.addActionListener(this);
		JMenuItem checkPuzzle = new JMenuItem("Check Me!");
		game.add(checkPuzzle);
		checkPuzzle.addActionListener(this);

		JMenu help = new JMenu("Help");
		JMenuItem installInstructions = new JMenuItem("Getting Started");
		installInstructions.addActionListener(this);
		help.add(installInstructions);
		JMenuItem playingInstructions = new JMenuItem("How to Use App");
		playingInstructions.addActionListener(this);
		help.add(playingInstructions);
		JMenuItem sI = new JMenuItem("How to Play Sudoku");
		sI.addActionListener(this);
		help.add(sI);
		JMenuItem difficultyInstructions = new JMenuItem("Switching Difficulty Level Instructions");
		help.add(difficultyInstructions);
		difficultyInstructions.addActionListener(this);
		JMenuItem about = new JMenuItem("About Author");
		about.addActionListener(this);
		help.add(about);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(game);
		menuBar.add(help);

		TableLayout myLayout = (TableLayout) getLayout();
		myLayout.setConstraints(this, new TableConstraints("fill=BOTH"));

		JFrame boardFrame = new JFrame("Sudoku");
		boardFrame.setJMenuBar(menuBar);
		boardFrame.add(this);
		boardFrame.addWindowListener(new SudokuBoardAdapter());
		boardFrame.pack();
		boardFrame.setVisible(true);

		openTextFile.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));

		save.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));

		quit.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));

		choosePuzzle.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.ALT_MASK));

		checkPuzzle.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.ALT_MASK));
	}

	public static void main(String args[]) {
		sudokuGame = new Sudoku();
		sudokuGame.loadFromFile(new File("sudoku.txt"));
	}

	/**
	 * This method adds a text field for each cell.
	 * 
	 * @param r the specified row
	 * @param c the specified column
	 */
	private JTextField addNumberCell(int r, int c) {
		JTextField basic = new JTextField(DEFAULT_CELL_WIDTH);
		basic.setHorizontalAlignment(JTextField.CENTER);
		basic.setBackground(Color.WHITE);
		basic.setPreferredSize(new Dimension(33, 33));
		basic.setMinimumSize(new Dimension(33, 33));
		basic.setBorder(null);
		basic.setEditable(false);
		basic.addActionListener(this);
		this.add(basic);
		return basic;

	}

	/**
	 * This method adds a vertical black separator.
	 * 
	 * @param isBlack whether the color is black
	 */
	private void addVerticalSeparator(boolean isBlack) {
		int height = 33;
		GRect bar = new GRect(BAR_WIDTH, height);
		if (isBlack) {
			bar.setColor(Color.BLACK);
		} else {
			bar.setColor(Color.LIGHT_GRAY);
		}
		bar.setFilled(true);
		GCanvas gc = new GCanvas();
		gc.setPreferredSize(new Dimension(BAR_WIDTH, height));
		gc.add(bar);
		this.add(gc);
	}

	/**
	 * This method adds a separator for an entire row using horizontal separators
	 * under each text field.
	 * 
	 * @param r the specified row
	 */
	private void addSeparatorRow(int r) {
		for (int c = 0; c < DEFAULT_COLUMNS; c++) {
			addHorizontalSeparator(r);
			if (c < DEFAULT_COLUMNS - 1) {
				addCrossingBlock((c % 3 == 2) || (r % 3 == 2));
			}
		}
	}

	/**
	 * This method adds a separator bar under a text field.
	 * 
	 * @param r the specified row
	 */
	private void addHorizontalSeparator(int r) {
		int height = 33;
		GRect bar = new GRect(height, BAR_WIDTH);
		if (r % 3 == 2) {
			bar.setColor(Color.BLACK);
		} else {
			bar.setColor(Color.LIGHT_GRAY);
		}
		bar.setFilled(true);
		GCanvas gc = new GCanvas();
		gc.setPreferredSize(new Dimension(height, BAR_WIDTH));
		gc.add(bar);
		this.add(gc);
	}

	/**
	 * This method adds a crossing block.
	 * 
	 * @param black whether the color is black
	 */
	private void addCrossingBlock(boolean isBlack) {
		GRect bar = new GRect(BAR_WIDTH, BAR_WIDTH);
		if (isBlack) {
			bar.setColor(Color.BLACK);
		} else {
			bar.setColor(Color.LIGHT_GRAY);
		}
		bar.setFilled(true);
		GCanvas gc = new GCanvas();
		gc.setPreferredSize(new Dimension(BAR_WIDTH, BAR_WIDTH));
		gc.add(bar);
		this.add(gc);
	}

	/**
	 * Gets all the values from the board.
	 * 
	 * @return the values from the board
	 */
	public String getState() {

		String s = "";
		for (int r = 0; r < DEFAULT_ROWS; r++) {
			for (int c = 0; c < DEFAULT_COLUMNS; c++) {
				s += getCellValue(r, c);
			}
			s += "\n";
		}
		return s;
	}

	/**
	 * Gets a specified cell's value.
	 * 
	 * @param r the row of the cell
	 * @param c the column of the cell
	 * @return the value of the cell
	 */
	public String getCellValue(int r, int c) {
		int cellsPerRow = (2 * DEFAULT_ROWS - 1) * 2;
		JTextField foo = (JTextField) getComponent(r * cellsPerRow + c * 2);
		return foo.getText();
	}

	/**
	 * Fills the board based on the file brought in.
	 * 
	 * @param state the current state of the board
	 */
	public void fillBoard(String state) {
		String[] rows = state.split("\n");
		int cellsPerRow = (2 * DEFAULT_ROWS - 1) * 2;
		for (int r = 0; r < DEFAULT_ROWS; r++) {
			for (int c = 0; c < DEFAULT_COLUMNS; c++) {
				JTextField foo = (JTextField) getComponent(r * cellsPerRow + c * 2);
				char ch = rows[r].charAt(c);
				foo.setText("" + ch);
				if (ch == ' ') {
					foo.setEditable(false);
					foo.setBackground(Color.WHITE);
				} else {
					foo.setBackground(new Color(240, 240, 240));
					foo.setEditable(false);
				}
			}
		}
	}

	/**
	 * Loads the numbers from a file to fill the Sudoku board.
	 * 
	 * @param selectedFileName the selected file
	 */
	private void loadFromFile(File selectedFileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(selectedFileName));
			String s = br.readLine();
			for (int r = 0; r < DEFAULT_ROWS; r++) {
				s += "\n" + br.readLine();
			}
			fillBoard(s);
			for (JTextField text : texts) {
				if (text.getBackground().equals(Color.WHITE)) {
					makeDifficultPopup(text);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if there are no errors within the board in terms of validity.
	 * 
	 * @param state the state of the board
	 * @return true if there are no errors, false if there are errors
	 */
	public static boolean noErrors(String state) {
		fillMatrix(state);

		for (int s = 0; s < 9; s++) {
			if (!rowValid(s)) {
				return false;
			}

			if (!columnValid(s)) {
				return false;
			}

			if (!sectorValid(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If there are no errors and no empty cells, then the Sudoku has been solved.
	 * 
	 * @param state the current state of the board
	 * @return true if the game is solved, false if not
	 */
	public static boolean solved(String state) {
		return noErrors(state) && emptyCells() == 0;
	}

	/**
	 * This method fills a 2d array with values from the state.
	 * 
	 * @param state the current state of the board
	 */
	private static void fillMatrix(String state) {
		data = new int[9][9];
		String[] rows = state.split("\n");

		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				char ch = rows[r].charAt(c);
				if (ch == ' ')
					data[r][c] = 0;
				else
					data[r][c] = Integer.parseInt("" + ch);
			}
		}

	}

	/**
	 * Checks how many cells are empty and returns that number.
	 * 
	 * @return number of empty cells
	 */
	private static int emptyCells() {
		int total = 0;
		for (int row = 0; row < 9; row++)
			for (int col = 0; col < 9; col++)
				if (isEmpty(row, col))
					total++;
		return total;
	}

	/**
	 * Checks for how many empty cells and returns true if there are none.
	 * 
	 * @param row the
	 * @param col
	 * @return true if there are no empty cells, false if not
	 */
	private static boolean isEmpty(int row, int col) {
		if ((0 > row) || (8 < row) || (0 > col) || (8 < col))
			throw new IllegalArgumentException();

		return data[row][col] == 0;
	}

	/**
	 * Handles the actions upon clicking menu items.
	 * 
	 * @see https://best-clipart.com/free-emoji-clip-art.html
	 * @see http://www.clipartsuggest.com/happy-emoji-cliparts/
	 * @see https://www.mkyong.com/swing/java-swing-jfilechooser-example/
	 * 
	 * @param e the action event
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if (s.equals("Quit")) {
			System.exit(0);
		} else if (s.equals("Choose Puzzle")) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView());
			int returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filepath = jfc.getSelectedFile().getAbsolutePath();
				sudokuGame.loadFromFile(new File(filepath));
			}
		} else if (s.equals("Getting Started")) {
			JOptionPane.showMessageDialog(null, "This game will best work in the Eclipse IDE. "
					+ "\nAction Items before playing:"
					+ "\n- Place shiki.png, won.png, lost.png, and sudoku.txt within the folder \nin which you are running the application."
					+ "\n- Place the Sudoku Puzzles folder into your Documents folder."
					+ "\n- Use the Open Puzzle or Choose Puzzle menu items to start Sudoku'ing!");

		} else if (s.equals("Save")) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView());
			int returnValue = jfc.showSaveDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filepath = jfc.getSelectedFile().getAbsolutePath();
				try {
					PrintWriter pw = new PrintWriter(filepath);
					pw.write(getState());
					pw.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		} else if (s.equals("Open Puzzle")) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView());
			int returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filepath = jfc.getSelectedFile().getAbsolutePath();
				sudokuGame.loadFromFile(new File(filepath));
			}
		} else if (s.equals("How to Play Sudoku")) {
			JOptionPane.showMessageDialog(null,
					"The Classic Sudoku is a number placing puzzle based on a 9x9 grid with several given numbers. \nThe object is to place the "
							+ "numbers 1 to 9 in the empty squares so that each row, "
							+ "\neach column and each 3x3 box contains the same number only once."
							+ "\n\nResource: https://www.conceptispuzzles.com/index.aspx?uri=puzzle/sudoku/rules",
					"Suduko Instructions", JOptionPane.WARNING_MESSAGE);
		} else if (s.equals("Switching Difficulty Level Instructions")) {
			JOptionPane.showMessageDialog(null,
					"When switching difficulty levels, please note: the new popups will only show up for the unfilled cells.\nSo, please make sure to clear the cells you are having difficulty with. ");
		} else if (s.equals("About Author")) {
			ImageIcon shikiImage = new ImageIcon("shiki.png");
			JOptionPane.showMessageDialog(null, "The developer of this application is Shikhar Dixit. "
					+ "\nHe is a passionate computer science student at "
					+ "St. Bonaventure University with an interest " + "in basketball and hip hop music."
					+ "\nIn this project, he worked with Dr. Levine to create his version of the" + " game Sudoku."
							+ "\n\n Contact info.: dixits15@bonaventure.edu",
					"About", JOptionPane.PLAIN_MESSAGE, shikiImage);
		} else if (s.equals("Easy")) {
			for (JTextField text : texts) {
				if (text.getBackground().equals(Color.WHITE)) {
					makeEasyPopup(text);
				}
			}
		} else if (s.equals("Difficult")) {
			for (JTextField text : texts) {
				if (text.getBackground().equals(Color.WHITE)) {
					makeDifficultPopup(text);
				}
			}
		} else if (s.equals("Medium")) {
			for (JTextField text : texts) {
				if (text.getBackground().equals(Color.WHITE)) {
					makeMediumPopup(text);
				}
			}
		} else if (s.equals("Check Me!")) {
			ImageIcon win = new ImageIcon("won.png");
			ImageIcon lose = new ImageIcon("lost.png");

			try {
				if (solved(getState())) {
					JOptionPane.showMessageDialog(null, "Congratulations!! You have won!", "Victor!!",
							JOptionPane.INFORMATION_MESSAGE, win);
				} else {
					JOptionPane.showMessageDialog(null,
							"I'm sorry. This is not correct.\nEither the board is not complete or number(s) are incorrectly placed.\nKeep at it!",
							"Not there yet...", JOptionPane.INFORMATION_MESSAGE, lose);
				}
			} catch (Exception f) {
				JOptionPane.showMessageDialog(null,
						"I'm sorry. This is not correct.\nEither the board is not complete or number(s) are incorrectly placed.\nKeep at it!",
						"Not there yet...", JOptionPane.INFORMATION_MESSAGE, lose);
			}
		} else if (s.equals("How to Use App")) {
			JOptionPane.showMessageDialog(null, "What you need to know:"
					+ "\n- Using the Choose Puzzle item, you can access easy, medium, and hard \npuzzles to choose from within the Sudoku Puzzles folder within Documents."
					+ "\n\n- You can save puzzles by choosing the Save option under the File Menu\n to the place of your choosing."
					+ "\n\n- Choose Difficulty Settings:" + "\n Easy: Popup menus will display only the valid numbers."
					+ "\n Medium: Popup menus will display all numbers, but will color red when the number placed is invalid."
					+ "\n Difficult: Popup menus will display all numbers with no additional aid."
					+ "\n\n- When you think you have achieved the solution, click Choose Me! to check your answer!"
					+ "\n\n- Keyboard shortcuts:" + "\n Open: Control + o" + "\n Save: Control + s"
					+ "\n Quit: Control + q" + "\n Choose Puzzle: Alt + p" + "\n Check Me!: Alt + c");
		}
	}

	/**
	 * Checks if the specified row is valid meaning no repeats.
	 * 
	 * @param row the specified row
	 * @return true if the row is valid, false if not
	 */
	private static boolean rowValid(int row) {
		if ((0 > row) || (row > 8))
			throw new IllegalArgumentException();
		boolean[] used = new boolean[10];
		for (int i = 0; i < 9; i++) {
			if (used[data[row][i]])
				return false;
			if (!isEmpty(row, i))
				used[data[row][i]] = true;
		}
		return true;
	}

	/**
	 * Checks if the column specified is valid meaning no repeats.
	 * 
	 * @param col the specified column
	 * @return true if the column is valid, false if not
	 */
	private static boolean columnValid(int col) {
		if ((0 > col) || (col > 8))
			throw new IllegalArgumentException();
		boolean[] used = new boolean[10];
		for (int i = 0; i < 9; i++) {
			if (used[data[i][col]])
				return false;
			if (!isEmpty(i, col))
				used[data[i][col]] = true;
		}
		return true;
	}

	/**
	 * Checks if the sector specified is valid meaning no repeats.
	 * 
	 * @param sec the specified sector
	 * @return true if the sector is valid, false if not
	 */
	private static boolean sectorValid(int sec) {
		if ((0 > sec) || (sec > 8))
			throw new IllegalArgumentException();
		boolean[] used = new boolean[10];
		int baseRow = 3 * (sec / 3);
		int baseCol = 3 * (sec % 3);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (used[data[baseRow + i][baseCol + j]])
					return false;
				if (!isEmpty(baseRow + i, baseCol + j))
					used[data[baseRow + i][baseCol + j]] = true;
			}
		}
		return true;
	}

	/**
	 * Makes a popup menu for a textfield with only valid numbers along with the
	 * option to clear the text field.
	 * 
	 * @param text the textfield for which the popup is being made
	 */
	public void makeEasyPopup(JTextField text) {
		final JPopupMenu popup = new JPopupMenu();
		ArrayList<Integer> easyNums = new ArrayList<Integer>();
		ArrayList<JMenuItem> numberList = new ArrayList<JMenuItem>();

		for (int i = 1; i < 10; i++) {
			easyNums.add(i);
		}

		int row = (int) (text.getBounds().getY()) / 36;
		int col = (int) (text.getBounds().getX()) / 36;
		int sec = row / 3 * 3 + col / 3;

		for (int i = 1; i < 10; i++) {
			text.setText("" + i);
			fillMatrix(getState());
			if (rowValid(row) && columnValid(col) && sectorValid(sec)) {
				numberList.add(new JMenuItem("" + i));
			}
		}

		text.setText(" ");

		numberList.add(new JMenuItem("Clear"));

		for (int i = 0; i < numberList.size(); i++) {
			popup.add(numberList.get(i));
			int ind = i;
			if (numberList.get(i).getText().equals("Clear")) {
				numberList.get(i).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						text.setText("");
						text.setBackground(Color.WHITE);
					}
				});
			} else {
				numberList.get(i).addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						text.setText(numberList.get(ind).getText());
						text.setBackground(new Color(240, 240, 240));
					}
				});
			}
		}

		handlePopup(text, popup);
	}

	/**
	 * Makes a popup menu for a textfield with all possible numbers along with the
	 * option to clear the text field. If an invalid number is placed, the textfield
	 * will color red.
	 * 
	 * @param text the textfield for which the popup is being made
	 */
	public void makeMediumPopup(JTextField text) {
		final JPopupMenu popup = new JPopupMenu();
		ArrayList<JMenuItem> numberList = new ArrayList<JMenuItem>();

		int row = (int) (text.getBounds().getY()) / 36;
		int col = (int) (text.getBounds().getX()) / 36;
		int sec = row / 3 * 3 + col / 3;

		for (int i = 1; i < 10; i++) {
			numberList.add(new JMenuItem("" + i));
		}
		numberList.add(new JMenuItem("Clear"));

		for (int i = 0; i < numberList.size(); i++) {
			popup.add(numberList.get(i));
			int ind = i;

			if (numberList.get(i).getText().equals("Clear")) {
				numberList.get(i).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						text.setText("");
						text.setBackground(Color.WHITE);
					}
				});
			} else {
				numberList.get(i).addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						text.setText(numberList.get(ind).getText());
						fillMatrix(getState());
						if (rowValid(row) && columnValid(col) && sectorValid(sec)) {
							text.setBackground(new Color(240, 240, 240));
						} else {
							text.setBackground(Color.PINK);
						}
					}
				});
			}
		}

		handlePopup(text, popup);
	}

	/**
	 * Makes a popup menu for a textfield with all numbers (1-9) and option to clear
	 * the text field.
	 * 
	 * @param text the textfield for which the popup is being made
	 */
	public void makeDifficultPopup(JTextField text) {
		final JPopupMenu popup = new JPopupMenu();
		ArrayList<JMenuItem> numberList = new ArrayList<JMenuItem>();

		for (int i = 1; i < 10; i++) {
			numberList.add(new JMenuItem("" + i));
		}
		numberList.add(new JMenuItem("Clear"));

		for (int i = 0; i < numberList.size(); i++) {
			popup.add(numberList.get(i));
			int ind = i;

			if (numberList.get(i).getText().equals("Clear")) {
				numberList.get(i).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						text.setText("");
						text.setBackground(Color.WHITE);
					}
				});
			} else {
				numberList.get(i).addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						text.setText(numberList.get(ind).getText());
						fillMatrix(getState());
						text.setBackground(new Color(240, 240, 240));
					}
				});
			}
		}

		handlePopup(text, popup);
	}

	/**
	 * Adds a listener to a textfield for specified popup menus.
	 * 
	 * @param text      the textfield for which the popup is being made
	 * @param popupMenu the popup menu for the textfield
	 */
	public void handlePopup(JTextField text, JPopupMenu popupMenu) {
		text.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

}

/**
 * This class helps handle the exit upon a window closing event.
 * 
 * @author dlevine, Shikhar Dixit
 * @version 4/27/19
 *
 */
class SudokuBoardAdapter extends WindowAdapter {

	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
}
