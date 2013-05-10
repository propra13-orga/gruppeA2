package dungeoncrawlerA2;

import javax.swing.JFrame;

// Main class
// Menue, Start Game, Exit

public class dungeoncrawler extends JFrame {

	// window Size -> not resizable
	int windowSizeX = 800;	
	int windowSizeY = 600;
	
	public dungeoncrawler(){
		
		// create Frame (Main window)
		setTitle("Dungeon");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(windowSizeX, windowSizeY);
		setLocationRelativeTo(null); // Window to center of screen
		setResizable(false);
		
		/*
		Später hier Menü erstellen und erst bei Klick auf Button Spiel starten, Menü ausblenden 
		Reihenfolge: remove(menuepanel), add(new Game())
		Wenn game beendet, zerstöre game und add(menuepanel)
		 */
		
		// start Game
		Game g = new Game();
		add(g);
		setVisible(true); // set Visible
		
	
	}
	
	
	
	
	public static void main(String[] args) {
		new dungeoncrawler();

	}

}
