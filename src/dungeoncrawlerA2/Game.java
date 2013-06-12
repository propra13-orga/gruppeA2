package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;

// Game: Menü, Spielfeld und Spielsteuerung
public class Game extends JPanel implements ActionListener{
	
	// Menüelemente
	private JButton b1;
	private JButton b2;
	private int buttonPosX;
	private int buttonPosY;
	
	// Hauptspielelemente
	private Timer timer;	// Timer für ActionEvent
	private boolean ingame; // Wert für Gameloop
	private boolean won; // für Abfrage ob gewonnen
	private boolean firstStart; // ist erster Start? - Menüdarstellung
	private int room; // aktueller Raum des Sungeons
	
	private Player player;	// Spielfigur
	private int startX, startY;	// Startwert Spielfigur 
	private int startLive; // Lebenspunkte zu beginn des Levels
	private int tollerance = 40; // Schadenstolleranz
	
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Ground> grounds = new ArrayList<Ground>();
	private ArrayList<Enemy> enemys = new ArrayList<Enemy>();
	
	// Designelemente
	private Color backgroundColor = Color.BLACK; // Hintergrundfarbe
	private int fps = 100;	// Bildwiedrholrate - (evtl später runtersetzen?)
	
	private int windowSizeX = 800;	
	private int windowSizeY = 560; // Angepasst auf 800x600 Bild, 40x40px Objekte, Ststusleiste abgezogen
	private int blockSize = 40;
	
	// Statusleiste
	private int statusBarX = 5;	// Koordinaten Statusleiste (in px)
	private int statusBarY = 562;
	private int statusBarSpace = 2; // Standartraum zwischen Elementen in Statusleiste
	
	private String statusBarLivePath = "images/live_01.png";
	private String statusBarBackgroundPath = "images/statusBar.png";
	
	private Image statusBarBackground;
	private Image statusBarLiveImage;
	private int statusBarLiveImageWidth, statusBarLiveImageHeight; // Größe der Lebenspunkte in Statusleiste
	
	// Leveldatenelemente
	private String levelpath = "leveldata/level01.txt";	// Dateipfad zu konkretem Level -> wird später aus anderer Hauptspieldatei(Enthält liste der Level in Reihenfolge) ausgelesen
	
	private String[] leveldata;
	private String[] enemydata;
	private String[] itemdata;
	private String[] interactdata; // für Shops und besondere Interaktionen
	private int[][] exitdata;
	
	private String intro;
	private String endBossLocation;
	private String levelName;
	private int levelNumber;
	
	// Zustandsvariablen & Zeitabhängige Events;
	private boolean playerCanGetDamage; 
	private int tolleranceTime; // Tolleranzzeit um bei Gegnerkontakt nicht alle Leben zu verlieren
	
	// Konstruktor
	public Game(){
		setLayout(null); // um Buttons beliebig zu positionieren
		
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(backgroundColor);
		setDoubleBuffered(true); 
		firstStart=true;	
		
		timer = new Timer(1000/fps, this); // Timer nach fps einstellen
		
		// Menü vorbereiten
		b1 = new JButton("Start Game");
		b2 = new JButton("Exit");
		initMenu();
		
	}
	
	public void initMenu(){
		// Zeichne Menüelemente
		// Lege Standartpositionen für Buttons fest
		buttonPosX = windowSizeX/2-100;
		buttonPosY = windowSizeY/2-40;
		// bestimme Position und Größe
		b1.setBounds(buttonPosX,buttonPosY,200,30);
		b2.setBounds(buttonPosX,buttonPosY+40,200,30);
		// benenne Aktionen
		b1.setActionCommand("start");
		b2.setActionCommand("end");
		// ActionListener hinzufügen
		b1.addActionListener(this);
		b2.addActionListener(this);
		// füge Buttons zum Panel hinzu
		add(b1);
		add(b2);
	}
	
	// Rückkehr zum Menü und Anzeige von Spielergebnis (Game Over oder You Win)
	public void paintMessage(String msg, Graphics g){
		// Mache Buttons wieder sichtbar
		b1.setVisible(true);
		b2.setVisible(true);
		
		Font small = new Font("Arial", Font.BOLD, 20);
		FontMetrics metr = this.getFontMetrics(small);
		
		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(msg, (windowSizeX - metr.stringWidth(msg))/2, 80);
		
	}
	
