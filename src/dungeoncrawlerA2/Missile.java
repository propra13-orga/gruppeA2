package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

public class Missile extends GameElement{

	private Image image;
	private int width;
	private int height;
	
	private int speed;
	private int damage;
	private boolean friendly;
	
	private int dx,dy;
	 
	private boolean visible;
	
	private String path; // Bilddateipfad
	 
	// Konstruktor
	public Missile(int x, int y, String type, int dir, boolean friendly) {
		 
		super(x, y); // Aufruf GameElement
		 
		if(type.equals("plasmagun")){
			path =  "images/m_plasma_01.png";
			speed = 4;
			damage = 1;
		}
		if(type.equals("FE3")){
			path =  "images/m_plasma_01.png";
			speed = 5;
			damage = 1;
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
	 
	public void move(){
		this.setX(this.getX()+dx);
		this.setY(this.getY()+dy);
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public int getDamage(){
		return this.damage;
	}
	
	public boolean getFriendly(){
		return this.friendly;
	}
	
	public Rectangle getBounds(){
		// Kollisionserkennung - effektiver Wirkradius
		return new Rectangle(this.getX()-2, this.getY()-2, width+4, height+4); 
	}
	
	public Rectangle getReducedBounds(){
		// Kollisionserkennung - für Wandkollision, damit Schuss entlang Wand möglich
		return new Rectangle(this.getX()+width/4, this.getY()+height/4, width/2, height/2); 
	}
}
