package Game;

import Search.IteratifHash;

public class PositionGris extends Position{
	/**
	 * gris représente les jetons ne pouvant participer à aucun alignement à venir (jetons isolés)
	 */
	long gris;
	/**
	 * jetons utiles du joueur et de l'adversaire, et jetons isolés, pour la position symétrique
	 */
	long playerPosSym, advPosSym, grisSym;

	/**
	 * 
	 * @param playerPos
	 * @param advPos
	 * @param maxWidth
	 * @param maxHeight
	 * Les positions inutiles du plateau sont déterminées (et instanciées dans gris) directement dans ce constructeur
	 */

	public PositionGris(long playerPos, long advPos, int maxWidth, int maxHeight) {
		super(playerPos, advPos, maxWidth, maxHeight);

		//On remplit le tableau de jetons blancs/noirs puis on grise les jetons ne donnant pas d'alignements de 4
		long playerToutSeul = 0;
		long advToutSeul = 0;
		//Remplissage

		//plateauPlein correspond à un plateau rempli, ligne du dessus comprise.
		//Pas de débordement de capacité du long en taille 6*7
		long plateauPlein = ((long) 1) << (maxWidth * (maxHeight+1));
		plateauPlein--;

		//PlayerToutSeul et advToutSeul vont contenir le plateau en entier avec que des 1 sauf aux 
		//positions des jetons de Adversary et de Player respectivement
		playerToutSeul = advPos ^ plateauPlein;
		advToutSeul = playerPos ^ plateauPlein;

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
		/* columnPlayer va contenir après cette étape un 1 à chaque position 
		 * qui est le jeton le plus bas d'un alignement vertical de 4 jetons du joueur (et des 0 partout ailleurs)
		 * Les 7 autres variables ont des sens analogues dans chaque direction et pour chaque joueur : par exemple,
		 * lineAdv contient un 1 pour chaque jeton le plus à gauche d'un alignement horizontal de 4 jetons de l'adversaire,
		 * et des 0 partout ailleurs
		 */
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

		//On détermine tous les jetons susceptibles de former une séquence de 4 dans chaque direction pour chaque joueur
		//(y compris les jetons virtuels)
		columnPlayer = (columnPlayer | (columnPlayer << 1) | (columnPlayer << 2) | (columnPlayer << 3));
		columnAdv = (columnAdv | (columnAdv << 1) | (columnAdv << 2) | (columnAdv << 3));

		linePlayer = (linePlayer | (linePlayer << maxHeight+1) | (linePlayer << 2*maxHeight+2) | (linePlayer << 3*maxHeight+3));
		lineAdv = (lineAdv | (lineAdv << maxHeight+1) | (lineAdv << 2*maxHeight+2) | (lineAdv << 3*maxHeight+3));

		diagPlayer = (diagPlayer | (diagPlayer << maxHeight) | (diagPlayer << 2*maxHeight) | (diagPlayer << 3*maxHeight));
		diagAdv = (diagAdv | (diagAdv << maxHeight) | (diagAdv << 2*maxHeight) | (diagAdv << 3*maxHeight));

		antidiagPlayer = (antidiagPlayer | (antidiagPlayer << maxHeight+2) | (antidiagPlayer << 2*maxHeight+4) | (antidiagPlayer << 3*maxHeight+6));
		antidiagAdv = (antidiagAdv | (antidiagAdv << maxHeight+2) | (antidiagAdv << 2*maxHeight+4) | (antidiagAdv << 3*maxHeight+6));

		//On s'abstrait de la direction des séquences
		long playerVirtualUtil = columnPlayer | linePlayer | diagPlayer | antidiagPlayer ;
		long advVirtualUtil = columnAdv | lineAdv | diagAdv | antidiagAdv;

		//Le "et" a lieu avec player/adv pour ne conserver que les jetons existants utiles pour le joueur en question
		long playerUtil = playerVirtualUtil & playerPos;
		long advUtil = advVirtualUtil & advPos;

		//Les jetons gris sont les inutiles pour les deux joueurs
		gris = (playerPos ^ playerUtil) | (advPos ^ advUtil);

		//Les champs playerPos et advPos ne prennent que les jetons non isolés
		this.playerPos=playerUtil;
		this.advPos=advUtil;
		
		//********** On détermine la position symétrique
		
		long player = this.playerPos, adv = this.advPos, gr = gris;
		
		//Entier correspondant à une colonne remplie de 1.
		long column = (long) 1 << (maxHeight + 1);
		column--;
		
		for(int i = 0; i < maxWidth; i++){
			//On symétrise player, adv et gr (même procédé que dans symmetricPosition () de la classe Position)
			playerPosSym <<= maxHeight+1;
			advPosSym <<= maxHeight+1;
			grisSym <<= maxHeight+1;
			
			playerPosSym |= (player & column);
			advPosSym |= (adv & column);
			grisSym |= (gr & column);
			
			player >>= maxHeight+1;
			adv >>= maxHeight+1;
			gr >>= maxHeight+1;
		}
	}
	
