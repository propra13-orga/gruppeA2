package dungeoncrawlerA2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * Leveleditor
 *
 */
public class Leveleditor extends JFrame implements ActionListener, MouseListener{
	
	// Editordaten
	private String editorObjects = "leveldata/editorobjects.txt";
	
	// Leveldatenelemente
	private String levelPath;
	private int rooms;
	
	private String newRoomData = "G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 "+
			"G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 G5 ";
		
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
	private int endBossX, endBossY;
	
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
	private Player player = new Player(0,0,0,0); // Dummy Player
	
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
	
	JButton addRoom, removeRoom;
	
	JButton levelInfos, raumDialog;
	
	JButton bSetFinal, bSetPlayer;
	JTextField tFinalRoom, tFinalX, tFinalY, tPlayerX, tPlayerY, tPlayerLive;
	
	JButton bSwitchGround, bSwitchWall, bSwitchDoor, bSwitchEnemy, bSwitchCheckpoint, bSwitchItem, bSwitchFinal, bSwitchNPC;
	
	JButton bSave, bExit;
	
	JRadioButton radioGround;
	JRadioButton radioWall;
	JRadioButton radioDoor;
	JRadioButton radioEnemy;
	JRadioButton radioCheckpoint;
	JRadioButton radioItem;
	JRadioButton radioNPC;
	
	ButtonGroup radioGroup;
	
	JCheckBox checkFinal;
	
	// Statusvariablen
	boolean levelLoaded;
	boolean editorLoaded;
	boolean finalIsSelected;
	int visibleRoom;
	int selectedGround, selectedWall, selectedDoor, selectedEnemy, selectedCheckpoint, selectedItem, selectedFinal, selectedNPC;
	String selectedElement;
	
	
	
	// Konstruktor
	/**
	 * Erzeugt neues Fenster mit dem Leveleditor zum Dungeoncrawler
	 */
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
	
	/**
	 * Initialisiert die Benutzeroberfl&auml;che des Editors 
	 */
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
		
		levelInfos = new JButton("Levelinfos");
		raumDialog = new JButton("Raumdialog");
		
		addRoom = new JButton("Raum hinzufügen");
		removeRoom = new JButton("Raum löschen");
		
		bSetFinal = new JButton("OK");
		bSetPlayer = new JButton("OK");
		
		tFinalRoom = new JTextField();
		tFinalX = new JTextField();
		tFinalY = new JTextField();
		tPlayerX = new JTextField();
		tPlayerY = new JTextField();
		tPlayerLive = new JTextField();
		
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
	
		radioGroup = new ButtonGroup();
	
		checkFinal = new JCheckBox("Final");
		
		// bestimme Position und Größe
		bSwitchRoom.setBounds(110,fieldSizeY+50,90,30);
		bSwitchExit0.setBounds(110,fieldSizeY+10,90,30);
		bSwitchExit1.setBounds(210,fieldSizeY+50,90,30);
		bSwitchExit2.setBounds(110,fieldSizeY+90,90,30);
		bSwitchExit3.setBounds(10,fieldSizeY+50,90,30);
		
		levelInfos.setBounds(700,fieldSizeY+170,180,30);
		raumDialog.setBounds(700,fieldSizeY+130,180,30);
		
		addRoom.setBounds(700,fieldSizeY+50,180,30);
		removeRoom.setBounds(700,fieldSizeY+90,180,30);

		bSetFinal.setBounds(560,fieldSizeY+90,90,30);
		bSetPlayer.setBounds(560,fieldSizeY+50,90,30);
		
		tFinalRoom.setBounds(400,fieldSizeY+90,30,30);
		tFinalX.setBounds(440,fieldSizeY+90,30,30);
		tFinalY.setBounds(480,fieldSizeY+90,30,30);
		tPlayerX.setBounds(440,fieldSizeY+50,30,30);
		tPlayerY.setBounds(480,fieldSizeY+50,30,30);
		tPlayerLive.setBounds(520,fieldSizeY+50,30,30);
		
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
		checkFinal.setBounds(fieldSizeX + 20, 440, 80, 40);
		
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
		
		checkFinal.setBackground(backgroundColor);
		checkFinal.setForeground(Color.white);
		
