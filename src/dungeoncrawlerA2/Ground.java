package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * Boden
 *
 */
public class Ground extends GameElement{

	 private Image image;
	 private int width;
	 private int height;
	 private int type;
	 
	 private String path; // Bilddateipfad
	 
	 // Konstruktor
	 /**
	  * Erstellt neuen Boden an Position x,y
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * @param type Typ des Bodens (Design)
	 */
	public Ground(int x, int y, int type) {
		 
		 super(x, y); // Aufruf GameElement
		 
		 type -= 48; // Von char in int
		 if(type==1) path =  "images/ground_01.png";
		 if(type==2) path =  "images/ground_02.png";
		 if(type==3) path =  "images/ground_03.png";
		 if(type==4) path =  "images/ground_04.png";
		 if(type==5) path =  "images/ground_05.png";
		 // Hier später mehr Optionen
		 
		 this.type = type;
		 
	     ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	     image = ii.getImage();
	     width = image.getWidth(null);
		 height = image.getHeight(null);
	     this.setImage(image);
	 }
	 
	 /**
	  * Gibt Typ des Bodens zur&uuml;ck
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