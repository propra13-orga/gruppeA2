package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

/**
 * Geschosse 
 *
 */
public class Missile extends GameElement{

	private Image image;
	private int width;
	private int height;
	
	private int speed;
	private int damage;
	private boolean friendly;
	
	private int dx,dy;
	 
	private boolean visible;
	
	private String element;
	
	private String path; // Bilddateipfad
	 
	// Konstruktor
	/**
	 * Erstellt neues Geschoss an Position x,y
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * @param type Typ des Geschosses
	 * @param dir Richtung des Geschosses (0 = oben,1=rechts,2=unten,3=links)
	 * @param friendly Wert: vom Player oder Gegner abgeschossen
	 */
	public Missile(int x, int y, String type, int dir, boolean friendly) {
		 
		super(x, y); // Aufruf GameElement
		 
		if(type.equals("plasmagun")){
			path =  "images/m_plasma_01.png";
			speed = 4;
			damage = 1;
			element = "plasma";
		}
		else if(type.equals("icegun")){
			path =  "images/m_ice_01.png";
			speed = 4;
			damage = 1;
			element = "ice";
		}
		else if(type.equals("firegun")){
			path =  "images/m_fire_01.png";
			speed = 4;
			damage = 1;
			element = "fire";
		}
		else if(type.equals("FE3")){
			path =  "images/m_plasma_01.png";
			speed = 5;
			damage = 1;
			element = "plasma";
		}
		
		this.friendly=friendly;
		// Richtung setzen
		dx = dy = 0;
		if(dir == 0){
			dy = -this.speed;
		}
		else if(dir == 1){
			dx = this.speed;
		}
		else if(dir == 2){
			dy = this.speed;
		}
		else if(dir == 3){
			dx = -this.speed;
		}
		
		this.visible = true;
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	 
	/**
	 * Bewegt Geschoss um dx,dy
	 */
	public void move(){
		this.setX(this.getX()+dx);
		this.setY(this.getY()+dy);
	}
	
	/**
	 * Setzt Sichtbarkeit des Geschosses
	 * @param visible Sichtbarkeit
	 */
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	/**
	 * Gibt Sichtbrkeit des Geschosses zur&uuml;ck
	 * @return Sichtbarkeit
	 */
	public boolean isVisible(){
		return visible;
	}
	
	/**
	 * Gibt Element des Typs zur&uuml;ck
	 * @return Element
	 */
	public String getElement(){
		return this.element;
	}
	
	/**
	 * Gibt Schaden zur&uuml;ck
	 * @return Schaden
	 */
	public int getDamage(){
		return this.damage;
	}
	
	/**
	 * Gibt zur&uuml;ck ob Geschoss vom Player abgeschossen
	 * @return vom Player abgeschossen
	 */
	public boolean getFriendly(){
		return this.friendly;
	}
	/**
	 * Gibt Rectangle (Position, Breite und L&auml;nge des Bildes) f&uuml;r die Kollisionserkennung zur&uuml;ck
	 * @return Rectangle (Position, Breite und L&auml;nge des Bildes)
	 */
	public Rectangle getBounds(){
		// Kollisionserkennung - effektiver Wirkradius
		return new Rectangle(this.getX()-2, this.getY()-2, width+4, height+4); 
	}
	/**
	 * Gibt (Reduziert) Rectangle (Position, Breite und L&auml;nge des Bildes) f&uuml;r die Kollisionserkennung zur&uuml;ck
	 * @return (Reduziert) Rectangle (Position, Breite und L&auml;nge des Bildes)
	 */
	public Rectangle getReducedBounds(){
		// Kollisionserkennung - für Wandkollision, damit Schuss entlang Wand möglich
		return new Rectangle(this.getX()+width/4, this.getY()+height/4, width/2, height/2); 
	}
}