		// benenne Aktionen
		bSwitchRoom.setActionCommand("switchRoom");
		bSwitchExit0.setActionCommand("switchExit0");
		bSwitchExit1.setActionCommand("switchExit1");
		bSwitchExit2.setActionCommand("switchExit2");
		bSwitchExit3.setActionCommand("switchExit3");
		
		levelInfos.setActionCommand("openLevelInfos");
		raumDialog.setActionCommand("openRaumDialog");
		
		addRoom.setActionCommand("addRoom");
		removeRoom.setActionCommand("removeRoom");
		
		bSetFinal.setActionCommand("setFinal");
		bSetPlayer.setActionCommand("setPlayer");
		
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
		checkFinal.setActionCommand("selFinal");
		
		// ActionListener hinzufügen
		bSwitchRoom.addActionListener(this);
		bSwitchExit0.addActionListener(this);
		bSwitchExit1.addActionListener(this);
		bSwitchExit2.addActionListener(this);
		bSwitchExit3.addActionListener(this);
		
		levelInfos.addActionListener(this);
		raumDialog.addActionListener(this);
		
		addRoom.addActionListener(this);
		removeRoom.addActionListener(this);
		
		bSetFinal.addActionListener(this);
		bSetPlayer.addActionListener(this);
		
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
		checkFinal.addActionListener(this);
		
		// füge Buttons zum Panel hinzu
		edit.add(bSwitchRoom);
		edit.add(bSwitchExit0);
		edit.add(bSwitchExit1);
		edit.add(bSwitchExit2);
		edit.add(bSwitchExit3);
		
		edit.add(levelInfos);
		edit.add(raumDialog);
		
		edit.add(addRoom);
		edit.add(removeRoom);
		
		edit.add(bSetFinal);
		edit.add(bSetPlayer);
		
		edit.add(tFinalRoom);
		edit.add(tFinalX);
		edit.add(tFinalY);
		edit.add(tPlayerX);
		edit.add(tPlayerY);
		edit.add(tPlayerLive);
		
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
		edit.add(checkFinal);
		
		// Buttons unsichtbar machen
		bSwitchRoom.setVisible(false);
		bSwitchExit0.setVisible(false);
		bSwitchExit1.setVisible(false);
		bSwitchExit2.setVisible(false);
		bSwitchExit3.setVisible(false);
		
		levelInfos.setVisible(false);
		raumDialog.setVisible(false);
		
		addRoom.setVisible(false);
		removeRoom.setVisible(false);
		
		bSetFinal.setVisible(false);
		bSetPlayer.setVisible(false);
		
