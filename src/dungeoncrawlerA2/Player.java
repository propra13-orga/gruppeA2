package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

// Evtl später auch GameElement

// Player: Spielfigur
/**
 * Spielfigur
 *
 */
public class Player{
	
	private String player;	// Pfad zum Bild - Spielfigur
	private int speed = 2;	// Geschwindigkeit der Spielfigur
	
	private int dx;	// Veränderung der Koordinaten für Bewegung
	private int dy;
	private int x;
	private int y;	
	private int goBackX; // Rücksprungwerte bei Kollision
	private int goBackY;
	private int dir;
	
	private int startX,startY;
	
	private int width;
    private int height;
	private Image image;
	private ImageIcon ii;

	private int live;	// aktuelle Lebenspunkte
	private int startLive; // Lebenspunkte zu Beginn - für komplettes Auffüllen
	
	private int armour;
	private String armourType;
	
	private int keys;
	
	private int money;
	private int mana;
	private int startMana = 1000;
	
	private String magicType = "immortality";
	private boolean immortal;
	
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private Item activeItem;
	private int activeItemNumber;
	
	private ArrayList<Missile> missiles = new ArrayList<Missile>();
	
	private String [] playerPath = {"images/player/player_up_0.png","images/player/player_right_0.png","images/player/player_down_0.png","images/player/player_left_0.png"};
	private String [] player2Path = {"images/player/player2_up_0.png","images/player/player2_right_0.png","images/player/player2_down_0.png","images/player/player2_left_0.png"};
	
	private Image leftimage, rightimage, upimage, downimage;
	
	
	// Konstruktor
	/**
	 * Erstellt neue Spielfigur an Position x,y
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * @param live Lebenspunkte
	 * @param type Typ
	 */
	public Player(int x, int y, int live, int type){
		if(type==0){
			ii = new ImageIcon(this.getClass().getResource(playerPath[0]));
			upimage = ii.getImage();
			ii = new ImageIcon(this.getClass().getResource(playerPath[1]));
			rightimage = ii.getImage();
			ii = new ImageIcon(this.getClass().getResource(playerPath[2]));
			downimage = ii.getImage();
			ii = new ImageIcon(this.getClass().getResource(playerPath[3]));
			leftimage = ii.getImage();
			
		}
		else if(type == 1){
			
			ii = new ImageIcon(this.getClass().getResource(player2Path[0]));
			upimage = ii.getImage();
			ii = new ImageIcon(this.getClass().getResource(player2Path[1]));
			rightimage = ii.getImage();
			ii = new ImageIcon(this.getClass().getResource(player2Path[2]));
			downimage = ii.getImage();
			ii = new ImageIcon(this.getClass().getResource(player2Path[3]));
			leftimage = ii.getImage();
		}
		
		// Bild laden plus Informationen
		image = rightimage;
		width = image.getWidth(null);
        height = image.getHeight(null);
        
        // Lebenspunkte ermitteln
        startLive = live;
        this.setLive(startLive);
        this.armour = 0;
        this.armourType = "none";
        
        // Geld auf 0 setzen
        this.money = 0;
        this.mana = 0;
        
        // Items und sonstige Werte setzen
        this.immortal = false;
        this.activeItem = null;
        this.activeItemNumber = 0;
        this.keys = 0;
        
        // Startkoordinate setzen
		this.startX = this.x = x;
		this.startY = this.y = y;
		this.dir = 0;
	}
	
	// generelle Bewegung - speziell: siehe "KeyEvent Methoden"
	/**
	 * Bewege Spieler um dx,dy vor
	 */
	public void move(){
		goBackX = x;
		goBackY = y;
		this.x += dx;
		this.y += dy;
		
		// aktuelle Bewegungsrichtung ermitteln
		if(this.x - goBackX > 0){
			dir = 1;
		}
		else if(this.x - goBackX < 0){
			dir = 3;
		}
		else if(this.y - goBackY > 0){
			dir = 2;
		}
		else if(this.y - goBackY < 0){
			dir = 0;
		}
	}
	
	/**
	 * L&auml;sst Spieler zur letztbekannten Position zur&uuml;ckspringen (CollisionDetection: stehenbleiben vor soliden Objekten)
	 */
	public void resetMovement(){
		this.x = goBackX;
		this.y = goBackY;
	}
	/**
	 * Spieler nutz Magie
	 */
	public void useMagic(){
		if(magicType.equals("immortality")){
			if(isImmortal()) this.setImmortal(false);
			else{
				if(this.mana>0) this.setImmortal(true);
			}
		}
	}
	/**
	 * F&uuml;gt der Raketenliste eine neue hinzu (Schiessen)
	 */
	public void fire(){
		if(activeItem != null){
			if(activeItem.hasMissiles() && activeItem.getAmount()!=0){
				Missile m;
				int mX = this.x;
				int mY = this.y;
				
				// ANDERN
				if(dir == 0){
					mX += 0;
					mY += 0;
				}
				else if(dir == 1){
					mX += 0;
					mY += 0;
				}
				else if(dir == 2){
					mX += 0;
					mY += 0;
				}
				else if(dir == 3){
					mX += 0;
					mY += 0;
				}
				
				m = new Missile(mX, mY, activeItem.getItemType(), dir, true);
				missiles.add(m);
				activeItem.addToAmount(-1);
				
			}
		}
		
	}
	
