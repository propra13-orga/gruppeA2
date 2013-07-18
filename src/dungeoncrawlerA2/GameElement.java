package dungeoncrawlerA2;

import java.awt.Image;

// GameElement: Spielobjekte (außer Player)
/**
 * Spielobjekte 
 *
 */
public class GameElement {
	
	private int x;
    private int y;
    private Image image;

    // Konstruktor
    /**
     * Erstellt neues Spielobjekt an Position x,y
     * @param x X Koordinate 
     * @param y Y Koordinate
     */
    public GameElement(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // get Methoden
    /** 
     * Gibt das Bild des Objekts zur&uuml;ck
     * @return Bild des Objekts
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Hole X-Koordinate des Objekts
     * @return X-Koordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Hole Y-Koordinate des Objekts
     * @return Y-Koordinate
     */
    public int getY() {
        return this.y;
    }
    
    // Set Methoden
    /** 
     * Setzt Bild des Objekts
     * @param pic Bild
     */
    public void setImage(Image pic) {
        this.image = pic;
    }
    
    /**
     * Setzt X-Koordinate des Objekts
     * @param x X-Koordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Setzt Y-Koordinate des Objekts
     * @param y Y-Koordinate
     */
    public void setY(int y) {
        this.y = y;
    }
}