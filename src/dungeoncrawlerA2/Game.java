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
import javax.swing.JButton;
import javax.swing.Timer;

// Game: Menü, Spielfeld und Spielsteuerung
public class Game extends JPanel implements ActionListener{
	
	private JButton b1;
	private JButton b2;
	private int buttonPosX;
	private int buttonPosY;
	
	private Timer timer;	// Timer für ActionEvent
	private boolean ingame; // Wert für Gameloop
	private boolean won; // für Abfrage ob gewonnen
	private boolean firstStart; // ist erster Start? - Menüdarstellung
	private int room; // aktueller Raum des Sungeons
	
	private Player player;	// Spielfigur
	
	private ArrayList walls = new ArrayList();
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
	public void initGame(){
		// Buttons ausblenden
		b1.setVisible(false);
		b2.setVisible(false);
		
		firstStart=false;
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
		
		// Kollisionen mit Gegner -> Spiel vorbei
		for(int j=0;j<enemys.size(); j++){
			Enemy e = (Enemy)enemys.get(j);
			Rectangle r_enemy = e.getBounds();
			
			if(r_player.intersects(r_enemy)){
				// Spieler stirbt
				ingame=false; // Spiel beenden
				timer.stop();
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
	
	// actionPerformed 
	public void actionPerformed(ActionEvent e){
		
		if(ingame){
			// im Spiel - wird vom Timer abhängig aufgerufen
			player.move();	// bewege Spielfigur
			checkCollision();	// prüfe Kollision
			checkRoom();	// prüfe ob Raum gewechselt oder Ziel erreicht
		}
		else{
			// im Menü - Aufruf bei button
			String action = e.getActionCommand();
			if(action.equals("start")) initGame();	// Spiel Starten
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