	// Startet Spiel
	public void initGame(String path){
		// Buttons ausblenden
		b1.setVisible(false);
		b2.setVisible(false);
		
		// Lade Level
		loadLevel(path);
		
		// Spielablaufparameter setzen
		firstStart=false;
		ingame = true;
		won = false;
		
		room = 0;
		initRoom(room); // ersten Raum aufbauen
		
		player = new Player(startX, startY, startLive); // setze neue Spielfigur an Stelle x,y
		playerCanGetDamage = true;
		tolleranceTime = 0;
		
		// Statusleiste vorbereiten
		ImageIcon ii;
		
		ii = new ImageIcon(this.getClass().getResource(statusBarBackgroundPath));	// Bild - Hintergrund Statusleiste
		statusBarBackground = ii.getImage();
		
		ii = new ImageIcon(this.getClass().getResource(statusBarLivePath)); // Bild Lebenspunkte
		statusBarLiveImage = ii.getImage();
		statusBarLiveImageWidth = statusBarLiveImage.getWidth(null);
		statusBarLiveImageHeight = statusBarLiveImage.getHeight(null);
		
		timer.start();
	}
	
	public void loadLevel(String path){
		boolean isReadingLevel = false;
		boolean isReadingIntro = false;
		int rooms = 0;
		String line = null;
		String request = null;
		String data1, data2;
		int z1, z2, r, count, count2;
		
		try {
			// Reader und Stream vorbereiten -> Jar fähig
			InputStream is = getClass().getResourceAsStream(path);
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader in = new BufferedReader(reader);
			
			// Hilfsvariablen auf 0 setzen
			r = count = count2 = 0;
			// Zeile einlesen
			while ((line = in.readLine()) != null) {
				// Counter auf 0 setzen
				z1 = z2 = 0;
				request=" ";
				
				StringTokenizer tokens = new StringTokenizer(line);
				
				// GET REQUEST
				if(line.length()>0) request = tokens.nextToken(); // Save Request
				
				// Requests analysieren
				
				// Generelle Levelaten einlesen
				// Levelnummer einlesen
				if(request.equals("#NR")){
					data1 = tokens.nextToken();
					z1 = data1.charAt(0)-48;	// von char nach int -> -48
					z2 = data1.charAt(1)-48;
					levelNumber = 10*z1+z2;
				}
				
				// Levelname einlesen
				if(request.equals("#TITLE")){
					if(tokens.hasMoreTokens()) {
						levelName = tokens.nextToken();
						while(tokens.hasMoreTokens()) levelName+=" "+tokens.nextToken();
					}
				}
				
				// Startwerte Spielfigur einlesens
				if(request.equals("#START")){
					data1 = tokens.nextToken();
					data2 = tokens.nextToken();
					z1 = data1.charAt(0)-48; // hole 100er
					z2 = data1.charAt(1)-48; // hole 10er
					startX = (data1.charAt(2)-48) + 100*z1 + 10*z2;	//bilde x koordinate
					z1 = data2.charAt(0)-48; // hole 100er
					z2 = data2.charAt(1)-48; // hole 10er
					startY = (data2.charAt(2)-48) + 100*z1 + 10*z2;	//bilde y koordinate
	
					// Lebenspunkte einlesen
					data1 = tokens.nextToken();
					z1 = data1.charAt(0)-48; // hole 10er
					z2 = data1.charAt(1)-48; // hole 1er
					startLive = 10*z1+z2;
				}
				
				// Intro einlesen
				if(isReadingIntro){
					if(request.equals("#END")) isReadingIntro = false;
					else 
						if(count2 == 0){
							intro = line;
							count2++;
						}
						else intro += line;
				}
				if(request.equals("#INTRO")){
					isReadingIntro = true;
				}
				
				// Endgegnerdaten einlesen
				if(request.equals("#FINAL")){
					endBossLocation = tokens.nextToken()+" ";
					for(int i = 0; i<3; i++) endBossLocation += tokens.nextToken() +" ";
				}
				
				// Anzahl Räume auslesen
				if(request.equals("#ROOMS")){
					data1 = tokens.nextToken();
					z1 = data1.charAt(0);	// Hole Zehner
					z2 = data1.charAt(1);	// Hole Einser
					rooms = 10*(z1-48)+(z2-48); // Hole Anzahl Räume
					
					// Erstelle Arrays 
					leveldata = new String[rooms];
					enemydata = new String[rooms];
					exitdata = new int[rooms][4];
					itemdata = new String[rooms];
					interactdata = new String[rooms];
				}
				
				// Spezielle leveldaten einlesen (pro Raum)
				// Raumwechsel feststellen
				if(request.equals("#NEWROOM")){
					count = 0;
					
					data1 = tokens.nextToken();
					z1 = data1.charAt(0);	// Hole Zehner
					z2 = data1.charAt(1);	// Hole Einser
					r = 10*(z1-48)+(z2-48); // Hole Anzahl Räume
				}
				
				// Raumaufbau auslesen
				if(isReadingLevel){
					if(count==0) leveldata[r]=line;
					else leveldata[r]+=line;
					count++;
					if(count==14) isReadingLevel = false;
				}
				if(request.equals("#LEVEL")){
					isReadingLevel = true;
				}
				
				// Feinde auslesen
				if(request.equals("#ENEMY")){
					if(tokens.hasMoreTokens()) enemydata[r]=tokens.nextToken()+" "; // hole ersten
					else enemydata[r]="";
					while(tokens.hasMoreTokens()) enemydata[r]+=tokens.nextToken()+" "; // hole Rest
				}
				
				// Items auslesen
				if(request.equals("#ITEM")){
					if(tokens.hasMoreTokens()) itemdata[r]=tokens.nextToken()+" "; // hole ersten
					else itemdata[r]="";
					while(tokens.hasMoreTokens()) itemdata[r]+=tokens.nextToken()+" "; // hole Rest
				}
				// Interaktive Elemente auslesen - zB Shops
				if(request.equals("#INTERACT")){
					if(tokens.hasMoreTokens()) interactdata[r]=tokens.nextToken()+" "; // hole ersten
					else interactdata[r]="";
					while(tokens.hasMoreTokens()) interactdata[r]+=tokens.nextToken()+" "; // hole Rest
				}
				
				// Ausgänge einlesen
				if(request.equals("#EXIT")){
					for(int i = 0; i<4; i++){
						data1 = tokens.nextToken();
						z1 = data1.charAt(0);
						z2 = data1.charAt(1);
						if(z1=='-'){
							exitdata[r][i]=-(z2-48); // negative Ausgänge (kein Ausgane / Endausgang) auslesen
						}
						else{
							exitdata[r][i]=(z1-48)*10+(z2-48);
						}
					}
				}
				
			}
			
			// BufferedReader schließen
			in.close();
		} 
		catch (IOException e) {
			System.out.println("Datei nicht gefunden, oder fehlerhaft.");
		}
		
	}
	
