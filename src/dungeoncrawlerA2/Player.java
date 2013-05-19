package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

// Evtl später auch GameElement

// Player: Spielfigur
public class Player{
	/*
	 * WICHTIG: Bilddateien müssen in den Ordner bin/dungeoncrawlerA2 kopiert werden
	 * evtl. gegen Ende try/catch Block zur Fehlervermeidung
	 * */
	private String player = "player.png";	// Pfad zum Bild - Spielfigur
	private int speed = 2;	// Geschwindigkeit der Spielfigur
	
	private int dx;
	private int dy;
	private int x;
	private int y;
	private Image image;
	
	// Konstruktor
	public Player(int x, int y){
		ImageIcon ii = new ImageIcon(this.getClass().getResource(player));
		image = ii.getImage();
		this.x = x;
		this.y = y;
	}
	
	// generelle Bewegung - speziell: siehe "KeyEvent Methoden"
	public void move(){
		this.x += dx;
		this.y += dy;
	}
	
	// get Methoden
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public Image getImage(){
		return this.image;
	}
	
	// set Methoden
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
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
