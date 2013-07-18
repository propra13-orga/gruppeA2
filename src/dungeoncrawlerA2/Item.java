package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * Item (Waffe, Schl&uuml;ssel, Healthpack, Energie, R&uuml;stung)
 *
 */
public class Item extends GameElement{
	
	private String path; // Bilddateipfad
	private Image image;
	private int width, height;
	private int type;
	
	private String ItemType;
	private int amount; // Anzahl an Geld, Munition, Magie, Waffen ... pro Item
	private boolean visible;
	private boolean ItemHasMissiles;
	
	/**
	 * Erstellt Item an Position x,y
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * @param type Typ des Items
	 */
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
			path = "images/key_01.png";
			ItemType = "key";
			amount=1;
			ItemHasMissiles = false;
		}
		else if(type == 5){
			// Armour - plasma
			path = "images/armour_plasma_01.png";
			ItemType = "armPlasma";
			amount=6;
			ItemHasMissiles = false;
		}
		else if(type == 6){
			// Armour - fire
			path = "images/armour_fire_01.png";
			ItemType = "armFire";
			amount=6;
			ItemHasMissiles = false;
		}
		else if(type == 7){
			// Armour - ice
			path = "images/armour_ice_01.png";
			ItemType = "armIce";
			amount=6;
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
	/**
	 * Setzt Sichtbarkeit des Items
	 * @param visible Sichtbarkeit
	 */
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	/**
	 * Setzt Anzahl des Items
	 * @param amount Anzahl
	 */
	public void setAmount(int amount){
		this.amount = amount;
	}
	
	/**
	 * F&uuml;gt zur anzahl der Items addAmount hinzu
	 * @param addAmount
	 */
	public void addToAmount(int addAmount){
		this.amount += addAmount;
		if(this.amount <= 0) this.amount = 0;
	}
	
	// get Methoden
	/**
	 * Gibt Sichtbarkeit des Items zur&uuml;ck
	 * @return Sichtbarkeit
	 */
	public boolean isVisible(){
		return visible;
	}
	
	/**
	 * Gibt zur&uuml;ck ob das Item Munition besitzt = Waffe
	 * @return Wert ob Item eine Waffe ist
	 */
	public boolean hasMissiles(){
		return ItemHasMissiles;
	}
	
	/**
	 * Gibt den Namen des Items zur&uuml;ck
	 * @return Name des Items
	 */
	public String getItemType(){
		return this.ItemType;
	}
	
	/**
	 * Gibt Anzahl des Items / Munition zur&uuml;ck
	 * @return Anzahl des Items
	 */
	public int getAmount(){
		return this.amount;
	}
	
	/**
	 * Gibt Typ des Items zur&uuml;ck
	 * @return Typ
	 */
	public int getType(){
		 return this.type;
	}
	/**
	 * Gibt Rectangle (Position, Breite und L&auml;nge des Bildes) f&uuml;r die Kollisionserkennung zur&uuml;ck
	 * @return Rectangle (Position, Breite und L&auml;nge des Bildes)
	 */
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(this.getX(), this.getY(), width, height); 
	}
}