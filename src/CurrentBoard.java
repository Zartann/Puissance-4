
public class CurrentBoard implements PlateauCourant {
	
	/**
	 * Plateau contenant l'état actuel du jeu.
	 * Pour le moment, on choisit de le stocker dans une matrice.
	 */
	public Box[][] board;
	
	public int maxWidth, maxHeight;
	
	/**
	 * Contient le nombre de jetons de chaque colonne
	 */
	public int[] heights;
	
	/**
	 * Crée un nouveau tableau initialisé à null de taille width*height
	 * @param width
	 * @param height
	 */
	CurrentBoard(int width, int height){
		board = new Box[height][width];
		
		for(Box[] line : board){
			for(Box box : line){
				box = Box.VOID;
			}
		}
		
		maxWidth = width;
		maxHeight = height;
		
		heights = new int[width];
	}

	@Override
	public boolean checkValid() {
		int countPlayer = 0, countAdv = 0;
		
		for(Box[] line : board){
			for(Box box : line){
				if(box.isPlayer())
					countPlayer++;
				else if(box.isAdv())
					countAdv++;
			}
		}
		
		int diff = countPlayer - countAdv;
		return (diff <= 1 && diff >= -1);
	}

	@Override
	public Boolean result() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean[] validShots() {

		boolean[] shots = new boolean[maxWidth];
		
		for(int i = 0; i < maxWidth; i++)
			shots[i] = (heights[i] < maxHeight);
		
		return shots;
	}

	@Override
	public void playCurrent(byte i) {

		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);
		
		board[i][heights[i]] = Box.PLAYER;
		
		heights[i]++;
		last = i;
		
	}

	@Override
	public void playAdverse(byte i) {
		
		if(heights[i] >= maxHeight)
			throw new IllegalMoveException("Colonne n°" + i);
		
		board[i][heights[i]] = Box.ADVERSARY;
		
		heights[i]++;
		last = i;
	}

	public byte last = -1;
	
	@Override
	public void undoLast() {

		if(last == -1)
			throw new IllegalMoveException("No last Move");
		
		board[last][heights[last]] = Box.VOID;
		
		heights[last]--;
		last = -1;
		
	}

	@Override
	public long playerStateConvert() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long adversaryStateConvert() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PlateauCourant importFromLong(long player, long adversary) {
		// TODO Auto-generated method stub
		return null;
	}

}
