package pxc.bandapanda;

public class CurrentPL extends Playlist{

	
    
	private static CurrentPL instance;
	
	static{
		instance = new CurrentPL();
	}
	
	private CurrentPL(){
	}
	
	public static CurrentPL getInstance(){
		return CurrentPL.instance;
	}
	
}
