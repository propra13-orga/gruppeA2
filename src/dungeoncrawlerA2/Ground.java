package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Ground extends GameElement{

	 private Image image;
	 
	 private String path; // Bilddateipfad
	 
	 // Konstruktor
	 public Ground(int x, int y, int type) {
		 
		 super(x, y); // Aufruf GameElement
		 
		 type -= 48; // Von char in int
		 if(type==1) path =  "ground_01.png";
		 // Hier später mehr Optionen
		 
	     ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	     image = ii.getImage();
	     this.setImage(image);
	 }
}
