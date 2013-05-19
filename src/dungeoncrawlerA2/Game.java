package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

// Game: Paint (Spielfeld), initiate, detect collisions, gameloop
/*
	Weitere Klassen: Wall, Opponent
	in Game: starten, zeichnen, kollisionen erkennen
 */

// Game: Spielfeld und Spielsteuerung
public class Game extends JPanel implements ActionListener{
	
	private Timer timer;	// Timer für ActionEvent
	private boolean ingame; // Wert für Gameloop
	private int room; // aktueller Raum des Sungeons
	
	private Player player;	// Spielfigur
	
	private ArrayList walls = new ArrayList();
	private ArrayList opponents = new ArrayList();
	private ArrayList grounds = new ArrayList();
	
	private Color backgroundColor = Color.WHITE; // Hintergrundfarbe
	private int startX = 50;	// Startwert Spielfigur - Später aus leveldata nehmen
	private int startY = 50;
	
	private int fps = 100;	// Bildwiedrholrate - (evtl später runtersetzen?)
	
	private int windowSizeX = 800;	
	private int windowSizeY = 600;
	private int widthInBlocks = 20; // Angepasst auf 800x600 Bild, 40x40px Objekte
	private int heightInBlocks = 15;
	private int blockSize = 40;
	
	// leveldata, enemydata, exitdata später füllen durch loadLevel(filename)
	// leveldata, enemydata, exitdata vor Verwendung überprüfen - checkLeveldata()
	
	// Aufbau pro Zeile: "AnzahlGegner : GegnerTyp(E/T,Nr) - posX posY : ..."
	private String [] enemydata = {
			"0 " +
			"2 : E1 - 400 400 : E1 - 200 400" +
			"1 : E1 - 20 380"};
	
	// Exits von Raum [] zu Raum [] - exitdata[r][h] - r=Raumnummer, h=0,1,2,3 (Norden,Osten,Süden,Westen), Inhalt: Zielraum oder -1(kein Exit)
	private int[][] exitdata = new int[][]{{1,-1,-1,-1},{-1,2,0,-1},{-1,-1,-1,1}};
	
	// W1: Wall Type 1
	// G1: Ground Type 1
 	private String [] leveldata = {
			"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 G1 G1 W1 W1 " +
			"W1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 W1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 W1 G1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 " +
			"W1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 G1 W1 W1 W1 W1 " +
			"W1 G1 W1 W1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 W1 G1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 W1 G1 G1 W1 W1 W1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 W1 G1 G1 G1 G1 W1 W1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 W1 G1 G1 G1 G1 G1 W1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 W1 W1 W1 W1 G1 G1 W1 G1 W1 W1 W1 W1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 ",
			
			"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 W1 W1 W1 G1 G1 G1 G1 G1 G1 W1 W1 W1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 W1 G1 W1 G1 G1 G1 G1 G1 G1 W1 G1 W1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 W1 W1 W1 G1 G1 G1 G1 G1 G1 W1 W1 W1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 W1 W1 W1 W1 W1 W1 W1 W1 G1 G1 G1 G1 G1 G1 " +
			"W1 G1 G1 G1 G1 G1 G1 W1 W1 W1 W1 W1 W1 G1 G1 G1 G1 G1 G1 G1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 W1 W1 W1 W1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 G1 G1 W1 W1 ",
			
			"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 " +
			"W1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 W1 G1 G1 G1 G1 " +
			"W1 G1 G1 G1 G1 W1 G1 G1 G1 G1 W1 W1 W1 W1 W1 W1 W1 G1 G1 G1 " +
			"W1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 W1 W1 G1 G1 G1 G1 " +
			"W1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 W1 W1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 " +
			"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 "
	};
	
	// Konstruktor
	public Game(){
		
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(backgroundColor);
		setDoubleBuffered(true); 
		
		// loadLevel()
		
		ingame = true;
		
		room = 0;
		initRoom(room); // ersten Raum aufbauen
		
		player = new Player(startX, startY); // setze neue Spielfigur an Stelle x,y
		
		timer = new Timer(1000/fps, this); // Timer nach fps einstellen
		timer.start();
	}
	
	// Raum erstellen
	public void initRoom(int roomnumber){
		int x = 0;
		int y = 0;
		
		Wall wall;
		Ground ground;
		
		walls.clear();
		grounds.clear();
		
		// Auslesen aus leveldata
		for(int i=0; i<leveldata[roomnumber].length(); i+=3){
			
			// Element und Typ(Version) aus String lesen
			char element = leveldata[roomnumber].charAt(i);
			char type = leveldata[roomnumber].charAt(i+1);
			
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
		
	}
	
	// Raum zeichnen
	public void buildRoom(Graphics g){
		ArrayList room = new ArrayList();
		room.addAll(walls);
		room.addAll(grounds);
		
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
		int newX = x; // Position des Spielers nach Raumwechsel
		int newY = y;
		int nextRoom = -1;
		
		if(y<0){
			// Norden
			nextRoom = exitdata[room][0];
			newY = windowSizeY;
		}
		else if(x>windowSizeX){
			// Osten
			nextRoom = exitdata[room][1];
			newX = 0;
		}
		else if(y>windowSizeY){
			// Süden
			nextRoom = exitdata[room][2];
			newY = 0;
		}
		else if(x<0){
			// Westen
			nextRoom = exitdata[room][3];
			newX = windowSizeX;
		}
		
		if(nextRoom != -1){
			this.room = nextRoom;
			initRoom(room);
			player.setX(newX);
			player.setY(newY);
		}
	}
	
	public void checkCollision(){
		Rectangle r_player = player.getBounds();
		
		for(int i=0;i<walls.size(); i++){
			Wall w = (Wall)walls.get(i);
			Rectangle r_wall = w.getBounds();
			
			if(r_player.intersects(r_wall)){
				player.resetMovement();
			}
		}
	}
	
	// Paint Methode - zeichnet Bildschirm
	public void paint(Graphics g){
		super.paint(g);
		
		/*
		 * Später hier Gameloop einfügen und Zeichnen von ingame abhängig machen
		 * */
		
		// zeichne Raum
		buildRoom(g);
		
		// zeichne Spielfigur
		g.drawImage(player.getImage(), player.getX(), player.getY(), this);
		
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	// actionPerformed - wird vom Timer abhängig aufgerufen
	public void actionPerformed(ActionEvent e){
		
		player.move();	// bewege Spielfigur
		checkCollision();
		checkRoom();
		
		repaint();	// zeichne Bildschirm neu - erneuter Aufruf von paint()
	}
	
	// Steuerung - Weitergabe der Keycodes nach Unten
	private class TAdapter extends KeyAdapter{
		
		public void keyReleased(KeyEvent e){
			player.keyReleased(e);
		}
		public void keyPressed(KeyEvent e){
			player.keyPressed(e);
		}
	}
}

