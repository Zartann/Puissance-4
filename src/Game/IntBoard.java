package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Search.ContinueStateValue;
import Search.StateValue;

public class IntBoard implements PlateauCourant {

	/**
	 * Plateaux contenant les positions du joueur et de son adversaire, sous forme d'entiers
	 */
	long playerBoard, adversaryBoard;
	
	/**
	 * Dimensions du plateau
	 */

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
			for(int line = maxHeight - 1; line >= 0; line--) //Parcours de bas en haut, de gauche à droite
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
		//Cette méthode nous a semblé inutile après coup
		return false;
	}

	@Override
	public StateValue result() {
		
		/* l'entier de columnPlayer va contenir après cette étape un 1 à chaque position 
		 * qui est le jeton le plus bas d'un alignement vertical de 4 jetons du joueur (et des 0 partout ailleurs)
		 * Les 7 autres variables ont des sens analogues dans chaque direction et pour chaque joueur : par exemple,
		 * l'entier de lineAdv contient un 1 pour chaque jeton le plus à gauche d'un alignement horizontal de 4 jetons de l'adversaire,
		 * et des 0 partout ailleurs
		 */
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
		
		//playerWins est différent de 0 ssi le joueur a constitué un alignement de 4 de ses jetons ; idem pour advWins
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
			//on ajoute le coup i ssi la colonne i n'est pas saturée
			if(heights[i] < maxHeight)
				shots.add(i);

		return shots;

	}
	
	/**
	 * Indique si true que c'est au joueur de jouer, sinon à l'adversaire.
	 * Pour le moment, on suppose que le joueur commence toujours
	 * Ne pas utiliser avec playCurrent ou playAdverse !!!
	 */
	//TODO : Implémenter une méthode à l'initialisation pour savoir à qui c'est le tour
	boolean whosNext = true;
	
	/**
	 * Pile des coups anciennement joués
	 */
	public Stack<Integer> lasts = new Stack<Integer>();

	@Override
	public void playCurrent(int i) {
		
		//Colonne saturée -> y jouer est impossible
		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);

		long box = 1;
		//box va contenir un 1 où va se trouver le nouveau jeton joué en colonne i
		box <<= ((maxHeight+1)*i + heights[i]);

		if((playerBoard & box) == 1 || (adversaryBoard & box) == 1)
			throw new IllegalMoveException("Impossible de jouer dans une case occupée : Colonne n°" + i
					+ "\n" + toString());

		//Plus habile que +=box
		playerBoard ^= box;

		//On actualise la hauteur de la colonne i, l'historique des coups joués et on indique que c'est à l'adversaire de jouer
		heights[i]++;
		lasts.push(i);
		whosNext = false;

	}

	@Override
	public void playAdverse(int i) {
		//Fonction analogue à playCurrent

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


	@Override
	public boolean playerIsNext(){
		return whosNext;
	}

	@Override
	public void playNext(int i) {
		//Généralise playCurrent et playAdverse ; commentaires analogues

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


	@Override
	public void undoLast() {

		if(lasts.isEmpty())
			throw new IllegalMoveException("No last Move");

		int lastColumn = lasts.pop();
		long box = 1;
		//box va contenir un 1 là où le dernier coup a été joué et uniquement là
		box <<= ((maxHeight+1)*lastColumn + heights[lastColumn] - 1);

		if((playerBoard & box) == 0 && (adversaryBoard & box) == 0)
			//ni le joueur ni l'adversaire n'ont un pion sur la case prétendûment jouée (en supoposant que heights est bien actualisé)
			throw new IllegalMoveException("Case non occupée : Colonne n°" + lastColumn
					+ "\n" + toString());

		//Si c'est au joueur de jouer, c'est l'adversaire qui a joué en dernier
		if(whosNext)
			//plus habile que adversaryBoard -= box
			adversaryBoard ^= box;
		else
			playerBoard ^= box;

		/*On actualise la hauteur de la colonne i et le joueur dont c'est le tour change ;
		L'historique des coups a déjà été modifiée par lasts.pop()*/
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
		//Finalement on modifie sur place notre IntBoard, d'où le renvoi de null
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
			for(int line = maxHeight-1; line >= 0; line--){ //Parcours de bas en haut, de gauche à droite : de 0 à 48 comme sur l'énoncé du problème

				if((player & 1) == 1)
					//player se termine par 1
					board[line][column] = Box.PLAYER;

				else if((adv & 1) == 1)
					//adv se termine par 1
					board[line][column] = Box.ADVERSARY;

				else
					board[line][column] = Box.VOID;
				//On a supposé qu'on ne rencontrait pas le cas où les deux joueurs ont un jeton sur la même case (d'autres méthodes l'éliminent)

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
			//Sans oublier les retours à la ligne
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
			//On ajoue pour caque colonne le nombre de cases encore vides dans cette colonne à nb
			nb += (maxHeight - heights[i]);
		}
		return nb;
	}

	@Override
	public StateValue eval() {
		//Fonction d'évaluation à 3 états LOSS, WIN et DRAW qui vérifie le principe de prudence
		return StateValue.DRAW;
	}

	@Override
	public ContinueStateValue evalContinue() {
		//Cette fonction va être aussi compatible avec le principe de prudence
		StateValue res = result();
		if(!res.isDraw())
			//La partie est terminée
			return new ContinueStateValue(res);
		//Dans aucun autre cas la valeur renvoyée sera LOSS ou WIN
			
		long playerToutSeul = 0;
		long advToutSeul = 0;
		//Remplissage

		//plateauPlein correspond à un plateau rempli, ligne du dessus comprise
		//En taille 6*7 on ne dépasse heureusement pas la capacité d'un long par ce décalage (au-delà on aurait par contre des résultas aberrants)
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

		//On élimine la ligne du dessus, actuellement remplie de 1
		// ^= est plus habile que -= ici
		playerToutSeul ^= ligneSup;
		advToutSeul ^= ligneSup;


		//On trouve les alignements ainsi créés (un jeton par alignement)
		//Cf commentaire dans result()
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

		/*
		columnPlayer = (columnPlayer | (columnPlayer << 1) | (columnPlayer << 2) | (columnPlayer << 3));
		columnAdv = (columnAdv | (columnAdv << 1) | (columnAdv << 2) | (columnAdv << 3));

		linePlayer = (linePlayer | (linePlayer << maxHeight+1) | (linePlayer << 2*maxHeight+2) | (linePlayer << 3*maxHeight+3));
		lineAdv = (lineAdv | (lineAdv << maxHeight+1) | (lineAdv << 2*maxHeight+2) | (lineAdv << 3*maxHeight+3));

		diagPlayer = (diagPlayer | (diagPlayer << maxHeight) | (diagPlayer << 2*maxHeight) | (diagPlayer << 3*maxHeight));
		diagAdv = (diagAdv | (diagAdv << maxHeight) | (diagAdv << 2*maxHeight) | (diagAdv << 3*maxHeight));

		antidiagPlayer = (antidiagPlayer | (antidiagPlayer << maxHeight+2) | (antidiagPlayer << 2*maxHeight+4) | (antidiagPlayer << 3*maxHeight+6));
		antidiagAdv = (antidiagAdv | (antidiagAdv << maxHeight+2) | (antidiagAdv << 2*maxHeight+4) | (antidiagAdv << 3*maxHeight+6));*/
		
		
		/*On détermine alors tous les jetons susceptibles de former un alignement, dans chaque direction, pour chaque joueur
		(extension du résultat précédent), et on compte chaque jeton autant de fois que le nombre d'alignements auxquels
		il est susceptible de participer (si le joueur qui le possède remplissait le reste du plateau)
		Le "et" a lieu avec playerBoard/adversaryBoard pour ne compter que les jetons déjà posés par le joueur en question*/
		int valPlayer =0,valAdv = 0;
		valPlayer += Long.bitCount(linePlayer & playerBoard);
		valPlayer += Long.bitCount(columnPlayer & playerBoard);
		valPlayer += Long.bitCount(diagPlayer & playerBoard);
		valPlayer += Long.bitCount(antidiagPlayer & playerBoard);
		
		valAdv += Long.bitCount(lineAdv & adversaryBoard);
		valAdv += Long.bitCount(columnAdv & adversaryBoard);
		valAdv += Long.bitCount(diagAdv & adversaryBoard);
		valAdv += Long.bitCount(antidiagAdv & adversaryBoard);
		
		for (int i=0;i<3;i++){
			valPlayer += Long.bitCount((linePlayer <<= maxHeight+1) & playerBoard);
			valPlayer += Long.bitCount((columnPlayer <<= 1) & playerBoard);
			valPlayer += Long.bitCount((diagPlayer <<= maxHeight) & playerBoard);
			valPlayer += Long.bitCount((antidiagPlayer <<= maxHeight+2) & playerBoard);
			
			valAdv += Long.bitCount((lineAdv <<= maxHeight+1) & adversaryBoard);
			valAdv += Long.bitCount((columnAdv <<= 1) & adversaryBoard);
			valAdv += Long.bitCount((diagAdv <<= maxHeight) & adversaryBoard);
			valAdv += Long.bitCount((antidiagAdv <<= maxHeight+2) & adversaryBoard);
		}
		/*valPlayer += Long.bitCount(linePlayer & playerBoard);
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
		valAdv += Long.bitCount((antidiagAdv << 3*maxHeight+6) & adversaryBoard);*/
		
		//Ainsi, la valeur continue renvoyée est positive ssi le joueur a plus de configurations potentielles gagnantes que l'adversaire
		return new ContinueStateValue(valPlayer - valAdv);
	}

	@Override
	public List<Integer> orderedValidShots() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int cons = maxWidth/2;
		//On joue en premier (si c'est possible) au milieu du plateau, côté droit si la largeur du plateau est paire
		if(heights[cons] < maxHeight)
			result.add(cons);
		for (int i=1;i<=(maxWidth-1)/2;i++){
			/*On cherche à remplir le plateau du milieu vers les extrémités, en alternant les côtés gauche et droit 
			  (seulement avec des coûts autorisés)*/
			if(heights[cons-i] < maxHeight)
				result.add(cons-i);
			if(heights[cons+i] < maxHeight)
				result.add(cons+i);
		}
		if ((maxWidth & 1) == 0 && heights[0] < maxHeight){
			//On ajoute en dernier le coup tout à gauche si c'est possible dans le cas où la largeur du plateau est paire
			result.add(0);
		}
		return result;
	}

	@Override
	public List<Integer> dynOrderedValidShots(int j) {
		//Même fonctionnement que orderedValidShots mis à part que cette fois on empêche le coup j d'être ajouté dans la liste
		ArrayList<Integer> result = new ArrayList<Integer>();
		int cons = maxWidth/2;
		if((heights[cons] < maxHeight)&&(cons!=j))
			result.add(cons);
		for (int i=1;i<=(maxWidth-1)/2;i++){
			if((heights[cons-i] < maxHeight)&&((cons-i)!=j))
				result.add(cons-i);
			if((heights[cons+i] < maxHeight)&&((cons+i)!=j))
				result.add(cons+i);
		}
		if (((maxWidth & 1) == 0 && heights[0] < maxHeight)&&(j!=0)){
			result.add(0);
		}
		return result;
	}
}
