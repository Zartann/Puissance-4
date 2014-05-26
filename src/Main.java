import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Game.Box;
import Game.CurrentBoard;
import Game.PlateauCourant;
import Search.MiniMaxElague;


public class Main {

	/**
	 * 
	 * @param path
	 * @return Plateau d�fini dans le fichier indiqu� par path
	 * @throws FileNotFoundException
	 */
	public static PlateauCourant importFromFile(String path) throws FileNotFoundException{
		File example = new File(path);

		Scanner scan = new Scanner(example);

		//On va avancer caract�re par caract�re dans le fichier
		scan.useDelimiter("");

		int width = Integer.parseInt(scan.next());
		scan.next();
		int height = Integer.parseInt(scan.next());
		scan.nextLine();

		Box[][] board = new Box[height][width];

		String nextBox;

		for(int line = 0; line < height; line++){
			for(int column = 0; column < width; column++){
				nextBox = scan.next();

				while(!nextBox.matches("\\.|0|@")){
					if(nextBox.equals("#")){
						scan.nextLine();
					}
					nextBox = scan.next();
				}

				if(nextBox.equals("."))
					board[line][column] = Box.VOID;
				else if(nextBox.equals("@"))
					board[line][column] = Box.ADVERSARY;
				else if(nextBox.equals("0"))
					board[line][column] = Box.PLAYER;
				else
					throw new RuntimeException("Le caract�re n'a pas �t� reconnu.");

			}

		}
		
		return new CurrentBoard(board);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			String path = "resources/boards/empty-4x4.cfg";
			PlateauCourant board = importFromFile(path);
			
			System.out.println(board);
			//board.playCurrent(0);
			//System.out.println(board);
			System.out.println(MiniMaxElague.miniMax(board, true));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
