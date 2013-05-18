package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

// Game: Paint (Spielfeld), initiate, detect collisions, gameloop
/*
	Weitere Klassen: Wall, Opponent
	in Game: starten, zeichnen, kollisionen erkennen
 */

public class Game extends JPanel implements ActionListener{
	
	private Timer timer;	// Timer für ActionEvent
	private Player player;	// Spielfigur
	private boolean ingame; // Wert für Gameloop
	
	private Color backgroundColor = Color.WHITE; // Hintergrundfarbe
	private int startX = 40;	// Startwert Spielfigur
	private int startY = 60;
	private int fps = 100;	// Bildwiedrholrate - (evtl später runtersetzen?)
	
	// Konstruktor
	public Game(){
		
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(backgroundColor);
		setDoubleBuffered(true); 
		
		ingame = true;
		
		// HIER später initRoom(0) aufrufen
		
		player = new Player(startX, startY); // setze neue Spielfigur an Stelle x,y
		
		timer = new Timer(1000/fps, this); // Timer nach fps einstellen
		timer.start();
	}
	
	// Paint Methode - zeichnet Bildschirm
	public void paint(Graphics g){
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(player.getImage(), player.getX(), player.getY(), this);
		
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	// actionPerformed - wird vom Timer abhängig aufgerufen
	public void actionPerformed(ActionEvent e){
		
		/*
		 * Später hier Gameloop einfügen und Rest von ingame abhängig machen
		 * */
		
		player.move();	// bewege Spielfigur
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