		tFinalRoom.setVisible(false);
		tFinalX.setVisible(false);
		tFinalY.setVisible(false);
		tPlayerX.setVisible(false);
		tPlayerY.setVisible(false);
		tPlayerLive.setVisible(false);
		
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
		checkFinal.setVisible(false);
	}
	
	/**
	 * L&auml;dt Leveldaten aus einer Textdatei
	 * @param path Pfad der Textdatei
	 */
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
			
			leveldata[0] = newRoomData;
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
							selectedFinal = endBossLocation.charAt(1)-49;
							int r10 = endBossLocation.charAt(3)-48;
							int r01 = endBossLocation.charAt(4)-48;
							
							int x10 = endBossLocation.charAt(6)-48;
							int x01 = endBossLocation.charAt(7)-48;
							endBossX = 10*x10+x01;
							endBossX *= blockSize;
							
							int y10 = endBossLocation.charAt(9)-48;
							int y01 = endBossLocation.charAt(10)-48;
							endBossY = 10*y10+y01;
							endBossY *= blockSize;
							
							endBossRoom = 10*r10+r01;
							
							checkFinal.setSelected(true);
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
	
	/**
	 * Schreibt Leveldaten in eine Textdatei
	 * @param path Pfad der Datei
	 */
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
					if(exitdata[i][k]<0 || exitdata[i][k]>=10) st = ""+exitdata[i][k];
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
	
	
	/**
	 * Startet Benutzeroberfl&auml;che des Editors 
	 */
	private void initWorkspace(){
		String line = null;
		String request = "";
		String type;
		int typeNumber;
		finalIsSelected = false;
		
		// Buttons entfernen wenn Editor gestartet
		bNewMap.setVisible(false);
		bLoadMap.setVisible(false);
		
		// Arbeitsfläche erstellen
		bSwitchRoom.setVisible(true);
		bSwitchExit0.setVisible(true);
		bSwitchExit1.setVisible(true);
		bSwitchExit2.setVisible(true);
		bSwitchExit3.setVisible(true);
		
		levelInfos.setVisible(true);
		raumDialog.setVisible(true);
		
		addRoom.setVisible(true);
		removeRoom.setVisible(true);
		
		bSetFinal.setVisible(true);
		bSetPlayer.setVisible(true);
		
		tFinalRoom.setVisible(true);
		tFinalX.setVisible(true);
		tFinalY.setVisible(true);
		tPlayerX.setVisible(true);
		tPlayerY.setVisible(true);
		tPlayerLive.setVisible(true);
		
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
		checkFinal.setVisible(true);
		
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
						// System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("GROUND")){
						Ground g = new Ground(0,0,typeNumber);
						groundTypes.add(g);
						// System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("DOOR")){
						Door d = new Door(0,0,typeNumber,false);
						doorTypes.add(d);
						// System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("CHECKPOINT")){
						Checkpoint c = new Checkpoint(0,0,typeNumber);
						checkpointTypes.add(c);
						// System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("ENEMY")){
						Enemy e = new Enemy(0,0,typeNumber);
						enemyTypes.add(e);
						// System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("FINAL")){
						FinalEnemy fe = new FinalEnemy(0,0,typeNumber);
						finalEnemyTypes.add(fe);
						// System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
					}
					else if(request.equals("ITEM")){
						Item it = new Item(0,0,typeNumber);
						itemTypes.add(it);
						// System.out.println(request +" Type: " + (typeNumber-48) +" = " + it.getItemType() + " hinzugefügt.");
					}
					else if(request.equals("NPC")){
						NPC npc = new NPC(0,0,typeNumber);
						npcTypes.add(npc);
						// System.out.println(request +" Type: " + (typeNumber-48) + " hinzugefügt.");
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
	/**
	 * Erstellt Raum aus Leveldaten 
	 * @param roomnumber Raumnummer
	 */
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
		System.out.println("initRoom Final: "+endBossLocation);
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
	/**
	 * Zeichnet den geladenen Raum
	 * @param g Grafik
	 */
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
		
		if(visibleRoom==0) g.drawImage(player.getImage(), startX, startY, this);
	}
		
	/* (non-Javadoc)
	 * @see java.awt.Window#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g){
		super.paint(g);
		if(levelLoaded){
			// im Editor zeichnen
			buildRoom(g);
			
			// Ausgänge und Raum Infos
			Font small = new Font("Arial", Font.BOLD, 14);
			Font smaller = new Font("Arial", Font.BOLD, 10);
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
				
				// Player und Final set Bereich
				String textFinalRoom,textFinalX,textFinalY;
				if(checkFinal.isSelected()&&endBossRoom>=0){
					textFinalRoom=""+endBossRoom;
					textFinalX = ""+endBossX;
					textFinalY=""+endBossY;
					
					tFinalRoom.setText(textFinalRoom);
					tFinalX.setText(textFinalX);
					tFinalY.setText(textFinalY);
				}
				else{
					tFinalRoom.setText("");
					tFinalX.setText("");
					tFinalY.setText("");
				}
				
				String label="Player:";
				String label2="Final:";
				
				String textX=""+startX;
				String textY=""+startY;
				String textLive=""+startLive;
				
				tPlayerX.setText(textX);
				tPlayerY.setText(textY);
				tPlayerLive.setText(textLive);
				
				g.drawString(label, 320, fieldSizeY+70);
				g.drawString(label2, 320, fieldSizeY+110);
				
				g.drawString("0", 410, fieldSizeY+70);
				g.drawString("X", 530, fieldSizeY+110);
				
				g.setFont(smaller);
				g.drawString("Room", 400, fieldSizeY+40);
				g.drawString("X", 450, fieldSizeY+40);
				g.drawString("Y", 490, fieldSizeY+40);
				g.drawString("Live", 520, fieldSizeY+40);
				g.setFont(small);
			}
		}
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	/**
	 * F&uuml;gt den Leveldaten einen Raum an letzter Position hinzu
	 */
	public void addRoom(){
		System.out.println("Füge Raum nach "+(rooms-1) +" hinzu.");
		
		String[] leveldataSave = new String[rooms];
		String[] enemydataSave = new String[rooms];
		String[] itemdataSave = new String[rooms];
		String[] interactdataSave = new String[rooms];
		int[][] exitdataSave = new int[rooms][4];
		String[] doordataSave = new String[rooms];
		String[] dialogSave = new String[rooms];
		
		// Sichern
		for(int i=0;i<rooms;i++){
			leveldataSave[i] = leveldata[i];
			enemydataSave[i] = enemydata[i];
			itemdataSave[i] = itemdata[i];
			interactdataSave[i] = interactdata[i];
			exitdataSave[i] = exitdata[i];
			doordataSave[i] = doordata[i];
			dialogSave[i] = dialog[i];
		}
		
		rooms++;
		
		leveldata = new String[rooms];
		enemydata = new String[rooms];
		exitdata = new int[rooms][4];
		itemdata = new String[rooms];
		interactdata = new String[rooms];
		doordata = new String[rooms];
		dialog = new String[rooms];
		
		for(int i=0;i<rooms;i++){
			if(i<rooms-1){
				leveldata[i] = leveldataSave[i];
				enemydata[i] = enemydataSave[i];
				exitdata[i][0]=exitdataSave[i][0];
				exitdata[i][1]=exitdataSave[i][1];
				exitdata[i][2]=exitdataSave[i][2];
				exitdata[i][3]=exitdataSave[i][3];
				itemdata[i] = itemdataSave[i];
				interactdata[i] = interactdataSave[i];
				doordata[i] = doordataSave[i];
				dialog[i] = dialogSave[i];
			}
			else{
				leveldata[i] = newRoomData;
				enemydata[i] = "";
				exitdata[i][0]=exitdata[i][1]=exitdata[i][2]=exitdata[i][3]=-1;
				itemdata[i] = "";
				interactdata[i] = "";
				doordata[i] = "";
				dialog[i] = "";
			}
		}
		
	}
	
	/**
	 * Entfernt den gerade sichtbaren Raum aus den Leveldaten
	 */
	public void removeRoom(){
		System.out.println("Lösche Raum "+visibleRoom+".");
		int counter = 0;
		
		if(rooms!=1){
			String[] leveldataSave = new String[rooms];
			String[] enemydataSave = new String[rooms];
			String[] itemdataSave = new String[rooms];
			String[] interactdataSave = new String[rooms];
			int[][] exitdataSave = new int[rooms][4];
			String[] doordataSave = new String[rooms];
			String[] dialogSave = new String[rooms];
			
			// Sichern
			for(int i=0;i<rooms;i++){
				leveldataSave[i] = leveldata[i];
				enemydataSave[i] = enemydata[i];
				itemdataSave[i] = itemdata[i];
				interactdataSave[i] = interactdata[i];
				exitdataSave[i] = exitdata[i];
				doordataSave[i] = doordata[i];
				dialogSave[i] = dialog[i];
			}
			
			
			leveldata = new String[rooms-1];
			enemydata = new String[rooms-1];
			exitdata = new int[rooms-1][4];
			itemdata = new String[rooms-1];
			interactdata = new String[rooms-1];
			doordata = new String[rooms-1];
			dialog = new String[rooms-1];
			
			for(int i=0;i<rooms;i++){
				if(i!=visibleRoom){
					leveldata[counter] = leveldataSave[i];
					enemydata[counter] = enemydataSave[i];
					exitdata[counter][0]=exitdataSave[i][0];
					exitdata[counter][1]=exitdataSave[i][1];
					exitdata[counter][2]=exitdataSave[i][2];
					exitdata[counter][3]=exitdataSave[i][3];
					itemdata[counter] = itemdataSave[i];
					interactdata[counter] = interactdataSave[i];
					doordata[counter] = doordataSave[i];
					dialog[counter] = dialogSave[i];
					counter++;
				}
			}

			rooms--;
			initRoom(visibleRoom);
		}
		else{
			System.out.println("Löschen des einzigen Raumes nicht möglich!");
		}
		
	}
	
	/**
	 * Codiert die Leveldaten in richtiger Reihenfolge 
	 */
	public void changeData(){
		int x,y;
		
		// Setze alles auf Ground
		// Baue leveldata START
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
		// Baue Leveldata ENDE
		
		// Baue Doordata
		doordata[visibleRoom] = "";
		for(int i=0;i<doors.size(); i++){
			Door dr = (Door)doors.get(i);
			Rectangle rd = dr.getBounds();
			String sx,sy;
			
			x = (int)rd.getX()/blockSize;
			y = (int)rd.getY()/blockSize;
			
			if(x>=10) sx=""+x;
			else sx="0"+x;

			if(y>=10) sy=""+y;
			else sy="0"+y;
			
			doordata[visibleRoom]+="D"+dr.getType()+" "+sx+" "+sy+" c ";
		}
		
		// Baue Itemdata
		itemdata[visibleRoom] = "";
		for(int i=0;i<items.size(); i++){
			Item it = (Item)items.get(i);
			String sx,sy;
			String itemCode;
			
			// Format bestimmen (I oder J)
			int type = it.getType();
			if(type>=10){
				type-=10;
				itemCode="J"+type;
			}
			else itemCode="I"+type;
			
			// Koordinaten bestimmen
			x = (int)it.getX();
			y = (int)it.getY();
			
			if(x>=100) sx= ""+x;
			else if(x>=10) sx="0"+x;
			else sx="00"+x;

			if(y>=100) sy= ""+y;
			else if(y>=10) sy="0"+y;
			else sy="00"+y;
			
			// Codieren
			itemdata[visibleRoom]+=itemCode + " " + sx + " " + sy + " ";
		}
		
		// Baue interactdata -> Checkpoints, NPC und Shop
		interactdata[visibleRoom]="";
		for(int i=0;i<checkpoints.size();i++){
			String sy, sx;
			Checkpoint cp = (Checkpoint)checkpoints.get(i);
			// Koordinaten bestimmen
			x = (int)cp.getX();
			y = (int)cp.getY();
						
			if(x>=100) sx= ""+x;
			else if(x>=10) sx="0"+x;
			else sx="00"+x;

			if(y>=100) sy= ""+y;
			else if(y>=10) sy="0"+y;
			else sy="00"+y;
			interactdata[visibleRoom]="C0 "+sx + " " + sy+ " ";
			
		}
		// +NPC
		for(int i=0;i<npcs.size();i++){
			String sy, sx, npcCode;
			NPC npc = (NPC)npcs.get(i);
			// Koordinaten bestimmen
			x = (int)npc.getX();
			y = (int)npc.getY();
						
			if(x>=100) sx= ""+x;
			else if(x>=10) sx="0"+x;
			else sx="00"+x;

			if(y>=100) sy= ""+y;
			else if(y>=10) sy="0"+y;
			else sy="00"+y;
			
			// Format bestimmen (N oder S)
			int type = npc.getType();
			if(type>=10){
				type-=10;
				npcCode="S"+type;
			}
			else npcCode="N"+type;
			
			interactdata[visibleRoom]+=npcCode+" "+sx + " " + sy+" ";
			
		}
		
		// Baue enemydata
		enemydata[visibleRoom]="";
		for(int i=0;i<enemys.size(); i++){
			Enemy en = (Enemy)enemys.get(i);
			Rectangle re = en.getBounds();
			String sx,sy;
			
			x = (int)en.getX()/blockSize;
			y = (int)en.getY()/blockSize;
			
			if(x>=10) sx=""+x;
			else sx="0"+x;

			if(y>=10) sy=""+y;
			else sy="0"+y;
			
			enemydata[visibleRoom]+="E"+en.getType()+" "+sx+" "+sy+" ";
		}
		
	}
	
	/**
	 * Schreibt die Position des Spielers in die Leveldaten
	 */
	public void changePlayerPos(){
		int pX,pY,pL;
		String spX,spY,spL;
		
		spX = tPlayerX.getText();
		if(spX.length()>3) pX=fieldSizeX-(int)player.getBounds().getWidth();
		else if(spX.length()==3) pX=(spX.charAt(0)-48)*100+(spX.charAt(1)-48)*10+(spX.charAt(2)-48);
		else if(spX.length()==2) pX=(spX.charAt(0)-48)*10+(spX.charAt(1)-48);
		else if(spX.length()==1) pX=spX.charAt(0)-48;
		else pX = 0;
		if(pX>fieldSizeX-(int)player.getBounds().getWidth()) pX = fieldSizeX-(int)player.getBounds().getWidth();
		
		spY = tPlayerY.getText();
		if(spY.length()>3) pY=fieldSizeY-(int)player.getBounds().getHeight();
		else if(spY.length()==3) pY=(spY.charAt(0)-48)*100+(spY.charAt(1)-48)*10+(spY.charAt(2)-48);
		else if(spY.length()==2) pY=(spY.charAt(0)-48)*10+(spY.charAt(1)-48);
		else if(spY.length()==1) pY=spY.charAt(0)-48;
		else pY = 0;
		if(pY>fieldSizeX-(int)player.getBounds().getHeight()) pY = fieldSizeX-(int)player.getBounds().getHeight();
		
		spL = tPlayerLive.getText();
		if(spL.length()>2) pL = 12;
		else if(spL.length()==2) pL = (spL.charAt(0)-48)*10+(spL.charAt(1)-48);
		else if(spL.length()==1) pL = spL.charAt(0)-48;
		else pL = 3;
		if(pL>12) pL=12;
		
		startX = pX;
		startY = pY;
		startLive = pL;
		
		System.out.println("Playerdata" +startX + " " + startY + " "+ startLive);

	}
	
	/**
	 * Schreibt die Position des Endgegners in die Leveldaten
	 */
	public void changeFinalData(){
		int fX, fY, fR;
		String sfX,sfY,sfR;
		FinalEnemy sfe = (FinalEnemy)finalEnemyTypes.get(selectedFinal);
		
		// Hole Pos aus Text
		sfX = tFinalX.getText();
		if(sfX.length()>3) fX=fieldSizeX-(int)sfe.getBounds().getWidth();
		else if(sfX.length()==3) fX=(sfX.charAt(0)-48)*100+(sfX.charAt(1)-48)*10+(sfX.charAt(2)-48);
		else if(sfX.length()==2) fX=(sfX.charAt(0)-48)*10+(sfX.charAt(1)-48);
		else if(sfX.length()==1) fX=sfX.charAt(0)-48;
		else fX = 0;
		if(fX>fieldSizeX-(int)sfe.getBounds().getWidth()) fX = fieldSizeX-(int)sfe.getBounds().getWidth();
		
		sfY = tFinalY.getText();
		if(sfY.length()>3) fY=fieldSizeY-(int)sfe.getBounds().getHeight();
		else if(sfY.length()==3) fY=(sfY.charAt(0)-48)*100+(sfY.charAt(1)-48)*10+(sfY.charAt(2)-48);
		else if(sfY.length()==2) fY=(sfY.charAt(0)-48)*10+(sfY.charAt(1)-48);
		else if(sfY.length()==1) fY=sfY.charAt(0)-48;
		else fY = 0;
		if(fY>fieldSizeY-(int)sfe.getBounds().getHeight()) fY = fieldSizeY-(int)sfe.getBounds().getHeight();
		
		sfR = tFinalRoom.getText();
		if(sfR.length()>2) fR = rooms-1;
		else if(sfR.length()==2) fR = (sfR.charAt(0)-48)*10+(sfR.charAt(1)-48);
		else if(sfR.length()==1) fR = sfR.charAt(0)-48;
		else fR = visibleRoom;
		if(fR>=rooms) fR=rooms-1;
		
		// Formatiere Pos
		fX= fX/blockSize;
		fY= fY/blockSize;
		

		endBossX = fX*blockSize;
		endBossY = fY*blockSize;
		endBossRoom = fR;
		
		
		if(fR<10) sfR = "0"+fR;
		else sfR = ""+fR;
		
		if(fX<10) sfX = "0"+fX;
		else sfX = "" + fX;

		if(fY<10) sfY = "0"+fY;
		else sfY = "" + fY;
				
		
		endBossLocation="";
		endBossLocation+="F"+(selectedFinal+1)+" "+sfR+" "+sfX+" "+sfY;
		
		finalEnemy = new FinalEnemy(endBossX,endBossY,selectedFinal+49);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
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
			
			// Door
			if(selectedElement.equals("door")){
				Door selD = (Door)doorTypes.get(selectedDoor);
				
				// Door auf Ground setzen 
				for(int j=0;j<grounds.size();j++){
					Ground gr = (Ground)grounds.get(j);
					r_element = gr.getBounds();
					int rem = -1;
					
					// Nachschauen ob Maus auf Element
					if(r_mouse.intersects(r_element)){
						boolean isSet = false;
						elementX = (int)r_element.getX();
						elementY = (int)r_element.getY();
						
						// Schaue nach ob Tür auf Platz
						for(int i = 0; i<doors.size(); i++){
							Door dr = (Door)doors.get(i);
							Rectangle r_door = dr.getBounds();
							
							if(r_mouse.intersects(r_door)){
								isSet = true;
							}
							
							if(isSet){
								 if(rem==-1) rem=i;
							}
						}
						if(!isSet) doors.add(new Door(elementX,elementY,selD.getType()+48,false));
						if(rem!=-1)  doors.remove(rem);
					}
				}	
				
			}
			
			// ITEM
			if(selectedElement.equals("item")){
				Item selI = (Item)itemTypes.get(selectedItem);
				boolean sameItemRemoved = false;
				boolean isCorrectPlace = false;
				Rectangle rsItem;
				
				for(int i=0;i<grounds.size();i++){
					// Stelle sicher, dass Item nur auf Ground plazierbar
					Ground gr = (Ground)grounds.get(i);
					Rectangle rg = gr.getBounds();
					
					if(rg.contains(mouseX, mouseY)) isCorrectPlace = true;
				}
				
				// Plaziere oder entferne Item
				if(isCorrectPlace){
					rsItem = new Rectangle(mouseX,mouseY,selI.getImage().getWidth(null),selI.getImage().getHeight(null));
					
					for(int i=0;i<items.size();i++){
						Item it = (Item)items.get(i);
						Rectangle rit = it.getBounds();
						
						if(rsItem.intersects(rit)){
							if(selI.getType()==it.getType()) sameItemRemoved = true;
							items.remove(i);
						}
					}
					
					if(!sameItemRemoved){
						items.add(new Item(mouseX,mouseY,selI.getType()+48));
					}
				}
			}
			
			// Checkpoint
			if(selectedElement.equals("checkpoint")){
				boolean isCorrectPlace = false;
				boolean intersectsOldCP = false;
				
				for(int i=0;i<grounds.size();i++){
					// Stelle sicher, dass Item nur auf Ground plazierbar
					Ground gr = (Ground)grounds.get(i);
					Rectangle rg = gr.getBounds();
					
					if(rg.contains(mouseX, mouseY)) isCorrectPlace = true;
				}
				
				if(isCorrectPlace){
					
					for(int j=0; j<checkpoints.size(); j++){
						Checkpoint cp = (Checkpoint)checkpoints.get(j);
						Rectangle rcp = cp.getBounds();
						if(rcp.contains(mouseX, mouseY)) intersectsOldCP = true;
					}
					
					// Maximal 1 checkpoint pro Raum
					checkpoints.clear();
					if(!intersectsOldCP) checkpoints.add(new Checkpoint(mouseX,mouseY,48));
				}
			}
			
			// NPC
			if(selectedElement.equals("npc")){
				NPC selN = (NPC)npcTypes.get(selectedNPC);
				boolean sameNPCRemoved = false;
				boolean isCorrectPlace = false;
				Rectangle rsNPC;
				
				for(int i=0;i<grounds.size();i++){
					// Stelle sicher, dass Item nur auf Ground plazierbar
					Ground gr = (Ground)grounds.get(i);
					Rectangle rg = gr.getBounds();
					
					if(rg.contains(mouseX, mouseY)) isCorrectPlace = true;
				}
				
				// Plaziere oder entferne Item
				if(isCorrectPlace){
					rsNPC = new Rectangle(mouseX,mouseY,selN.getImage().getWidth(null),selN.getImage().getHeight(null));
					
					for(int i=0;i<npcs.size();i++){
						NPC npc = (NPC)npcs.get(i);
						Rectangle rNPC = npc.getBounds();
						
						if(rsNPC.intersects(rNPC)){
							if(selN.getType()==npc.getType()) sameNPCRemoved = true;
							npcs.remove(i);
						}
					}
					
					if(!sameNPCRemoved){
						npcs.add(new NPC(mouseX,mouseY,selN.getType()+48));
					}
				}
			}
			
			// ENEMY
			if(selectedElement.equals("enemy")){
				Enemy selE = (Enemy)enemyTypes.get(selectedEnemy);
				boolean sameEnemyRemoved = false;
				boolean isCorrectPlace = false;
				Rectangle rsEnemy;
				elementX=elementY=0;
				
				for(int i=0;i<grounds.size();i++){
					// Stelle sicher, dass Item nur auf Ground plazierbar
					Ground gr = (Ground)grounds.get(i);
					Rectangle rg = gr.getBounds();
					
								
					if(rg.contains(mouseX, mouseY)){
						 isCorrectPlace = true;
						 elementX=(int)rg.getX();
						 elementY=(int)rg.getY();
					}
				}
							
				// Plaziere oder entferne Item
				if(isCorrectPlace){			
					rsEnemy = new Rectangle(elementX,elementY,selE.getImage().getWidth(null),selE.getImage().getHeight(null));
					for(int i=0;i<enemys.size();i++){
						Enemy en = (Enemy)enemys.get(i);
						Rectangle ren = en.getBounds();
									
						if(rsEnemy.intersects(ren)){
							if(selE.getType()==en.getType()) sameEnemyRemoved = true;
							enemys.remove(i);
						}
					}
								
					if(!sameEnemyRemoved){
						enemys.add(new Enemy(elementX,elementY,selE.getType()+48));
					}
				}
			}
			
			changeData();
			
			
		}
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		
	}
	
	
	// Action Listener
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	
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
				 if(checkFinal.isSelected()) changeFinalData();
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
			else if(action.equals("selFinal")){
				 if(checkFinal.isSelected()){
					 finalIsSelected = true;
					 changeFinalData();
				 }
				 else{
					 finalIsSelected = false;
					 finalEnemy = null;
					 endBossLocation="";
					 endBossRoom=-1;
				 }
			}
			else if(action.equals("setFinal")){
				 if(checkFinal.isSelected()) changeFinalData();
			}
				
			else if(action.equals("setPlayer")){
				changePlayerPos();
			}
			else if(action.equals("addRoom")){
				addRoom();
			}
			else if(action.equals("removeRoom")){
				removeRoom();
			}
			else if(action.equals("openLevelInfos")){
				new InfoFrame("level");
			}
			else if(action.equals("openRaumDialog")){
				new InfoFrame("dialog");
			}
			
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
	
	// Zusätzlicher Frame für Infos
	/**
	 * Fenster mit Texte des Levels
	 *
	 */
	private class InfoFrame extends JFrame implements ActionListener{
		// Konstruktor
		
		JPanel infoPanel;
		JTextArea text;
		JTextField title;
		JButton OK;
		boolean isDialog;
		
		/**
		 * Erstellt neues Fenster in dem entweder Leveltexte oder NPC Dialoge bearbeitet werden k&ouml;nnen
		 * @param command diaog f&uuml;r Dialogfenster, sonst. f&uuml;r Levelinfos
		 */
		private InfoFrame(String command){
			
			isDialog = false;
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(800, 400);
			setLocationRelativeTo(null); // Fenster in Mitte des Bildschirms setzen
			setResizable(false);
			
			infoPanel = new JPanel();
			infoPanel.setLayout(null);
			infoPanel.setBackground(backgroundColor);
			
			OK = new JButton("OK");
			OK.setBounds(650,120,90,30);
			OK.setActionCommand("OK");
			OK.addActionListener(this);
			OK.setVisible(true);
			
			title = new JTextField();
			title.setBounds(20,30,600,30);
			
			text = new JTextArea();
			text.setBounds(20,120,600,200);
			text.setVisible(true);
			
			if(command.equals("dialog")){
				// Dialoginfobox
				isDialog = true;
				setTitle("Dialog in Raum "+visibleRoom);
				text.setText(dialog[visibleRoom]);
			}
			else{
				// Levelinfobox
				setTitle("Levelinfos");
				
				title.setText(levelName);
				title.setVisible(true);
				infoPanel.add(title);
				
				text.setText(intro);
			}
			
			infoPanel.add(text);
			infoPanel.add(OK);
			add(infoPanel);
			setVisible(true);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if(action.equals("OK")){
				if(isDialog){
					dialog[visibleRoom]=text.getText();
					
				}
				else{
					intro = text.getText();
					levelName = title.getText();
				}
				this.setVisible(false);
				this.dispose();
			}
			
		}
		
		
	}
}
