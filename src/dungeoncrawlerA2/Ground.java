package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Ground extends GameElement{

	 private Image image;
	 private int width;
	 private int height;
	 private int type;
	 
	 private String path; // Bilddateipfad
	 
	 // Konstruktor
	 public Ground(int x, int y, int type) {
		 
		 super(x, y); // Aufruf GameElement
		 
		 type -= 48; // Von char in int
		 if(type==1) path =  "images/ground_01.png";
		 if(type==2) path =  "images/ground_02.png";
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
