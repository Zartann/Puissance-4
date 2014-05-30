package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Search.ContinueStateValue;
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

		linePlayer = (playerBoard & (playerBoard >> maxHeight+1) & (playerBoard >> 2*maxHeight+2) & (playerBoard >> 3*maxHeight+3)) != 0;
		lineAdv = (adversaryBoard & (adversaryBoard >> maxHeight+1) & (adversaryBoard >> 2*maxHeight+2) & (adversaryBoard >> 3*maxHeight+3)) != 0;

		diagPlayer = (playerBoard & (playerBoard >> maxHeight) & (playerBoard >> 2*maxHeight) & (playerBoard >> 3*maxHeight)) != 0;
		diagAdv = (adversaryBoard & (adversaryBoard >> maxHeight) & (adversaryBoard >> 2*maxHeight) & (adversaryBoard >> 3*maxHeight)) != 0;

		antidiagPlayer = (playerBoard & (playerBoard >> maxHeight+2) & (playerBoard >> 2*maxHeight+4) & (playerBoard >> 3*maxHeight+6)) != 0;
		antidiagAdv = (adversaryBoard & (adversaryBoard >> maxHeight+2) & (adversaryBoard >> 2*maxHeight+4) & (adversaryBoard >> 3*maxHeight+6)) != 0;

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
		box <<= ((maxHeight+1)*i + heights[i]);

		if((playerBoard & box) == 1 || (adversaryBoard & box) == 1)
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());

		playerBoard ^= box;

		heights[i]++;
		lasts.push(i);
		whosNext = false;

	}

	@Override
	public void playAdverse(int i) {

		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);

		long box = 1;
		box <<= ((maxHeight+1)*i + heights[i]);

		if((playerBoard & box) == 1 || (adversaryBoard & box) == 1)
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());

		adversaryBoard ^= box;

		heights[i]++;
		lasts.push(i);
		whosNext = true;

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
		box <<= ((maxHeight+1)*i + heights[i]);

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
		box <<= ((maxHeight+1)*lastColumn + heights[lastColumn] - 1);

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
		playerBoard = player;
		adversaryBoard = adversary;
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
	public Position cle() {
		return new Position (playerBoard, adversaryBoard, maxWidth, maxHeight);
	}

	@Override
	public PositionGris cleGris() {
		return new PositionGris (playerBoard, adversaryBoard, maxWidth, maxHeight);
	}
	
	@Override
	public int nombreCoupsRestants() {
		int nb = 0;
		for(int i = 0; i < maxWidth; i++){
			nb += (maxHeight - heights[i]);
		}
		return nb;
	}

	@Override
	public StateValue eval() {
		return StateValue.DRAW;
	}

	@Override
	public ContinueStateValue evalContinue() {
		long playerToutSeul = 0;
		long advToutSeul = 0;
		//Remplissage

		//plateauPlein correspond à un plateau rempli, ligne du dessus comprise.
		long plateauPlein = ((long) 1) << (maxWidth * (maxHeight+1));
		plateauPlein--;

		//PlayerToutSeul et advToutSeul vont contenir le plateau en entier avec que des 1 sauf aux 
		//positions des jetons de Adversary et de Player respectivement
		playerToutSeul = adversaryBoard ^ plateauPlein;
		advToutSeul = playerBoard ^ plateauPlein;

		//ligneSup correspond à la seule ligne du dessus remplie avec des 1.
		long ligneSup = 0;
		for(int i = 0; i < maxWidth; i++){
			ligneSup++;
			ligneSup <<= maxHeight+1;
		}
		ligneSup >>= 1;

		//On élimine la ligne du dessus, actuellement remplie de 0
		playerToutSeul ^= ligneSup;
		advToutSeul ^= ligneSup;


		//On trouve les alignements ainsi créés
		long columnPlayer, linePlayer, diagPlayer, antidiagPlayer;
		long columnAdv, lineAdv, diagAdv, antidiagAdv;

		columnPlayer = (playerToutSeul & (playerToutSeul >> 1) & (playerToutSeul >> 2) & (playerToutSeul >> 3));
		columnAdv = (advToutSeul & (advToutSeul >> 1) & (advToutSeul >> 2) & (advToutSeul >> 3));

		linePlayer = (playerToutSeul & (playerToutSeul >> maxHeight+1) & (playerToutSeul >> 2*maxHeight+2) & (playerToutSeul >> 3*maxHeight+3));
		lineAdv = (advToutSeul & (advToutSeul >> maxHeight+1) & (advToutSeul >> 2*maxHeight+2) & (advToutSeul >> 3*maxHeight+3));

		diagPlayer = (playerToutSeul & (playerToutSeul >> maxHeight) & (playerToutSeul >> 2*maxHeight) & (playerToutSeul >> 3*maxHeight));
		diagAdv = (advToutSeul & (advToutSeul >> maxHeight) & (advToutSeul >> 2*maxHeight) & (advToutSeul >> 3*maxHeight));

		antidiagPlayer = (playerToutSeul & (playerToutSeul >> maxHeight+2) & (playerToutSeul >> 2*maxHeight+4) & (playerToutSeul >> 3*maxHeight+6));
		antidiagAdv = (advToutSeul & (advToutSeul >> maxHeight+2) & (advToutSeul >> 2*maxHeight+4) & (advToutSeul >> 3*maxHeight+6));

		/*//On détermine tous les jetons susceptibles de former une séquence de 4 dans chaque direction pour chaque joueur
		//(y compris les jetons virtuels)
		columnPlayer = (columnPlayer | (columnPlayer << 1) | (columnPlayer << 2) | (columnPlayer << 3));
		columnAdv = (columnAdv | (columnAdv << 1) | (columnAdv << 2) | (columnAdv << 3));

		linePlayer = (linePlayer | (linePlayer << maxHeight+1) | (linePlayer << 2*maxHeight+2) | (linePlayer << 3*maxHeight+3));
		lineAdv = (lineAdv | (lineAdv << maxHeight+1) | (lineAdv << 2*maxHeight+2) | (lineAdv << 3*maxHeight+3));

		diagPlayer = (diagPlayer | (diagPlayer << maxHeight) | (diagPlayer << 2*maxHeight) | (diagPlayer << 3*maxHeight));
		diagAdv = (diagAdv | (diagAdv << maxHeight) | (diagAdv << 2*maxHeight) | (diagAdv << 3*maxHeight));

		antidiagPlayer = (antidiagPlayer | (antidiagPlayer << maxHeight+2) | (antidiagPlayer << 2*maxHeight+4) | (antidiagPlayer << 3*maxHeight+6));
		antidiagAdv = (antidiagAdv | (antidiagAdv << maxHeight+2) | (antidiagAdv << 2*maxHeight+4) | (antidiagAdv << 3*maxHeight+6));*/
		
		//Le "et" a lieu avec player/adv pour ne conserver que les jetons existants utiles pour le joueur en question
		int valPlayer =0;
		valPlayer += Long.bitCount(linePlayer & playerBoard);
		valPlayer += Long.bitCount((linePlayer << maxHeight+1) & playerBoard);
		valPlayer += Long.bitCount((linePlayer << 2*maxHeight+2) & playerBoard);
		valPlayer += Long.bitCount((linePlayer << 3*maxHeight+3) & playerBoard);
		
		valPlayer += Long.bitCount(columnPlayer & playerBoard);
		valPlayer += Long.bitCount((columnPlayer << 1) & playerBoard);
		valPlayer += Long.bitCount((columnPlayer << 2) & playerBoard);
		valPlayer += Long.bitCount((columnPlayer << 3) & playerBoard);
		
		valPlayer += Long.bitCount(diagPlayer & playerBoard);
		valPlayer += Long.bitCount((diagPlayer << maxHeight) & playerBoard);
		valPlayer += Long.bitCount((diagPlayer << 2*maxHeight) & playerBoard);
		valPlayer += Long.bitCount((diagPlayer << 3*maxHeight) & playerBoard);
		
		valPlayer += Long.bitCount(antidiagPlayer & playerBoard);
		valPlayer += Long.bitCount((antidiagPlayer << maxHeight+2) & playerBoard);
		valPlayer += Long.bitCount((antidiagPlayer << 2*maxHeight+4) & playerBoard);
		valPlayer += Long.bitCount((antidiagPlayer << 3*maxHeight+6) & playerBoard);
		
		int valAdv = 0;
		valAdv += Long.bitCount(lineAdv & adversaryBoard);
		valAdv += Long.bitCount((lineAdv << maxHeight+1) & adversaryBoard);
		valAdv += Long.bitCount((lineAdv << 2*maxHeight+2) & adversaryBoard);
		valAdv += Long.bitCount((lineAdv << 3*maxHeight+3) & adversaryBoard);
		
		valAdv += Long.bitCount(columnAdv & adversaryBoard);
		valAdv += Long.bitCount((columnAdv << 1) & adversaryBoard);
		valAdv += Long.bitCount((columnAdv << 2) & adversaryBoard);
		valAdv += Long.bitCount((columnAdv << 3) & adversaryBoard);
		
		valAdv += Long.bitCount(diagAdv & adversaryBoard);
		valAdv += Long.bitCount((diagAdv << maxHeight) & adversaryBoard);
		valAdv += Long.bitCount((diagAdv << 2*maxHeight) & adversaryBoard);
		valAdv += Long.bitCount((diagAdv << 3*maxHeight) & adversaryBoard);
		
		valAdv += Long.bitCount(antidiagAdv & adversaryBoard);
		valAdv += Long.bitCount((antidiagAdv << maxHeight+2) & adversaryBoard);
		valAdv += Long.bitCount((antidiagAdv << 2*maxHeight+4) & adversaryBoard);
		valAdv += Long.bitCount((antidiagAdv << 3*maxHeight+6) & adversaryBoard);
		
		return new ContinueStateValue(valPlayer - valAdv);
	}
}
