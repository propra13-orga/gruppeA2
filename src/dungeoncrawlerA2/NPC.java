package dungeoncrawlerA2;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;

/**
 * NPC, Shop
 *
 */
public class NPC extends GameElement{

	private Image image;
	private int width;
	private int height;
	private int type;
	
	boolean shop;
	boolean dialog;
	 
	private String path; // Bilddateipfad
	
	// Konstruktor
	/**
	 * Erstellt einen NPC an Position x,y
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * @param type Typ
	 */
	public NPC(int x, int y, int type) {
		 
		super(x, y); // Aufruf GameElement
		 
		type -= 48; // Von char in int
		if(type == 1){
			// NPC - Dialog
			 path =  "images/npc_01.png";
			 shop = false;
			 dialog = true;
		}
		else if(type == 11){
			// SHOP
			 path =  "images/shop_01.png";
			 shop = true;
			 dialog=false;
		}
		 
		this.type=type;
		
	    ImageIcon ii = new ImageIcon(this.getClass().getResource(path));
	    image = ii.getImage();
	    width = image.getWidth(null);
	    height = image.getHeight(null);
	    this.setImage(image);
	}
	
	/**
	 * Gibt zur&uuml;ck ob der NPC ein Shop ist.
	 * @return NPC ist Shop
	 */
	public boolean isShop(){
		return this.shop;
	}
	
	/**
	 * Gibt zur&uuml;ck ob der NPC einen Dialog hast.
	 * @return NPC hat einen Dialog
	 */
	public boolean hasDialog(){
		return this.dialog;
	}
	 
	/**
	 * Gibt den Typ des NPC zur&uuml;ck
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
		return new Rectangle(this.getX()-4, this.getY()-4, width+8, height+8); 
	}
}