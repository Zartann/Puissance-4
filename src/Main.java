import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import Game.Box;
import Game.IntBoard;
import Game.PlateauCourant;
import Search.AlphaBeta;
import Search.AlphaBetaHash;
import Search.DynIteratifHash;
import Search.Iteratif;
import Search.IteratifHash;
import Search.MiniMaxElague;
import Search.NegaMaxElague;
import Search.StateValue;


public class Main {
	
	/**
	 * Nombre de millisecondes à attendre avant que l'on arrête les Thread
	 */
	static long timeOut = 2000;

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
		
		return new IntBoard(board);
	}

	/**
	 * Demande le nom du fichier à analyser qui doit se trouver dans resources/boards/
	 */
	public static void ask(){
		Scanner asker = new Scanner(System.in);
		
		System.out.println("Indiquer un fichier ou \"all\" : ");
		String path = asker.nextLine();
		
		if(path.equals("all")){
			System.out.println("Quel time-out ? (temps en secondes)");
			timeOut = asker.nextInt() * 1000;
			asker.close();
			autoTest();
			return;
		}
		
		path = "resources/boards/" + path;
		
		PlateauCourant board;
		try {
			board = importFromFile(path);
		
			System.out.println(board);
			
			long debutTime = System.currentTimeMillis();
			
			int cas = 6;
	
			switch(cas){
			case 0 :
				System.out.println(MiniMaxElague.miniMax(board, true));
				break;
				
			case 1 :
				System.out.println(NegaMaxElague.negaMax(board));
				break;
				
			case 2 :
				AlphaBeta ab = new AlphaBeta();
				System.out.println(ab.alphaBeta(board, StateValue.LOSS, StateValue.WIN));
				System.out.println("Nombre total de positions évaluées : " + ab.totalPositions);
				break;
				
			case 3 :
				//pronfondeurMax = 10
				AlphaBetaHash abh = new AlphaBetaHash(20);
				System.out.println (abh.alphaBetaHache(board, StateValue.LOSS, StateValue.WIN, 0));
				System.out.println("Nombre total de positions évaluées : " + abh.totalPositions);
				break;
				
			case 4 :
				//pronfondeurMax = 10
				Iteratif it2 = new Iteratif(20);
				System.out.println (it2.iteratif2(board));
				System.out.println("Nombre total de positions évaluées : " + it2.totalPositions);
				break;
				
			case 5 :
				//pronfondeurMax = 10
				Iteratif itGris = new Iteratif(20);
				System.out.println (itGris.iteratifGris(board));
				System.out.println("Nombre total de positions évaluées : " + itGris.totalPositions);
				//break;
				
			case 6 :
				IteratifHash it = new IteratifHash();
				System.out.println (it.iteratifHash(board));
				System.out.println("Nombre total de positions évaluées : " + it.totalPositions);
				//break;
				
			case 7 :
				DynIteratifHash dynIt = new DynIteratifHash();
				System.out.println (dynIt.dynIteratifHash(board));
				System.out.println("Nombre total de positions évaluées : " + dynIt.totalPositions);
				break;
				
			}
			
			long endTime = System.currentTimeMillis();
			double time = ((double) (endTime - debutTime))/1000;
			
			System.out.println("Le résultat a été trouvé en " + time + " secondes.");
			
		} catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}
		
		asker.close();
	}
	
	public static void autoTest(){
		
		File tests = new File("resources/boards");
		File result = new File("Result.txt");
		try {
			PrintStream out = new PrintStream(result);
			System.setOut(out);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Timeout de " + (((double)timeOut)/1000) + " secondes.");
		
		PlateauCourant board;
		for(String path : tests.list()){
			if(path.substring(path.length()-4).equals(".out") || path.equals("Test.cfg"))
				continue;
			try{
				board = importFromFile("resources/boards/" + path);
				
				System.out.println();
				System.out.println(path + " :");
				System.out.println(board);
				for(int i = 0; i <= 7; i++){
					AutoTester test = new AutoTester(board,
							"resources/boards/" +  path.substring(0, path.length()-4)+".out", 20, i);
					
					test.start();
					try {
						Thread.sleep(timeOut);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(test.isAlive()){
						test.stop();
						System.out.println("Trop long");
					}

					board = importFromFile("resources/boards/" + path);
				}
			}
			catch (FileNotFoundException e) {

				System.out.println("Le fichier est introuvable !");
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ask();
	}

}