	// Items
	/**
	 * Setze das Aktive Item
	 * @param item aktives Item
	 */
	public void setActiveItem(Item item){
		this.activeItem = item;
	}
	/**
	 * Setze n&auml;chstes Item aus Liste als aktives Item
	 */
	public void switchActiveItem(){
		Item it;
		
		if(this.activeItem!=null){
			this.activeItemNumber++;
			if(this.activeItemNumber>=itemList.size()) this.activeItemNumber = 0;
			
			it=itemList.get(this.activeItemNumber);
			setActiveItem(it);
		}
	}
	/**
	 * Gibt aktives Item zur&uuml;ck
	 * @return aktives Item
	 */
	public Item getActiveItem(){
		return this.activeItem;
	}
	/**
	 * F&uuml;ge der Itemliste ein Item hinzu
	 * @param item hinzuzuf&uuml;gendes Item
	 */
	public void addItem(Item item){
		Item it;
		boolean hasItem  = false;
		
		if(activeItem==null) this.setActiveItem(item);
		for(int i = 0; i<itemList.size(); i++){
			it = (Item)itemList.get(i);
			// prüfe ob Item schon vorhanden --> keine Doppelbelegung, nur Munition aufladen
			if(it.getItemType().equals(item.getItemType())){
				hasItem = true;
				it.setAmount(item.getAmount());
			}
		}
		if(hasItem == false) itemList.add(item);
	}
	
	// get Methoden
	/**
	 * Gibt Breite des Bildes zur&uuml;ck
	 * @return Breite des Bildes
	 */
	public int getWidth(){
		return this.width;
	}
	/**
	 * Gibt H&ouml;he des Bildes zur&uuml;ck
	 * @return H&ouml;he des Bildes
	 */
	public int getHeight(){
		return this.height;
	}
	/**
	 * Gibt Richtung des Netzwerkspielers zur&uuml;ck
	 * @return Richtung
	 */
	public int getDir(){
		return this.dir;
	}
	/**
	 * Gibt X-Koordinate zur&uuml;ck
	 * @return X-Koordinate
	 */
	public int getX(){
		return this.x;
	}
	/**
	 * Gibt Y-Koordinate zur&uuml;ck
	 * @return Y-Koordinate
	 */
	public int getY(){
		return this.y;
	}
	/**
	 * Gibt Liste der Geschosse zur&uuml;ck
	 * @return Liste der Geschosse
	 */
	public ArrayList<Missile> getMissiles(){
		return missiles;
	}
	
	public ArrayList<Item> getItemList(){
		return this.itemList;
	}
	
