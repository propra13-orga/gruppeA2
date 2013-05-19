package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
	private Player player;	// Spielfigur
	private boolean ingame; // Wert für Gameloop
	
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
	
	// leveldata später füllen mit loadLevel(filename)
	// leveldata vor Verwendung überprüfen - checkLeveldata()
	// W1: Wall Type 1
	// G1: Ground Type 1
	private String [] leveldata = {
			"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 G1 W1 W1 W1 " +
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
			
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 ",
			
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 " +
			"G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 "
	};
	
	// Konstruktor
	public Game(){
		
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(backgroundColor);
		setDoubleBuffered(true); 
		
		// loadLevel()
		
		ingame = true;
		
		initRoom(0); // ersten Raum aufbauen
		
		player = new Player(startX, startY); // setze neue Spielfigur an Stelle x,y
		
		timer = new Timer(1000/fps, this); // Timer nach fps einstellen
		timer.start();
	}
	
	public void initRoom(int roomnumber){
		int x = 0;
		int y = 0;
		
		Wall wall;
		Ground ground;
		
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
	
	public void buildRoom(Graphics g){
		ArrayList room = new ArrayList();
		room.addAll(walls);
		room.addAll(grounds);
		
		for(int i=0; i<room.size(); i++){
			
			GameElement element = (GameElement)room.get(i);
			g.drawImage(element.getImage(),element.getX(),element.getY(), this);
		}
	}
	
	// Paint Methode - zeichnet Bildschirm
	public void paint(Graphics g){
		super.paint(g);
		
		// zeichne Raum
		buildRoom(g);
		
		// zeichne Spielfigur
		g.drawImage(player.getImage(), player.getX(), player.getY(), this);
		
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	// actionPerformed - wird vom Timer abhängig aufgerufen
	public void actionPerformed(ActionEvent e){
		
		/*
		 * Später hier Gameloop einfügen und Rest von ingame abhängig machen
		 * */
		
		player.move();	// bewege Spielfigur
		
		// if leftscreen -> initRoom(int Roomnumber, int setX, inst setY)
		
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

