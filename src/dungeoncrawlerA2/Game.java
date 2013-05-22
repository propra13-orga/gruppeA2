package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
	private boolean won; // für Abfrage ob gewonnen
	private int room; // aktueller Raum des Sungeons
	
	private Player player;	// Spielfigur
	
	private ArrayList walls = new ArrayList();
	private ArrayList opponents = new ArrayList();
	private ArrayList grounds = new ArrayList();
	private ArrayList enemys = new ArrayList();
	
	private Color backgroundColor = Color.BLACK; // Hintergrundfarbe
	private int startX = 50;	// Startwert Spielfigur - Später aus leveldata nehmen
	private int startY = 50;
	
	private int fps = 100;	// Bildwiedrholrate - (evtl später runtersetzen?)
	
	private int windowSizeX = 800;	
	private int windowSizeY = 600; // Angepasst auf 800x600 Bild, 40x40px Objekte
	private int blockSize = 40;
	
	// leveldata, enemydata, exitdata später füllen durch loadLevel(filename)
	// leveldata, enemydata, exitdata vor Verwendung überprüfen - checkLeveldata()
	
	// Aufbau pro Zeile: "GegnerTyp(E/T,Nr) posX posY  ..."
	private String [] enemydata = {
			"",
			"E1 03 12 E1 15 11 E1 17 07 ",
			"E1 03 12 E1 03 07 E1 17 08 "};
	
	// Exits von Raum [] zu Raum [] - exitdata[r][h] - r=Raumnummer, h=0,1,2,3 (Norden,Osten,Süden,Westen), Inhalt: Zielraum oder -1(kein Exit), -2 SpielEnde
	private int[][] exitdata = new int[][]{{1,-1,-1,-1},{-1,2,0,-1},{-1,-2,-1,1}};
	
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
		
		timer = new Timer(1000/fps, this); // Timer nach fps einstellen
		
		// Starte Spiel
		initGame();
		
	}
	
	public void initGame(){
		ingame = true;
		won = false;
		
		room = 0;
		initRoom(room); // ersten Raum aufbauen
		
		player = new Player(startX, startY); // setze neue Spielfigur an Stelle x,y
		
		timer.start();
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
		ArrayList room = new ArrayList();
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
		
		if(nextRoom >= 0){
			this.room = nextRoom;
			initRoom(room);
			player.setX(newX);
			player.setY(newY);
		}
		else if(nextRoom == -2){
			won = true;
			ingame = false;
		}
	}
	
	// Kollision prüfen
	public void checkCollision(){
		Rectangle r_player = player.getBounds();
		
		// Kollisionen mit Wand -> stehenbleiben
		for(int i=0;i<walls.size(); i++){
			Wall w = (Wall)walls.get(i);
			Rectangle r_wall = w.getBounds();
			
			if(r_player.intersects(r_wall)){
				player.resetMovement();
			}
		}
		
		// Kollisionen mit Gegner -> Spiel vorbei
		for(int j=0;j<enemys.size(); j++){
			Enemy e = (Enemy)enemys.get(j);
			Rectangle r_enemy = e.getBounds();
			
			if(r_player.intersects(r_enemy)){
				// Spieler stirbt
				ingame=false; // Spiel beenden
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
		}
		else{
			// paintMenue(String Message) [Muss aus konstruktor aufgerufen werden] wenn menü gezeichnet am ende timer.stop()
			String msg;

			if(won) msg = "YOU WIN";
			else msg = "GAME OVER";
			Font small = new Font("Arial", Font.BOLD, 20);
			FontMetrics metr = this.getFontMetrics(small);
			
			g.setColor(Color.white);
			g.setFont(small);
			g.drawString(msg, (windowSizeX - metr.stringWidth(msg))/2, windowSizeY/2);
		}
		
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

