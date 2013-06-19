package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

public class Checkpoint extends GameElement{

	private Image image;
	private int width;
	private int height;
	
	private boolean active;
	 
	private String path; // Bilddateipfad
	private String pathList[] = {"images/checkpoint_inactive.png","images/checkpoint_active.png"};
	
	private ImageIcon ii;
	 
	// Konstruktor
	public Checkpoint(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type == 0){
			this.setActive(false);
		}
		else if(type == 1){
			 this.setActive(true);
		}
	}
	
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
	
	public boolean getActive(){
		return this.active;
	}
	 
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX()+4, this.getY()+4, width-8, height-8); 
	}
}