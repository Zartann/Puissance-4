
public enum Box {
	VOID, PLAYER, ADVERSARY;
	
	public boolean isVoid(){
		return (this == VOID);
	}
	
	public boolean isPlayer(){
		return (this == PLAYER);
	}
	
	public boolean isAdv(){
		return (this == ADVERSARY);
	}
}
