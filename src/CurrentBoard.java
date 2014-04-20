
public class CurrentBoard implements PlateauCourant {
	
	/**
	 * Plateau contenant l'état actuel du jeu.
	 * Pour le moment, on choisit de le stocker dans une matrice.
	 * Si une case est inoccupée, elle est à null.
	 */
	public Boolean[][] board;
	
	/**
	 * Contient le nombre de jetons de chaque colonne
	 */
	public int[] heigths;
	
	/**
	 * Crée un nouveau tableau initialisé à null de taille width*height
	 * @param width
	 * @param height
	 */
	CurrentBoard(int width, int height){
		board = new Boolean[height][width];
		heigths = new int[width];
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean result() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean[] validShots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playCurrent(byte i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playAdverse(byte i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void undoLast() {
		// TODO Auto-generated method stub

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
