package ttt;

/*Author Matthew Vorsteg
 * This class represents a 3x3 grid of cells
 */

public class Block {

	private Cell[][] arr;
	private int owner;
	private Symbol sym;
	
	private int x;
	private int y;
	
	public Block(int x, int y) {
		arr = new Cell[3][3];
		this.x = x;
		this.y = y;
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) 
				arr[r][c] = new Cell();
		}
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	//checks a block to see if there are 3 cells in a row with same owner
	private boolean check(int owner) {
		boolean returnVal = false;
		//checks the straight lines
		for(int x = 0; x < 3; x++) {
			if (getCell(x, 0).getOwner() == owner && getCell(x, 1).getOwner() == 
					owner && getCell(x, 2).getOwner() == owner) {
				returnVal = true;
			}
			if (getCell(0, x).getOwner() == owner && getCell(1, x).getOwner() == 
					owner && getCell(2, x).getOwner() == owner) {
				returnVal = true;
			}
			
		}
		//checks diagonals
		if (getCell(0, 0).getOwner() == owner && getCell(1, 1).getOwner() == 
				owner && getCell(2, 2).getOwner() == owner) {
			returnVal = true;
		}
		if (getCell(0, 2).getOwner() == owner && getCell(1, 1).getOwner() == 
				owner && getCell(2, 0).getOwner() == owner) {
			returnVal = true;
		}
		//if (returnVal)
			//System.out.println("AAAAAA");
		return returnVal;
	}
	
	//returns true if owner == 0
	public boolean isEmpty() {
		return (owner == 0);
	}
	
	
	//getters and setters
	public void setOwner(int owner, Symbol sym) {
		this.owner = owner;
		this.sym = sym;
	}
	
	public boolean setCell(int owner, Symbol sym, int x, int y){
		arr[x][y].setOwner(owner, sym);
		return check(owner);
	}
	
	public int getOwner() {
		return owner;
	}
	
	public Symbol getSymbol() {
		return sym;
	}
	
	public Cell getCell(int x, int y){
		return arr[x][y];
	}
}
