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
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.Timer;

// Game: Menü, Spielfeld und Spielsteuerung
public class Game extends JPanel implements ActionListener{
	
	// Netzwerkelemente
	private boolean inNetworkMenu;
	private boolean isServer;
	private boolean isClient;
	private boolean inNetworkGame;
	
	private InetAddress localIP;
	private InetAddress remoteIP;
	private int port = 6789;
	
	private ServerSocket serverSocket;
	private Socket client;
	
	private NetPlayer player2;
	private Thread thread;
	//private DataOutputStream os;
	private PrintStream os;
	
	private int delay; // zum verzögern bei netzwerkspiel
	
	private JTextField ip0,ip1,ip2,ip3;
	
	// Menüelemente
	private JButton b1;
	private JButton b2;
	private JButton b3; // Leveleditor
	private JButton b4; // Multiplayer
	// Netzwerkmenüelemente
	private JButton netStartServer, netConnect, back;
	
	private int buttonPosX;
	private int buttonPosY;
	
	// Hauptspielelemente
	private Timer timer;	// Timer für ActionEvent
	private boolean ingame; // Wert für Gameloop
	private boolean won; // für Abfrage ob gewonnen
	private boolean firstStart; // ist erster Start? - Menüdarstellung
	private int room; // aktueller Raum des Dungeons
	
	private Player player;	// Spielfigur
	private int startX, startY;	// Startwert Spielfigur 
	private int startLive; // Lebenspunkte zu beginn des Levels
	private int tollerance = 60; // Schadenstolleranz
	private int startLeftTry = 3;
	private int leftTry;
	
	private ArrayList<Door> doors = new ArrayList<Door>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Ground> grounds = new ArrayList<Ground>();
	private ArrayList<Enemy> enemys = new ArrayList<Enemy>();
	private ArrayList<Item> items = new ArrayList<Item>();
	private ArrayList<Missile> missiles = new ArrayList<Missile>();
	private ArrayList<Missile> missiles2 = new ArrayList<Missile>();
	private ArrayList<Missile> eMissiles = new ArrayList<Missile>();
	private ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	private ArrayList<NPC> npcs = new ArrayList<NPC>();
	private FinalEnemy finalEnemy;
	
	private ArrayList<Item> shopItems = new ArrayList<Item>();
	private String[] shopList = {"mana","health","plasmagun"};
	private int[] shopPrice = {50, 50, 100};

	private int shopX = 150;
	private int shopY = 150;
	private int chosenItem;

	private int activeCheckpointRoom, activeCheckpointX, activeCheckpointY, activeCheckpointLevel;
	
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
	private int statusBarLiveContainer = 270;
	
	private String statusBarLivePath = "images/live_01.png";
	private String statusBarBackgroundPath = "images/statusBar.png";
	private String statusBarMoneyPath = "images/money_01.png";
	private String statusBarArmourPath = "images/armour_01.png";
	
	private Image statusBarBackground;
	private Image statusBarMoneyImage;
	private Image statusBarLiveImage;
	private Image statusBarArmourImage;
	
	private int statusBarLiveImageWidth, statusBarLiveImageHeight; // Größe der Lebenspunkte in Statusleiste
	private int itemBoxSize = 30;
	
	private int manaBoxHeight = 20;
	
	
	// Leveldatenelemente
	private String[] levelpath = {"leveldata/level01.txt","leveldata/level02.txt","leveldata/level03.txt"};	
	private int level;
	
	private String[] leveldata;
	private String[] enemydata;
	private String[] itemdata;
	private String[] interactdata; // für Shops und besondere Interaktionen
	private int[][] exitdata;
	private String[] doordata;
	private String dialog = "Hallo, ich kann dir was erzählen.";
	
	private String intro;
	private String levelName;
	private int levelNumber;
	
	private String endBossLocation;
	private int endBossRoom;
	
	// Zustandsvariablen & Zeitabhängige Events;
	private boolean playerCanGetDamage; 
	private int tolleranceTime; // Tolleranzzeit um bei Gegnerkontakt nicht alle Leben zu verlieren
	private Item playerActiveItem;
	private Image activeItemImage;
	private int ammunition = 0;
	
	private boolean inDialog;
	private boolean inShop;
	
	
	// TODO setFont hinzufügen und Schriftarten anpassen
	
	// Konstruktor
	public Game(){
		// Setze kurzspeicherelemente auf 0, bzw inaktiv
		level = 0;
		activeCheckpointLevel = activeCheckpointRoom = activeCheckpointX = activeCheckpointY = -1;
		leftTry = startLeftTry;
		
		setLayout(null); // um Buttons beliebig zu positionieren
		
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(backgroundColor);
		setDoubleBuffered(true); 
		firstStart=true;	
		inNetworkMenu=false;
		inNetworkGame = isServer = isClient = false;
		delay = 0;
		
		timer = new Timer(1000/fps, this); // Timer nach fps einstellen
		
		// Menü vorbereiten
		b1 = new JButton("Start");
		b2 = new JButton("Exit");
		b3 = new JButton("Leveleditor");
		b4 = new JButton("Multiplayer");
		
		netStartServer = new JButton("Server erstellen");
		netConnect = new JButton("Verbinden");
		back = new JButton("Hauptmenü");
		
		ip0 = new JTextField("192");
		ip1 = new JTextField("168");
		ip2 = new JTextField("1");
		ip3 = new JTextField("98");
		
		initMenu();
		
	}
	
