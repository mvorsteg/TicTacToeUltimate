package ttt;

/* Author Matthew Vorsteg
 * This class represents a 3x3 grid of Blocks
 * And holds data for said blocks in the game
 */

public class Board {
	
	private Block[][] arr;
	
	public Board() {
		arr = new Block[3][3];
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) 
				arr[r][c] = new Block(r, c);
		}
	}
	
	//checks a block to see if there are 3 cells in a row with same owner
	public boolean check(int owner) {
		boolean returnVal = false;
		//checks the straight lines
		for(int x = 0; x < 3; x++) {
			if (getBlock(x, 0).getOwner() == owner && getBlock(x, 1).getOwner() == 
					owner && getBlock(x, 2).getOwner() == owner) {
				returnVal = true;
			}
			if (getBlock(0, x).getOwner() == owner && getBlock(1, x).getOwner() == 
					owner && getBlock(2, x).getOwner() == owner) {
				returnVal = true;
			}
				
		}
		//checks diagonals
		if (getBlock(0, 0).getOwner() == owner && getBlock(1, 1).getOwner() == 
				owner && getBlock(2, 2).getOwner() == owner) {
			returnVal = true;
		}
		if (getBlock(0, 2).getOwner() == owner && getBlock(1, 1).getOwner() == 
				owner && getBlock(2, 0).getOwner() == owner) {
			returnVal = true;
		}
		return returnVal;
	}
	
	//returns a block
	public Block getBlock(int x, int y) {
		return arr[x][y];
	}
	
	//sets the owner and symbool of a block
	public void setBlock(int owner, Symbol sym, int x, int y) {
		arr[x][y].setOwner(owner, sym);
	}
	
	//sets a specific cell of a specific block
	public boolean setCell(int owner, Symbol sym, int bx, int by, int x, int y) {
		arr[bx][by].setCell(owner, sym, x, y);
		return check(owner);
	}
	
	
	
	

}
