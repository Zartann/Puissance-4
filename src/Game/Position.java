package Game;

public class Position {
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
		return (Math.min(a, asym)*Math.min(b, bsym));
	}
	
	/**
	 * 
	 * @param a (long)
	 * @return le ième bit de a (0<=i<49)
	 */
	/*public static byte trouveIemeBit (long a, int i){
		long b = a >> i;
		return (byte)(b&1);
	}*/
	
	/**
	 * place le bit k à l'emplacement i dans a (long)
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
	 * Retourne la position symétrique
	 */
	public Position symmetricPosition (){
		
		long player = playerPos, adv = advPos;
		long playerSym = 0, advSym = 0;
		
		//Entier correspondant à une colonne remplie de 1.
		long column = (long) 1 << (maxHeight + 1);
		column--;
		
		for(int i = 0; i < maxWidth; i++){
			playerSym <<= maxHeight+1;
			advSym <<= maxHeight+1;
			
			playerSym |= (player & column);
			advSym |= (adv & column);
			
			player >>= maxHeight+1;
			adv >>= maxHeight+1;
		}
		
		return new Position(playerSym,advSym,maxWidth, maxHeight);
	}
	
	/**
	 * Test d'égalité : en tenant compte des symétries
	 */
	@Override
	public boolean equals (Object pos){
		if (pos instanceof Position){
			Position pos2 = (Position) pos;
			boolean a =((playerPos==pos2.playerPos) && (advPos==pos2.advPos));
			Position sym = pos2.symmetricPosition();
			boolean b = ((playerPos==sym.playerPos)&&(advPos==sym.advPos));
			return (a||b);
		}
		return false;
	} 

}
