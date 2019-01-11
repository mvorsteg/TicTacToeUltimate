package ttt;

/*Author Matthew Vorsteg
 * This class represents a single profile
 * which stores data for one player
 */

import java.awt.Color;

public class Profile {
	
	static int numProfiles = 0;
	public static final int MAX_LEVEL = 50;
	
	private	String name;	//player name
	private Symbol sym;		//player's customized symbol
	private int level;		//player level
	
	private int exp;		//cumulative experience points
	private int reqExp;		//amount of exp to next level
	
	private int wins;
	private int losses;
	
	private int turn;		//will be 1 or 2 in game
	
	//new profile with custom name
	public Profile(String name) {
		this.name = name;
		//System.out.println(numProfiles);
		numProfiles ++;
		this.sym = new Symbol();
		level = 1;
		exp = 0;
		reqExp = 50;
		wins = 0;
		losses = 0;
	}
	//default name
	public Profile() {
	//	System.out.println(numProfiles);

		name= "player"+ ++numProfiles;
		this.sym = new Symbol();
		level = 1;
		exp = 0;
		reqExp = 50;
		wins = 0;
		losses = 0;
	}
	
	//gain exp
	public boolean addExp(int exp) {
		if (level < MAX_LEVEL) {
			this.exp += exp;
			if (this.exp >= reqExp) {
				this.levelUp();
				return true;
			}
		}
		return false;
	}
	
	//move to next level
	public void levelUp() {
		level ++;
		if (level < MAX_LEVEL) {
			reqExp = 30+ (level*20);
			exp = 0;
		}else {
			exp = 0;
			reqExp = 0;
		}
	}
	
	//increases wins or losses based on end status
	public void end(boolean rf) {
		if (rf) 
			wins ++;
		else
			losses ++;
	}
	
	//changes player's symbol color
	public void setColor(Color color) {
		sym.setColor(color);
	}
	
	//changes player's symbol design
	public void setDesign(Design design) {
		sym.setDesign(design);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	//getter methods
	public String getName() {
		return name;
	}
	
	public Symbol getSymbol() {
		return sym;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public int getExp() {
		return exp;
	}
	
	public void setReqExp(int reqExp) {
		this.reqExp = reqExp;
	}
	
	public int getReqExp() {
		return reqExp;
	}
	
	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public int getWins() {
		return wins;
	}
	
	public void setLosses(int losses) {
		this.losses = losses;
	}
	
	public int getLosses() {
		return losses;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public String toString() {
		return this.name;
	}

}
