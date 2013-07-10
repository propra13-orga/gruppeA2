package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

public class Door extends GameElement{
	
	private Image image;
	private int width;
	private int height;
	private int type;
	
	private boolean open;
	 
	private String path; // Bilddateipfad
	private String pathList[] = {"images/door_01_closed.png","images/door_01_open.png"};
	
	private ImageIcon ii;
	 
	// Konstruktor
	public Door(int x, int y, int type, boolean open) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type == 1){
			this.setOpen(open);
		}
		this.type = type;
	}
	
	public void setOpen(boolean open){
		this.open = open;
		if(open) this.path = pathList[1];
		else this.path = pathList[0];
		// neues Bild laden
		ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	
	public boolean getOpen(){
		return this.open;
	}
	
	public int getType(){
		 return this.type;
	 }
	 
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX(), this.getY(), width, height); 
	}
}