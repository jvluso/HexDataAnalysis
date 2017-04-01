package AWS;

public class WinRate {
	private int wins;
	private int losses;
	
	public WinRate(){
		wins = 0;
		losses = 0;
	}
	
	public int getWins(){
		return wins;
	}

	public int getLosses(){
		return losses;
	}
	
	public void addWin(){
		wins++;
	}
	
	public void addLoss(){
		losses++;
	}
}
