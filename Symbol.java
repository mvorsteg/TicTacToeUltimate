package ttt;

/*Author Matthew Vorsteg
 * This class represents a symblol
 * and stores color and design
 */

import java.awt.Color;

public class Symbol {
	
	private Color color;
	private Design design;
	
	
	
	public Symbol() {
		color = Color.BLACK;
		design = Design.X;
	}
	
	public void setColor(Color color) {
		
		this.color = color;
	}
	
	public void setDesign(Design design) {
		this.design = design;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Design getDesign() {
		return design;
	}

}
