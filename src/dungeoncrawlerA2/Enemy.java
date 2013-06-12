package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

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
	
	 
	private String path; // Bilddateipfad
	
	private int damage; // Schadenspunkte
	private int speed; // Geschwindigkeit
	
	// Konstruktor
	public Enemy(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		this.type = type;
		if(type==1){
			// Ratte
			path =  "images/enemy_01.png";
			damage = 1; 
			dir = 1;
			speed = 1;
		} 
		this.steps = 0;
		
		this.dx = 0;
		this.dy = 0;
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	
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
		
		// Bewegen
		if(type==1){
			// Ratte
			this.setX(goBackX+dx);
			this.setY(goBackY+dy);
			steps++;
			if(steps%(10*randomNumber+50) == 0){
				this.setDirectionOfMovement(randomNumber); // Zufallswechsel
			}
			if(steps > 480+2*randomNumber){
				this.setDirectionOfMovement(3); // links herum
				this.steps = 0;
			}
			
		}
	}
	
	public void resetMovement(){
		this.setX(goBackX);
		this.setY(goBackY);
	}
	
	// set methoden
	public void setDirectionOfMovement(int d){
		// 0 = up, 1 = right, 2 = down, 3 = left
		this.dir += d;
		this.dir = this.dir%4;
		
	}
	 
	// get Methoden
	public int getDamage(){
		return damage;
	}
	
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX()+collisionTollerance/2, this.getY()+collisionTollerance/2, width - collisionTollerance, height - collisionTollerance); 
	}
	

}
