package dungeoncrawlerA2;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

// Evtl später auch GameElement

// Player: Spielfigur
public class NetPlayer implements Runnable{
	
	private Socket socket;
	private int chosenMap;
	private boolean ready;
	
	private String player;	// Pfad zum Bild - Spielfigur
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
	
	private int armour;
	private int startArmour = 3;
	
	private int money;
	private int mana;
	private int startMana = 1000;
	
	private String magicType = "immortality";
	private boolean immortal;
	
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private Item activeItem;
	
	private ArrayList<Missile> missiles = new ArrayList<Missile>();

	
	// Konstruktor
	public NetPlayer(int x, int y, int live, int type, Socket socket){
		if(type==0){
			player = "images/player.png";
		}
		else if(type == 1){
			player = "images/player2.png";
		}
		
		this.socket = socket;
		this.chosenMap = 0;
		this.ready = false;
		
		// Bild laden plus Informationen
		ImageIcon ii = new ImageIcon(this.getClass().getResource(player));
		image = ii.getImage();
		width = image.getWidth(null);
        height = image.getHeight(null);
        
        // Lebenspunkte ermitteln
        startLive = live;
        this.setLive(startLive);
        this.armour = startArmour;
        
        // Geld auf 0 setzen
        this.money = 0;
        this.mana = 0;
        
        // Items und sonstige Werte setzen
        this.immortal = false;
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
	
	public void useMagic(){
		if(magicType.equals("immortality")){
			if(isImmortal()) this.setImmortal(false);
			else{
				if(this.mana>0) this.setImmortal(true);
			}
		}
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
				
				m = new Missile(mX, mY, activeItem.getItemType(), dir, false);
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
	public int getChosenMap(){
		return this.chosenMap;
	}
	
	public boolean getReady(){
		return this.ready;
	}
	
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
	
	public int getArmour(){
		return this.armour;
	}
	
	public int getMoney(){
		return this.money;
	}
	
	public int getMana(){
		return this.mana;
	}
	
	public Image getImage(){
		return this.image;
	}
	
	public Rectangle getBounds(){
		// Kollisionserkennung
		return new Rectangle(x, y, width, height); 
	}
	
	public boolean isImmortal(){
		return this.immortal;
	}
	
	// set Methoden
	public void setImmortal(boolean mort){
		this.immortal = mort;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void hasEnteredShopOrDialog(){
		// damit Spieler stillsteht 
		dx=dy=0;
	}
	
	public void setLive(int changeLive){
		// ändert Anzahl der Lebenspunkte || Rüstung 
		// TODO später etl von außen getrennt
		if(armour>0 && changeLive<=0) this.armour--;
		else this.live += changeLive;
		if(this.live>12) this.live=12; // bestimme maximale Anzahl an leben
		
	}
	
	public void setMoney(int changeMoney){
		// ändert Anzahl der Lebenspunkte
		this.money += changeMoney;
		if(this.money>999) this.money=999; // bestimme maximale Anzahl an Geld
	}
	
	public void resetMana(){
		// ändert Anzahl Mana
		this.mana = startMana;
	}
	
	public void removeMana(int rem){
		this.mana -= rem;
		if(this.mana<0) this.mana = 0;
		else if(this.mana > startMana) this.mana = startMana;
	}
	
	// KeyEvent Methoden - von oben weitergereicht
	// Key gedrückt
	public void keyPressed(int key){
		
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
		if(key == KeyEvent.VK_CONTROL){
			useMagic();
		}
	}
	
	// Key losgelassen
	public void keyReleased(int key){
		
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

	@Override
	public void run() {
		try {
			getKeycodeOverNetwork();
		} 
		catch (Exception e) {
			System.out.println("Fehlgeschlagen!");
		}
		
	}
	
	public void getKeycodeOverNetwork() throws Exception{
		
		InputStream is = this.socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringTokenizer t;
		String line;
		String command;
		String StrKeycode;
		String sX, sY;
		int x,y;
		int keycode;
		
		while(true){
			x=this.x;
			y=this.y;
			line = br.readLine();
			if(line==null) break;
			
			t = new StringTokenizer(line);
			command = t.nextToken();
			
			
			if(command.equals("P")){
				StrKeycode = t.nextToken();
				if(StrKeycode.length()==2){
					keycode= 10*(StrKeycode.charAt(0)-48)+(StrKeycode.charAt(1)-48);
					this.keyPressed(keycode);
				}
			}
			else if(command.equals("R")){
				StrKeycode = t.nextToken();
				if(StrKeycode.length()==2){
					keycode= 10*(StrKeycode.charAt(0)-48)+(StrKeycode.charAt(1)-48);
					this.keyReleased(keycode);
				}
			}
			else if(command.equals("LV")){
				// Level wählen
				StrKeycode = t.nextToken();
				
				keycode = StrKeycode.charAt(0)-48;
				this.chosenMap = keycode;
				
			}
			else if(command.equals("START")){
				// Spieler bereit
				StrKeycode = t.nextToken();
				
				keycode = StrKeycode.charAt(0)-48;
				if(keycode==1) this.ready=true;
				else this.ready = false;
				
			}
			else{
				// Position abgleichen
				sX = command;
				x=100*(sX.charAt(0)-48)+10*(sX.charAt(1)-48)+(sX.charAt(2)-48);
				
				
				this.setX(x);
				
				sY = t.nextToken();
				y=100*(sY.charAt(0)-48)+10*(sY.charAt(1)-48)+(sY.charAt(2)-48);
				
				
				
				this.setY(y);
			}
			
		}
		
	}
	
	
	
}