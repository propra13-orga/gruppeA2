package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Enemy extends GameElement{

	private int collisionTollerance = 10; // Toleranz bei Kollision - Gegner muss richtig berührt werden, nicht nur angeschnitten
	
	private Image image;
	private int width;
	private int height;
	 
	private String path; // Bilddateipfad
	
	private int damage; // Schadenspunkte
	 
	// Konstruktor
	public Enemy(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type==1){
			// Ratte
			path =  "images/enemy_01.png";
			damage = 1;
		} 
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
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
