package Game;
/*
 * On définit une interface pour les plateaux afin de définir les fonctions dont on va avoir besoin.
 * Ceci nous permet de ne pas être complètement dépendant du type de stockage des plateaux.
 * @author Alexis
 *
 */
public interface PlateauCourant {
	
	/**
	 * Vérifie que le plateau soit valide (i.e. contienne bien un nombre cohérent de pions par joueur).
	 * @return true si ok, false sinon
	 */
	public boolean checkValid();

	/**
	 * 
	 * @return Résultat du plateau courant -> True si victoire, false si défaite, null si draw ou indéterminé
	 */
	/*
	 * TODO : Pas nécessairement très efficace dans un premier temps. A améliorer par la suite.
	 * Doit pouvoir être faisable en temps linéaire en le nombre de cases au plus.
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
	 * Annule le dernier coup joué
	 */
	public void undoLast();
	
	/**
	 * 
	 * @return Instantané du plateau pour le joueur courant sous format d'un entier.
	 */
	public long playerStateConvert();
	
	/**
	 * 
	 * @return Instantané du plateau pour l'adversaire sous format d'un entier.
	 */
	public long adversaryStateConvert();
	
	/**
	 * 
	 * @param player
	 * @param adversary
	 * @return Plateau correspondant aux entiers player et adversary
	 */
	public PlateauCourant importFromLong(long player, long adversary); //Pas certain qu'elle serve
	
	/**
	 * Ecrit sur la console l'état actuel du jeu.
	 */
	public void print();
	
}