	/**
	 * Constructeur à n'utiliser que si on connaît déjà les jetons gris et qu'ils ne sont pas présents
	 * dans les autres plateaux
	 * @param playerPos
	 * @param advPos
	 * @param maxWidth
	 * @param maxHeight
	 * @param gris
	 */
	public PositionGris(long playerPos, long advPos, int maxWidth, int maxHeight, long gris,
			long playerPosSym, long advPosSym, long grisSym){
		super(playerPos, advPos, maxWidth, maxHeight);
		this.gris = gris;
		this.playerPosSym = playerPosSym;
		this.advPosSym = advPosSym;
		this.grisSym = grisSym;
		
	}
	
	@Override
	public int hashCode (){
		//Le hashCode doit être identique en passant à la position symétrique
		int a = ((Long) playerPos).hashCode();
		int asym = ((Long) playerPosSym).hashCode();
		int b = ((Long) advPos).hashCode();
		int bsym = ((Long) advPosSym).hashCode();
		//Ci-dessous notre fonction de hash initiale ; nous l'avons remplacée en choisissant 373 car ce nombre est premier et
		//car sa décomposition en base 2 rend les calculs faciles
		//int hash = (Math.min(a, asym)*Math.min(b, bsym)) % IteratifHash.tailleTable;
		int hash = (Math.min(373*a+b,373*asym+bsym)) % IteratifHash.tailleTable;
		if(hash < 0)
			hash += IteratifHash.tailleTable;
		//On évite les OutOfBoundsException en garantissant ainsi que 0 <= hash < IteratifHash.tailleTable
		return hash;
	}

	/**
	 * Indique si la position actuelle est la symétrique de pos2
	 * @param pos2
	 * @return boolean
	 */
	public boolean isSymetrical(PositionGris pos2){
		return ((playerPos==pos2.playerPosSym)&&(advPos==pos2.advPosSym) && (gris == pos2.grisSym));
	}
	
	/**
	 * Renvoie le coup symétrique de i
	 * @param i
	 * @return
	 */
	public int symetricalShot(int i){
		return (maxWidth - i - 1);
	}
	
	/**
	 * Test d'égalité : en tenant compte des symétries
	 */
	@Override
	public boolean equals (Object pos){
		if (pos instanceof PositionGris){
			PositionGris pos2 = (PositionGris) pos;
			boolean same =((playerPos==pos2.playerPos) && (advPos==pos2.advPos) && (gris == pos2.gris));
			boolean symetrical = ((playerPos==pos2.playerPosSym)&&(advPos==pos2.advPosSym) && (gris == pos2.grisSym));
			//On renvoie vrai si les positions sont identiques ou symétriques l'une de l'autre
			return (same||symetrical);
		}
		//Si pos n'est pas de type PositionGris, renvoyer false
		return false;
	} 
	
	/**
	 * Affiche le plateau en affichant les pions gris avec 1.
	 */
	public String toString(){
		String result = "";

		long player = playerPos, adv = advPos, gr = gris;

		int[][] board = new int[maxHeight][maxWidth];
		//board va contenir une forme visualisable du plateau de jeu

		for(int column = 0; column < maxWidth; column++){
			for(int line = maxHeight-1; line >= 0; line--){ //Parcours de bas en haut, de gauche à droite

				if((player & 1) == 1)
					//player se termine par un 1
					board[line][column] = 1;

				else if((adv & 1) == 1)
					board[line][column] = 2;
				
				else if((gr & 1) == 1)
					board[line][column] = 3;

				else
					//Pas de jeton ici
					board[line][column] = 0;

				//On décale d'un bit vers la droite
				player >>= 1;
				adv >>= 1;
				gr >>= 1;
			}

			//On décale encore d'un bit à droite à chaque colonne pour gérer la ligne vide en haut.
			player >>= 1;
			adv >>= 1;
			gr >>= 1;
		}

		//Ensuite on affiche
		for(int[] line : board){
			for(int box : line){

				if(box == 1)
					result += "0";
				else if(box == 2)
					result += "@";
				else if(box == 3)
					result += "1";
				else
					result += ".";

			}
			result += "\n";
		}

		return result;
	}
}
