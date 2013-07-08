package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class Leveleditor extends JFrame implements ActionListener{
	
	// Leveldatenelemente
	private String levelPath;
	private int rooms;
		
	private String[] leveldata;
	private String[] enemydata;
	private String[] itemdata;
	private String[] interactdata; // für Shops und besondere Interaktionen
	private int[][] exitdata;
	private String[] doordata;
	private String dialog = "Hallo, ich kann dir was erzählen.";
		
	private String intro;
	private String levelName;
	private int levelNumber;
		
	private String endBossLocation;
	private int endBossRoom;
	
	private int startX, startY;	// Startwert Spielfigur 
	private int startLive; // Lebenspunkte zu beginn des Levels
	
	// Fenstergröße
	private int windowSizeX = 1200;	
	private int windowSizeY = 800;
	private Color backgroundColor = Color.BLACK;
	
	// Fensterelemente, Buttons, usw
	JPanel edit = new JPanel();
	JButton bNewMap;
	JButton bLoadMap;
	
	// Statusvariablen
	boolean levelLoaded;
	int room;
	
	// Konstruktor
	public Leveleditor(){
		// Status setzen
		levelLoaded = false;
		
		// definiere Hauptfenster
		setTitle("Leveleditor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(windowSizeX, windowSizeY);
		setLocationRelativeTo(null); // Fenster in Mitte des Bildschirms setzen
		setResizable(false);
		startEditor();
		add(edit);
		setVisible(true);
	}
	
	private void startEditor(){
		edit.setLayout(null); // um Buttons beliebig zu positionieren
		edit.setBackground(backgroundColor);
		
		// Startbuttons hinzufügen
		bNewMap = new JButton("Level erstellen");
		bLoadMap = new JButton("Level bearbeiten");
		
		// Lege Standartpositionen für Buttons fest
		int buttonPosX = windowSizeX/2-100;
		int buttonPosY = windowSizeY/2-40;
		// bestimme Position und Größe
		bNewMap.setBounds(buttonPosX,buttonPosY,200,30);
		bLoadMap.setBounds(buttonPosX,buttonPosY+40,200,30);
		// benenne Aktionen
		bNewMap.setActionCommand("newMap");
		bLoadMap.setActionCommand("loadMap");
		// ActionListener hinzufügen
		bNewMap.addActionListener(this);
		bLoadMap.addActionListener(this);
		// füge Buttons zum Panel hinzu
		add(bNewMap);
		add(bLoadMap);
		// Buttons sichtbar machen
		bNewMap.setVisible(true);
		bLoadMap.setVisible(true);
	}
	
	private void loadLevel(String path){
		if(path.equals("")){
			// neues Level erstellen
			System.out.println("Neues Level erstellen");
			
			// Leeres Standartlevel erstellen
			levelNumber = 00;
			levelName = "";
			startX = 150;
			startY = 150;
			startLive = 6;
			intro = "";
			endBossLocation = "F1 00 10 06";
			// Raum vom Endgegner einlesen
			int r10 = endBossLocation.charAt(3)-48;
			int r01 = endBossLocation.charAt(4)-48;
			endBossRoom = 10*r10+r01;
			
			rooms = 1; // Hole Anzahl Räume
			
			// Erstelle Arrays 
			leveldata = new String[rooms];
			enemydata = new String[rooms];
			exitdata = new int[rooms][4];
			itemdata = new String[rooms];
			interactdata = new String[rooms];
			doordata = new String[rooms];
			
			leveldata[0] = "W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 G1 W1 "+
"W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 W1 ";
			enemydata[0] = "";
			exitdata[0][0]=exitdata[0][1]=exitdata[0][2]=exitdata[0][3]=-1;
			itemdata[0] = "";
			interactdata[0] = "";
			doordata[0] = "";
			
			// Level geladen - am Ende
			levelLoaded = true;
		}
		else{
			// bestehendes Level laden - aus Game übernommen + erweitert
			boolean isReadingLevel = false;
			boolean isReadingIntro = false;
			String line = null;
			String request = null;
			String data1, data2;
			int z1, z2, r, count, count2;
			int elementCounter = 0; // Counter der Anzahl der Elemente (#...) zählt, um zu wissen ob richtige Datei
			
			try {
				
				System.out.println("Lade Datei: "+path);
				// Reader vorbereiten 
				FileReader reader = new FileReader(path);
				BufferedReader in = new BufferedReader(reader);
				
				// Hilfsvariablen auf 0 setzen
				r = count = count2 = 0;
				// Zeile einlesen
				while ((line = in.readLine()) != null) {
					// Counter auf 0 setzen
					z1 = z2 = 0;
					request=" ";
					
					StringTokenizer tokens = new StringTokenizer(line);
					
					// GET REQUEST
					if(line.length()>0) request = tokens.nextToken(); // Save Request
					
					// Requests analysieren
					
					// Generelle Levelaten einlesen
					// Levelnummer einlesen
					if(request.equals("#NR")){
						elementCounter++;
						data1 = tokens.nextToken();
						z1 = data1.charAt(0)-48;	// von char nach int -> -48
						z2 = data1.charAt(1)-48;
						levelNumber = 10*z1+z2;
					}
					
					// Levelname einlesen
					if(request.equals("#TITLE")){
						elementCounter++;
						if(tokens.hasMoreTokens()) {
							levelName = tokens.nextToken();
							while(tokens.hasMoreTokens()) levelName+=" "+tokens.nextToken();
						}
					}
					
					// Startwerte Spielfigur einlesens
					if(request.equals("#START")){
						elementCounter++;
						data1 = tokens.nextToken();
						data2 = tokens.nextToken();
						z1 = data1.charAt(0)-48; // hole 100er
						z2 = data1.charAt(1)-48; // hole 10er
						startX = (data1.charAt(2)-48) + 100*z1 + 10*z2;	//bilde x koordinate
						z1 = data2.charAt(0)-48; // hole 100er
						z2 = data2.charAt(1)-48; // hole 10er
						startY = (data2.charAt(2)-48) + 100*z1 + 10*z2;	//bilde y koordinate
		
						// Lebenspunkte einlesen
						data1 = tokens.nextToken();
						z1 = data1.charAt(0)-48; // hole 10er
						z2 = data1.charAt(1)-48; // hole 1er
						startLive = 10*z1+z2;
					}
					
					// Intro einlesen
					if(isReadingIntro){
						if(request.equals("#END")) isReadingIntro = false;
						else 
							if(count2 == 0){
								intro = line;
								count2++;
							}
							else intro += line;
					}
					if(request.equals("#INTRO")){
						elementCounter++;
						isReadingIntro = true;
					}
					
					// Endgegnerdaten einlesen
					if(request.equals("#FINAL")){
						elementCounter++;
						endBossLocation = tokens.nextToken()+" ";
						for(int i = 0; i<3; i++) endBossLocation += tokens.nextToken() +" ";
						// Raum vom Endgegner einlesen
						int r10 = endBossLocation.charAt(3)-48;
						int r01 = endBossLocation.charAt(4)-48;
						endBossRoom = 10*r10+r01;
						
						System.out.println(endBossLocation + " "+endBossRoom);
					}
					
					// Anzahl Räume auslesen
					if(request.equals("#ROOMS")){
						elementCounter++;
						data1 = tokens.nextToken();
						z1 = data1.charAt(0);	// Hole Zehner
						z2 = data1.charAt(1);	// Hole Einser
						rooms = 10*(z1-48)+(z2-48); // Hole Anzahl Räume
						
						// Erstelle Arrays 
						leveldata = new String[rooms];
						enemydata = new String[rooms];
						exitdata = new int[rooms][4];
						itemdata = new String[rooms];
						interactdata = new String[rooms];
						doordata = new String[rooms];
					}
					
					// Spezielle leveldaten einlesen (pro Raum)
					// Raumwechsel feststellen
					if(request.equals("#NEWROOM")){
						elementCounter++;
						count = 0;
						
						data1 = tokens.nextToken();
						z1 = data1.charAt(0);	// Hole Zehner
						z2 = data1.charAt(1);	// Hole Einser
						r = 10*(z1-48)+(z2-48); // Hole Anzahl Räume
					}
					
					// Raumaufbau auslesen
					if(isReadingLevel){
						if(count==0) leveldata[r]=line;
						else leveldata[r]+=line;
						count++;
						if(count==14) isReadingLevel = false;
					}
					if(request.equals("#LEVEL")){
						elementCounter++;
						isReadingLevel = true;
					}
					
					// Feinde auslesen
					if(request.equals("#ENEMY")){
						elementCounter++;
						if(tokens.hasMoreTokens()) enemydata[r]=tokens.nextToken()+" "; // hole ersten
						else enemydata[r]="";
						while(tokens.hasMoreTokens()) enemydata[r]+=tokens.nextToken()+" "; // hole Rest
					}
					
					// Items auslesen
					if(request.equals("#ITEM")){
						elementCounter++;
						if(tokens.hasMoreTokens()) itemdata[r]=tokens.nextToken()+" "; // hole ersten
						else itemdata[r]="";
						while(tokens.hasMoreTokens()) itemdata[r]+=tokens.nextToken()+" "; // hole Rest
					}
					// Interaktive Elemente auslesen - zB Shops
					if(request.equals("#INTERACT")){
						elementCounter++;
						if(tokens.hasMoreTokens()) interactdata[r]=tokens.nextToken()+" "; // hole ersten
						else interactdata[r]="";
						while(tokens.hasMoreTokens()) interactdata[r]+=tokens.nextToken()+" "; // hole Rest
					}
					
					// Türen einlesen
					if(request.equals("#DOOR")){
						elementCounter++;
						if(tokens.hasMoreTokens()) doordata[r]=tokens.nextToken()+" "; // hole ersten
						else doordata[r]="";
						while(tokens.hasMoreTokens()) doordata[r]+=tokens.nextToken()+" "; // hole Rest
						
					}
					
					// Ausgänge einlesen
					if(request.equals("#EXIT")){
						elementCounter++;
						for(int i = 0; i<4; i++){
							data1 = tokens.nextToken();
							z1 = data1.charAt(0);
							z2 = data1.charAt(1);
							if(z1=='-'){
								exitdata[r][i]=-(z2-48); // negative Ausgänge (kein Ausgane / Endausgang) auslesen
							}
							else{
								exitdata[r][i]=(z1-48)*10+(z2-48);
							}
						}
					}
					
				}
				
				// BufferedReader schließen
				in.close();
				// Prüfe ob genug Elemente vorhanden
				if(elementCounter >= 13){
					levelLoaded = true;
				}
				else{
					System.out.println("Fehlerhafte Datei. Laden nicht möglich.");
				}
			} 
			catch (IOException e) {
				System.out.println("Datei nicht gefunden, oder fehlerhaft.");
			}
		}
		if(levelLoaded){
			// Buttons entfernen wenn Editor gestartet
			bNewMap.setVisible(false);
			bLoadMap.setVisible(false);
		}
	}
	
	// Action Listener
	public void actionPerformed(ActionEvent e){
		String action = e.getActionCommand();
		
		if(levelLoaded){
			// im Editor
		}
		else{
			// im Editor-Menü
			if(action.equals("newMap")){
				loadLevel("");
			}
			else if(action.equals("loadMap")){
				// JFileChooser-Objekt erstellen
		        JFileChooser chooser = new JFileChooser();
		        // Dialog zum Oeffnen von Dateien anzeigen
		        int value = chooser.showOpenDialog(null);
		        
		        if(value == JFileChooser.APPROVE_OPTION)
		        {
		             // Ausgabe der ausgewaehlten Datei
		        	levelPath = chooser.getSelectedFile().getAbsolutePath();
		            System.out.println("Die zu öffnende Datei ist: " + levelPath);
		            loadLevel(levelPath);
		            
		        }
		        
			}
		}
		
	}
}
