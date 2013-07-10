package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

public class Wall extends GameElement{

	private Image image;
	private int width;
	private int height;
	private int type;
	 
	private String path; // Bilddateipfad
	 
	// Konstruktor
	public Wall(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type == 1) path =  "images/wall_01.png";
		// Hier später mehr Optionen
		
		this.type = type;
		 
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	 
	public int getType(){
		 return this.type;
	 }
	
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX(), this.getY(), width, height); 
	}
}
