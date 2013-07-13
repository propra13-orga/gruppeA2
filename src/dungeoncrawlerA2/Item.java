package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Item extends GameElement{
	
	private String path; // Bilddateipfad
	private Image image;
	private int width, height;
	private int type;
	
	private String ItemType;
	private int amount; // Anzahl an Geld, Munition, Magie, Waffen ... pro Item
	private boolean visible;
	private boolean ItemHasMissiles;
	
	public Item(int x, int y, int type){
		
		super(x,y); // Position auf Spielfeld
		
		type -= 48;
		
		if(type == 1){
			// Geld
			path = "images/money_01.png";
			ItemType = "money";
			amount=1;
			ItemHasMissiles = false;
		}
		else if(type == 2){
			// Mana
			path = "images/mana_01.png";
			ItemType = "mana";
			amount=100;
			ItemHasMissiles = false;
		}
		else if(type == 3){
			// Health
			path = "images/healthpack_01.png";
			ItemType = "health";
			amount=100;
			ItemHasMissiles = false;
		}
		else if(type == 4){
			//Key
			path = "images/healthpack_01.png"; // <----------------------- Pfad ändern
			ItemType = "key";
			amount=1;
			ItemHasMissiles = false;
		}
		else if(type == 5){
			// Armour - plasma
			path = "images/armour_plasma_01.png";
			ItemType = "armPlasma";
			amount=3;
			ItemHasMissiles = false;
		}
		else if(type == 6){
			// Armour - fire
			path = "images/armour_fire_01.png";
			ItemType = "armFire";
			amount=3;
			ItemHasMissiles = false;
		}
		else if(type == 7){
			// Armour - ice
			path = "images/armour_ice_01.png";
			ItemType = "armIce";
			amount=3;
			ItemHasMissiles = false;
		}
		else if(type == 11){
			// Plasmagun
			path = "images/w_plasmagun_01.png";
			ItemType = "plasmagun";
			amount=100; // Hier für Munition
			ItemHasMissiles = true;
		}
		else if(type == 12){
			// Icegun
			path = "images/w_icegun_01.png";
			ItemType = "icegun";
			amount=100; // Hier für Munition
			ItemHasMissiles = true;
		}
		else if(type == 13){
			// Firegun
			path = "images/w_firegun_01.png";
			ItemType = "firegun";
			amount=100; // Hier für Munition
			ItemHasMissiles = true;
		}
		
		
		visible = true;
		
		this.type=type;
		
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
	
	public void setAmount(int amount){
		this.amount = amount;
	}
	
	public void addToAmount(int addAmount){
		this.amount += addAmount;
		if(this.amount <= 0) this.amount = 0;
	}
	
	// get Methoden
	public boolean isVisible(){
		return visible;
	}
	
	public boolean hasMissiles(){
		return ItemHasMissiles;
	}
	
	public String getItemType(){
		return this.ItemType;
	}
	
	public int getAmount(){
		return this.amount;
	}
	
	public int getType(){
		 return this.type;
	}
	
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX(), this.getY(), width, height); 
	}
}