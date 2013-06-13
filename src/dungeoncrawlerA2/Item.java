package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Item extends GameElement{
	
	private String path; // Bilddateipfad
	private Image image;
	private int width, height;
	private String ItemType;
	private int amount; // Anzahl an Geld, Munition, Magie, Waffen ... pro Item
	private boolean visible;
	
	public Item(int x, int y, int type){
		
		super(x,y); // Position auf Spielfeld
		
		type -= 48;
		
		if(type == 1){
			path = "images/money_01.png";
			ItemType = "money";
			amount=1;
		}
		
		visible = true;
		
		ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
		image = ii.getImage();
		width = image.getWidth(null);
		height = image.getHeight(null);
		this.setImage(image);
		
	}
	
	// Set Methoden
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	// get Methoden
	public boolean isVisible(){
		return visible;
	}
	
	public String getItemType(){
		return this.ItemType;
	}
	
	public int getAmount(){
		return this.amount;
	}
	
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX(), this.getY(), width, height); 
	}
}
