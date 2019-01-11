package ttt;

/*Author Matthew Vorsteg
 * This class represents a single cell in the game
 * which can be changed by player intervention
 */

public class Cell {
	
	private Symbol sym;		//symbol displayed in cell
	private int owner;		//if p1 or p2 owns cell, 0 if empty
	
	public Cell() {
		owner = 0;
	}
	
	public void setOwner(int owner, Symbol sym) {
		this.owner = owner;
		this.sym = sym;
	}
	
	public boolean isEmpty() {
		return owner == 0;
	}
	
	public int getOwner() {
		return owner;
	}
	
	public Symbol getSymbol() {
		return sym;
	}
		
}
