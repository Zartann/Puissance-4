package Game;

import java.util.List;

import Search.StateValue;

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
	public StateValue result();
	
	/**
	 * 
	 * @return Tableau de six cases indiquant si le joueur peut jouer � la colonne i
	 */
	public List<Integer> validShots();
	
	/**
	 * Joueur courant joue en colonne i
	 * @param i entre 0 et 5
	 */
	public void playCurrent(int i);
	
	/**
	 * Joueur adverse joue en colonne i
	 * @param i entre 0 et 5
	 */
	public void playAdverse(int i);
	
	/**
	 * @return boolean indiquant si le joueur joue le coup suivant
	 */
	public boolean playerIsNext();
	
	/**
	 * Fait jouer le prochain coup en colonne i
	 * N�cessite de savoir � qui est-ce le tour
	 * @param i entre 0 et 5
	 */
	public void playNext(int i);
	
	/**
	 * Annule le dernier coup jou�
	 */
	public void undoLast();
	
	/**
	 * 
	 * @return Instantan� du plateau pour le joueur courant sous format d'un entier.
	 */
	public long playerStateConvert();
	
	/**
	 * 
	 * @return Instantan� du plateau pour l'adversaire sous format d'un entier.
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
	 * Convertit le plateau en String pour affichage
	 */
	public String toString();
	
	/**
	 * @return Renvoie la cl� associ�e � la position du plateau
	 */
	public Position cle ();
	
	/**
	 * @return Renvoie la cl� avec jetons gris�s associ�e au plateau
	 */
	public PositionGris cleGris();
	
	/**
	 * @return Nombre de coups restants � jouer sur le plateau.
	 */
	public int nombreCoupsRestants();
	
	/**
	 * @return Evalue la position
	 */
	public StateValue eval();
}
