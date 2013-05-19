package dungeoncrawlerA2;

import javax.swing.JFrame;

// Hauptklasse
// Menü, Start Game, Exit

// dungeoncrawler: Hauptklasse
public class dungeoncrawler extends JFrame {

	// Fenstergröße
	private int windowSizeX = 800;	
	private int windowSizeY = 600;
	
	// Konstruktor
	public dungeoncrawler(){
		
		// definiere Hauptfenster
		setTitle("Dungeon");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(windowSizeX, windowSizeY);
		setLocationRelativeTo(null); // Fenster in Mitte des Bildschirms setzen
		setResizable(false);
		
		/*
		Später hier Menü erstellen und erst bei Klick auf Button Spiel starten, Menü ausblenden 
		Reihenfolge: remove(menuepanel), add(new Game())
		Wenn game beendet, zerstöre game und add(menuepanel)
		 */
		
		// Spiel starten
		Game g = new Game();
		add(g); // Panel "Game" zum JFrame hinzufügen
		setVisible(true); // Sichtbar machen!
		
	
	}
	
	
	
	
	public static void main(String[] args) {
		new dungeoncrawler();

	}

}
