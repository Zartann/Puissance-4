/*
 * On d�finit une interface pour les plateaux afin de d�finir les fonctions dont on va avoir besoin.
 * Ceci nous permet de ne pas �tre compl�tement d�pendant du type de stockage des plateaux.
 * @author Alexis
 *
 */
public interface PlateauCourant {

	/**
	 * 
	 * @return R�sultat du plateau courant -> True si victoire, false si d�faite, null si draw ou ind�termin�
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
	 * Renvoie un instantan� du plateau sous format d'un entier.
	 * @return
	 */
	public long statusConvert();
	
}
