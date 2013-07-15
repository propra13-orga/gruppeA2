package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

public class Leveleditor extends JFrame implements ActionListener, MouseListener{
	
	// Editordaten
	private String editorObjects = "leveldata/editorobjects.txt";
	
	// Leveldatenelemente
	private String levelPath;
	private int rooms;
		
	private String[] leveldata;
	private String[] enemydata;
	private String[] itemdata;
	private String[] interactdata; // für Shops und besondere Interaktionen
	private int[][] exitdata;
	private String[] doordata;
	private String[] dialog;
		
	private String intro;
	private String levelName;
	private int levelNumber;
		
	private String endBossLocation;
	private int endBossRoom;
	
	private int startX, startY;	// Startwert Spielfigur 
	private int startLive; // Lebenspunkte zu beginn des Levels
	
	// Levellisten
	private ArrayList<Door> doors = new ArrayList<Door>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Ground> grounds = new ArrayList<Ground>();
	private ArrayList<Enemy> enemys = new ArrayList<Enemy>();
	private ArrayList<Item> items = new ArrayList<Item>();
	private ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	private ArrayList<NPC> npcs = new ArrayList<NPC>();
	private FinalEnemy finalEnemy;
	private ArrayList<GameElement> room = new ArrayList<GameElement>();
	
	// Editor-Levelelemente
	private ArrayList<Door> doorTypes = new ArrayList<Door>();
	private ArrayList<Wall> wallTypes = new ArrayList<Wall>();
	private ArrayList<Ground> groundTypes = new ArrayList<Ground>();
	private ArrayList<Enemy> enemyTypes = new ArrayList<Enemy>();
	private ArrayList<Item> itemTypes = new ArrayList<Item>();
	private ArrayList<Checkpoint> checkpointTypes = new ArrayList<Checkpoint>();
	private ArrayList<NPC> npcTypes = new ArrayList<NPC>();
	private ArrayList<FinalEnemy> finalEnemyTypes = new ArrayList<FinalEnemy>();
	
	// Fenstergröße
	private int windowSizeX = 1200;	
	private int windowSizeY = 800;
	private Color backgroundColor = Color.BLACK;
	
	// Spielfelddaten
	private int fieldSizeX = 800;	
	private int fieldSizeY = 560;
	private int blockSize = 40;
	
	// Fensterelemente, Buttons, usw
	JPanel edit = new JPanel();
	JButton bNewMap;
	JButton bLoadMap;
	
	JButton bSwitchRoom;
	JButton bSwitchExit0;
	JButton bSwitchExit1;
	JButton bSwitchExit2;
	JButton bSwitchExit3;
	
	JButton bSwitchGround, bSwitchWall, bSwitchDoor, bSwitchEnemy, bSwitchCheckpoint, bSwitchItem, bSwitchFinal, bSwitchNPC;
	
	JButton bSave, bExit;
	
	JRadioButton radioGround;
	JRadioButton radioWall;
	JRadioButton radioDoor;
	JRadioButton radioEnemy;
	JRadioButton radioCheckpoint;
	JRadioButton radioItem;
	JRadioButton radioFinal;
	JRadioButton radioNPC;
	
	ButtonGroup radioGroup;
	
	// Statusvariablen
	boolean levelLoaded;
	boolean editorLoaded;
	int visibleRoom;
	int selectedGround, selectedWall, selectedDoor, selectedEnemy, selectedCheckpoint, selectedItem, selectedFinal, selectedNPC;
	String selectedElement;
	
