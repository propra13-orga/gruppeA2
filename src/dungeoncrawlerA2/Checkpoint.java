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
	 
	// Konstruktor
	public Checkpoint(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type == 0) path =  "images/checkpoint_inactive.png";
		else if(type == 1) path =  "images/checkpoint_active.png";
		// Hier später mehr Optionen
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	 
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX(), this.getY(), width, height); 
	}
}