package Game;

import java.util.ArrayList;
import java.util.Stack;

import Search.StateValue;


public class CurrentBoard implements PlateauCourant {

	/**
	 * Plateau contenant l'état actuel du jeu.
	 * Pour le moment, on choisit de le stocker dans une matrice.
	 * La dernière ligne, d'indice maxHeight - 1, contient la ligne inférieure du plateau
	 */
	public Box[][] board;

	public int maxWidth, maxHeight;

	/**
	 * Contient le nombre de jetons de chaque colonne
	 */
	public int[] heights;

	/**
	 * Crée un nouveau plateau initialisé à null de taille width*height
	 * @param width
	 * @param height
	 */
	public CurrentBoard(int width, int height){
		board = new Box[height][width];

		for(Box[] line : board){
			for(Box box : line){
				box = Box.VOID;
			}
		}

		maxWidth = width;
		maxHeight = height;

		heights = new int[width];
	}
	
	/**
	 * Crée un nouveau tableau plateau correspondant au plateau envoyé
	 * @param width
	 * @param height
	 */
	public CurrentBoard(Box[][] boardd){
		board = boardd;

		maxWidth = board[0].length;
		maxHeight = board.length;

		heights = new int[maxWidth];
		
		for(int column = 0; column < maxWidth; column++)
			for(int line = maxHeight - 1; line >= 0; line--) //Parcours de bas en haut
				if(!board[line][column].isVoid())
					heights[column]++;
		
		
	}

	@Override
	public boolean checkValid() {
		int countPlayer = 0, countAdv = 0;

		for(Box[] line : board){
			for(Box box : line){
				if(box.isPlayer())
					countPlayer++;
				else if(box.isAdv())
					countAdv++;
			}
		}

		int diff = countPlayer - countAdv;
		return (diff <= 1 && diff >= -1);
	}

