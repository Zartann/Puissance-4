package Game;

public class Position {
	long playerPos;
	long advPos;
	
	//Constructeur
	public Position(long playerPos, long advPos) {
		super();
		this.playerPos = playerPos;
		this.advPos = advPos;
	}
	
	/**
	 * Renvoie le code de hachage de cette position
	 */
	public int hashCode (){
		int a = ((Long) playerPos).hashCode();
		int b = ((Long) advPos).hashCode();
		return (a*b);
	}

}