	// Raum erstellen
	public void initRoom(int roomnumber){
		int x = 0;
		int y = 0;
		
		Wall wall;
		Ground ground;
		Enemy enemy;
		
		char element;
		char type;
		
		walls.clear();
		grounds.clear();
		enemys.clear();
		
		// Raum auslesen aus leveldata
		for(int i=0; i<leveldata[roomnumber].length(); i+=3){
			
			// Element und Typ(Version) aus String lesen
			element = leveldata[roomnumber].charAt(i);
			type = leveldata[roomnumber].charAt(i+1);
			
			// Beginne Hinzufügen von Elementen zu Array Lists
			if(element == 'W'){
				wall = new Wall(x, y, type);
				walls.add(wall);
			}
			else if(element == 'G'){
				ground = new Ground(x, y, type);
				grounds.add(ground);
			}			
			
			// erhöhe x, y um 1 Block
			x+=blockSize;
			// Falls Bildschirm verlassen -> neue Zeile
			if(x>=windowSizeX){
				x=0;
				y+=blockSize;
			}
		}
		
		// Gegner auslesen aus enemydata
		for(int j=0; j<enemydata[roomnumber].length(); j+=9){
			// Gegnertyp
			element = enemydata[roomnumber].charAt(j);
			type = enemydata[roomnumber].charAt(j+1);

			// Koordinaten der Gegner aus String filtern und in Pixel umwandeln
			int x10 = enemydata[roomnumber].charAt(j+3)-48;
			int x01 = enemydata[roomnumber].charAt(j+4)-48;
			int enemyX = (10*x10 + x01)*blockSize;
			
			int y10 = enemydata[roomnumber].charAt(j+6)-48;
			int y01 = enemydata[roomnumber].charAt(j+7)-48;
			int enemyY = (10*y10 + y01)*blockSize;
			
			// Gegner erstellen und in Liste einfügen
			if(element == 'E'){
				enemy = new Enemy(enemyX, enemyY, type);
				enemys.add(enemy);
			}
		}
		
	}
	
	// Raum zeichnen
	public void buildRoom(Graphics g){
		ArrayList<GameElement> room = new ArrayList<GameElement>();
		// Raumelemente einfügen
		room.addAll(walls);
		room.addAll(grounds);
		// Gegner einfügen - wichtig: Erst Raum, dann andere Objekte
		room.addAll(enemys);
		
		for(int i=0; i<room.size(); i++){
			// Element holen und zeichnen
			GameElement element = (GameElement)room.get(i);
			g.drawImage(element.getImage(),element.getX(),element.getY(), this);
		}
	}
	
