package Game;

public class PositionGris extends Position{
	long gris;

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
		long columnPlayer, linePlayer, diagPlayer, antidiagPlayer;
		long columnAdv, lineAdv, diagAdv, antidiagAdv;

		columnPlayer = (playerToutSeul & (playerToutSeul >> 1) & (playerToutSeul >> 2) & (playerToutSeul >> 3));
		columnAdv = (advToutSeul & (advToutSeul >> 1) & (advToutSeul >> 2) & (advToutSeul >> 3));

		linePlayer = (playerToutSeul & (playerToutSeul >> maxHeight+1) & (playerToutSeul >> 2*maxHeight+2) & (playerToutSeul >> 3*maxHeight+3));
		lineAdv = (advToutSeul & (advToutSeul >> maxHeight+1) & (advToutSeul >> 2*maxHeight+2) & (advToutSeul >> 3*maxHeight+3));

		diagPlayer = (playerToutSeul & (playerToutSeul >> maxHeight) & (playerToutSeul >> 2*maxHeight) & (playerToutSeul >> 3*maxHeight));
		diagAdv = (advToutSeul & (advToutSeul >> maxHeight) & (advToutSeul >> 2*maxHeight) & (advToutSeul >> 3*maxHeight));

		antidiagPlayer = (playerPos & (playerPos >> maxHeight+2) & (playerPos >> 2*maxHeight+4) & (playerPos >> 3*maxHeight+6));
		antidiagAdv = (advPos & (advPos >> maxHeight+2) & (advPos >> 2*maxHeight+4) & (advPos >> 3*maxHeight+6));

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

		this.playerPos=playerUtil;
		this.advPos=advUtil;
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
	public PositionGris(long playerPos, long advPos, int maxWidth, int maxHeight, long gris){
		super(playerPos, advPos, maxWidth, maxHeight);
		this.gris = gris;
	}
	
	@Override
	public int hashCode (){
		Position sym = symmetricPosition();
		int a = ((Long) playerPos).hashCode();
		int asym = ((Long) sym.playerPos).hashCode();
		int b = ((Long) advPos).hashCode();
		int bsym = ((Long) sym.advPos).hashCode();
		return (Math.min(a, asym)*Math.min(b, bsym));
	}
	
	/**
	 * Retourne la position symétrique
	 */
	@Override
	public PositionGris symmetricPosition (){
		
		long player = playerPos, adv = advPos, gr = gris;
		long playerSym = 0, advSym = 0, grSym = 0;
		
		//Entier correspondant à une colonne remplie de 1.
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
		}
		
		return new PositionGris(playerSym,advSym,maxWidth, maxHeight, grSym);
	}
	
	/**
	 * Test d'égalité : en tenant compte des symétries
	 */
	@Override
	public boolean equals (Object pos){
		if (pos instanceof PositionGris){
			PositionGris pos2 = (PositionGris) pos;
			boolean a =((playerPos==pos2.playerPos) && (advPos==pos2.advPos) && (gris == pos2.gris));
			PositionGris sym = pos2.symmetricPosition();
			boolean b = ((playerPos==sym.playerPos)&&(advPos==sym.advPos) && (gris == sym.gris));
			return (a||b);
		}
		return false;
	} 
}
