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
/**
 * Netzwerkspieler Abbild und Steuerung
 *
 */
public class NetPlayer implements Runnable{
	
	private Socket socket;
	private int chosenMap;
	private boolean ready;
	private String message;
	private boolean opponentLostTheGame;
	
	private String player;	// Pfad zum Bild - Spielfigur
	private int speed = 2;	// Geschwindigkeit der Spielfigur
	
	private int dx;	// Veränderung der Koordinaten für Bewegung
	private int dy;
	private int x;
	private int y;	
	private int goBackX; // Rücksprungwerte bei Kollision
	private int goBackY;
	private int dir;
	
	private int startX, startY;
	
	private int width;
    private int height;
	private Image image;

	private int live;	// aktuelle Lebenspunkte
	private int startLive; // Lebenspunkte zu Beginn - für komplettes Auffüllen
	
	private int armour;
	private String armourType;
	
	private int money;
	private int mana;
	private int startMana = 1000;
	
	private String magicType = "immortality";
	private boolean immortal;
	
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private Item activeItem;
	private int activeItemNumber;
	
	private ArrayList<Missile> missiles = new ArrayList<Missile>();

	
	// Konstruktor
	/**
	 * Erstellt neuen Netzwerkspieler an Stelle x,y
	 * @param x X-Koordinate
	 * @param y Y-Koordinate
	 * @param live Lebenspunkte
	 * @param type Typ 
	 * @param socket Socket zum Netzwerkspieler
	 */
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
		this.message = "";
		this.opponentLostTheGame = false;
		
		// Bild laden plus Informationen
		ImageIcon ii = new ImageIcon(this.getClass().getResource(player));
		image = ii.getImage();
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
        
        // Startkoordinate setzen
		this.startX = this.x = x;
		this.startY = this.y = y;
		this.dir = 0;
	}
	
	// generelle Bewegung - speziell: siehe "KeyEvent Methoden"
	/**
	 * Bewege Netzwerkspieler um dx,dy
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
				
				m = new Missile(mX, mY, activeItem.getItemType(), dir, false);
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
	 * Gibt aktives Item zur&uuml;ck
	 * @return aktives Item
	 */
	public Item getActiveItem(){
		return this.activeItem;
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
	 * F&uuml;ge der Itemliste ein Item hinzu
	 * @param item hinzuzuf&uuml;gendes Item
	 */
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
	/**
	 * Gibt zur&uuml;ck ob Netzwerkspieler verloren hat
	 * @return Netzwerkspieler hat Verloren
	 */
	public boolean getLost(){
		return this.opponentLostTheGame;
	}
	
	/**
	 * Gibt empfangene Chatnachricht zur&uuml;ck
	 * @return empfangene Nachricht
	 */
	public String getMessage(){
		return this.message;
	}
	
	/**
	 * Gibt gew&auml;hltes Level zur&uuml;ck
	 * @return gew&auml;hltes Level
	 */
	public int getChosenMap(){
		return this.chosenMap;
	}
	
	/**
	 * Gibt zur&uuml;ck ob Netzwerspieler bereit ist zu spielen
	 * @return Bereitschaft
	 */
	public boolean getReady(){
		return this.ready;
	}
	
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
	
	// set Methoden
	/**
	 * Setzt die empfangene Nachricht auf leer zur&uuml;ck
	 */
	public void resetMessage(){
		this.message = "";
	}
	
	/**
	 * Setzt Netzwerkspieler auf Unsterblich
	 * @param mort Unsterblichkeit an/aus
	 */
	public void setImmortal(boolean mort){
		this.immortal = mort;
	}
	
	/**
	 * Setzt X-Koordinate des Netzwerkspielers
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
		
		this.opponentLostTheGame = false;
		this.ready = false;
		
		this.live = startLive;
        this.armour = 0;
        this.armourType = "none";
        
        // Geld auf 0 setzen
        this.money = 0;
        this.mana = 0;
        
        // Items und sonstige Werte setzen
        this.immortal = false;
        this.activeItem = null;
        
        missiles = new ArrayList<Missile>();
    	itemList = new ArrayList<Item>();
        
	}
	
	// KeyEvent Methoden - von oben weitergereicht
	// Key gedrückt
	/**
	 * Verarbeitet den Keycode bei gefr&uuml;ckter Taste, steuert den Spieler
	 * @param key Keycode
	 */
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
		if(key == KeyEvent.VK_SHIFT){
			switchActiveItem();
		}
	}
	
	// Key losgelassen
	/**
	 * Verarbeitet den Keycode bei losgelassener Taste, steuert den Spieler
	 * @param key Keycode
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			getKeycodeOverNetwork();
		} 
		catch (Exception e) {
			System.out.println("Fehlgeschlagen!");
		}
		
	}
	
	/**
	 * Empf&auml;ngt Daten vom entfernten Spieler und verarbeitet diese
	 * @throws Exception
	 */
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
			else if(command.equals("MSG")){
				// Chat
				StrKeycode = "";
				while(t.hasMoreTokens()) StrKeycode+=t.nextToken()+" ";
				this.message = StrKeycode;
				
			}
			else if(command.equals("LOST")){
				// Anderer Spieler verloren
				this.opponentLostTheGame = true;
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