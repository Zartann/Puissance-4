import java.io.FileNotFoundException;

import Game.PlateauCourant;
import Game.Position;
import Search.AlphaBeta;
import Search.MiniMaxElague;
import Search.NegaMaxElague;
import Search.StateValue;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path ="resources/boards/ended.cfg";
		
		PlateauCourant board;
		try {
			board = Main.importFromFile(path);
		
			System.out.println(board);
			
			Position sym = board.cle().symmetricPosition();
			board.importFromLong(sym.playerPos, sym.advPos);
			System.out.println(board);
			
		} catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}
	}

}
