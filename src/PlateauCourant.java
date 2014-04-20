/*
 * On définit une interface pour les plateaux afin de définir les fonctions dont on va avoir besoin.
 * Ceci nous permet de ne pas être complètement dépendant du type de stockage des plateaux.
 * @author Alexis
 *
 */
public interface PlateauCourant {

	/**
	 * 
	 * @return Résultat du plateau courant -> True si victoire, false si défaite, null si draw ou indéterminé
	 */
	public Boolean result();
	
	/**
	 * 
	 * @return Tableau de six cases indiquant si le joueur peut jouer à la colonne i
	 */
	//TODO : Peut-être à modifier en liste des colonnes (liste de byte).
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
	 * Dernier coup joué
	 * Inutile d'utiliser plus qu'un byte pour stocker la colonne
	 * -1 signifie qu'il n'y a pas de dernier coup à annuler.
	 */
	public byte last = -1;
	
	/**
	 * Annule le dernier coup joué
	 */
	public void undoLast();
	
	/**
	 * Renvoie un instantané du plateau sous format d'un entier.
	 * @return
	 */
	public long statusConvert();
	
}
