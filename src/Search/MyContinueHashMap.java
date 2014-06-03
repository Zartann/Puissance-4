package Search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Game.PositionGris;
//Notre table de hachage ad hoc dont on a �tendu les valeurs au-del� de WIN, LOSS et DRAW par des ContinueStateValue(WithBound)
public class MyContinueHashMap implements Map<PositionGris, ContinueStateValueWithBound> {
	/**
	 *Information totale contenue dans une case de la table
	 */
	private class MyValue{
		public PositionGris pos;
		public ContinueStateValueWithBound val;
		public int cout = 0;
		
		//Deux constructeurs diff�rents en fonction du mode de remplacement choisi
		public MyValue(PositionGris key, ContinueStateValueWithBound value) {
			pos = key;
			val = value;
		}
		
		public MyValue(PositionGris key, ContinueStateValueWithBound value, int cout) {
			pos = key;
			val = value;
			this.cout = cout;
		}
	}
	
	//Le meilleur coup (ordonnancement dynamique)
	private class MyBestShot{
		public PositionGris pos;
		public int bestShot;
		
		public MyBestShot(PositionGris key, int best) {
			pos = key;
			bestShot = best;
		}
	}
	
	/**
	 * Contient les valeurs de la table
	 */
	MyValue[] map;
	
	/**
	 * Contient les meilleurs coups associ�s � chaque position
	 * Si la position est remplac�e dans la table des valeurs, elle est remplac�e ici aussi.
	 */
	MyBestShot[] bestCoups;
	
	/**
	 * Type de la table, en fonction de la strat�gie de remplacement
	 */
	boolean keepRecent;
	
	/**
	 * 
	 * @param taille
	 * @param keepRecent 
	 */
	public MyContinueHashMap(int taille, boolean keepRecent){
		map = new MyValue[taille];
		bestCoups = new MyBestShot[taille];
		this.keepRecent = keepRecent;
	}
	
	@Override
	public void clear() {
		throw new RuntimeException("Unimplemented : clear");
	}

	@Override
	public boolean containsKey(Object key) {
		if(key instanceof PositionGris)
			//Tout �l�ment de type positionGris de la table est on nul de champ pos non nul (cf constructeurs) 
			return (map[key.hashCode()].pos.equals(key));
		
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new RuntimeException("Unimplemented : containsValue");
	}

	@Override
	public Set<java.util.Map.Entry<PositionGris, ContinueStateValueWithBound>> entrySet() {
		throw new RuntimeException("Unimplemented : entrySet");
	}

	@Override
	public ContinueStateValueWithBound get(Object key) {
		throw new RuntimeException("Mauvaise m�thode get !!! Utiliser getValue ou getBestCoup !");
	}
	
	/**
	 * Renvoie la valeur associ�e � la position si elle est dans la table
	 * Renvoie null sinon
	 * @param pos
	 * @return
	 */
	public ContinueStateValueWithBound getValue(PositionGris pos){
		MyValue elem = map[pos.hashCode()];
		
		if(elem == null)
			return null;
		//elem!= null -> son champ pos est non nul (cf constructeurs)
		if(elem.pos.equals(pos)){
			return elem.val;
		}
		return null;
	}
	
	/**
	 * Renvoie le meilleur coup associ� � la position si elle est dans la table
	 * Renvoie le coup sym�trique si le sym�trique de la position est dans la table
	 * Renvoie -1 sinon
	 * @param pos
	 * @return
	 */
	public int getBestCoup(PositionGris pos){
		MyBestShot shot = bestCoups[pos.hashCode()];
		
		if(shot == null)
			return -1;
		
		if(shot.pos.equals(pos)){
			if(shot.pos.isSymetrical(pos))
				return pos.symetricalShot(shot.bestShot);
			else
				return shot.bestShot;
		}
		//Dans ce cas le hashCode utilis� envoie sur une position diff�rente -> Aucune info sur la position qui nous int�resse
		return -1;
	}

	@Override
	public boolean isEmpty() {
		throw new RuntimeException("Unimplemented : isEmpty");
	}

