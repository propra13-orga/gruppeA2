package dungeoncrawlerA2;

import java.awt.Color;

import javax.swing.JPanel;

// Game: Paint (Spielfeld), initiate, detect collisions, gameloop
/*
	Weitere Klassen: Wall, Player, Opponent
	in Game: starten, zeichnen, kollisionen erkennen
 */

public class Game extends JPanel{
	
	// Background color
	private Color backgroundColor = Color.black;
	
	public Game(){
		setBackground(backgroundColor);
	}
}

