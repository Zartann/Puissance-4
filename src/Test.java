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
		
		PlateauCourant board;
		try {
			board = Main.importFromFile(path);
		
			System.out.println(board);
			
			PositionGris cle = board.cleGris();
			System.out.println(cle);
			
			System.out.println(board.evalContinue());
			
		} catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}
	}

}
