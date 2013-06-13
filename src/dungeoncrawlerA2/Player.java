package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

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
	
	private int width;
    private int height;
	private Image image;

	private int live;	// aktuelle Lebenspunkte
	private int startLive; // Lebenspunkte zu Beginn - für komplettes Auffüllen
	
	private int money;
	
	// Konstruktor
	public Player(int x, int y, int live){
		// Bild laden plus Informationen
		ImageIcon ii = new ImageIcon(this.getClass().getResource(player));
		image = ii.getImage();
		width = image.getWidth(null);
        height = image.getHeight(null);
        
        // Lebenspunkte ermitteln
        startLive = live;
        this.live = startLive;
        
        // Geld auf 0 setzen
        this.money = 0;
        
        // Startkoordinate setzen
		this.x = x;
		this.y = y;
	}
	
	// generelle Bewegung - speziell: siehe "KeyEvent Methoden"
	public void move(){
		goBackX = x;
		goBackY = y;
		this.x += dx;
		this.y += dy;
	}
	
	public void resetMovement(){
		this.x = goBackX;
		this.y = goBackY;
	}
	
	// get Methoden
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
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
		}
		if(key == KeyEvent.VK_RIGHT){
			dx = speed;
		}
		if(key == KeyEvent.VK_UP){
			dy = -speed;
		}
		if(key == KeyEvent.VK_DOWN){
			dy = speed;
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