	// Menü erstellen
	public void initMenu(){
		// Zeichne Menüelemente
		// Lege Standartpositionen für Buttons fest
		buttonPosX = windowSizeX/2-100;
		buttonPosY = windowSizeY/2-40;
		// bestimme Position und Größe
		b1.setBounds(buttonPosX,buttonPosY,200,30);
		b4.setBounds(buttonPosX,buttonPosY+40,200,30);
		b3.setBounds(buttonPosX,buttonPosY+80,200,30);
		b2.setBounds(buttonPosX,buttonPosY+120,200,30);
		
		netStartServer.setBounds(buttonPosX,buttonPosY+40,200,30);
		netConnect.setBounds(buttonPosX,buttonPosY+120,200,30);
		back.setBounds(buttonPosX,buttonPosY,200,30);
		
		ip0.setBounds(buttonPosX,buttonPosY+160,40,30);
		ip1.setBounds(buttonPosX+50,buttonPosY+160,40,30);
		ip2.setBounds(buttonPosX+100,buttonPosY+160,40,30);
		ip3.setBounds(buttonPosX+150,buttonPosY+160,40,30);
		// benenne Aktionen
		b1.setActionCommand("start");
		b2.setActionCommand("end");
		b3.setActionCommand("level");
		b4.setActionCommand("multi");
		
		netStartServer.setActionCommand("startServer");
		netConnect.setActionCommand("connectToServer");
		back.setActionCommand("back");
		
		// ActionListener hinzufügen
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		
		netStartServer.addActionListener(this);
		netConnect.addActionListener(this);
		back.addActionListener(this);
		
		netStartServer.setVisible(false);
		netConnect.setVisible(false);
		back.setVisible(false);
		ip0.setVisible(false);
		ip1.setVisible(false);
		ip2.setVisible(false);
		ip3.setVisible(false);
		
		// füge Buttons zum Panel hinzu
		add(b1);
		add(b2);
		add(b3);
		add(b4);
		add(netStartServer);
		add(netConnect);
		add(back);
		add(ip0);
		add(ip1);
		add(ip2);
		add(ip3);
		
	}
	
	// Rückkehr zum Menü und Anzeige von Spielergebnis (Game Over oder You Win)
	public void paintMessage(String msg, Graphics g){
		if(!inNetworkMenu){
			// Im Menü - Mache Buttons wieder sichtbar
			b1.setVisible(true);
			b2.setVisible(true);
			b3.setVisible(true);
			b4.setVisible(true);
			netStartServer.setVisible(false);
			netConnect.setVisible(false);
			back.setVisible(false);
			ip0.setVisible(false);
			ip1.setVisible(false);
			ip2.setVisible(false);
			ip3.setVisible(false);
		}
		else if(isServer||isClient){
			// Netzwerkspiel eingeleitet
			b1.setVisible(false);
			b2.setVisible(false);
			b3.setVisible(false);
			b4.setVisible(false);
			netStartServer.setVisible(false);
			netConnect.setVisible(false);
			back.setVisible(false);
			ip0.setVisible(false);
			ip1.setVisible(false);
			ip2.setVisible(false);
			ip3.setVisible(false);
		}
		else {
			// Im Netzwerkmenü - mache Netzwerkbuttons sichtbar
			b1.setVisible(false);
			b2.setVisible(false);
			b3.setVisible(false);
			b4.setVisible(false);
			netStartServer.setVisible(true);
			netConnect.setVisible(true);
			back.setVisible(true);
			ip0.setVisible(true);
			ip1.setVisible(true);
			ip2.setVisible(true);
			ip3.setVisible(true);
		
		}
		
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
		b3.setVisible(false);
		b4.setVisible(false);
		
		endBossRoom = -1; // setze auf -1, falls kein Endgegner
		// Lade Level
		loadLevel(path);
		
		System.out.println("Endgegner: "+endBossLocation);
		System.out.println("In Raum: "+endBossRoom);
		
		// Spielablaufparameter setzen
		firstStart=false;
		ingame = true;
		won = false;
		inDialog = inShop = false;
		
		// Starte Abfrage ob Start von Checkpoint
		if(leftTry<0){
			activeCheckpointLevel = activeCheckpointX = activeCheckpointY = activeCheckpointRoom = -1;
			leftTry = startLeftTry;
			level=0;
		}
		
		if(activeCheckpointX!=-1 && activeCheckpointY!=-1 && activeCheckpointRoom!=-1 && activeCheckpointLevel == level){
			// Wenn cp aktiviert
			room = activeCheckpointRoom;
			startX = activeCheckpointX;
			startY = activeCheckpointY;
		}
		else{
			room = 0;
		}
		
		initRoom(room); // ersten Raum aufbauen
		
		player = new Player(startX, startY, startLive, 0); // setze neue Spielfigur an Stelle x,y
		playerCanGetDamage = true;
		tolleranceTime = 0;
		
		// Statusleiste vorbereiten
		prepareStatusBar();
		
		timer.start();
	}
	