	@Override
	/*
	 * On va checker chaque ligne, chaque colonne, chaque diagonale et chaque antidiagonale.
	 * Ceci donne une complexité en 4*nombre de cases.
	 * Il y a en tout :
	 * 		height lignes
	 * 		width colonnes
	 * 		height + width - 1 diagonales, dont 6 inutiles (car moins de 4 cases dessus)
	 * 		autant d'anti-diagonales
	 * 
	 * Les diagonales sont données par i-j = cste
	 * Les antidiagonales sont données par i+j = cste
	 */
	public StateValue result() {

		boolean playerWins = false, advWins = false;

		/*
		 * Nombres de jetons consécutifs pour le joueur et son adversaire
		 */
		int consPlayer = 0, consAdv = 0;

		//Numéros de la ligne et de la colonne qu'on étudie actuellement
		int line, column;

		//Vérifications sur les colonnes
		for(column = 0; column < maxWidth; column++){

			consPlayer = 0;
			consAdv = 0;

			//La ligne du bas est celle qui a pour indice maxHeight - 1 !!!
			//Ici, on parcourt de bas en haut
			for(line = maxHeight - 1; line >= 0; line--){

				if(board[line][column].isPlayer()){
					consPlayer++;
					consAdv = 0;

					if(consPlayer == 4)
						playerWins = true;
				}
				else if(board[line][column].isAdv()){
					consAdv++;
					consPlayer = 0;

					if(consAdv == 4)
						advWins = true;
				}
				else{
					consPlayer = 0;
					consAdv = 0;
					//Si on rencontre une case vide, inutile de monter plus, car on est sur une colonne.
					break;
				}
			}
		}


		//Vérifications sur les lignes
		for(line = maxHeight - 1; line >= 0; line--){

			consPlayer = 0;
			consAdv = 0;

			for(column = 0; column < maxWidth; column++){

				if(board[line][column].isPlayer()){
					consPlayer++;
					consAdv = 0;

					if(consPlayer == 4)
						playerWins = true;
				}
				else if(board[line][column].isAdv()){
					consAdv++;
					consPlayer = 0;

					if(consAdv == 4)
						advWins = true;
				}
				else{
					consPlayer = 0;
					consAdv = 0;
				}
			}
		}


		//Vérification sur les diagonales
		//diag = column - line + maxHeight - 1
		//Diagonale en bas à gauche -> n°0, en haut à droite -> n°maxWidth + maxHeight - 2
		//Les 3 premières ne servent à rien, les trois dernière non plus.
		for(int diag = 3; diag < maxHeight + maxWidth - 4; diag++){

			consPlayer = 0;
			consAdv = 0;

			//On parcourt la diagonale en allant de haut en bas (de gauche à droite).
			if(diag < maxHeight){
				line = maxHeight - 1 - diag;
				column = 0;
			}
			else{
				line = 0;
				column = diag - maxHeight + 1;
			}

			while(line < maxHeight - 1 && column < maxWidth - 1){

				if(board[line][column].isPlayer()){
					consPlayer++;
					consAdv = 0;

					if(consPlayer == 4)
						playerWins = true;
				}
				else if(board[line][column].isAdv()){
					consAdv++;
					consPlayer = 0;

					if(consAdv == 4)
						advWins = true;
				}
				else{
					consPlayer = 0;
					consAdv = 0;
				}

				line++;
				column++;
			}
		}


		//Vérification sur les antidiagonales
		//antidiag = column + line
		//Antidiagonale en haut à gauche -> n°0, en bas à droite -> n°maxWidth + maxHeight - 2
		//Les 3 premières ne servent à rien, les trois dernière non plus.
		for(int adiag = 3; adiag < maxHeight + maxWidth - 4; adiag++){

			consPlayer = 0;
			consAdv = 0;

			//On parcourt l'antidiagonale en allant de haut en bas (de droite à gauche).
			if(adiag < maxWidth){
				line = 0;
				column = adiag;
			}
			else{
				line = adiag - maxWidth + 1;
				column = 0;
			}

			while(line < maxHeight - 1 && column >= 0){

				if(board[line][column].isPlayer()){
					consPlayer++;
					consAdv = 0;

					if(consPlayer == 4)
						playerWins = true;
				}
				else if(board[line][column].isAdv()){
					consAdv++;
					consPlayer = 0;

					if(consAdv == 4)
						advWins = true;
				}
				else{
					consPlayer = 0;
					consAdv = 0;
				}

				line++;
				column--;
			}
		}

		//DRAW si et seulement si aucun ne gagne
		//Erreur si les deux gagnent
		if(playerWins && !advWins)
			return StateValue.WIN;
		else if(advWins && !playerWins)
			return StateValue.LOSS;
		else if(playerWins && advWins)
			throw new IllegalMoveException("Deux gagnants :\n" + toString());
		else
			return StateValue.DRAW;
	}

	@Override
	public ArrayList<Integer> validShots() {

		ArrayList<Integer> shots = new ArrayList<Integer>();

		for(int i = 0; i < maxWidth; i++)
			if(heights[i] < maxHeight)
				shots.add(i);

		return shots;
	}

	@Override
	public void playCurrent(int i) {

		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);

		if(!board[maxHeight - 1 - heights[i]][i].isVoid())
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());
		
		board[maxHeight - 1 - heights[i]][i] = Box.PLAYER;

		heights[i]++;
		lasts.push(i);

	}

	@Override
	public void playAdverse(int i) {

		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);
		
		if(!board[maxHeight - 1 - heights[i]][i].isVoid())
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());

		board[maxHeight - 1 - heights[i]][i] = Box.ADVERSARY;

		heights[i]++;
		lasts.push(i);
	}

	public Stack<Integer> lasts = new Stack<Integer>();

	@Override
	public void undoLast() {

		if(lasts.isEmpty())
			throw new IllegalMoveException("No last Move");

		int lastColumn = lasts.pop();
		board[maxHeight - 1 - heights[lastColumn] + 1][lastColumn] = Box.VOID;

		heights[lastColumn]--;

	}

	@Override
	public long playerStateConvert() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long adversaryStateConvert() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PlateauCourant importFromLong(long player, long adversary) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString(){
		String result = "";
		
		for(Box[] line : board){
			for(Box box : line){

				if(box.isPlayer())
					result += "0";
				else if(box.isAdv())
					result += "@";
				else
					result += ".";
				
			}
			result += "\n";
		}
		
		return result;
	}

}
