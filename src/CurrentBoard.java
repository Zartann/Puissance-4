
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
	/*
	 * On va checker chaque ligne, chaque colonne, chaque diagonale et chaque antidiagonale.
	 * Ceci donne une complexité en 4*nombre de cases.
	 * Il y a en tout :
	 * 		height lignes
	 * 		width colonnes
	 * 		height + width - 1 diagonales, dont 6 inutiles (car moins de 4 cases dessus)
	 * 		autant d'anti-diagonales
	 * 
	 * Les diagonales sont données par i-j = cste
	 * Les antidiagonales sont données par i+j = cste
	 */
	public Boolean result() {

		boolean playerWins = false, advWins = false;

		/*
		 * Nombres de jetons consécutifs pour le joueur et son adversaire
		 */
		int consPlayer = 0, consAdv = 0;

		//Vérifications sur les colonnes
		for(int i = 0; i < maxWidth; i++){

			consPlayer = 0;
			consAdv = 0;

			for(int j = 0; j < maxHeight; j++){

				if(board[j][i] == Box.PLAYER){
					consPlayer++;
					consAdv = 0;
					
					if(consPlayer == 4)
						playerWins = true;
				}
				else if(board[j][i] == Box.ADVERSARY){
					consAdv++;
					consPlayer = 0;
					
					if(consAdv == 4)
						advWins = true;
				}
				else{
					consPlayer = 0;
					consAdv = 0;
					//Si on rencontre une case vide, inutile de monter plus, car on est sur une colonne.
					break;
				}
			}
		}


		//Vérifications sur les lignes
		for(int i = 0; i < maxHeight; i++){

			consPlayer = 0;
			consAdv = 0;

			for(int j = 0; j < maxWidth; j++){

				if(board[i][j] == Box.PLAYER){
					consPlayer++;
					consAdv = 0;
					
					if(consPlayer == 4)
						playerWins = true;
				}
				else if(board[i][j] == Box.ADVERSARY){
					consAdv++;
					consPlayer = 0;
					
					if(consAdv == 4)
						advWins = true;
				}
				else{
					consPlayer = 0;
					consAdv = 0;
				}
			}
		}
		
		
		//TODO : Vérification sur les diagonales et les anti-diagonales
		
		//DRAW si et seulement si aucun ne gagne ou les deux gagnent
		//ATTENTION : On passe par la victoire d'un des deux avant un DRAW où les deux gagnent ! Ne pas arriver là !
		if(playerWins && !advWins)
			return true;
		else if(advWins && !playerWins)
			return false;
		else
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
