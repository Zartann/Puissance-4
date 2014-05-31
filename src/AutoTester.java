import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Game.PlateauCourant;
import Search.AlphaBeta;
import Search.AlphaBetaHash;
import Search.DynIteratifHash;
import Search.Iteratif;
import Search.IteratifHash;
import Search.MiniMaxElague;
import Search.NegaMaxElague;
import Search.StateValue;


public class AutoTester extends Thread {
	
	/**
	 * Profondeur max � laquelle on va faire certains tests
	 */
	int profondeurMax;
	
	/**
	 * Plateau � analyser
	 */
	PlateauCourant board;
	
	/**
	 * Solution th�orique
	 */
	StateValue solution;
	
	/**
	 * Type d'analyse � r�aliser
	 */
	int type;
	
	public AutoTester(PlateauCourant board, String solutionPath, int prof, int type){
		this.board = board;
		try {
			Scanner in = new Scanner(new File(solutionPath));
			String sol = in.nextLine();
			
			if(sol.equals("LOSS"))
				this.solution = StateValue.LOSS;
			else if(sol.equals("WIN"))
				this.solution = StateValue.WIN;
			else if(sol.equals("DRAW"))
				this.solution = StateValue.DRAW;
			else 
				throw new RuntimeException("Solution incomprise");
			
			in.close();
				
		} catch (FileNotFoundException e) {
			this.solution = null;
			System.out.println("Le fichier "+ solutionPath + " est introuvable !");
		}
		
		this.profondeurMax = prof;
		this.type = type;
	}
	
	public void run(){
		
		StateValue valeur;
		switch(type){
		case 0 :
			System.out.print("MiniMax : ");
			valeur = MiniMaxElague.miniMax(board, true);
			if(valeur == solution)
				System.out.println("Solution trouv�e");
			else
				System.out.println("Erreur");
			break;
			
		case 1 :
			System.out.print("NegaMax : ");
			valeur = NegaMaxElague.negaMax(board);

			if(valeur == solution)
				System.out.println("Solution trouv�e");
			else
				System.out.println("Erreur");
			break;
			
		case 2 :
			System.out.print("AlphaBeta : ");
			AlphaBeta ab = new AlphaBeta();
			valeur = ab.alphaBeta(board, StateValue.LOSS, StateValue.WIN);

			if(valeur == solution)
				System.out.println("Solution trouv�e en " + ab.totalPositions + " coups");
			else
				System.out.println("Erreur");
			//System.out.println("Nombre total de positions �valu�es : " + AlphaBeta.totalPositions);
			break;
			
		case 3 :
			System.out.print("AlphaBetaHash " + profondeurMax + " : ");
			AlphaBetaHash abh = new AlphaBetaHash(profondeurMax);
			valeur = abh.alphaBetaHache(board, StateValue.LOSS, StateValue.WIN, 0);

			if(valeur == solution)
				System.out.println("Solution trouv�e en " + abh.totalPositions + " coups");
			else
				System.out.println("Erreur");
			//System.out.println("Nombre total de positions �valu�es : " + AlphaBetaHash.totalPositions);
			break;
			
		case 4 :
			System.out.print("Iteratif " + profondeurMax + " : ");
			Iteratif it = new Iteratif(profondeurMax);
			valeur = it.iteratif2(board);

			if(valeur == solution)
				System.out.println("Solution trouv�e en " + it.totalPositions + " coups");
			else
				System.out.println("Erreur");
			//System.out.println("Nombre total de positions �valu�es : " + it.totalPositions);
			break;
			
		case 5 :
			System.out.print("IteratifGris " + profondeurMax + " : ");
			Iteratif itGris = new Iteratif(profondeurMax);
			valeur = itGris.iteratifGris(board);

			if(valeur == solution)
				System.out.println("Solution trouv�e en " + itGris.totalPositions + " coups");
			else
				System.out.println("Erreur");
			//System.out.println("Nombre total de positions �valu�es : " + itGris.totalPositions);
			break;
			
		case 6 :
			System.out.print("IteratifHash  : ");
			IteratifHash ith = new IteratifHash();
			valeur = ith.iteratifHash(board);

			if(valeur == solution)
				System.out.println("Solution trouv�e en " + ith.totalPositions + " coups");
			else
				System.out.println("Erreur");
			//System.out.println("Nombre total de positions �valu�es : " + ith.totalPositions);
			break;
			
		case 7 :
			System.out.print("DynIteratifHash  : ");
			DynIteratifHash dynIt = new DynIteratifHash();
			valeur = dynIt.dynIteratifHash(board);

			if(valeur == solution)
				System.out.println("Solution trouv�e en " + dynIt.totalPositions + " coups");
			else
				System.out.println("Erreur");
			//System.out.println("Nombre total de positions �valu�es : " + dynIt.totalPositions);
			break;
			
		}
	}

}
