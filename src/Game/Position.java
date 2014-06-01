package Game;

import Search.IteratifHash;

public class Position {
	/**
	 * playerPos et advPos repr�sentent respectivement les jetons du joueur et de l'adversaire
	 * maxWidth et maxHeight repr�sentent les dimensions du plateau de jeu
	 */
	public long playerPos;
	public long advPos;
	int maxWidth;
	int maxHeight;
	
	//Constructeur
	public Position(long playerPos, long advPos, int maxWidth, int maxHeight) {
		super();
		this.playerPos = playerPos;
		this.advPos = advPos;
		this.maxWidth=maxWidth;
		this.maxHeight=maxHeight;
	}
	
	/**
	 * Renvoie le code de hachage de cette position
	 */
	@Override
	public int hashCode (){
		Position sym = symmetricPosition();
		int a = ((Long) playerPos).hashCode();
		int asym = ((Long) sym.playerPos).hashCode();
		int b = ((Long) advPos).hashCode();
		int bsym = ((Long) sym.advPos).hashCode();
		//Cette formule garantit qu'une position et sa position sym�trique ont le m�me hashCode
		int hash = (Math.min(a, asym)*Math.min(b, bsym)) % IteratifHash.tailleTable;
		if(hash < 0)
			hash += IteratifHash.tailleTable;
		//Ainsi on garantit 0 <= hash < IteratifHash.tailleTable : pas d'IndexOutofBoundsException ici
		return hash;
	}
	
	/*/**
	 * 
	 * @param a (long)
	 * @return le i�me bit de a (0<=i<49)
	 */
	/*public static byte trouveIemeBit (long a, int i){
		long b = a >> i;
		return (byte)(b&1);
	}*/
	
	/*/**
	 * place le bit k � l'emplacement i dans a (long)
	 */
	/*public static long placeIemeBit (long a, int i, byte k){
		//if ((k!=1)&&(k!=0)) return a;
		if (trouveIemeBit(a,i)==k) return a;
		long b = 1;
		b <<=i;
		return a^b;
		/*if (k==1) return (a+b);
		else return (a-b);
	}*/
	
	/**
	 * Retourne la position sym�trique
	 */
	public Position symmetricPosition (){
		
		long player = playerPos, adv = advPos;
		long playerSym = 0, advSym = 0;
		
		//Entier correspondant � une colonne remplie de 1.
		long column = (long) 1 << (maxHeight + 1);
		column--;
		
		for(int i = 0; i < maxWidth; i++){//de gauche � droite
			//On lib�re la colonne de droite de playerSym et advSym en les d�calant d'une colonne � gauche
			playerSym <<= maxHeight+1;
			advSym <<= maxHeight+1;
			
			//on ajoute � playerSym et advSym les jetons de la colonne de droite de player et adv dans leur colonne de droite
			playerSym |= (player & column);
			advSym |= (adv & column);
			
			//On d�cale player et adv d'une colonne vers la droite
			player >>= maxHeight+1;
			adv >>= maxHeight+1;
		}
		//Ainsi on a rempli playerSym et advSym colonne par colonne en sym�trisant player et adv
		return new Position(playerSym,advSym,maxWidth, maxHeight);
	}
	
	/**
	 * Test d'�galit� : en tenant compte des sym�tries
	 */
	@Override
	public boolean equals (Object pos){
		if (pos instanceof Position){
			Position pos2 = (Position) pos;
			boolean a =((playerPos==pos2.playerPos) && (advPos==pos2.advPos));
			Position sym = pos2.symmetricPosition();
			boolean b = ((playerPos==sym.playerPos)&&(advPos==sym.advPos));
			// On renvoie true ssi pos est this ou la position sym�trique de this
			return (a||b);
		}
		//Si pos n'est pas de type Position equals doit renvoyer false
		return false;
	} 

}
