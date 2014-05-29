import java.io.FileNotFoundException;

import Game.PlateauCourant;
import Game.Position;
import Game.PositionGris;
import Search.AlphaBeta;
import Search.MiniMaxElague;
import Search.NegaMaxElague;
import Search.StateValue;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path ="resources/boards/Test.cfg";
		String path2 ="resources/boards/ended.cfg";
		
		PlateauCourant board;
		PlateauCourant board2;
		try {
			board = Main.importFromFile(path);
			board2 = Main.importFromFile(path2);
		
			System.out.println(board);
			System.out.println(board2);
			
			PositionGris cle = board.cleGris();
			System.out.println(cle);
			
			PositionGris cle2 = board2.cleGris();
			System.out.println(cle2);
			
			System.out.println(cle.equals(cle2));
			System.out.println(cle.hashCode());
			System.out.println(cle2.hashCode());
			
		} catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}
	}

}
