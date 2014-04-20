/*
 * On d�finit une interface pour les plateaux afin de d�finir les fonctions dont on va avoir besoin.
 * Ceci nous permet de ne pas �tre compl�tement d�pendant du type de stockage des plateaux.
 * @author Alexis
 *
 */
public interface PlateauCourant {
	
	/**
	 * V�rifie que le plateau soit valide (i.e. contienne bien un nombre coh�rent de pions par joueur).
	 * @return true si ok, false sinon
	 */
	public boolean checkValid();

	/**
	 * 
	 * @return R�sultat du plateau courant -> True si victoire, false si d�faite, null si draw ou ind�termin�
	 */
	/*
	 * TODO : Pas n�cessairement tr�s efficace dans un premier temps. A am�liorer par la suite.
	 * Doit pouvoir �tre faisable en temps lin�aire en le nombre de cases au plus.
	 */
	public Boolean result();
	
	/**
	 * 
	 * @return Tableau de six cases indiquant si le joueur peut jouer � la colonne i
	 */
	//TODO : Peut-�tre � modifier en liste des colonnes (liste de byte).
	public boolean[] validShots();
	
	/**
	 * Joueur courant joue en colonne i
	 * @param i entre 0 et 5
	 */
	public void playCurrent(byte i);
	
	/**
	 * Joueur adverse joue en colonne i
	 * @param i entre 0 et 5
	 */
	public void playAdverse(byte i);
	
	/**
	 * Dernier coup jou�
	 * Inutile d'utiliser plus qu'un byte pour stocker la colonne
	 * -1 signifie qu'il n'y a pas de dernier coup � annuler.
	 */
	public byte last = -1;
	
	/**
	 * Annule le dernier coup jou�
	 */
	public void undoLast();
	
	/**
	 * 
	 * @return Instantan� du plateau sous format d'un entier.
	 */
	public long statusConvert();
	
	/**
	 * 
	 * @param i Etat du plateau
	 * @return Plateau correspondant � l'entier i
	 */
	public PlateauCourant importFromLong(long i); //Pas certain qu'elle serve
	
}
