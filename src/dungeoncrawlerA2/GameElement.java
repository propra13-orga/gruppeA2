package dungeoncrawlerA2;

import java.awt.Image;

// GameElement: Spielobjekte (außer Player)
public class GameElement {
	private int x;
    private int y;
    private Image image;

    // Konstruktor
    public GameElement(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // get Methoden
    public Image getImage() {
        return this.image;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
    
    // Set Methoden
    public void setImage(Image pic) {
        this.image = pic;
    }
    
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