	public void initNetGame(Socket client){
		// Buttons ausblenden
		netStartServer.setVisible(false);
		netConnect.setVisible(false);
		back.setVisible(false);
		ip0.setVisible(false);
		ip1.setVisible(false);
		ip2.setVisible(false);
		ip3.setVisible(false);
				
		endBossRoom = -1; // setze auf -1, falls kein Endgegner
		// Lade Lobby
		loadLevel("leveldata/lobby.txt");
				
		// Spielablaufparameter setzen
		firstStart=false;
		ingame = true;
		inNetworkGame = true;
		won = false;
		inDialog = inShop = false;
				
		initRoom(0); // ersten Raum der Lobby aufbauen
		
		if(isServer){
			player = new Player(50, windowSizeY/2, startLive, 0); // setze neue Spielfigur an Stelle x,y
			player2 = new NetPlayer(windowSizeX-70, windowSizeY/2, startLive, 1, client);
		}
		else{
			player = new Player(windowSizeX-70, windowSizeY/2, startLive, 1); // setze neue Spielfigur an Stelle x,y
			player2 = new NetPlayer(50, windowSizeY/2, startLive, 0, client);
		}
		
		try {
			//os = new DataOutputStream(client.getOutputStream());
			os = new PrintStream(client.getOutputStream(), true);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
		
		thread = new Thread(player2);
		thread.start();
				
		playerCanGetDamage = true;
		tolleranceTime = 0;
				
		// Statusleiste vorbereiten
		prepareStatusBar();
				
		timer.start();
	}
	
	// Statusleiste vorbereiten
	public void prepareStatusBar(){
		ImageIcon ii;
		
		ii = new ImageIcon(this.getClass().getResource(statusBarBackgroundPath));	// Bild - Hintergrund Statusleiste
		statusBarBackground = ii.getImage();
		
		ii = new ImageIcon(this.getClass().getResource(statusBarMoneyPath)); // Bild Geld
		statusBarMoneyImage = ii.getImage();
		
		ii = new ImageIcon(this.getClass().getResource(statusBarArmourPath)); // Bild Rüstung
		statusBarArmourImage = ii.getImage();
		
		ii = new ImageIcon(this.getClass().getResource(statusBarLivePath)); // Bild Lebenspunkte
		statusBarLiveImage = ii.getImage();
		statusBarLiveImageWidth = statusBarLiveImage.getWidth(null);
		statusBarLiveImageHeight = statusBarLiveImage.getHeight(null);
	}
	
	// Startet Netzwerkmenü
	public void initNetworkMenu(){
		
		inNetworkMenu = true;
		
	}
	
	// Starte Netzwerkspiel als Server
	public void startServer(){
		try {
			serverSocket = new ServerSocket(port);
			isServer = true; // Definiere Server
			
			System.out.println("Warte auf Verbindung...");
			client = serverSocket.accept(); // wartet bis verbunden
			
			
			System.out.println("Verbindung hergestellt!");
			
			initNetGame(client); // Netzwerkspiel starten
		} 
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	// Starte Netwerkspiel als Client
	public void startClient(){
		String IP = "";
		IP += ip0.getText()+"."+ip1.getText()+"."+ip2.getText()+"."+ip3.getText();
		System.out.println("Verbinde mit: "+IP);
		try {
			client = new Socket(IP, port);
			
			if(client.isConnected()){
				System.out.println("Verbindung erfolgreich!");
				
				isClient = true; // Definiere Client
				initNetGame(client); // Netzwerkspiel starten
			}
			
		} 
		catch(ConnectException e){
			System.out.println("Verbindung fehlgeschlagen!");
		}
		catch (UnknownHostException e) {
			
			System.out.println("Unbekannte IP! Verbindung fehlgeschlagen!");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	// Lädt Level aus Textdatei
	public void loadLevel(String path){
		boolean isReadingLevel = false;
		boolean isReadingIntro = false;
		int rooms = 0;
		String line = null;
		String request = null;
		String data1, data2;
		int z1, z2, r, count, count2;
		
		try {
			
			System.out.println("Lade Datei: "+path);
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
					if(tokens.hasMoreTokens()){
						endBossLocation = tokens.nextToken()+" ";
						for(int i = 0; i<3; i++) endBossLocation += tokens.nextToken() +" ";
						// Raum vom Endgegner einlesen
						int r10 = endBossLocation.charAt(3)-48;
						int r01 = endBossLocation.charAt(4)-48;
						endBossRoom = 10*r10+r01;
					}
					else{
						endBossLocation = "";
						endBossRoom = -1; // kein Endgegner
					}
					
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
					doordata = new String[rooms];
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
				
				// Türen einlesen
				if(request.equals("#DOOR")){
					if(tokens.hasMoreTokens()) doordata[r]=tokens.nextToken()+" "; // hole ersten
					else doordata[r]="";
					while(tokens.hasMoreTokens()) doordata[r]+=tokens.nextToken()+" "; // hole Rest
					
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
		Item item;
		Door door;
		NPC npc;
		
		char element;
		char type;
		
		doors.clear();
		walls.clear();
		npcs.clear();
		grounds.clear();
		enemys.clear();
		items.clear();
		missiles.clear();
		missiles2.clear();
		eMissiles.clear();
		checkpoints.clear();
		finalEnemy = null;
		
		// Endgegner, falls vorhanden, auslesen
		if(roomnumber == endBossRoom){
			element = endBossLocation.charAt(0);
			type = endBossLocation.charAt(1);
			
			// Koordinaten der Gegner aus String filtern und in Pixel umwandeln
			int x10 = endBossLocation.charAt(6)-48;
			int x01 = endBossLocation.charAt(7)-48;
			int fEnemyX = (10*x10 + x01)*blockSize;
						
			int y10 = endBossLocation.charAt(9)-48;
			int y01 = endBossLocation.charAt(10)-48;
			int fEnemyY = (10*y10 + y01)*blockSize;
			
			finalEnemy = new FinalEnemy(fEnemyX, fEnemyY, type);
		}
		
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
		
		// Türen auslesen aus doordata	
		for(int d=0;d<doordata[roomnumber].length(); d+=11){
			element = doordata[roomnumber].charAt(d);
			type = doordata[roomnumber].charAt(d+1);
				
			// Position der Tür aus String filtern und in Pixel umwandeln
			int x10 = doordata[roomnumber].charAt(d+3)-48;
			int x01 = doordata[roomnumber].charAt(d+4)-48;
			int doorX = (10*x10 + x01)*blockSize;
						
			int y10 = doordata[roomnumber].charAt(d+6)-48;
			int y01 = doordata[roomnumber].charAt(d+7)-48;
			int doorY = (10*y10 + y01)*blockSize;
			
			// Zustand Tür ermitteln und Tür erstellen
			char zc = doordata[roomnumber].charAt(d+9);
			if(zc == 'c'){
				door = new Door(doorX, doorY, type, false);
			}
			else{
				door = new Door(doorX, doorY, type, true);
			}
			doors.add(door);
			
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
		
		// Items auslesen aus Itemdata
		for(int k=0;k<itemdata[roomnumber].length(); k+=11){
			// Itemtyp
			element = itemdata[roomnumber].charAt(k);
			type = itemdata[roomnumber].charAt(k+1);
			
			// Koordinaten in px auslesen
			int x100 = itemdata[roomnumber].charAt(k+3)-48;
			int x010 = itemdata[roomnumber].charAt(k+4)-48;
			int x001 = itemdata[roomnumber].charAt(k+5)-48;
			int itemX = 100*x100+10*x010+x001;

			int y100 = itemdata[roomnumber].charAt(k+7)-48;
			int y010 = itemdata[roomnumber].charAt(k+8)-48;
			int y001 = itemdata[roomnumber].charAt(k+9)-48;
			int itemY = 100*y100+10*y010+y001;
			
			// Itemtyp ermitteln (I -> 0 -> Standartitems, J ->1 -> Waffen)
			if(element == 'J') type += 10;
			
			// Item erstellen und in Liste einfügen
			item = new Item(itemX, itemY, type);
			items.add(item);
			
		}		
		
		// Interaktive Elemente auslesen - Checkpoints, Shop, NPC
		for(int l = 0; l<interactdata[roomnumber].length(); l+=11){
			// Typ
			element = interactdata[roomnumber].charAt(l);
			type = interactdata[roomnumber].charAt(l+1);
						
			// Koordinaten in px auslesen
			int x100 = interactdata[roomnumber].charAt(l+3)-48;
			int x010 = interactdata[roomnumber].charAt(l+4)-48;
			int x001 = interactdata[roomnumber].charAt(l+5)-48;
			int interX = 100*x100+10*x010+x001;

			int y100 = interactdata[roomnumber].charAt(l+7)-48;
			int y010 = interactdata[roomnumber].charAt(l+8)-48;
			int y001 = interactdata[roomnumber].charAt(l+9)-48;
			int interY = 100*y100+10*y010+y001;
						
			// Element erstellen und in Liste einfügen
			if(element == 'C'){
				// Checkpoint
				Checkpoint cp;
				// prüfe ob CP schon aktiv
				if(activeCheckpointRoom == roomnumber && activeCheckpointX == interX && activeCheckpointY == interY && activeCheckpointLevel == level){
					cp = new Checkpoint(interX, interY, type+1);
				}
				else{
					cp = new Checkpoint(interX, interY, type);
				}
				checkpoints.add(cp);
			}
			else if(element == 'N'){
				// NPC 
				npc = new NPC(interX,interY,type);
				npcs.add(npc);
			}
			else if(element == 'S'){
				// Shop 
				type+=10;
				npc = new NPC(interX,interY,type);
				npcs.add(npc);
			}
		}
				
		
	}
	
	// Raum zeichnen
	public void buildRoom(Graphics g){
		ArrayList<GameElement> room = new ArrayList<GameElement>();
		// Raumelemente einfügen
		room.addAll(walls);
		room.addAll(grounds);
		room.addAll(doors);
		room.addAll(checkpoints);
		// Items einfügen - wichtig: nach Raum
		room.addAll(items);
		room.addAll(npcs);
		// Gegner einfügen - wichtig: Erst Raum und Items, dann andere Objekte
		room.addAll(enemys);
		
		missiles=player.getMissiles();
		if(inNetworkGame) missiles2=player2.getMissiles();
		
		if(finalEnemy!=null){
			room.add(finalEnemy);
			eMissiles=finalEnemy.getMissiles();
		}
		room.addAll(missiles);
		room.addAll(missiles2);
		room.addAll(eMissiles);
		
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
			level++; // Erhöhe Level um 1
			System.out.println("Gehe zu Level "+level);
			won = true;
			ingame = false;
			timer.stop();
		}
	}
	
	// Kollision prüfen
	public void checkCollision(){
		// TODO Effizienter gestalten
		Rectangle r_enemy;
		Rectangle r_wall;
		Rectangle r_door;
		Rectangle r_item;
		Rectangle r_missile;
		Rectangle r_checkpoint;
		Rectangle r_finalEnemy;
		Rectangle r_npc;
		Rectangle r_player2;
		
		// Spieler Ausmaße ermitteln
		Rectangle r_player = player.getBounds();
		if(inNetworkGame) r_player2 = player2.getBounds();
		else r_player2 = new Rectangle(-10,-10,1,1);
		
		// Kollision mit npc
		if(!inNetworkGame){
			for(int p = 0; p<npcs.size(); p++){
				NPC n = (NPC)npcs.get(p);
				r_npc = n.getBounds();
				
				if(r_npc.intersects(r_player)){
					player.resetMovement();
					if(n.isShop()){
						// Shop
						enterShop();
					}
					else if(n.hasDialog()){
						// NPC+Dialog
						enterDialog(dialog);
					}
				}
			}
		}
		
		// Kollision mit Tür
		for(int a=0; a<doors.size(); a++){
			Door dr = (Door)doors.get(a);
			r_door = dr.getBounds();
			boolean open = dr.getOpen();
			
			if(open==false){
				// Player - Tür
				if(r_player.intersects(r_door)) player.resetMovement();
				 
				// Gegner - Tür
				for(int b = 0; b<enemys.size(); b++){
					Enemy en = (Enemy)enemys.get(b);
					r_enemy = en.getBounds();
					if(r_enemy.intersects(r_door)){
						en.resetMovement();
						en.setDirectionOfMovement(1);
					}
				}
				
				// Missile - Tür
				for(int c = 0; c<missiles.size(); c++){
					Missile ms = (Missile)missiles.get(c);
					r_missile = ms.getBounds();
					if(r_missile.intersects(r_door)) ms.setVisible(false);
				}
				if(inNetworkGame){
					for(int c = 0; c<missiles2.size(); c++){
						Missile ms = (Missile)missiles2.get(c);
						r_missile = ms.getBounds();
						if(r_missile.intersects(r_door)) ms.setVisible(false);
					}
				}
				
				// Endboss - Tür
				if(room==endBossRoom && finalEnemy!=null){
					r_finalEnemy = finalEnemy.getBounds();
					if(r_finalEnemy.intersects(r_door)){
						finalEnemy.resetMovement();
						finalEnemy.setDirectionOfMovement(2);
					}
				}
			}
			
		}
		
		// eventuelle Kollision im Netzwerkspiel
		if(inNetworkGame){
			for(int h=0; h<missiles2.size(); h++){
				Missile ms = (Missile)missiles2.get(h);
				r_missile = ms.getBounds();
				if(!ms.getFriendly() && ms.isVisible()){
					if(r_missile.intersects(r_player)){
						// Missile trifft Player
						player.setLive(-ms.getDamage());
						ms.setVisible(false);
						playerCanGetDamage = false;
						tolleranceTime = tollerance; // Tolleranzwert
					}
				}
			}
			for(int h=0; h<missiles.size(); h++){
				Missile ms = (Missile)missiles.get(h);
				r_missile = ms.getBounds();
				if(ms.getFriendly() && ms.isVisible()){
					if(r_missile.intersects(r_player2)){
						// Missile trifft Player2
						ms.setVisible(false);
					}
				}
			}
			
		}
		
		// eventuelle Kollision mit Endgegner
		if(room==endBossRoom && finalEnemy!=null){
			r_finalEnemy = finalEnemy.getBounds();
			
			// Player mit endBoss
			if(r_player.intersects(r_finalEnemy)){
				// Stehenbleiben
				player.resetMovement();
				finalEnemy.resetMovement();
				finalEnemy.setDirectionOfMovement(2);
				// Leben reduzieren
				if(playerCanGetDamage && player.isImmortal()==false){
					player.setLive(-finalEnemy.getDamage()); // Hier Schadensfunktion aufrufen
					playerCanGetDamage = false;
					tolleranceTime = tollerance; // Tolleranzwert
				}
			}
			
			// Missile mit endBoss
			for(int h=0; h<missiles.size(); h++){
				Missile ms = (Missile)missiles.get(h);
				r_missile = ms.getBounds();
				if(ms.getFriendly() && ms.isVisible()){
					if(r_missile.intersects(r_finalEnemy)){
						// Missile trifft finalEnemy
						finalEnemy.setLive(-ms.getDamage());
						ms.setVisible(false);
					}
				}
			}
			for(int h=0; h<eMissiles.size();h++){
				Missile ms = (Missile)eMissiles.get(h);
				r_missile = ms.getBounds();
				if(r_missile.intersects(r_player)&&ms.isVisible()){
					// Missile trifft Player
					player.setLive(-ms.getDamage());
					ms.setVisible(false);
					playerCanGetDamage = false;
					tolleranceTime = tollerance; // Tolleranzwert
				}
				
			}
		}
		
		// Kollisionen mit Wand -> stehenbleiben || Missile remove || wenn Endboss da, richtung wechseln
		for(int i=0;i<walls.size(); i++){
			Wall w = (Wall)walls.get(i);
			r_wall = w.getBounds();
			
			// Kollision mit Endgegner
			if(room==endBossRoom && finalEnemy!=null){
				r_finalEnemy = finalEnemy.getBounds();
				if(r_finalEnemy.intersects(r_wall)){
					finalEnemy.resetMovement();
					finalEnemy.setDirectionOfMovement(2);
				}
			}
			
			// Kollision mit player
			if(r_player.intersects(r_wall)){
				player.resetMovement();
			}
			
			// Kollision mit Missile
			for(int n = 0; n<missiles.size(); n++){
				Missile ms = (Missile)missiles.get(n);
				r_missile = ms.getReducedBounds();
				if(r_wall.intersects(r_missile)) ms.setVisible(false);
			}
			if(inNetworkGame){
				for(int n = 0; n<missiles2.size(); n++){
					Missile ms = (Missile)missiles2.get(n);
					r_missile = ms.getReducedBounds();
					if(r_wall.intersects(r_missile)) ms.setVisible(false);
				}
			}
		}
		
		// Kollisionen mit Gegner -> Lebenspunkte weg || Missile remove
		if(!inNetworkGame){
			for(int j=0;j<enemys.size(); j++){
				Enemy e = (Enemy)enemys.get(j);
				r_enemy = e.getBounds();
				
				if(r_player.intersects(r_enemy)){
					// Leben reduzieren
					if(playerCanGetDamage && player.isImmortal()==false){
						player.setLive(-e.getDamage()); // Hier Schadensfunktion aufrufen
						playerCanGetDamage = false;
						tolleranceTime = tollerance; // Tolleranzwert
					}
					// Gegner dreht um
					e.resetMovement();
					if(player.getDir()!=e.getDirectionOfMovement()) e.setDirectionOfMovement(2); 
					// Spieler bleibt stehen
					player.resetMovement();
				}
				
				// Kollision mit Missile
				for(int n = 0; n<missiles.size(); n++){
					Missile ms = (Missile)missiles.get(n);
					r_missile = ms.getBounds();
					if(r_enemy.intersects(r_missile)){
						ms.setVisible(false);
						e.setVisible(false); // Hier später Schaden verteilen
					}
				}
			}
		}
		
		// Kollisionen Gegner mit Wand oder Spieler
		if(!inNetworkGame){
			for(int k = 0; k<enemys.size(); k++){
				// Hole Gegner
				Enemy e = (Enemy)enemys.get(k);
				r_enemy = e.getBounds();
				
				for(int l = 0; l<walls.size(); l++){
					// Hole Wand
					Wall w = (Wall)walls.get(l);
					r_wall = w.getBounds();
					if(r_enemy.intersects(r_wall)){
						e.resetMovement();
						e.setDirectionOfMovement(1);
					}
				}
				
				// verhindern dass Gegner Raum verlässt --> umdrehen wenn Bildschirm verlassen
				if(e.getX()<0 || e.getX()>windowSizeX-e.getImage().getWidth(null) || e.getY()<0 || e.getY()>windowSizeY-e.getImage().getHeight(null)) e.setDirectionOfMovement(2);
				
			}
		}
		
		// Kollisionen Spieler mit Checkpoint
		if(!inNetworkGame){
			for(int o = 0; o<checkpoints.size(); o++){
				Checkpoint cp = (Checkpoint)checkpoints.get(o);
				r_checkpoint = cp.getBounds();
				boolean active = cp.getActive();
				if(active == false){
					if(r_player.intersects(r_checkpoint)){
						cp.setActive(true);
						activeCheckpointX = cp.getX();
						activeCheckpointY = cp.getY();
						activeCheckpointRoom = this.room;
						activeCheckpointLevel = this.level;
						System.out.println("Checkpoint aktiviert in Raum "+activeCheckpointRoom+" mit Koordinaten "+activeCheckpointX + " "+ activeCheckpointY);
					}
				}
			}
		}
		
		// Kollisionen Spieler mit Item
		for(int m=0;m<items.size(); m++){
			Item it = (Item)items.get(m);
			r_item = it.getBounds();
			
			if(r_player.intersects(r_item)){
				// prüfe ob Item auswählbar
				if(it.isVisible()){
					
					if(it.getItemType().equals("money")){
						// Geld zu Spieler hinzufügen
						player.setMoney(it.getAmount());
					}
					else if(it.getItemType().equals("mana")){
						// Mana zu Spieler hinzufügen
						player.resetMana();
					}
					else if(it.getItemType().equals("health")){
						// Gesundheit herstellen
						player.setLive(3); 
					}
					else{
						// Waffen und nutzbare Items zu Spieler hinzufügen
						player.addItem(it);
						System.out.println(it.getItemType() + " added to Player.");
					}
					// item zum Entfernen vorbereiten
					it.setVisible(false);
				}
			}
			if(inNetworkGame){
				if(r_player2.intersects(r_item)){
					if(it.getItemType().equals("mana")){
						// Mana zu Spieler hinzufügen
						player2.resetMana();
					}
					else if(it.getItemType().equals("health")){
						// Gesundheit herstellen
						player2.setLive(3); 
					}
					else{
						// Waffen und nutzbare Items zu Spieler hinzufügen
						player2.addItem(it);
						System.out.println(it.getItemType() + " added to Player2.");
					}
					it.setVisible(false);
				}
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
			
			// zeichne ggf Netzwerkspieler
			if(inNetworkGame) g.drawImage(player2.getImage(), player2.getX(), player2.getY(), this);
			
			// zeichne Statusleiste
			paintStatusBar(g);
			
			// Prüfe ob in Shop und zeichne
			if(inShop) paintShop(g);
			if(inDialog) paintDialog(dialog, g);
		}
		else{
			// Nachricht (Game Over/You Win) und Rückkehr ins Menü vorbereiten 
			String msg;
			
			if(won){
				msg = "Level "+level+" abgeschlossen.";
			}
			else if(firstStart) msg = " ";	// Für ersten Start leere Nachricht
			else if(leftTry<0) msg = "GAME OVER";
			else msg = "Verloren! Noch "+(leftTry+1)+" Versuche über.";
			
			paintMessage(msg,g);
		}
		
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	public void paintDialog(String dialog, Graphics g){
		// Nachricht analysieren
		Font dia = new Font("Arial", Font.BOLD, 15);
		FontMetrics metr = this.getFontMetrics(dia);
		
		int length = metr.stringWidth(dialog);
		
		int dialogY = windowSizeY/2;
		int dialogSizeY = 100;
		int dialogX =  windowSizeX/2-250;
		int dialogSizeX = 500;
		
		// Zeichnen
		g.setColor(Color.BLACK);
		g.fillRoundRect(dialogX, dialogY, dialogSizeX , dialogSizeY, 30, 30); // Kasten für Dialog
		g.setColor(Color.WHITE);
		g.drawString(dialog, dialogX+20, dialogY+20); // Zeichne String
		
		
	}
	
	public void paintShop(Graphics g){
		int shopSizeX = windowSizeX-2*shopX;
		int shopSizeY = windowSizeY-2*shopY;
		g.setColor(Color.BLACK);
		g.fillRoundRect(shopX, shopY, shopSizeX , shopSizeY, 30, 30); // Kasten für Shop
		
		// Willkommenstext 
		g.setColor(Color.WHITE);
		
		Font shop = new Font("Arial", Font.BOLD, 15);
		FontMetrics metr = this.getFontMetrics(shop);
		
		String welcome = "Hallo, suche dir ein Item aus.";
		int msgPos = shopX+shopSizeX/2-metr.stringWidth(welcome)/2;
		g.drawString(welcome, msgPos, shopY+20); // Zeichne String
		
		Font price = new Font("Arial", Font.ITALIC, 10);
		String itemPrice;
		// Shopelemente erstellen und zeichnen
		shopItems.clear();
		Item it = null;
		for(int i = 0; i<shopList.length; i++){
			if(shopList[i].equals("mana")) it = new Item(0,0,2+48); 
			else if(shopList[i].equals("health")) it = new Item(0,0,3+48);
			else if(shopList[i].equals("plasmagun")) it = new Item(0,0,11+48); 
			
			if(it!=null){
				 shopItems.add(it);
				 Image itemI = it.getImage();
				 g.drawImage(itemI,shopX+170+i*(this.itemBoxSize+30), shopY + 60, this);
				 
				 itemPrice = "" + shopPrice[i];
				 
				 g.drawString(itemPrice,shopX+170+i*(this.itemBoxSize+30), shopY+90+itemBoxSize); // Zeichne Preis
				 
				 if(i==chosenItem){
					 g.drawRoundRect(shopX+170+i*(this.itemBoxSize+30)-10, shopY + 60-10, itemBoxSize+20, itemBoxSize+20, 10, 10);
				 }
			}
		}
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
		
		// Lebenspunkte ermitteln + Darstellen
		int a = player.getArmour();
		for(int i = 0; i<a; i++){
			g.drawImage(statusBarArmourImage, statusBarX+i*(statusBarLiveImageWidth+statusBarSpace), statusBarY+statusBarLiveImage.getHeight(null), this);
		}
		
		// Geld ermitteln und darstellen
		int m = player.getMoney();
		
		String mon = "x ";	// Anzahl Geld in String umwandeln
		if(m<10) mon+="00"+m;
		else if(m<100) mon+="0"+m;
		else mon+=m;
		
		g.drawImage(statusBarMoneyImage, statusBarX+statusBarLiveContainer, statusBarY+2, this); // Bild Geld zeichnen
		
		Font small = new Font("Arial", Font.ITALIC, 12);
		FontMetrics metr = this.getFontMetrics(small);
		
		int barX = statusBarX+statusBarLiveContainer+statusBarMoneyImage.getWidth(null)+4; // momentane X Position auf Leiste
		
		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(mon, barX, statusBarY + statusBarMoneyImage.getWidth(null)/2+4); // Zeichne String
		
		barX += metr.stringWidth(mon) + 25;
		
		// Item darstellen
		g.drawRoundRect(barX, statusBarY+2, itemBoxSize, itemBoxSize, 5, 5); // Kasten für Item
		g.fillRoundRect(barX, statusBarY+2, itemBoxSize, itemBoxSize, 5, 5);
		barX+=3;
		
		playerActiveItem = player.getActiveItem();
		if(playerActiveItem != null){
			// Item Bild und Anzahl Munition holen
			activeItemImage = playerActiveItem.getImage();
			ammunition = playerActiveItem.getAmount();
			
			g.drawImage(activeItemImage,barX, statusBarY+3, this); // Bild zeichnen
			
			String amm = "x ";
			if(ammunition <10) amm += "0";
			amm+=ammunition;
			
			if(playerActiveItem.hasMissiles()) g.drawString(amm, barX+itemBoxSize+2, statusBarY + itemBoxSize-5); // Zeichne String - Anzahl Munition
			
		}
		
		// Anzahl versuche 
		String tr = "Life left: "+leftTry;
		g.drawString(tr, barX+4*itemBoxSize, statusBarY + itemBoxSize-5);
		
		// Mana ermitteln und darstellen
		int mana = player.getMana();
		g.setColor(Color.GREEN);
		g.drawRoundRect(windowSizeX-statusBarLiveContainer/2 - statusBarX*2, statusBarY+2, statusBarLiveContainer/2, manaBoxHeight, 5, 5);
		g.fillRoundRect(windowSizeX-statusBarLiveContainer/2 - statusBarX*2, statusBarY+2, mana*statusBarLiveContainer/2000, manaBoxHeight, 5, 5);
		
	}
	
	public void enterShop(){
		chosenItem = 0;
		inShop = true;
		player.hasEnteredShopOrDialog();
	}
	
	public void enterDialog(String dialog){
		inDialog = true;
		player.hasEnteredShopOrDialog();
	}
	
	// Aktion -> Timer oder Button 
	public void actionPerformed(ActionEvent e){
		int x;
		String xOut="";
		int y;
		String yOut="";
		if(ingame){
			// im Spiel - wird vom Timer abhängig aufgerufen
			
			if(inShop||inDialog){
				// Spiel "anhalten"
			}
			else{
				// prüfe ob Item sichtbar sonst entfernen
				for(int i = 0; i<items.size(); i++){
					Item it = (Item)items.get(i);
					if(it.isVisible()==false) items.remove(i);
				}
				
				if(player.getLive() <= 0){ // prüfe ob Spieler noch im Spiel oder Lebenspunkte 0
					// Spieler stirbt, Spiel beenden
					ingame = false;
					leftTry--;
					timer.stop();
				}
				else{
					// Prüfe ob Spielfigur Schaden erleiden kann
					if(tolleranceTime>0) tolleranceTime--;
					else playerCanGetDamage = true;
					
					// Mana für Unverwundbarkeit (Zauber0) abziehen
					int man = player.getMana();
					if(man > 0){
						if(player.isImmortal()){
							player.removeMana(2);
						}
					}
					else{
						if(player.isImmortal()) player.setImmortal(false);
					}
					
					// Endgegnerbehandlung
					if(finalEnemy != null){
						if(finalEnemy.getLive()<=0){
							// Gegner besiegt
							finalEnemy = null;
							
							for(int a=0; a<doors.size(); a++){
								Door dr = (Door)doors.get(a);
								dr.setOpen(true);
							}
						}
						else{
							finalEnemy.move(player.getX(), player.getY());
						}
					}
					
					player.move();	// bewege Spielfigur
					if(inNetworkGame){
						 delay++;
						if(delay >= 5){
							x = player.getX();
							y = player.getY();
							
							if(x<10) xOut = "00"+x;
							else if(x<100) xOut = "0"+x;
							else xOut = ""+x;
							
							if(y<10) yOut = "00"+y;
							else if(y<100) yOut = "0"+y;
							else yOut = ""+y;
							
							os.println(xOut + " " + yOut);
							delay = 0;
						}
						player2.move(); // bewege Netzwerkspieler wenn da
					}
					
					// Bewege Gegner
					for(int j=0;j<enemys.size(); j++){
						Enemy en = (Enemy)enemys.get(j);
						if(en.isVisible() == false) enemys.remove(j); // entfernen falls weg
						en.move();
					}
					
					// Bewege Geschosse
					for(int k=0;k<missiles.size(); k++){
						Missile m = (Missile)missiles.get(k);
						if(m.isVisible() == false) missiles.remove(k); // entfernen falls weg
						m.move();
					}
					
					for(int k=0;k<missiles2.size(); k++){
						Missile m = (Missile)missiles2.get(k);
						if(m.isVisible() == false) missiles2.remove(k); // entfernen falls weg
						m.move();
					}
					
					
					for(int k=0;k<eMissiles.size(); k++){
						Missile m = (Missile)eMissiles.get(k);
						if(m.isVisible() == false) eMissiles.remove(k); // entfernen falls weg
						m.move();
					}
					
					
					checkCollision();	// prüfe Kollision
					checkRoom();	// prüfe ob Raum gewechselt oder Ziel erreicht
				}
			}
			
		}
		else{
			// im Menü - Aufruf bei button
			String action = e.getActionCommand();
			if(action.equals("start")){
				if(level>=levelpath.length) level--; 
				initGame(levelpath[level]);	// Spiel Starten - Später: vorher aktuelles/gewähltes Level in levelpath laden!
			}
			else if(action.equals("level")){
				new Leveleditor();
			}
			else if(action.equals("multi")){
				initNetworkMenu();
			}
			else if(action.equals("end")) System.exit(0);	// Programm beenden
			else if(action.equals("back")){
				inNetworkMenu = false;
			}
			else if(action.equals("startServer")) startServer();
			else if(action.equals("connectToServer")) startClient();
		}

		repaint();	// zeichne Bildschirm neu - erneuter Aufruf von paint()
		
	}
	
	
	// Steuerung - Weitergabe der Keycodes nach Unten
	private class TAdapter extends KeyAdapter{
		
		public void keyReleased(KeyEvent e){
			if(ingame && inNetworkGame){
				String key = "R "+e.getKeyCode();
				
				os.println(key);
			}
			if(ingame && inShop==false && inDialog==false) player.keyReleased(e);
			
		}
		public void keyPressed(KeyEvent e){
			if(ingame && inNetworkGame){
				String key = "P "+e.getKeyCode();
				
				os.println(key);
			}
			if(ingame && inShop==false && inDialog==false) player.keyPressed(e);
			else if(inDialog){
				if(e.getKeyCode()==KeyEvent.VK_SPACE || e.getKeyCode()==KeyEvent.VK_ENTER) inDialog=false;
			}
			else if(inShop){
				if(e.getKeyCode()==KeyEvent.VK_RIGHT){
					if(chosenItem < shopList.length-1) chosenItem++;
				}
				else if(e.getKeyCode()==KeyEvent.VK_LEFT){
					if(chosenItem > 0) chosenItem--;
				}
				else if(e.getKeyCode()==KeyEvent.VK_ENTER||e.getKeyCode()==KeyEvent.VK_SPACE){
					Item it = shopItems.get(chosenItem);
					
					// Prüfe ob genug Geld
					if(player.getMoney()>=shopPrice[chosenItem]){
						player.setMoney(-shopPrice[chosenItem]);
						if(it.getItemType().equals("mana")){
							// Mana zu Spieler hinzufügen
							player.resetMana();
						}
						else if(it.getItemType().equals("health")){
							// Gesundheit herstellen
							player.setLive(3); 
						}
						else{
							// Waffen und nutzbare Items zu Spieler hinzufügen
							player.addItem(it);
							System.out.println(it.getItemType() + " added to Player.");
						}
					}
					
					inShop=false;
				}
			}
		}
	}
}
