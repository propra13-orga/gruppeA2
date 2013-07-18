package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * Gegner
 *
 */
public class Enemy extends GameElement{

	private int collisionTollerance = 10; // Toleranz bei Kollision - Gegner muss richtig berührt werden, nicht nur angeschnitten
	
	private Image image;
	private int width;
	private int height;
	private int type;
	private int dx;
	private int dy;
	private int dir;
	private int goBackX, goBackY;
	private int steps;
	private int randomNumber;
	private int live;
	private String element;
	
	private boolean visible;
	
	 
	private String path; // Bilddateipfad
	
	private int damage; // Schadenspunkte
	private int speed; // Geschwindigkeit
	
	// Konstruktor
	/**
	 * Erstellt neuen Gegner vom Typ type an Position x,y
	 * @param x X Koordinate
	 * @param y Y Koordinate
	 * @param type Typ
	 */
	public Enemy(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		this.type = type;
		if(type==1){
			// Ratte
			path =  "images/enemy_01.png";
			damage = 1; 
			dir = 1;
			speed = 2;
			live = 2;
			element = "plasma";
		}
		else if(type==2){
			// Anfangs Unbeweglicher Stachelkopf
			path =  "images/enemy_02.png";
			damage = 1; 
			dir = 1;
			speed = 0;
			live = 4;
			element = "plasma";
		} 
		this.steps = 0;
		
		this.dx = 0;
		this.dy = 0;
		
		this.visible=true;
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	
	/**
	 * Bewegt Gegner nach vom Typ abh&auml;ngigen Muster um dx und dy
	 */
	public void move(){
		// Richtung ermitteln
		// 0 = up, 1 = right, 2 = down, 3 = left
		if(dir==0){
			this.dx = 0;
			this.dy = -speed;
		}
		else if(dir==1){
			this.dx = speed;
			this.dy = 0;
		}
		else if(dir==2){
			this.dx = 0;
			this.dy = speed;
		}
		else if(dir==3){
			this.dx = -speed;
			this.dy = 0;
		}
		
		// Position vor Bewegung ermitteln
		goBackX = this.getX();
		goBackY = this.getY();
		
		// Erzeuge Zufallszahl für bewegung
	    randomNumber = (int) (Math.random() * 10);
		
		// Bewegung erstellen
		if(type==1){
			// Ratte
			steps++;
			if(steps%(10*randomNumber+50) == 0){
				this.setDirectionOfMovement(randomNumber); // Zufallswechsel
			}
			if(steps > 480+2*randomNumber){
				this.setDirectionOfMovement(3); // links herum
				this.steps = 0;
			}
			
		}
		else if(type==2){
			// Anfangs Unbeweglicher Stachelkopf
			// Läuft bei Leben < 2 automatisch im Kreis
		}
		// Bewegen
		this.setX(goBackX+dx);
		this.setY(goBackY+dy);
	}
	
	/**
	 * L&auml;sst Gegner zur letztbekannten Position zur&uuml;ckspringen (CollisionDetection: stehenbleiben vor soliden Objekten)
	 */
	public void resetMovement(){
		this.setX(goBackX);
		this.setY(goBackY);
	}
	
	// set methoden
	/**
	 * Setzt eine neue Bewegungsrichtung
	 * @param d Bewegungsrichtung (0 = up, 1 = right, 2 = down, 3 = left)
	 */
	public void setDirectionOfMovement(int d){
		// 0 = up, 1 = right, 2 = down, 3 = left
		this.dir += d;
		this.dir = this.dir%4;
		
	}
	
	/**
	 * Setze Gegner sichtbar/unsichtbar
	 * @param visible Sichtbar
	 */
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	/**
	 * &Auml;ndert den Wert der Lebenspunkte um addLive
	 * @param addLive &Auml;nderung der Lebenspunkte
	 */
	public void setLive(int addLive){
		this.live += addLive;
		if(this.live==1) this.speed = 1;
	}
	
	// get Methoden
	
	/**
	 * Gibt die Anzahl der Lebenspunkte zur&uuml;ck
	 * @return Anzahl Lebenspunkte
	 */
	public int getLive(){
		return this.live;
	}
	
	/**
	 * Gibt aktuelle Bewegungsrichtung zur&uuml;ck
	 * @return Aktuelle Bewegungsrichting
	 */
	public int getDirectionOfMovement(){
		return this.dir;
	}
	
	/**
	 * Gibt Sichtbarkeit des Gegners zur&uuml;ck
	 * @return Sichtbarkeit
	 */
	public boolean isVisible(){
		return visible;
	}
	 
	/**
	 * Gibt Schaden zur&uuml;ck
	 * @return Schaden
	 */
	public int getDamage(){
		return damage;
	}
	
	/**
	 * Gibt Elementtyp des Gegners zur&uuml;ck
	 * @return Element (fire, plasma, ice)
	 */
	public String getElement(){
		return this.element;
	}
	
	/**
	 * Gibt den Typ des Gegners zurück (f&uuml;r erneutes codieren der Level)
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
		return new Rectangle(this.getX()+collisionTollerance/2, this.getY()+collisionTollerance/2, width - collisionTollerance, height - collisionTollerance); 
	}
	

}