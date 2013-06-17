package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

// Evtl später auch GameElement

// Player: Spielfigur
public class Player{
	
	private String player = "images/player.png";	// Pfad zum Bild - Spielfigur
	private int speed = 2;	// Geschwindigkeit der Spielfigur
	
	private int dx;	// Veränderung der Koordinaten für Bewegung
	private int dy;
	private int x;
	private int y;	
	private int goBackX; // Rücksprungwerte bei Kollision
	private int goBackY;
	private int dir;
	
	private int width;
    private int height;
	private Image image;

	private int live;	// aktuelle Lebenspunkte
	private int startLive; // Lebenspunkte zu Beginn - für komplettes Auffüllen
	
	private int money;
	
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private Item activeItem;
	
	private ArrayList<Missile> missiles = new ArrayList<Missile>();

	
	// Konstruktor
	public Player(int x, int y, int live){
		// Bild laden plus Informationen
		ImageIcon ii = new ImageIcon(this.getClass().getResource(player));
		image = ii.getImage();
		width = image.getWidth(null);
        height = image.getHeight(null);
        
        // Lebenspunkte ermitteln
        startLive = live;
        this.live = 0;
        this.setLive(startLive);
        
        // Geld auf 0 setzen
        this.money = 0;
        
        // Items und sonstige Werte setzen
        this.activeItem = null;
        
        // Startkoordinate setzen
		this.x = x;
		this.y = y;
		this.dir = 0;
	}
	
	// generelle Bewegung - speziell: siehe "KeyEvent Methoden"
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
	
	public void resetMovement(){
		this.x = goBackX;
		this.y = goBackY;
	}
	
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
				
				m = new Missile(mX, mY, activeItem.getItemType(), dir);
				missiles.add(m);
				activeItem.addToAmount(-1);
			}
		}
		
	}
	
	// Items
	public void setActiveItem(Item item){
		this.activeItem = item;
	}
	
	public Item getActiveItem(){
		return this.activeItem;
	}
	
	public void addItem(Item item){
		Item it;
		boolean hasItem  = false;
		
		if(activeItem==null) this.setActiveItem(item);
		for(int i = 0; i<itemList.size(); i++){
			it = itemList.get(i);
			// prüfe ob Item schon vorhanden --> keine Doppelbelegung, nur Munition aufladen
			if(it.getItemType().equals(item.getItemType())){
				hasItem = true;
				it.setAmount(item.getAmount());
			}
		}
		if(hasItem == false) itemList.add(item);
	}
	
	// get Methoden
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public int getDir(){
		return this.dir;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public ArrayList<Missile> getMissiles(){
		return missiles;
	}
	
	public int getLive(){
		return this.live;
	}
	
	public int getMoney(){
		return this.money;
	}
	
	public Image getImage(){
		return this.image;
	}
	
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(x, y, width, height); 
	}
	
	// set Methoden
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setLive(int changeLive){
		// ändert Anzahl der Lebenspunkte
		this.live += changeLive;
		if(this.live>24) this.live=24; // bestimme maximale Anzahl an leben
	}
	
	public void setMoney(int changeMoney){
		// ändert Anzahl der Lebenspunkte
		this.money += changeMoney;
		if(this.money>999) this.money=999; // bestimme maximale Anzahl an Geld
	}
	
	// KeyEvent Methoden - von oben weitergereicht
	// Key gedrückt
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_LEFT){
			dx = -speed;
			// dir = 3;
		}
		if(key == KeyEvent.VK_RIGHT){
			dx = speed;
			// dir = 1;
		}
		if(key == KeyEvent.VK_UP){
			dy = -speed;
			// dir = 0;
		}
		if(key == KeyEvent.VK_DOWN){
			dy = speed;
			// dir = 2;
		}
		if(key == KeyEvent.VK_SPACE){
			fire();
		}
	}
	
	// Key losgelassen
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
