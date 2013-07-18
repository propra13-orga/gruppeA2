package dungeoncrawlerA2;

import javax.swing.JFrame;

// Hauptklasse
// Menü, Start Game, Exit

// dungeoncrawler: Hauptklasse
/**
 * Startet das Programm
 *
 */
public class dungeoncrawler extends JFrame {

	// Fenstergröße
	private int windowSizeX = 800;	
	private int windowSizeY = 630;
	
	// Konstruktor
	/**
	 * Leitet ein neues Spiel ein, Programmstart
	 */
	public dungeoncrawler(){
		
		// definiere Hauptfenster
		setTitle("Dungeon");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(windowSizeX, windowSizeY);
		setLocationRelativeTo(null); // Fenster in Mitte des Bildschirms setzen
		setResizable(false);
		
		// Spiel starten
		Game g = new Game();
		add(g); // Panel "Game" zum JFrame hinzufügen
		setVisible(true); // Sichtbar machen!
		
	
	}
	
	
	
	
	/**
	 * main-Klasse
	 * @param args nicht belegt
	 */
	public static void main(String[] args) {
		new dungeoncrawler();

	}

}