import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Game.Box;
import Game.CurrentBoard;
import Game.PlateauCourant;
import Search.AlphaBeta;
import Search.MiniMaxElague;
import Search.NegaMaxElague;
import Search.StateValue;


public class Main {

	/**
	 * 
	 * @param path
	 * @return Plateau défini dans le fichier indiqué par path
	 * @throws FileNotFoundException
	 */
	public static PlateauCourant importFromFile(String path) throws FileNotFoundException{
		File example = new File(path);

		Scanner scan = new Scanner(example);

		//On va avancer caractère par caractère dans le fichier
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
				else{
					scan.close();
					throw new RuntimeException("Le caractère n'a pas été reconnu.");
				}

			}

		}
		
		scan.close();
		
		return new CurrentBoard(board);
	}

	/**
	 * Demande le nom du fichier à analyser qui doit se trouver dans resources/boards/
	 */
	public static void ask(){
		Scanner asker = new Scanner(System.in);
		
		String path = asker.nextLine();
		
		path = "resources/boards/" + path;
		
		PlateauCourant board;
		try {
			board = importFromFile(path);
		
			System.out.println(board);
			
			int cas = 2;
	
			switch(cas){
			case 0 :
				System.out.println(MiniMaxElague.miniMax(board, true));
				break;
				
			case 1 :
				System.out.println(NegaMaxElague.negaMax(board));
				break;
				
			case 2 :
				System.out.println(AlphaBeta.alphaBeta(board, StateValue.LOSS, StateValue.WIN));
				break;
				
			}
			
		} catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}
		
		asker.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ask();

	}

}
