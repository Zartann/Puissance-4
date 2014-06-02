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
	 * Si à true, l'interface est verbeuse.
	 */
	static boolean verbose = false;

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

		//On parse le fichier
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

		//Demande de la commande
		if(verbose)
			System.out.println("Indiquer un fichier ou \"all\" : ");
		String path = asker.nextLine();
		if(verbose)
			System.out.println();

		//Traitement automatisé
		if(path.equals("all")){
			if(verbose){
				System.out.println("Quel time-out ? (temps en secondes)");
				timeOut = asker.nextInt() * 1000;
			}
			asker.close();
			autoTest();
			return;
		}

		PlateauCourant board;
		try {
			//Importation du plateau
			board = importFromFile("resources/boards/" + path);

			long debutTime = 0;

			int cas = 5;

			if(verbose){
				//Demande de la méthode
				System.out.println("Quelle méthode utiliser ?");
				System.out.println("MiniMax : 0; NegaMax : 1; AlphaBeta : 2; AlphaBetaHash : 3");
				System.out.println("Iteratif : 4; IteratifGris : 5; IteratifHash : 6; DynIterativeHash : 7");
				cas = asker.nextInt();
				System.out.println();
			}
			int prof = 20;
			switch(cas){
			case 0 :

				if(verbose){
					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				System.out.println(MiniMaxElague.miniMax(board, true));
				break;

			case 1 :

				if(verbose){
					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				System.out.println(NegaMaxElague.negaMax(board));
				break;

			case 2 :

				if(verbose){
					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				AlphaBeta ab = new AlphaBeta();
				System.out.println(ab.alphaBeta(board, StateValue.LOSS, StateValue.WIN));

				if(verbose){
					System.out.println("Nombre total de positions évaluées : " + ab.totalPositions);
				}
				break;

			case 3 :

				//Demande de la profondeur maximale
				if(verbose){
					System.out.println("Quelle profondeur maximale ?");
					prof = asker.nextInt();
					System.out.println();

					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				AlphaBetaHash abh = new AlphaBetaHash(prof);
				System.out.println (abh.alphaBetaHache(board, StateValue.LOSS, StateValue.WIN, 0));
				if(verbose){
					System.out.println("Nombre total de positions évaluées : " + abh.totalPositions);
				}
				break;

			case 4 :

				if(verbose){
					//Demande de la profondeur maximale
					System.out.println("Quelle profondeur maximale ?");
					prof = asker.nextInt();
					System.out.println();

					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				Iteratif it2 = new Iteratif(prof);
				System.out.println (it2.iteratif2(board));
				if(verbose){
					System.out.println("Nombre total de positions évaluées : " + it2.totalPositions);
				}
				break;

			case 5 :

				if(verbose){
					//Demande de la profondeur maximale
					System.out.println("Quelle profondeur maximale ?");
					prof = asker.nextInt();
					System.out.println();

					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				Iteratif itGris = new Iteratif(prof);
				System.out.println (itGris.iteratifGris(board));

				if(verbose){
					System.out.println("Nombre total de positions évaluées : " + itGris.totalPositions);
				}
				break;

			case 6 :

				if(verbose){
					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				IteratifHash it = new IteratifHash();
				System.out.println (it.iteratifHash(board));
				if(verbose){
					System.out.println("Nombre total de positions évaluées : " + it.totalPositions);
				}
				break;

			case 7 :

				if(verbose){
					System.out.println(path);
					System.out.println(board);
				}
				debutTime = System.currentTimeMillis();

				DynIteratifHash dynIt = new DynIteratifHash();
				System.out.println (dynIt.dynIteratifHash(board));
				if(verbose){
					System.out.println("Nombre total de positions évaluées : " + dynIt.totalPositions);
				}
				break;

			default :

				debutTime = System.currentTimeMillis();
				if(verbose){
					System.out.println("Cas inconnu !");
				}
			}

			long endTime = System.currentTimeMillis();
			double time = ((double) (endTime - debutTime))/1000;

			//Affichage du temps d'exécution
			if(verbose){
				System.out.println("Temps d'exécution : " + time + " secondes.");
			}

		} catch (FileNotFoundException e) {

			System.out.println("Le fichier est introuvable !");
		}

		asker.close();
	}

	public static void autoTest(){

		File tests = new File("resources/boards");

		//Transfert de la sortie standard
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

			/*
			 * Si le fichier n'est pas un fichier test (en .cfg) 
			 * ou alors est Test.cfg qui est potentiellement incorrect,
			 * on ne fait rien
			 */
			if(path.substring(path.length()-4).equals(".out") || path.equals("Test.cfg"))
				continue;

			try{
				board = importFromFile("resources/boards/" + path);

				//Affichage du fichier
				System.out.println();
				System.out.println(path + " :");
				System.out.println(board);

				//Test de toutes les méthodes
				for(int i = 0; i <= 7; i++){
					//Lancement du Thread de test
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
						//Si le Thread ne s'est pas arrêté, on le tue
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