	@Override
	public Set<PositionGris> keySet() {
		Set<PositionGris> set = new HashSet<PositionGris>();
		for(int i = 0; i < map.length; i++){
			if(map[i] != null)
				set.add(map[i].pos);
		}
		return set;
	}

	@Override
	public ContinueStateValueWithBound put(PositionGris key, ContinueStateValueWithBound value){
		throw new RuntimeException("Mauvaise m�thode put !!! Sp�cifier le cout !");
	}
	
	//Certes on aurait pu faire rentrer ces informations dans value pour respecter l'Override, mais nous n'avons pas besoin
	//d'utiliser notre table comme une table de hachage g�n�rale, aussi �a n'a pas �t� n�cessaire
	public ContinueStateValueWithBound put(PositionGris key, 
										ContinueStateValueWithBound value, int bestShot, int cout) {
		
		//On tient compte de la strat�gie de remplacement
		if(keepRecent)
			return putRecent(key, value, bestShot);
		else
			return putMostComplex(key, value, bestShot, cout);

	}
	
	/**
	 * On remplace dans les deux tableaux l'ancienne position par la nouvelle.
	 * @param key
	 * @param value
	 * @param bestShot
	 * @return
	 */
	public ContinueStateValueWithBound putRecent(PositionGris key, 
									ContinueStateValueWithBound value, int bestShot){
		int hash = key.hashCode();
		MyValue elem = map[hash];
		//On remplace dans tous les cas car le coup demand� est plus r�cent que le coup d�j� pr�sent
		map[hash] = new MyValue(key, value);
		bestCoups[hash] = new MyBestShot(key, bestShot);
		
		//On renvoie l'ancienne valeur
		if(elem == null)
			return null;
		
		return elem.val;
	}
	
	/**
	 * On garde dans les deux tableaux la position la plus co�teuse.
	 * @param key
	 * @param value
	 * @param bestShot
	 * @param cout
	 * @return
	 */
	public ContinueStateValueWithBound putMostComplex(PositionGris key, 
			ContinueStateValueWithBound value, int bestShot, int cout){
		int hash = key.hashCode();
		
		MyValue elem = map[hash];
		//Si la table ne contient aucune entr�e associ�e � ce hashCode, on �crit la nouvelle entr�e
		if(elem == null){
			map[hash] = new MyValue(key, value, cout);
			bestCoups[hash] = new MyBestShot(key, bestShot);
			return null;
		}
		
		//Sinon on l'�crit seulement si elle est plus co�teuse que l'ancienne entr�e
		if(elem.cout < cout){
			map[hash] = new MyValue(key, value, cout);
			bestCoups[hash] = new MyBestShot(key, bestShot);
		}
		
		//On renvoie la valeur initialement pr�sente (null s'il n'y en avait pas, cf plus haut)
		return elem.val;
	}

	@Override
	public void putAll(Map<? extends PositionGris, ? extends ContinueStateValueWithBound> m) {
		throw new RuntimeException("Unimplemented : putAll");
	}

	@Override
	public ContinueStateValueWithBound remove(Object key) {
		if(!(key instanceof PositionGris))
			return null;
		
		MyValue elem = map[key.hashCode()];
		//On v�rifie que l'�l�ment dans le tableau correspond bien � la position � supprimer
		if(elem.pos.equals(key)){
			map[key.hashCode()] = null;
			return elem.val;
		}
		return null;
	}

	@Override
	public int size() {
		throw new RuntimeException("Unimplemented : size");
	}

	@Override
	public Collection<ContinueStateValueWithBound> values() {
		throw new RuntimeException("Unimplemented : values");
	}
	
	/**
	 * Supprime de la table tous les �l�ments qui ne sont pas � Win ou Loss
	 * et ne servent donc pas � l'it�ration suivante
	 */
	public void clearNonFinalPos(){
		for(int i = 0; i < map.length; i++){
			//On �limine tous les �l�ments qui ne sont pas Win ou Loss
			if(map[i] != null){
				//Un �l�ment non nul a son champ va non nul dont le champ value est non nul (cf constructeurs)
				if(map[i].val.value.isNotWinNorLoss())
					map[i] = null;
				else
					//Dans ce cas la valeur est accurate (principe de prudence)
					map[i].val.setBound(0);
			}
		}
	}

}