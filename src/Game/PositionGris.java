package Game;

import Search.IteratifHash;

public class PositionGris extends Position{
	long gris;
	long playerPosSym, advPosSym, grisSym;

	/**
	 * 
	 * @param playerPos
	 * @param advPos
	 * @param maxWidth
	 * @param maxHeight
	 * Les positions inutiles du plateau sont d�termin�es (et instanci�es dans gris) directement dans ce constructeur
	 */

	public PositionGris(long playerPos, long advPos, int maxWidth, int maxHeight) {
		super(playerPos, advPos, maxWidth, maxHeight);

		//On remplit le tableau de jetons blancs/noirs puis on grise les jetons ne donnant pas d'alignements de 4
		long playerToutSeul = 0;
		long advToutSeul = 0;
		//Remplissage

		//plateauPlein correspond � un plateau rempli, ligne du dessus comprise.
		long plateauPlein = ((long) 1) << (maxWidth * (maxHeight+1));
		plateauPlein--;

		//PlayerToutSeul et advToutSeul vont contenir le plateau en entier avec que des 1 sauf aux 
		//positions des jetons de Adversary et de Player respectivement
		playerToutSeul = advPos ^ plateauPlein;
		advToutSeul = playerPos ^ plateauPlein;

		//ligneSup correspond � la seule ligne du dessus remplie avec des 1.
		long ligneSup = 0;
		for(int i = 0; i < maxWidth; i++){
			ligneSup++;
			ligneSup <<= maxHeight+1;
		}
		ligneSup >>= 1;

		//On �limine la ligne du dessus, actuellement remplie de 0
		playerToutSeul ^= ligneSup;
		advToutSeul ^= ligneSup;


		//On trouve les alignements ainsi cr��s
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

		//On d�termine tous les jetons susceptibles de former une s�quence de 4 dans chaque direction pour chaque joueur
		//(y compris les jetons virtuels)
		columnPlayer = (columnPlayer | (columnPlayer << 1) | (columnPlayer << 2) | (columnPlayer << 3));
		columnAdv = (columnAdv | (columnAdv << 1) | (columnAdv << 2) | (columnAdv << 3));

		linePlayer = (linePlayer | (linePlayer << maxHeight+1) | (linePlayer << 2*maxHeight+2) | (linePlayer << 3*maxHeight+3));
		lineAdv = (lineAdv | (lineAdv << maxHeight+1) | (lineAdv << 2*maxHeight+2) | (lineAdv << 3*maxHeight+3));

		diagPlayer = (diagPlayer | (diagPlayer << maxHeight) | (diagPlayer << 2*maxHeight) | (diagPlayer << 3*maxHeight));
		diagAdv = (diagAdv | (diagAdv << maxHeight) | (diagAdv << 2*maxHeight) | (diagAdv << 3*maxHeight));

		antidiagPlayer = (antidiagPlayer | (antidiagPlayer << maxHeight+2) | (antidiagPlayer << 2*maxHeight+4) | (antidiagPlayer << 3*maxHeight+6));
		antidiagAdv = (antidiagAdv | (antidiagAdv << maxHeight+2) | (antidiagAdv << 2*maxHeight+4) | (antidiagAdv << 3*maxHeight+6));

		//On s'abstrait de la direction des s�quences
		long playerVirtualUtil = columnPlayer | linePlayer | diagPlayer | antidiagPlayer ;
		long advVirtualUtil = columnAdv | lineAdv | diagAdv | antidiagAdv;

		//Le "et" a lieu avec player/adv pour ne conserver que les jetons existants utiles pour le joueur en question
		long playerUtil = playerVirtualUtil & playerPos;
		long advUtil = advVirtualUtil & advPos;

		//Les jetons gris sont les inutiles pour les deux joueurs
		gris = (playerPos ^ playerUtil) | (advPos ^ advUtil);

		this.playerPos=playerUtil;
		this.advPos=advUtil;
		
		//********** On d�termine la position sym�trique
		
		long player = this.playerPos, adv = this.advPos, gr = gris;
		
		//Entier correspondant � une colonne remplie de 1.
		long column = (long) 1 << (maxHeight + 1);
		column--;
		
		for(int i = 0; i < maxWidth; i++){
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
	 * Constructeur � n'utiliser que si on conna�t d�j� les jetons gris et qu'ils ne sont pas pr�sents
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
		//Position sym = symmetricPosition();
		int a = ((Long) playerPos).hashCode();
		//int asym = ((Long) sym.playerPos).hashCode();
		int asym = ((Long) playerPosSym).hashCode();
		int b = ((Long) advPos).hashCode();
		//int bsym = ((Long) sym.advPos).hashCode();
		int bsym = ((Long) advPosSym).hashCode();
		int hash = (Math.min(a, asym)*Math.min(b, bsym)) % IteratifHash.tailleTable;
		if(hash < 0)
			hash += IteratifHash.tailleTable;
		return hash;
	}
	
	/**
	 * Retourne la position sym�trique
	 */
//	@Override
	//public PositionGris symmetricPosition (){
		
		/*long player = playerPos, adv = advPos, gr = gris;
		long playerSym = 0, advSym = 0, grSym = 0;
		
		//Entier correspondant � une colonne remplie de 1.
		long column = (long) 1 << (maxHeight + 1);
		column--;
		
		for(int i = 0; i < maxWidth; i++){
			playerSym <<= maxHeight+1;
			advSym <<= maxHeight+1;
			grSym <<= maxHeight+1;
			
			playerSym |= (player & column);
			advSym |= (adv & column);
			grSym |= (gr & column);
			
			player >>= maxHeight+1;
			adv >>= maxHeight+1;
			gr >>= maxHeight+1;
		}*/
		
	//	return new PositionGris(playerPosSym,advPosSym,maxWidth, maxHeight, grisSym, playerPos, advPos, gris);
	//}
	
	/**
	 * Indique si la position actuelle est la sym�trique de pos2
	 * @param pos2
	 * @return boolean
	 */
	public boolean isSymetrical(PositionGris pos2){
		return ((playerPos==pos2.playerPosSym)&&(advPos==pos2.advPosSym) && (gris == pos2.grisSym));
	}
	
	/**
	 * Test d'�galit� : en tenant compte des sym�tries
	 */
	@Override
	public boolean equals (Object pos){
		if (pos instanceof PositionGris){
			PositionGris pos2 = (PositionGris) pos;
			boolean same =((playerPos==pos2.playerPos) && (advPos==pos2.advPos) && (gris == pos2.gris));
			//PositionGris sym = pos2.symmetricPosition();
			boolean symetrical = ((playerPos==pos2.playerPosSym)&&(advPos==pos2.advPosSym) && (gris == pos2.grisSym));
			return (same||symetrical);
		}
		return false;
	} 
	
	/**
	 * Affiche le plateau en affichant les pions gris avec 1.
	 */
	public String toString(){
		String result = "";

		long player = playerPos, adv = advPos, gr = gris;

		int[][] board = new int[maxHeight][maxWidth];

		for(int column = 0; column < maxWidth; column++){
			for(int line = maxHeight-1; line >= 0; line--){ //Parcours de bas en haut, de gauche � droite

				if((player & 1) == 1)
					board[line][column] = 1;

				else if((adv & 1) == 1)
					board[line][column] = 2;
				
				else if((gr & 1) == 1)
					board[line][column] = 3;

				else
					board[line][column] = 0;

				//On d�cale d'un bit vers la droite
				player >>= 1;
				adv >>= 1;
				gr >>= 1;
			}

			//On d�cale encore d'un bit � droite � chaque colonne pour g�rer la ligne vide en haut.
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