	// Konstruktor
	public Leveleditor(){
		// Status setzen
		levelLoaded = false;
		editorLoaded = false;
		
		// definiere Hauptfenster
		setTitle("Leveleditor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(windowSizeX, windowSizeY);
		setLocationRelativeTo(null); // Fenster in Mitte des Bildschirms setzen
		setResizable(false);
		startEditor();
		add(edit);
		setVisible(true);
		
		addMouseListener(this);
		
		visibleRoom = 0;
		selectedGround = selectedWall = selectedDoor = selectedEnemy = selectedCheckpoint = selectedItem = selectedFinal = selectedNPC = 0;
	}
	
	private void startEditor(){
		
		edit.setLayout(null); // um Buttons beliebig zu positionieren
		edit.setBackground(backgroundColor);
		
		// Menü
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
		edit.add(bNewMap);
		edit.add(bLoadMap);
		// Buttons sichtbar machen
		bNewMap.setVisible(true);
		bLoadMap.setVisible(true);
		
		// Editor
		// Buttons hinzufügen
		bSwitchRoom = new JButton("Room");
		bSwitchExit0 = new JButton("Exit0");
		bSwitchExit1 = new JButton("Exit1");
		bSwitchExit2 = new JButton("Exit2");
		bSwitchExit3 = new JButton("Exit3");
		
		bSave = new JButton("Speichern");
		bExit = new JButton("Beenden");
		
		bSwitchGround = new JButton(">");
		bSwitchWall = new JButton(">");
		bSwitchDoor = new JButton(">");
		bSwitchCheckpoint = new JButton(">");
		bSwitchItem = new JButton(">");
		bSwitchNPC = new JButton(">");
		bSwitchEnemy = new JButton(">");
		bSwitchFinal = new JButton(">");
		
		radioGround = new JRadioButton("Ground");
		radioWall = new JRadioButton("Wall");
		radioDoor = new JRadioButton("Door");
		radioCheckpoint = new JRadioButton("Checkpoint");
		radioItem = new JRadioButton("Item");
		radioNPC = new JRadioButton("NPC");
		radioEnemy = new JRadioButton("Enemy");
		radioFinal = new JRadioButton("Final");
		radioGroup = new ButtonGroup();
		
		// bestimme Position und Größe
		bSwitchRoom.setBounds(110,fieldSizeY+50,90,30);
		bSwitchExit0.setBounds(110,fieldSizeY+10,90,30);
		bSwitchExit1.setBounds(210,fieldSizeY+50,90,30);
		bSwitchExit2.setBounds(110,fieldSizeY+90,90,30);
		bSwitchExit3.setBounds(10,fieldSizeY+50,90,30);
		
		bSave.setBounds(windowSizeX-200,windowSizeY-100,120,30);
		bExit.setBounds(windowSizeX-200,windowSizeY-60,120,30);
		
		bSwitchGround.setBounds(fieldSizeX + 200, 20, 50, 40);
		bSwitchWall.setBounds(fieldSizeX + 200, 80, 50, 40);
		bSwitchDoor.setBounds(fieldSizeX + 200, 140, 50, 40);
		bSwitchCheckpoint.setBounds(fieldSizeX + 200, 200, 50, 40);
		bSwitchItem.setBounds(fieldSizeX + 200, 260, 50, 40);
		bSwitchNPC.setBounds(fieldSizeX + 200, 320, 50, 40);
		bSwitchEnemy.setBounds(fieldSizeX + 200, 380, 50, 40);
		bSwitchFinal.setBounds(fieldSizeX + 200, 440, 50, 40);
		
		radioGround.setBounds(fieldSizeX + 20, 20, 80, 40);
		radioWall.setBounds(fieldSizeX + 20, 80, 80, 40);
		radioDoor.setBounds(fieldSizeX + 20, 140, 80, 40);
		radioCheckpoint.setBounds(fieldSizeX + 20, 200, 120, 40);
		radioItem.setBounds(fieldSizeX + 20, 260, 80, 40);
		radioNPC.setBounds(fieldSizeX + 20, 320, 80, 40);
		radioEnemy.setBounds(fieldSizeX + 20, 380, 80, 40);
		radioFinal.setBounds(fieldSizeX + 20, 440, 80, 40);
		
		radioGround.setSelected(true);
		selectedElement = "ground";
		
		// Radiobuttons gruppieren
		radioGroup.add(radioGround);
		radioGroup.add(radioWall);
		radioGroup.add(radioDoor);
		radioGroup.add(radioCheckpoint);
		radioGroup.add(radioItem);
		radioGroup.add(radioNPC);
		radioGroup.add(radioEnemy);
		radioGroup.add(radioFinal);
		
		// Radiobuttonhintergrund
		radioGround.setBackground(backgroundColor);
		radioGround.setForeground(Color.white);
		
		radioWall.setBackground(backgroundColor);
		radioWall.setForeground(Color.white);
		
		radioDoor.setBackground(backgroundColor);
		radioDoor.setForeground(Color.white);
		
		radioCheckpoint.setBackground(backgroundColor);
		radioCheckpoint.setForeground(Color.white);
		
		radioItem.setBackground(backgroundColor);
		radioItem.setForeground(Color.white);
		
		radioNPC.setBackground(backgroundColor);
		radioNPC.setForeground(Color.white);
		
		radioEnemy.setBackground(backgroundColor);
		radioEnemy.setForeground(Color.white);
		
		radioFinal.setBackground(backgroundColor);
		radioFinal.setForeground(Color.white);
		
		// benenne Aktionen
		bSwitchRoom.setActionCommand("switchRoom");
		bSwitchExit0.setActionCommand("switchExit0");
		bSwitchExit1.setActionCommand("switchExit1");
		bSwitchExit2.setActionCommand("switchExit2");
		bSwitchExit3.setActionCommand("switchExit3");
		
		bSave.setActionCommand("save");
		bExit.setActionCommand("end");
		
		bSwitchGround.setActionCommand("switchGround");
		bSwitchWall.setActionCommand("switchWall");
		bSwitchDoor.setActionCommand("switchDoor");
		bSwitchCheckpoint.setActionCommand("switchCheckpoint");
		bSwitchItem.setActionCommand("switchItem");
		bSwitchNPC.setActionCommand("switchNPC");
		bSwitchEnemy.setActionCommand("switchEnemy");
		bSwitchFinal.setActionCommand("switchFinal");
		
		radioGround.setActionCommand("selGround");
		radioWall.setActionCommand("selWall");
		radioDoor.setActionCommand("selDoor");
		radioCheckpoint.setActionCommand("selCheckpoint");
		radioItem.setActionCommand("selItem");
		radioNPC.setActionCommand("selNPC");
		radioEnemy.setActionCommand("selEnemy");
		radioFinal.setActionCommand("selFinal");
		
		// ActionListener hinzufügen
		bSwitchRoom.addActionListener(this);
		bSwitchExit0.addActionListener(this);
		bSwitchExit1.addActionListener(this);
		bSwitchExit2.addActionListener(this);
		bSwitchExit3.addActionListener(this);
		
		bSave.addActionListener(this);
		bExit.addActionListener(this);
		
		bSwitchGround.addActionListener(this);
		bSwitchWall.addActionListener(this);
		bSwitchDoor.addActionListener(this);
		bSwitchCheckpoint.addActionListener(this);
		bSwitchItem.addActionListener(this);
		bSwitchNPC.addActionListener(this);
		bSwitchEnemy.addActionListener(this);
		bSwitchFinal.addActionListener(this);
		
		radioGround.addActionListener(this);
		radioWall.addActionListener(this);
		radioDoor.addActionListener(this);
		radioCheckpoint.addActionListener(this);
		radioItem.addActionListener(this);
		radioNPC.addActionListener(this);
		radioEnemy.addActionListener(this);
		radioFinal.addActionListener(this);
		
		// füge Buttons zum Panel hinzu
		edit.add(bSwitchRoom);
		edit.add(bSwitchExit0);
		edit.add(bSwitchExit1);
		edit.add(bSwitchExit2);
		edit.add(bSwitchExit3);
		
		edit.add(bSave);
		edit.add(bExit);
		
		edit.add(bSwitchGround);
		edit.add(bSwitchWall);
		edit.add(bSwitchDoor);
		edit.add(bSwitchCheckpoint);
		edit.add(bSwitchItem);
		edit.add(bSwitchNPC);
		edit.add(bSwitchEnemy);
		edit.add(bSwitchFinal);
		
		edit.add(radioGround);
		edit.add(radioWall);
		edit.add(radioDoor);
		edit.add(radioCheckpoint);
		edit.add(radioItem);
		edit.add(radioNPC);
		edit.add(radioEnemy);
		edit.add(radioFinal);
		
		// Buttons unsichtbar machen
		bSwitchRoom.setVisible(false);
		bSwitchExit0.setVisible(false);
		bSwitchExit1.setVisible(false);
		bSwitchExit2.setVisible(false);
		bSwitchExit3.setVisible(false);
		
		bSave.setVisible(false);
		bExit.setVisible(false);
		
		bSwitchGround.setVisible(false);
		bSwitchWall.setVisible(false);
		bSwitchDoor.setVisible(false);
		bSwitchCheckpoint.setVisible(false);
		bSwitchItem.setVisible(false);
		bSwitchNPC.setVisible(false);
		bSwitchEnemy.setVisible(false);
		bSwitchFinal.setVisible(false);
		
		radioGround.setVisible(false);
		radioWall.setVisible(false);
		radioDoor.setVisible(false);
		radioCheckpoint.setVisible(false);
		radioItem.setVisible(false);
		radioNPC.setVisible(false);
		radioEnemy.setVisible(false);
		radioFinal.setVisible(false);
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
			endBossLocation = "";
			endBossRoom = -1;
			
			rooms = 1; // Hole Anzahl Räume
			
			// Erstelle Arrays 
			leveldata = new String[rooms];
			enemydata = new String[rooms];
			exitdata = new int[rooms][4];
			itemdata = new String[rooms];
			interactdata = new String[rooms];
			doordata = new String[rooms];
			dialog = new String[rooms];
			
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
			dialog[0] = "";
			
			// Level geladen - am Ende
			levelLoaded = true;
		}
		else{
			// bestehendes Level laden - aus Game übernommen + erweitert
			boolean isReadingLevel = false;
			boolean isReadingIntro = false;
			boolean isReadingDialog = false;
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
							else intro += "\n"+line;
					}
					if(request.equals("#INTRO")){
						elementCounter++;
						isReadingIntro = true;
					}
					
					// Endgegnerdaten einlesen
					if(request.equals("#FINAL")){
						elementCounter++;
						if(tokens.hasMoreTokens()){
							endBossLocation = tokens.nextToken()+" ";
							for(int i = 0; i<3; i++) endBossLocation += tokens.nextToken() +" ";
							// Raum vom Endgegner einlesen
							int r10 = endBossLocation.charAt(3)-48;
							int r01 = endBossLocation.charAt(4)-48;
							endBossRoom = 10*r10+r01;
						}
						else{
							endBossLocation = "";
							endBossRoom = -1; // kein Endgegner
						}
						
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
						dialog = new String[rooms];
						for(int i=0;i<dialog.length;i++) dialog[i] = "";
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
					
					// Dialog einlesen
					if(isReadingDialog){
						if(request.equals("#DIAEND")) isReadingDialog = false;
						else dialog[r] += line+"\n";
					}
					if(request.equals("#DIALOG")){
						isReadingDialog = true;
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
			initWorkspace();
		}
	}
	
	private void saveLevel(String path){
		File f = new File(path);
		FileWriter writer;
		String st = ""; // Hilfsstring
		
		try{
			writer = new FileWriter(f);
			
			// Allgemeine Levelinfos
			// #NR
			if(levelNumber<10) st = "0"+levelNumber;
			else if(levelNumber>99) st = "99";
			else st = "" + levelNumber;
			writer.write("#NR "+st+"\n");
			
			// #TITLE
			writer.write("#TITLE "+levelName+"\n");
			
			// #ROOMS
			if(rooms<10) st = "0"+rooms;
			else if(rooms>99) st="99";
			else st = "" + rooms;
			writer.write("#ROOMS "+st+"\n");
			
			// #FINAL
			writer.write("#FINAL "+endBossLocation+"\n");
			
			// #START
			if(startX<10) st="00"+startX + " ";
			else if (startX<100) st="0"+startX+ " ";
			else st=""+startX+ " ";
			
			if(startY<10) st+="00"+startX + " ";
			else if (startY<100) st+="0"+startX+ " ";
			else st+=""+startY+ " ";
			
			if(startLive<10) st+= "0"+startLive;
			else st += startLive;
			
			writer.write("#START "+st+"\n");
			
			// #INTRO
			writer.write("#INTRO\n"+intro+"\n#END\n");
			
			// Raumspezifische infos
			for(int i=0;i<rooms;i++){
				// #NEWROOM
				writer.write("#NEWROOM ");
				if(i<10) st="0"+i;
				else st=""+i;
				writer.write(st+"\n");
				
				// #LEVEL
				writer.write("#LEVEL\n");
				for(int j=0;j<leveldata[i].length();j++){
					writer.write(leveldata[i].charAt(j));
					if((j+1)%60==0) writer.write("\n");
				}
				
				
				// #DOOR
				writer.write("\n#DOOR "+ doordata[i] + "\n");
				
				// #EXIT
				writer.write("#EXIT ");
				for(int k = 0; k<4; k++){
					if(exitdata[i][k]<0 || exitdata[i][k]>10) st = ""+exitdata[i][k];
					else st = "0"+exitdata[i][k];
					writer.write(st+" ");
				}
				writer.write("\n");
				
				// #ENEMY
				writer.write("#ENEMY "+enemydata[i] +"\n");
				
				// #INTERACT
				writer.write("#INTERACT "+interactdata[i] +"\n");
				
				// #ITEM
				writer.write("#ITEM "+itemdata[i] +"\n");
				
				// #DIALOG
				writer.write("#DIALOG\n"+dialog[i]+"\n#DIAEND\n");
				
				writer.write("\n");
			}
			
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			
		}
	}
	
	
	private void initWorkspace(){
		String line = null;
		String request = "";
		String type;
		int typeNumber;
		
		// Buttons entfernen wenn Editor gestartet
		bNewMap.setVisible(false);
		bLoadMap.setVisible(false);
		
		// Arbeitsfläche erstellen
		bSwitchRoom.setVisible(true);
		bSwitchExit0.setVisible(true);
		bSwitchExit1.setVisible(true);
		bSwitchExit2.setVisible(true);
		bSwitchExit3.setVisible(true);
		
		bSave.setVisible(true);
		bExit.setVisible(true);
		
		bSwitchGround.setVisible(true);
		bSwitchWall.setVisible(true);
		bSwitchDoor.setVisible(true);
		bSwitchCheckpoint.setVisible(true);
		bSwitchItem.setVisible(true);
		bSwitchNPC.setVisible(true);
		bSwitchEnemy.setVisible(true);
		bSwitchFinal.setVisible(true);
		
		radioGround.setVisible(true);
		radioWall.setVisible(true);
		radioDoor.setVisible(true);
		radioCheckpoint.setVisible(true);
		radioItem.setVisible(true);
		radioNPC.setVisible(true);
		radioEnemy.setVisible(true);
		radioFinal.setVisible(true);
		
		// ersten Raum initialisieren
		initRoom(visibleRoom);
		repaint();
		
		// Verfügbare Objekte laden
		try{
			InputStream is = getClass().getResourceAsStream(editorObjects);
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader in = new BufferedReader(reader);
			
			while ((line = in.readLine()) != null){
				StringTokenizer tokens = new StringTokenizer(line);
				
				if(line.length()>0) request = tokens.nextToken();
				else request = "";
				
				// je 1 Objekt jeder Art erstellen
				while(tokens.hasMoreTokens()){
					type = tokens.nextToken();
					int t10 = type.charAt(0)-48;
					int t01 = type.charAt(1)-48;
					typeNumber = 10*t10+t01+48;
					
					if(request.equals("WALL")){
						Wall w = new Wall(0,0,typeNumber);
						wallTypes.add(w);
						System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("GROUND")){
						Ground g = new Ground(0,0,typeNumber);
						groundTypes.add(g);
						System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("DOOR")){
						Door d = new Door(0,0,typeNumber,false);
						doorTypes.add(d);
						System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("CHECKPOINT")){
						Checkpoint c = new Checkpoint(0,0,typeNumber);
						checkpointTypes.add(c);
						System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("ENEMY")){
						Enemy e = new Enemy(0,0,typeNumber);
						enemyTypes.add(e);
						System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("FINAL")){
						FinalEnemy fe = new FinalEnemy(0,0,typeNumber);
						finalEnemyTypes.add(fe);
						System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("ITEM")){
						Item it = new Item(0,0,typeNumber);
						itemTypes.add(it);
						System.out.println(request +" Type: " + (typeNumber-48) +" = " + it.getItemType() + " hinzugefügt.");
					}
					else if(request.equals("NPC")){
						NPC npc = new NPC(0,0,typeNumber);
						npcTypes.add(npc);
						System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
				}
				
			}
			editorLoaded = true;
		}
		catch (Exception e) {
			System.out.println("Objektdatei nicht gefunden, oder fehlerhaft.");
		}
		
		// Benutzeroberfläche Laden - Buttons, Textfelder, usw erstellen
		
		
	}
	
	// Raum erstellen
	public void initRoom(int roomnumber){
		int x = 0;
		int y = 0;
			
		Wall wall;
		Ground ground;
		Enemy enemy;
		Item item;
		Door door;
		NPC npc;
			
		char element;
		char type;
			
		doors.clear();
		walls.clear();
		npcs.clear();
		grounds.clear();
		enemys.clear();
		items.clear();
		checkpoints.clear();
		finalEnemy = null;
			
		// Endgegner, falls vorhanden, auslesen
		if(roomnumber == endBossRoom){
			element = endBossLocation.charAt(0);
			type = endBossLocation.charAt(1);
			
			// Koordinaten der Gegner aus String filtern und in Pixel umwandeln
			int x10 = endBossLocation.charAt(6)-48;
			int x01 = endBossLocation.charAt(7)-48;
			int fEnemyX = (10*x10 + x01)*blockSize;
						
			int y10 = endBossLocation.charAt(9)-48;
			int y01 = endBossLocation.charAt(10)-48;
			int fEnemyY = (10*y10 + y01)*blockSize;
			
			finalEnemy = new FinalEnemy(fEnemyX, fEnemyY, type);
		}
			
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
			if(x>=fieldSizeX){
				x=0;
				y+=blockSize;
			}
		}
			
		// Türen auslesen aus doordata	
		for(int d=0;d<doordata[roomnumber].length(); d+=11){
			element = doordata[roomnumber].charAt(d);
			type = doordata[roomnumber].charAt(d+1);
				
			// Position der Tür aus String filtern und in Pixel umwandeln
			int x10 = doordata[roomnumber].charAt(d+3)-48;
			int x01 = doordata[roomnumber].charAt(d+4)-48;
			int doorX = (10*x10 + x01)*blockSize;
						
			int y10 = doordata[roomnumber].charAt(d+6)-48;
			int y01 = doordata[roomnumber].charAt(d+7)-48;
			int doorY = (10*y10 + y01)*blockSize;
				
			// Zustand Tür ermitteln und Tür erstellen
			char zc = doordata[roomnumber].charAt(d+9);
			if(zc == 'c'){
				door = new Door(doorX, doorY, type, false);
			}
			else{
				door = new Door(doorX, doorY, type, true);
			}
			doors.add(door);
				
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
			
		// Items auslesen aus Itemdata
		for(int k=0;k<itemdata[roomnumber].length(); k+=11){
			// Itemtyp
			element = itemdata[roomnumber].charAt(k);
			type = itemdata[roomnumber].charAt(k+1);
				
			// Koordinaten in px auslesen
			int x100 = itemdata[roomnumber].charAt(k+3)-48;
			int x010 = itemdata[roomnumber].charAt(k+4)-48;
			int x001 = itemdata[roomnumber].charAt(k+5)-48;
			int itemX = 100*x100+10*x010+x001;

			int y100 = itemdata[roomnumber].charAt(k+7)-48;
			int y010 = itemdata[roomnumber].charAt(k+8)-48;
			int y001 = itemdata[roomnumber].charAt(k+9)-48;
			int itemY = 100*y100+10*y010+y001;
				
			// Itemtyp ermitteln (I -> 0 -> Standartitems, J ->1 -> Waffen)
			if(element == 'J') type += 10;
				
			// Item erstellen und in Liste einfügen
			item = new Item(itemX, itemY, type);
			items.add(item);
				
		}		
			
		// Interaktive Elemente auslesen - Checkpoints, Shop, NPC
		for(int l = 0; l<interactdata[roomnumber].length(); l+=11){
			// Typ
			element = interactdata[roomnumber].charAt(l);
			type = interactdata[roomnumber].charAt(l+1);
							
			// Koordinaten in px auslesen
			int x100 = interactdata[roomnumber].charAt(l+3)-48;
			int x010 = interactdata[roomnumber].charAt(l+4)-48;
			int x001 = interactdata[roomnumber].charAt(l+5)-48;
			int interX = 100*x100+10*x010+x001;

			int y100 = interactdata[roomnumber].charAt(l+7)-48;
			int y010 = interactdata[roomnumber].charAt(l+8)-48;
			int y001 = interactdata[roomnumber].charAt(l+9)-48;
			int interY = 100*y100+10*y010+y001;
							
			// Element erstellen und in Liste einfügen
			if(element == 'C'){
				// Checkpoint
				Checkpoint cp;
				cp = new Checkpoint(interX, interY, type);
				checkpoints.add(cp);
			}
			else if(element == 'N'){
				// NPC 
				npc = new NPC(interX,interY,type);
				npcs.add(npc);
			}
			else if(element == 'S'){
				// Shop 
				type+=10;
				npc = new NPC(interX,interY,type);
				npcs.add(npc);
			}
		}
					
			
	}
		
		// Raum zeichnen - aus Game()
	public void buildRoom(Graphics g){
		room.clear();
		// Raumelemente einfügen
		room.addAll(walls);
		room.addAll(grounds);
		room.addAll(doors);
		room.addAll(checkpoints);
		// Items einfügen - wichtig: nach Raum
		room.addAll(items);
		room.addAll(npcs);
		// Gegner einfügen - wichtig: Erst Raum und Items, dann andere Objekte
		room.addAll(enemys);
			
		if(finalEnemy!=null) room.add(finalEnemy);
			
		for(int i=0; i<room.size(); i++){
			// Element holen und zeichnen
			GameElement element = (GameElement)room.get(i);
			g.drawImage(element.getImage(),element.getX(),element.getY(), this);
		}
	}
		
	public void paint(Graphics g){
		super.paint(g);
		if(levelLoaded){
			// im Editor zeichnen
			buildRoom(g);
			
			// Ausgänge und Raum Infos
			Font small = new Font("Arial", Font.BOLD, 14);
			g.setColor(Color.white);
			g.setFont(small);
			String vRoom = ""+visibleRoom;
			String exit0 = ""+exitdata[visibleRoom][0];
			String exit1 = ""+exitdata[visibleRoom][1];
			String exit2 = ""+exitdata[visibleRoom][2];
			String exit3 = ""+exitdata[visibleRoom][3];
			
			g.drawString(vRoom, 145, fieldSizeY+190);
			g.setColor(Color.red);
			g.drawString(exit0, 145, fieldSizeY+160);
			g.drawString(exit1, 175, fieldSizeY+190);
			g.drawString(exit2, 145, fieldSizeY+220);
			g.drawString(exit3, 115, fieldSizeY+190);
			g.setColor(Color.white);
			
			if(editorLoaded){
				// Wenn Listen aller Objekte bereit -> Bilder zeichnen
				Ground gr = (Ground)groundTypes.get(selectedGround);
				g.drawImage(gr.getImage(),fieldSizeX + 140,20,this);
				
				Wall wl = (Wall)wallTypes.get(selectedWall);
				g.drawImage(wl.getImage(),fieldSizeX + 140,80,this);
				
				Door dr = (Door)doorTypes.get(selectedDoor);
				g.drawImage(dr.getImage(),fieldSizeX + 140,140,this);
				
				Checkpoint cp = (Checkpoint)checkpointTypes.get(selectedCheckpoint);
				g.drawImage(cp.getImage(),fieldSizeX + 140,200,this);
				
				Item it = (Item)itemTypes.get(selectedItem);
				g.drawImage(it.getImage(),fieldSizeX + 140,260,this);
				
				NPC npc = (NPC)npcTypes.get(selectedNPC);
				g.drawImage(npc.getImage(),fieldSizeX + 140,320,this);
				
				Enemy en = (Enemy)enemyTypes.get(selectedEnemy);
				g.drawImage(en.getImage(),fieldSizeX + 140,380,this);
				
				FinalEnemy fen = (FinalEnemy)finalEnemyTypes.get(selectedFinal);
				g.drawImage(fen.getImage(),fieldSizeX + 140,440,50,50,this);
				
			}
		}
	}
	
	public void changeData(){
		int x,y;
		// Setze alles auf Ground
		int[][][] field = {{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
				{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}}}; 
		
		// Finde Böden
		for(int i = 0; i<grounds.size(); i++){
			Ground gr = (Ground)grounds.get(i);
			Rectangle rg = gr.getBounds();
			
			x = (int)rg.getX()/blockSize;
			y = (int)rg.getY()/blockSize;
			
			field[y][x][1] = gr.getType();
 		}
		
		// Finde Wände
		for(int i = 0; i<walls.size(); i++){
			Wall wl = (Wall)walls.get(i);
			Rectangle rw = wl.getBounds();
					
			x = (int)rw.getX()/blockSize;
			y = (int)rw.getY()/blockSize;
					
			field[y][x][0] = 1;
			field[y][x][1] = wl.getType();
		 }
		
		leveldata[visibleRoom] = "";
		
		for(int i=0; i<field.length;i++){
			for(int j=0;j<field[i].length;j++){
				if(field[i][j][0]==1){
					// Wall
					leveldata[visibleRoom]+="W"+field[i][j][1]+" ";
					
				}
				else{
					// Ground
					leveldata[visibleRoom]+="G"+field[i][j][1] + " ";
				}
			}
		}
		
	}
	
	public void mouseClicked(MouseEvent e) {
		int mouseX, mouseY;
		Rectangle r_mouse;
		int elementX, elementY;
		Rectangle r_element;
		
		if(levelLoaded && editorLoaded){
			mouseX = (int)e.getX();
			mouseY = (int)e.getY();
			System.out.println("Position der Maus: " + mouseX+":"+mouseY);
			r_mouse = new Rectangle(mouseX-3,mouseY-3,6,6);
			
			if(selectedElement.equals("ground")){
				Ground selG = (Ground)groundTypes.get(selectedGround);
				
				// Ground gegen Ground tauschen
				for(int i=0;i<grounds.size();i++){
					Ground gr = (Ground)grounds.get(i);
					r_element = gr.getBounds();
					
					// Nachschauen ob Maus auf Element
					if(r_mouse.intersects(r_element)){
						
						elementX = (int)r_element.getX();
						elementY = (int)r_element.getY();
						
						// Prüfe ob gleicher Typ
						if(selG.getType()!=gr.getType()){
							grounds.remove(i);
							grounds.add(i, new Ground(elementX,elementY,selG.getType()+48));
						}
					}
				}
				// Wall gegen Ground tauschen
				for(int j=0;j<walls.size();j++){
					Wall wl = (Wall)walls.get(j);
					r_element = wl.getBounds();
					
					// Nachschauen ob Maus auf Element
					if(r_mouse.intersects(r_element)){
						
						elementX = (int)r_element.getX();
						elementY = (int)r_element.getY();
						
						walls.remove(j);
						grounds.add(new Ground(elementX,elementY,selG.getType()+48));
					}
				}	
			}
			
			if(selectedElement.equals("wall")){
				Wall selW = (Wall)wallTypes.get(selectedWall);
				
				// Wall gegen Wall tauschen
				for(int i=0;i<walls.size();i++){
					Wall wl = (Wall)walls.get(i);
					r_element = wl.getBounds();
					
					// Nachschauen ob Maus auf Element
					if(r_mouse.intersects(r_element)){
						
						elementX = (int)r_element.getX();
						elementY = (int)r_element.getY();
						
						// Prüfe ob gleicher Typ
						if(selW.getType()!=wl.getType()){
							walls.remove(i);
							walls.add(i, new Wall(elementX,elementY,selW.getType()+48));
						}
					}
				}
				// Ground gegen Wall tauschen
				for(int j=0;j<grounds.size();j++){
					Ground gr = (Ground)grounds.get(j);
					r_element = gr.getBounds();
					
					// Nachschauen ob Maus auf Element
					if(r_mouse.intersects(r_element)){
						
						elementX = (int)r_element.getX();
						elementY = (int)r_element.getY();
						
						grounds.remove(j);
						walls.add(new Wall(elementX,elementY,selW.getType()+48));
					}
				}	
			}
			
			changeData();
			
			/*
			else if(action.equals("selDoor")) selectedElement = "door";
			else if(action.equals("selCheckpoint")) selectedElement = "checkpoint";
			else if(action.equals("selItem")) selectedElement = "item";
			else if(action.equals("selNPC")) selectedElement = "npc";
			else if(action.equals("selEnemy")) selectedElement = "enemy";
			else if(action.equals("selFinal")) selectedElement = "final";*/
			
		}
		repaint();
	}
	
	public void mousePressed(MouseEvent e) {
		
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}
	
	public void mouseExited(MouseEvent e) {
		
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}
	
	
	// Action Listener
	public void actionPerformed(ActionEvent e){
		String action = e.getActionCommand();
		
		if(levelLoaded){
			// im Editor
			if(action.equals("switchRoom")){
				visibleRoom++;
				if(visibleRoom>=rooms) visibleRoom = 0;
				initRoom(visibleRoom);
			}
			else if(action.equals("switchExit0")){
				exitdata[visibleRoom][0]++;
				if(exitdata[visibleRoom][0]>=rooms) exitdata[visibleRoom][0] = -2;
			}
			else if(action.equals("switchExit1")){
				exitdata[visibleRoom][1]++;
				if(exitdata[visibleRoom][1]>=rooms) exitdata[visibleRoom][1] = -2;
			}
			else if(action.equals("switchExit2")){
				exitdata[visibleRoom][2]++;
				if(exitdata[visibleRoom][2]>=rooms) exitdata[visibleRoom][2] = -2;
			}
			else if(action.equals("switchExit3")){
				exitdata[visibleRoom][3]++;
				if(exitdata[visibleRoom][3]>=rooms) exitdata[visibleRoom][3] = -2;
			}
			else if(action.equals("switchGround")){
				selectedGround++;
				if(selectedGround>=groundTypes.size()) selectedGround=0;
			}
			else if(action.equals("switchWall")){
				selectedWall++;
				if(selectedWall>=wallTypes.size()) selectedWall=0;
			}
			else if(action.equals("switchDoor")){
				selectedDoor++;
				if(selectedDoor>=doorTypes.size()) selectedDoor=0;
			}
			else if(action.equals("switchCheckpoint")){
				selectedCheckpoint++;
				if(selectedCheckpoint>=checkpointTypes.size()) selectedCheckpoint=0;
			}
			else if(action.equals("switchItem")){
				selectedItem++;
				if(selectedItem>=itemTypes.size()) selectedItem=0;
			}
			else if(action.equals("switchNPC")){
				selectedNPC++;
				if(selectedNPC>=npcTypes.size()) selectedNPC=0;
			}
			else if(action.equals("switchEnemy")){
				selectedEnemy++;
				if(selectedEnemy>=enemyTypes.size()) selectedEnemy=0;
			}
			else if(action.equals("switchFinal")){
				selectedFinal++;
				if(selectedFinal>=finalEnemyTypes.size()) selectedFinal=0;
			}
			else if(action.equals("save")){
				 JFileChooser chooser = new JFileChooser();
				 int value = chooser.showSaveDialog(null);
				 if(value == JFileChooser.APPROVE_OPTION)
			     {
					 // Ausgabe der ausgewaehlten Datei
					 levelPath = chooser.getSelectedFile().getAbsolutePath();
			         System.out.println("Die zu speichernde Datei ist: " + levelPath);
			         saveLevel(levelPath);
			            
			     }
		         
			}
			else if(action.equals("end")){
				setVisible(false);
				dispose();
			}
			else if(action.equals("selGround")) selectedElement = "ground";
			else if(action.equals("selWall")) selectedElement = "wall";
			else if(action.equals("selDoor")) selectedElement = "door";
			else if(action.equals("selCheckpoint")) selectedElement = "checkpoint";
			else if(action.equals("selItem")) selectedElement = "item";
			else if(action.equals("selNPC")) selectedElement = "npc";
			else if(action.equals("selEnemy")) selectedElement = "enemy";
			else if(action.equals("selFinal")) selectedElement = "final";
			
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
		repaint();
		
	}
}


