package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

/**
 * Wand
 *
 */
public class Wall extends GameElement{

	private Image image;
	private int width;
	private int height;
	private int type;
	 
	private String path; // Bilddateipfad
	 
	// Konstruktor
	/**
	  * Erstellt neue Wand an Position x,y
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * @param type Typ der Wand(Design)
	 */
	public Wall(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type == 1) path =  "images/wall_01.png";
		if(type == 2) path =  "images/wall_02.png";
		if(type == 3) path =  "images/wall_03.png";
		if(type == 4) path =  "images/wall_04.png";
		// Hier später mehr Optionen
		
		this.type = type;
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	 /**
	  * Gibt Typ der Wand zur&uuml;ck
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