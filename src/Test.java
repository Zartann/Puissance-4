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
		
		Thread t = new Thread(){
			public void run(){
				for(int i = 0; i < 1000000; i++)
					System.out.println(i);
			}
		};
		t.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(t.isAlive())
			t.stop();
		/*
		t = new Thread(){
			public void run(){
				for(int i = 0; i < 1000000; i++)
					System.out.println(i);
			}
		};
		
		t.start();*/
		/*
		PlateauCourant board;
		try{
			board = Main.importFromFile("resources/boards/" + path);

			System.out.println(path + " :");
			System.out.println(board);
			AutoTester test = new AutoTester(board,
					"resources/boards/" +  path.substring(0, path.length()-4)+".out", 20, 0);

			test.run();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(test.isAlive()){
				test.interrupt();
				System.out.println("Trop long");
			}
		}
		catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}*/
	}

}
