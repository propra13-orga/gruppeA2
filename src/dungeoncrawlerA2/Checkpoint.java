package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

/**
 * Checkpoints die den Spieler im Level sp&auml;ter starten lassen
 *
 */
public class Checkpoint extends GameElement{

	private Image image;
	private int width;
	private int height;
	private int type;
	
	private boolean active;
	 
	private String path; // Bilddateipfad
	private String pathList[] = {"images/checkpoint_inactive.png","images/checkpoint_active.png"};
	
	private ImageIcon ii;
	 
	// Konstruktor
	/**
	 * Erstellt einen aktiven oder inaktiven Checkpoint an Position x,y 
	 * @param x X Koordinate
	 * @param y Y Koordinate
	 * @param type inaktiv = 0, aktiv = 1 (oder Sonstiges)
	 */
	public Checkpoint(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type == 0){
			this.setActive(false);
		}
		else{
			 this.setActive(true);
		}
		
		this.type = type;
	}
	
	/**
	 * Setze den Checkpoint auf aktiv oder inaktiv
	 * @param active Checkpoint aktiv
	 */
	public void setActive(boolean active){
		this.active = active;
		if(active) this.path = pathList[1];
		else this.path = pathList[0];
		// neues Bild laden
		ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	
	/**
	 * @return Checkpoint aktiv
	 */
	public boolean getActive(){
		return this.active;
	}
	 
	/**
	 * Gibt den Typ des Checkpoints zurück (f&uuml;r erneutes codieren der Level)
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
		return new Rectangle(this.getX()+4, this.getY()+4, width-8, height-8); 
	}
}