	/**
	 * Gibt Anzahl der Lebenspunkte zur&uuml;ck
	 * @return Anzahl der Lebenspunkte
	 */
	public int getLive(){
		return this.live;
	}
	/**
	 * Gibt Anzahl der R&uuml;stungspunkte zur&uuml;ck
	 * @return Anzahl der R&uuml;stungspunkte
	 */
	public int getArmour(){
		return this.armour;
	}
	/**
	 * Gibt R&uuml;stungsbezeichnung zur&uuml;ck
	 * @return R&uuml;stungsbezeichnung
	 */
	public String getArmourType(){
		return this.armourType;
	}
	/**
	 * Gibt Anzahl an Geld zur&uuml;ck
	 * @return Anzahl an Geld
	 */
	public int getMoney(){
		return this.money;
	}
	/**
	 * Gibt anzahl an Energie zur&uuml;ck
	 * @return Energie
	 */
	public int getMana(){
		return this.mana;
	}
	/**
	 * Gibt aktuelles Bild zur&uuml;ck
	 * @return Bild
	 */
	public Image getImage(){
		return this.image;
	}
	/**
	 * Gibt Rectangle (Position, Breite und L&auml;nge des Bildes) f&uuml;r die Kollisionserkennung zur&uuml;ck
	 * @return Rectangle (Position, Breite und L&auml;nge des Bildes)
	 */
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(x, y, width, height); 
	}
	/**
	 * Gibt zur&uuml;ck ob Spieler unsterblich ist
	 * @return Unsterblichkeit 
	 */
	public boolean isImmortal(){
		return this.immortal;
	}
	
	/** Gibt die Anzahl der Schl&uuml;ssel zur&uuml;ck
	 * @return Anzahl der Schl&uuml;ssel
	 */
	public int getKeys(){
		return this.keys;
	}
	
	// set Methoden
	/**
	 * &Auml;ndert die Anzahl der Schl&uuml;ssel
	 * @param key Anzahl der hinzuzuf&uuml;genden Schl&uuml;ssel
	 */
	public void changeKeys(int key){
		this.keys += key;
		if(this.keys<0) this.keys = 0;
	}
	/**
	 * Setzt Spieler auf Unsterblich
	 * @param mort Unsterblichkeit an/aus
	 */
	public void setImmortal(boolean mort){
		this.immortal = mort;
	}
	/**
	 * Setzt X-Koordinate des Spielers
	 * @param x X-Koordinate
	 */
	public void setX(int x){
		this.x = x;
	}
	/**
	 * Setzt Y-Koordinate des Netzwerkspielers
	 * @param y Y-Koordinate
	 */
	public void setY(int y){
		this.y = y;
	}
	/**
	 * Setzt dx/dy auf 0 
	 */
	public void hasEnteredShopOrDialog(){
		// damit Spieler stillsteht 
		dx=dy=0;
	}
	/**
	 * F&uuml;gt dem Spieler Lebenspunkte hinzu
	 * @param changeLive Lebenspunkte
	 */
	public void setLive(int changeLive){
		// ändert Anzahl der Lebenspunkte 
		this.live += changeLive;
		if(this.live>12) this.live=12; // bestimme maximale Anzahl an leben
		
	}
	/**
	 * F&uuml;gt dem Spieler R&uuml;stungspunkte hinzu
	 * @param changeArmour R&uuml;stungspunkte
	 */
	public void addArmour(int changeArmour){
		this.armour += changeArmour;
		if (this.armour<0) this.armour = 0;
		if(this.armour == 0) this.armourType="none";
	}
	/**
	 * Setze aktuelle R&uuml;stung;
	 * @param amount Anzahl
	 * @param type R&uuml;stungsbezeichnung;
	 */
	public void setArmour(int amount, String type){
		this.armourType = type;
		this.armour = amount;
	}
	/**
	 * Setzt den Spieler auf die Startwerte zur&uuml;ck
	 */
	public void resetPlayer(){
		this.x = this.startX;
		this.y = this.startY;
		
		this.live = startLive;
        this.armour = 0;
        this.armourType = "none";
        
        // Geld auf 0 setzen
        this.money = 0;
        this.mana = 0;
        
        // Items und sonstige Werte setzen
        this.immortal = false;
        this.activeItem = null;
        this.keys = 0;
        
        missiles = new ArrayList<Missile>();
    	itemList = new ArrayList<Item>();
        
	}
	/**
	 * F&uuml;gt dem Spieler Geld hinzu
	 * @param changeMoney Geld
	 */
	public void setMoney(int changeMoney){
		// ändert Anzahl der Lebenspunkte
		this.money += changeMoney;
		if(this.money>999) this.money=999; // bestimme maximale Anzahl an Geld
	}
	/**
	 * setzt Energie auf den Startwert zur&uuml;ck
	 */
	public void resetMana(){
		// ändert Anzahl Mana
		this.mana = startMana;
	}
	public void setAbsoluteLive(int live){
		this.live = live;
	}
	public void setAbsoluteMana(int mana){
		this.mana = mana;
	}
	
	/**
	 * Entfernt eine Anzahl an Energie
	 * @param rem Energie
	 */
	public void removeMana(int rem){
		this.mana -= rem;
		if(this.mana<0) this.mana = 0;
		else if(this.mana > startMana) this.mana = startMana;
	}
	
	/**
	 * Setze Bild des Spielers
	 * @param pic Bild
	 */
	public void setImage(Image pic) {
        this.image = pic;
        width = image.getWidth(null);
        height = image.getHeight(null);
    }
	
	// KeyEvent Methoden - von oben weitergereicht
	// Key gedrückt
	
	/**
	 * Verarbeitet den Keycode bei gefr&uuml;ckter Taste, steuert den Spieler
	 * @param e Keycode
	 */
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_LEFT){
			dx = -speed;
			setImage(leftimage);
			// dir = 3;
		}
		if(key == KeyEvent.VK_RIGHT){
			dx = speed;
			setImage(rightimage);
			// dir = 1;
		}
		if(key == KeyEvent.VK_UP){
			dy = -speed;
			setImage(upimage);
			// dir = 0;
		}
		if(key == KeyEvent.VK_DOWN){
			dy = speed;
			setImage(downimage);
			// dir = 2;
		}
		if(key == KeyEvent.VK_SPACE){
			fire();
		}
		if(key == KeyEvent.VK_CONTROL){
			useMagic();
		}
		if(key == KeyEvent.VK_SHIFT){
			switchActiveItem();
		}
		
	}
	
	// Key losgelassen
	/**
	 * Verarbeitet den Keycode bei losgelassener Taste, steuert den Spieler
	 * @param e Keycode
	 */
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_LEFT){
			dx = 0;
		}
		if(key == KeyEvent.VK_RIGHT){
			dx = 0;
		}
		if(key == KeyEvent.VK_UP){
			dy = 0;
		}
		if(key == KeyEvent.VK_DOWN){
			dy = 0;
		}
	}
	
}