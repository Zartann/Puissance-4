package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Search.StateValue;

public class IntBoard implements PlateauCourant {

	/**
	 * Plateaux contenant les positions du joueur et de son adversaire, sous forme d'un entier
	 */
	long playerBoard, adversaryBoard;

	public int maxWidth, maxHeight;

	/**
	 * Contient le nombre de jetons de chaque colonne
	 */
	public int[] heights;

	/**
	 * Crée un nouveau plateau vide de taille width*height
	 * @param width
	 * @param height
	 */
	public IntBoard(int width, int height){
		playerBoard = 0;
		adversaryBoard = 0;

		maxWidth = width;
		maxHeight = height;

		heights = new int[width];
	}

	/**
	 * Crée un nouveau tableau plateau correspondant au plateau envoyé
	 * @param width
	 * @param height
	 */
	public IntBoard(Box[][] board){
		maxWidth = board[0].length;
		maxHeight = board.length;

		heights = new int[maxWidth];

		for(int column = 0; column < maxWidth; column++)
			for(int line = maxHeight - 1; line >= 0; line--) //Parcours de bas en haut
				if(!board[line][column].isVoid())
					heights[column]++;

		playerBoard = 0;
		adversaryBoard = 0;

		for(int column = maxWidth - 1; column >= 0; column--){

			//On décale encore d'un bit à gauche à chaque colonne pour créer la ligne vide en haut.
			playerBoard <<= 1;
			adversaryBoard <<= 1;

			for(int line = 0; line < maxHeight; line++){ //Parcours de haut en bas, de droite à gauche

				//On décale d'un bit vers la gauche à chaque fois avant de commencer.
				playerBoard <<= 1;
				adversaryBoard <<= 1;

				if(board[line][column].isPlayer())
					playerBoard++;

				else if(board[line][column].isAdv())
					adversaryBoard++;
			}

		}
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StateValue result() {

		boolean columnPlayer, linePlayer, diagPlayer, antidiagPlayer;
		boolean columnAdv, lineAdv, diagAdv, antidiagAdv;

		columnPlayer = (playerBoard & (playerBoard >> 1) & (playerBoard >> 2) & (playerBoard >> 3)) != 0;
		columnAdv = (adversaryBoard & (adversaryBoard >> 1) & (adversaryBoard >> 2) & (adversaryBoard >> 3)) != 0;

		linePlayer = (playerBoard & (playerBoard >> 7) & (playerBoard >> 14) & (playerBoard >> 21)) != 0;
		lineAdv = (adversaryBoard & (adversaryBoard >> 7) & (adversaryBoard >> 14) & (adversaryBoard >> 21)) != 0;

		diagPlayer = (playerBoard & (playerBoard >> 6) & (playerBoard >> 12) & (playerBoard >> 18)) != 0;
		diagAdv = (adversaryBoard & (adversaryBoard >> 6) & (adversaryBoard >> 12) & (adversaryBoard >> 18)) != 0;

		antidiagPlayer = (playerBoard & (playerBoard >> 8) & (playerBoard >> 16) & (playerBoard >> 24)) != 0;
		antidiagAdv = (adversaryBoard & (adversaryBoard >> 8) & (adversaryBoard >> 16) & (adversaryBoard >> 24)) != 0;

		boolean playerWins = columnPlayer || linePlayer || diagPlayer || antidiagPlayer;
		boolean advWins = columnAdv || lineAdv || diagAdv || antidiagAdv;

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
	public List<Integer> validShots() {

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

		long box = 1;
		box <<= (7*i + heights[i]);

		if((playerBoard & box) == 1 || (adversaryBoard & box) == 1)
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());

		playerBoard ^= box;

		heights[i]++;
		lasts.push(i);

	}

	@Override
	public void playAdverse(int i) {

		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);

		long box = 1;
		box <<= (7*i + heights[i]);

		if((playerBoard & box) == 1 || (adversaryBoard & box) == 1)
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());

		adversaryBoard ^= box;

		heights[i]++;
		lasts.push(i);

	}

	/**
	 * Indique si true que c'est au joueur de jouer, sinon à l'adversaire.
	 * Pour le moment, on suppose que le joueur commence toujours
	 * Ne pas utiliser avec playCurrent ou playAdverse !!!
	 */
	//TODO : Implémenter une méthode à l'initialisation pour savoir à qui c'est le tour
	boolean whosNext = true;

	@Override
	public boolean playerIsNext(){
		return whosNext;
	}

	@Override
	public void playNext(int i) {

		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);

		long box = 1;
		box <<= (7*i + heights[i]);

		if((playerBoard & box) == 1 || (adversaryBoard & box) == 1)
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());

		if(whosNext)
			playerBoard ^= box;
		else
			adversaryBoard ^= box;

		heights[i]++;
		lasts.push(i);
		whosNext = !whosNext;
		
	}

	public Stack<Integer> lasts = new Stack<Integer>();

	@Override
	public void undoLast() {

		if(lasts.isEmpty())
			throw new IllegalMoveException("No last Move");

		int lastColumn = lasts.pop();
		long box = 1;
		box <<= (7*lastColumn + heights[lastColumn] - 1);

		if((playerBoard & box) == 0 && (adversaryBoard & box) == 0)
			throw new IllegalMoveException("Case non occupée : Colonne n°" + lastColumn
					+ "\n" + toString());

		//Si c'est au joueur de jouer, c'est l'adversaire qui a joué en dernier
		if(whosNext)
			adversaryBoard ^= box;
		else
			playerBoard ^= box;

		heights[lastColumn]--;
		whosNext = !whosNext;
	}

	@Override
	public long playerStateConvert() {

		return playerBoard;

	}

	@Override
	public long adversaryStateConvert() {

		return adversaryBoard;

	}

	@Override
	public PlateauCourant importFromLong(long player, long adversary) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(){
		String result = "";

		long player = playerBoard, adv = adversaryBoard;

		Box[][] board = new Box[maxHeight][maxWidth];

		for(int column = 0; column < maxWidth; column++){
			for(int line = maxHeight-1; line >= 0; line--){ //Parcours de bas en haut, de gauche à droite

				if((player & 1) == 1)
					board[line][column] = Box.PLAYER;

				else if((adv & 1) == 1)
					board[line][column] = Box.ADVERSARY;

				else
					board[line][column] = Box.VOID;

				//On décale d'un bit vers la droite
				player >>= 1;
				adv >>= 1;
			}

			//On décale encore d'un bit à droite à chaque colonne pour gérer la ligne vide en haut.
			player >>= 1;
			adv >>= 1;
		}

		//Ensuite on affiche
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

	@Override
	public Position hachage() {
		// TODO Auto-generated method stub
		return new Position (playerBoard, adversaryBoard);
	}
}
