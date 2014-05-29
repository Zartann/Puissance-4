package Game;

public class Position {
	long playerPos;
	long advPos;
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
	public int hashCode (){
		int a = ((Long) playerPos).hashCode();
		int b = ((Long) advPos).hashCode();
		return (a*b);
	}
	
	/**
	 * 
	 * @param a (long)
	 * @return le ième bit de a (0<=i<49)
	 */
	public static byte trouveIemeBit (long a, int i){
		long b = a >> i;
		return (byte)(b&1);
	}
	
	/**
	 * place le bit k à l'emplacement i dans a (long)
	 */
	public static long placeIemeBit (long a, int i, byte k){
		//if ((k!=1)&&(k!=0)) return a;
		if (trouveIemeBit(a,i)==k) return a;
		long b = 1;
		b <<=i;
		return a^b;
		/*if (k==1) return (a+b);
		else return (a-b);*/
	}
	
	/**
	 * Retourne la position symétrique
	 */
	public Position symmetricPosition (){
		//facteur utile
		int cons = maxHeight+1;
		//facteur à décrémenter
		int decr = maxWidth;
		//Réceptacles : b pour playerPos, c pour advPos
		long b=0, c=0;
		//Traitement des positions colonne par colonne
		for (int i=0;i<=(maxWidth-1)/2;i++){
			//On remonte les lignes
			for (int j=i*cons;j<(i+1)*cons;j++){
				//j2, le symétrique de j
				int j2 = j+cons*decr;
				b=placeIemeBit(b,j,trouveIemeBit(playerPos, j2));
				b=placeIemeBit(b,j2,trouveIemeBit(playerPos, j));
				c=placeIemeBit(c,j,trouveIemeBit(advPos, j2));
				c=placeIemeBit(c,j2,trouveIemeBit(advPos, j));
			}
			decr-=2;
		}
		return new Position (b,c,maxWidth, maxHeight);
	}
	
	/**
	 * Test d'égalité : en tenant compte des symétries
	 */
	public boolean equals (Position pos){
		
		boolean a =((playerPos==pos.playerPos) && (advPos==pos.advPos));
		Position sym = pos.symmetricPosition();
		boolean b = ((playerPos==sym.playerPos)&&(advPos==sym.advPos));
		return (a||b);
	} 

}
