import java.util.ArrayList;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String path ="resources/boards/Test.cfg";

		/*PlateauCourant board;
		try {
			board = Main.importFromFile(path);

			System.out.println(board);

			PositionGris cle = board.cleGris();
			System.out.println(cle);

			System.out.println(board.evalContinue());

		} catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}*/
		
		ArrayList<Integer> l = new ArrayList<Integer>();
		
		l.add(0);l.add(1);l.add(2);l.add(3);
		int i;
		while(!l.isEmpty()){
			i = l.remove(0);
			System.out.println(i);
			if(i == 1)
				break;
		}
		System.out.println("breaké");
		while(!l.isEmpty()){
			i = l.remove(0);
			System.out.println(i);
			if(i == 1)
				break;
		}
	}

}
