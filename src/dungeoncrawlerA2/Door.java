package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

/**
 * T&uuml;r
 *
 */
public class Door extends GameElement{
	
	private Image image;
	private int width;
	private int height;
	private int type;
	
	private boolean open;
	 
	private String path; // Bilddateipfad
	private String pathList[] = {"images/door_01_closed.png","images/door_01_open.png"};
	
	private ImageIcon ii;
	 
	// Konstruktor
	/**
	 * @param x X Koordinate
	 * @param y Y Koordinate
	 * @param type Typ (Aussehen) der T&uuml;r
	 * @param open T&uuml;r ge&ouml;ffnet 
	 */
	public Door(int x, int y, int type, boolean open) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		this.setOpen(open);
		this.type = type;
	}
	
	/**
	 * &Ouml;ffnet oder Schliesst T&uuml;r
	 * @param open T&uuml;r ge&ouml;ffnet  
	 */
	public void setOpen(boolean open){
		this.open = open;
		if(open) this.path = pathList[1];
		else this.path = pathList[0];
		// neues Bild laden
		ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	
	/**
	 * Gibt den Wert zur&uuml;ck der anzeigt, ob die T&uuml;r ge&ouml;ffnet ist
	 * @return T&uuml;r ge&ouml;ffnet
	 */
	public boolean getOpen(){
		return this.open;
	}
	
	/**
	 * Gibt Typ der T&uuml;r zur&uuml;ck
	 * @return Typ
	 */
	public int getType(){
		 return this.type;
	 }
	 
	/**
	 * Gibt Rectangle (Position, Breite und L&auml;nge des Bildes) f&uuml;r die Kollisionserkennung zur&uuml;ck
	 * @return Rectangle (Position, Breite und L&auml;nge des Bildes)
	 */
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX(), this.getY(), width, height); 
	}
}