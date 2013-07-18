package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 * Endgegner
 *
 */
public class FinalEnemy extends GameElement{

	private int collisionTollerance = 10; // Toleranz bei Kollision - Gegner muss richtig berührt werden, nicht nur angeschnitten
	private int saveTime = 25;
	private int saveTimeCounter;
	
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
	
	private boolean visible;
	
	private ArrayList<Missile> missiles = new ArrayList<Missile>();
	Missile m = null;
	 
	private String path; // Bilddateipfad
	
	private int damage; // Schadenspunkte
	private int speed; // Geschwindigkeit
	private int live;
	private String element;
	
	// Konstruktor
	/**
	 * Erstellt neuen Endgegner vom Typ type an Position x,y
	 * @param x X Koordinate
	 * @param y Y Koordinate
	 * @param type Typ
	 */
	public FinalEnemy(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		this.type = type;
		if(type==1){
			// Erster Endgegner
			path =  "images/final_enemy_01.png";
			damage = 2; 
			dir = 4;
			speed = 1;
			live = 20;
			element = "plasma";
		} 
		else if(type==2){
			// Zweiter Endgegner
			path =  "images/final_enemy_02.png";
			damage = 3; 
			dir = 2;
			speed = 3;
			live = 40;
			element = "fire";
		} 
		else if(type==3){
			// Dritter Endgegner
			path =  "images/final_enemy_03.png";
			damage = 3; 
			dir = 3;
			speed = 5;
			live = 70;
			element = "ice";
		} 
		this.steps = 0;
		
		this.dx = 0;
		this.dy = 0;
		
		this.saveTimeCounter = 0;
		
		this.visible=true;
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	
	/**
	 * Bewegt Gegner nach vom Typ und Position des Spielers abh&auml;ngigen Muster um dx und dy
	 * @param playerX X Koordinate des Spielers
	 * @param playerY Y Koordinate des Spielers
	 */
	public void move(int playerX, int playerY){
		
		if(saveTimeCounter > 0) saveTimeCounter--; 
		// Richtung ermitteln
		// 0 = up, 1 = right, 2 = down, 3 = left, 4 = standStill
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
		else if(dir==4){
			this.dx = 0;
			this.dy = 0;
		}
		
		steps++;
		
		// Position vor Bewegung ermitteln
		goBackX = this.getX();
		goBackY = this.getY();
		
		// Erzeuge Zufallszahl für bewegung
	    randomNumber = (int) (Math.random() * 10);
		
	    int diffX = playerX - (goBackX-width/2); // Differenz ermitteln
		int diffY = playerY - (goBackY-height/2);
	
	    // Bewegung erstellen
		if(type==1){
			// erster Endgegner
			if(steps>=60 && steps<300){
				// 1. Stillstehen
				// 2. Bewege auf Player zu
				if(diffX<0 && saveTimeCounter<=0) dx = -speed; // x setzen
				else dx = speed;
				if(diffY<0 && saveTimeCounter<=0) dy = -speed; // y setzen
				else dy = speed;
				
			}
			else if(steps>=300){
				// 3. Zufallsbewegung
				if(steps>500) steps=60;
				if(steps==300)this.setDirectionOfMovement(randomNumber);
			}
		}
		else if(type==2){
			// zweiter EG
			if(steps%300==0) this.setDirectionOfMovement(randomNumber); 
		}
		else if(type==3){
			// feuern
			//if(steps%200==0) this.setDirectionOfMovement(randomNumber); 
			
			if(steps==50){
				m = new Missile(this.getX()+width/2, this.getY()+height, "FE3", 2, false);
				missiles.add(m);
				steps=0;
			}
			
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
	 * Setzt eine bestimmte Zeit in der der Endgegner sich nicht auf den Player zubewegt
	 */
	public void setSave(){
		// Zeit zum flüchten
		this.saveTimeCounter = saveTime;
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
	}
	
	// get Methoden
	
	/**
	 * Gibt Liste an geschossenen Missiles zur&uuml;ck
	 * @return Liste an Missiles
	 */
	public ArrayList<Missile> getMissiles(){
		return missiles;
	}
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