	// Prüfen ob Raum gewechselt
	public void checkRoom(){
		int x = player.getX();
		int y = player.getY();
		int pw = player.getWidth();
		int ph = player.getHeight();
		
		int newX = x; // Position des Spielers nach Raumwechsel
		int newY = y;
		int nextRoom = -1;
		
		if(y<0-ph/2){
			// Norden
			nextRoom = exitdata[room][0];
			newY = windowSizeY-ph/2;
		}
		else if(x>windowSizeX-pw/2){
			// Osten
			nextRoom = exitdata[room][1];
			newX = 0-pw/2;
		}
		else if(y>windowSizeY-ph/2){
			// Süden
			nextRoom = exitdata[room][2];
			newY = 0-ph/2;
		}
		else if(x<0-pw/2){
			// Westen
			nextRoom = exitdata[room][3];
			newX = windowSizeX-pw/2;
		}
		
		if(nextRoom >= 0){
			// bei Raumwechsel neuen Raum laden und neue Koordinaten bestimmen
			this.room = nextRoom;
			initRoom(room);
			player.setX(newX);
			player.setY(newY);
		}
		else if(nextRoom == -2){
			// wenn Ziel erreicht
			won = true;
			ingame = false;
			timer.stop();
		}
	}
	
	// Kollision prüfen
	public void checkCollision(){
		
		// Spieler Ausmaße ermitteln
		Rectangle r_player = player.getBounds();
		
		// Kollisionen mit Wand -> stehenbleiben
		for(int i=0;i<walls.size(); i++){
			Wall w = (Wall)walls.get(i);
			Rectangle r_wall = w.getBounds();
			
			if(r_player.intersects(r_wall)){
				player.resetMovement();
			}
		}
		
		// Kollisionen mit Gegner -> Lebenspunkte weg
		for(int j=0;j<enemys.size(); j++){
			Enemy e = (Enemy)enemys.get(j);
			Rectangle r_enemy = e.getBounds();
			
			if(r_player.intersects(r_enemy)){
				// Leben reduzieren
				if(playerCanGetDamage){
					player.setLive(-e.getDamage()); 
					playerCanGetDamage = false;
					tolleranceTime = tollerance; // Tolleranzwert
				}
				player.resetMovement();
				System.out.println(player.getLive());
			}
		}
		
	}
	
	// Paint Methode - zeichnet Bildschirm
	public void paint(Graphics g){
		super.paint(g);
		
		// Prüfe ob im Spiel
		if(ingame){
			// zeichne Raum
			buildRoom(g);
			
			// zeichne Spielfigur
			g.drawImage(player.getImage(), player.getX(), player.getY(), this);
			
			// zeichne Statusleiste
			paintStatusBar(g);
		}
		else{
			// Nachricht (Game Over/You Win) und Rückkehr ins Menü vorbereiten 
			String msg;
			
			if(won) msg = "YOU WIN";
			else if(firstStart) msg = " ";	// Für ersten Start leere Nachricht
			else msg = "GAME OVER";
			
			paintMessage(msg,g);
		}
		
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	// Statusleiste zeichnen
	public void paintStatusBar(Graphics g){
		// Statusleiste zeichnen - Hintergrund
		g.drawImage(statusBarBackground, 0, windowSizeY, this);
		
		// Lebenspunkte ermitteln + Darstellen
		int l = player.getLive();
		for(int i = 0; i<l; i++){
			g.drawImage(statusBarLiveImage, statusBarX+i*(statusBarLiveImageWidth+statusBarSpace), statusBarY, this);
		}
		
	}
	
	// actionPerformed 
	public void actionPerformed(ActionEvent e){
		if(ingame){
			// im Spiel - wird vom Timer abhängig aufgerufen
			if(player.getLive() <= 0){ // prüfe ob Spieler noch im Spiel oder Lebenspunkte 0
				// Spieler stirbt, Spiel beenden
				ingame = false;
				timer.stop();
			}
			else{
				// Prüfe ob Spielfigur Schaden erleiden kann
				if(tolleranceTime>0) tolleranceTime--;
				else playerCanGetDamage = true;
				player.move();	// bewege Spielfigur
				checkCollision();	// prüfe Kollision
				checkRoom();	// prüfe ob Raum gewechselt oder Ziel erreicht
			}
			
		}
		else{
			// im Menü - Aufruf bei button
			String action = e.getActionCommand();
			if(action.equals("start")) initGame(levelpath);	// Spiel Starten - Später: vorher aktuelles/gewähltes Level in levelpath laden!
			else if(action.equals("end")) System.exit(0);	// Programm beenden
		}

		repaint();	// zeichne Bildschirm neu - erneuter Aufruf von paint()
		
	}
	
	// Steuerung - Weitergabe der Keycodes nach Unten
	private class TAdapter extends KeyAdapter{
		
		public void keyReleased(KeyEvent e){
			if(ingame) player.keyReleased(e);
		}
		public void keyPressed(KeyEvent e){
			if(ingame) player.keyPressed(e);
		}
	}
}

