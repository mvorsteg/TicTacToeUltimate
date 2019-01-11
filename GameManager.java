package ttt;

/*Author Matthew Vorsteg
 *This class handles all graphics and button interfaces for the game
 *as well as managing player data
 */

import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFileChooser;
import javafx.application.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameManager extends Application{
	
	private BasicLinkedList<Profile> profiles;
	private Clip clip;
	private Board board;
	private Profile[] players;
	private BufferedImage[][] graphics;
	private BufferedImage[] symbols;
	private BufferedImage grid;
	private Block currBlock;
	private int turn;
	private int[] coords, exp;
	private boolean choosingBlock;
	private boolean selected;
	private Button[][] leftGrid, rightGrid;
	private String path, musicOn, newName;
	private Stage stage;
	private int symb, freeBlocks;
	
	public GameManager() {
		board = new Board();
		profiles = new BasicLinkedList<Profile>();
		players = new Profile[2];
		symbols = new BufferedImage[5];
		graphics = new BufferedImage[3][3];
		coords = new int[2];
		exp = new int[2];
		leftGrid = new Button[3][3];
		rightGrid = new Button[3][3];
		choosingBlock = true;
		stage = null;
		path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
		musicOn = "On";
		newName = null;
		symb= 0;
		freeBlocks = 9;
		try {
			grid =  ImageIO.read(getClass().getResourceAsStream("/ttt/Grid.png"));
			symbols[0] = ImageIO.read(getClass().getResourceAsStream("/ttt/Empty.png"));
			symbols[1] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol1.png"));
			symbols[2] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol2.png"));
			symbols[3] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol1B.png"));
			symbols[4] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol2B.png"));
			clip = AudioSystem.getClip();
			BufferedInputStream stream = new BufferedInputStream(getClass().getResourceAsStream("/ttt/ttt.wav"));
			clip.open(AudioSystem.getAudioInputStream(stream));
			//System.out.println(getClass().getResource("ttt.wav").getPath());
			for (int r=0; r<3; r++) {
				for (int c=0; c<3; c++) {
					graphics[r][c] = ImageIO.read(getClass().getResourceAsStream("/ttt/Grid.png"));
				}
			}
		}catch (Exception e) {
			grid =  null;
			symbols[0] = null;
			symbols[1] = null;
			symbols[2] = null;
			symbols[3] = null;
			symbols[4] = null;
			e.printStackTrace();
		}
		
		//symbols[3].createGraphics().drawImage(symbols[1], 0, 0, 160, 160, null);
		//symbols[4].createGraphics().drawImage(symbols[2], 0, 0, 160, 160, null);
		
	}
	
	//simply decides if player 1 or 2 will go first
	public int coinToss() {
		Random rand = new Random();
		return 1 + rand.nextInt(2);
	}
	
	//the player will make a move on the coords given
	public void makeMove(int player, int x, int y) {
		exp[player-1] += 1;
		BufferedImage g = graphics[currBlock.getX()][currBlock.getY()];
		g.getGraphics().drawImage(symbols[turn], 55*x, 55*y, null);
		leftGrid[currBlock.getX()][currBlock.getY()].setGraphic(new ImageView(SwingFXUtils.toFXImage(g, null)));
		
		if (currBlock.setCell(player, players[turn-1].getSymbol(), x, y)) {
			//there is a claim to the block
			currBlock.setOwner(player, players[turn-1].getSymbol());
			freeBlocks--;
			leftGrid[currBlock.getX()][currBlock.getY()].setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[turn+2], null)));
			exp[player-1] += 5;
			//if (player == 1)
			if (board.check(player)) {
				exp[player-1] += 25;
				gameOver(player);
			}else if (freeBlocks == 0)
				gameOver(-1);
		}
		
		nextTurn();
		
		//forces next player to the correct block if its not owned
		if (board.getBlock(x, y).isEmpty()) {
			loadBlock(x,y);
			
		}
		else {
			choosingBlock = true;
			leftGrid[currBlock.getX()][currBlock.getY()].setStyle(null);
			if (!(currBlock.isEmpty())){
				currBlock = null;
				for(int r=0; r<3; r++) {
					for(int c=0; c<3; c++) {
						rightGrid[r][c].setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[0], null)));
					}
				}
			}
		}
	}
	
	//loads block (x,y) of the board into play on the right grid
	public void loadBlock(int x, int y) {
		if (currBlock != null) {
			leftGrid[currBlock.getX()][currBlock.getY()].setStyle(null);
		}
		currBlock = board.getBlock(x, y);

		
		for (int r=0; r<3; r++) {
			for (int c=0; c<3; c++) {
				
				int own = board.getBlock(x, y).getCell(r, c).getOwner();
				rightGrid[r][c].setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[own], null)));
			}
		}
		leftGrid[x][y].setStyle("-fx-background-color: green;");
		
	}
	
	//sets a cell on the right grid to be empty
	private void removeSymbol(int x, int y) {
		rightGrid[x][y].setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[0], null)));
	}
	
	//switches to other player's turn
	private int nextTurn() {
		if (turn == 1)
			turn = 2;
		else
			turn = 1;
		return turn;
	}
	
	//changes all non-transparent pixels of an image to the desired color
	private void colorChange(BufferedImage image, Color color) {
		
    	for (int x=0; x<image.getWidth(); x++) {
    		for(int y=0; y<image.getHeight(); y++) {
    			//System.out.println(image.getRGB(x, y));
    			if (image.getRGB(x, y) < 0)
    				image.setRGB(x, y, color.getRGB());
    		}
    	}
    }
	
	//gives exp to players, brings up gameOver menu
	public void gameOver(int player) {
		boolean[] levelUp = new boolean[2];
		levelUp[0] = players[0].addExp(exp[0]);
		levelUp[1] = players[1].addExp(exp[1]);
		if (player >= 0) {
			players[player-1].end(true);
			players[player == 1 ? 0 : 1].end(false);
		}
		reset();
		profiles.addToFront(players[1]);
		profiles.addToFront(players[0]);
		//Profile.numProfiles++;
		try {
			storeProfiles();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        final Stage popup = new Stage();
        
        Button close = new Button("Return to menu");
        close.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
            	try {
            		for (int i=1; i<5; i++) {
            			symbols[i] = null;
            		}
					loadMenu();
				} catch (Exception e) {
					e.printStackTrace();
				}
                popup.close();
            }
        });
        Button rematch = new Button("Rematch!");
        rematch.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
            	try {
					gameStart();
				} catch (Exception e) {
					e.printStackTrace();
				}
                popup.close();
            }
        });
        VBox vbox = new VBox();
        vbox.setLayoutX(20);
        vbox.setLayoutY(20);
        Label text = new Label();
        String str;
        if (player >= 0)
        	str = players[player-1].getName()+" has won the game!";
        else
        	str = "We have a tie!";
        for (int i=0; i<2; i++) {
        	if (levelUp[i]) 
        		str += "\n"+players[i].getName()+" has reached level "+players[i].getLevel()+"!";
        }
        text.setText(str);
        vbox.getChildren().add(text);
        vbox.getChildren().add(rematch);
        vbox.getChildren().add(close);
        Scene popupScene = new Scene(new Group());
        ((Group)popupScene.getRoot()).getChildren().add(vbox);
        
        popup.initModality(Modality.WINDOW_MODAL);
		popup.initOwner(stage);
        popup.setScene(popupScene);
        popup.showAndWait();
	}
	
	//reads all profiles from file
	public void loadProfiles() throws Exception{
		profiles = new BasicLinkedList<Profile>();
		File file;
		try {
			file = new File(path+"\\tttUltimateSaveData.txt");
			BufferedReader scan = new BufferedReader(new FileReader(file));

			int numProfiles = Integer.parseInt(scan.readLine());
			for (int i=0; i<numProfiles; i++) {
				Profile profile = new Profile(scan.readLine());
				profile.setLevel(Integer.parseInt(scan.readLine()));
				profile.setExp(Integer.parseInt(scan.readLine()));
				profile.setReqExp(Integer.parseInt(scan.readLine()));
				profile.setWins(Integer.parseInt(scan.readLine()));
				profile.setLosses(Integer.parseInt(scan.readLine()));
				profiles.add(profile);
			}
			scan.close();
		}catch (FileNotFoundException e) {
			filePopup();
		}
		profiles.add(new Profile("Add New"));
	}

	//writes profile data to the file
	public void storeProfiles() {
		try {
		File file = new File(path+"\\tttUltimateSaveData.txt");
		PrintWriter writer = new PrintWriter(new FileWriter(file, false));
		writer.println(profiles.size-1);
		for (Profile profile: profiles) {
			if (profile.getName().compareTo("Add New") != 0) {
				writer.println(profile.getName());
				writer.println(profile.getLevel());
				writer.println(profile.getExp());
				writer.println(profile.getReqExp());
				writer.println(profile.getWins());
				writer.println(profile.getLosses());
			}
		}
		writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//gets relevant player data and returns it as a string
	private String displayPlayerInfo(Profile p) {
		String str = "";
		if (p.getName().equals("Add New"))
			str += "Add a new player\nto the roster";
		else 
			str += p.getName() + "\nLvl: " + p.getLevel() + "\nWins: "+ p.getWins() + "\nLosses: " + p.getLosses();
		return str;
	}
	
	//brings up the menu where players choose their profile
	public void loadMenu() throws Exception{
		loadProfiles();
		turn = 0;
		players[0] = null;
		players[1] = null;
		BufferedImage redX, arrow, reverseArrow;
		//gets resources for the menu
		try {
			redX = ImageIO.read(getClass().getResourceAsStream("/ttt/RedX.png"));
    		arrow = ImageIO.read(getClass().getResourceAsStream("/ttt/Arrow.png"));
    		reverseArrow =ImageIO.read(getClass().getResourceAsStream("/ttt/ReverseArrow.png"));
		} catch(Exception e) {
			reverseArrow = null;
			redX = null;
			arrow = null;
		}
		
		GridPane pane = new GridPane();
		pane.setStyle("-fx-background-color: white;");
        
        Label p1 = new Label("P1");
        p1.setStyle("-fx-font: 36 oblique;");
        p1.setMinSize(75, 75);
        p1.setMaxSize(75, 75);
        
        Label p1Name = new Label();
        p1Name.setStyle("-fx-font: 16 oblique;");
        p1Name.setMinSize(120, 75);
        p1Name.setMaxSize(120, 75);
        
        Label p2 = new Label("P2");
        p2.setStyle("-fx-font: 36 oblique;");
        p2.setMinSize(75, 75);
        p2.setMaxSize(75, 75);
        Label p2Name = new Label();
        
        p2Name.setStyle("-fx-font: 16 oblique;");
        p2Name.setMinSize(120, 75);
        p2Name.setMaxSize(120, 75);

        //displays the player data
        Label info = new Label(displayPlayerInfo(profiles.getFirst()));
        info.setStyle("-fx-font: 36 oblique;");
        
        Button startButton = new Button("Start");
        startButton.setStyle("-fx-font: 36 oblique;");
        startButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent arg0) { 
            	if (players[0] != null && players[1] != null) {
            		try{
            			customize();
            		}catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
            }
        });
        
        Button center = new Button();
		center.setMaxWidth(400);
		center.setMinWidth(400);
        center.setStyle("-fx-font: 72 oblique;");
        if (profiles.getSize() != 0)
        	center.setText(profiles.getFirst().getName());
        center.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent arg0) { 
            	boolean exited = false;
            	//if we still need players
            	if (players[1] == null || players[0] == null) {
            		//need separate case if adding new
	            	if (profiles.getFirst().getName() == "Add New" ) {
	            		popup();
	            		if (newName == null) 
	            			exited = true;
	            		else if (newName != "")
	            			players[turn] = new Profile(newName);
	            		else
	            			players[turn] = new Profile();
	            		
	            	}
	            	//otherwise it is a valid profile
	            	else {
	            		players[turn] = profiles.getFirst();
	            		profiles.remove();
	            		
	            		profiles.head = profiles.head.next;
	                	center.setText(profiles.getFirst().getName());
	                	info.setText(displayPlayerInfo(profiles.getFirst()));
	            		}
	            	//if we exited the new player window correctly
	            	if (exited == false) {
		            	if(turn == 0)
		        			p1Name.setText(players[turn].getName());
		        		else
		        			p2Name.setText(players[turn].getName());
		            	turn += (turn > 1) ? 0 : 1;
	            	}
	            }
            }
        });
        
        Button left = new Button();
		left.setGraphic(new ImageView(SwingFXUtils.toFXImage(reverseArrow, null)));
		left.setMinSize(175, 150);
		left.setMaxSize(175, 150);
        left.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent arg0) { 
            	if (profiles.size > 1) {
	            	profiles.head = profiles.head.prev;
	            	center.setText(profiles.getFirst().getName());
	            	info.setText(displayPlayerInfo(profiles.getFirst()));
	            }
            }
        });
        
        Button right = new Button();
        right.setGraphic(new ImageView(SwingFXUtils.toFXImage(arrow, null)));
        right.setMinSize(175, 150);
		right.setMaxSize(175, 150);
        right.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent arg0) { 
            	if (profiles.size > 1) {
            		profiles.head = profiles.head.next;
            		center.setText(profiles.getFirst().getName());
            		info.setText(displayPlayerInfo(profiles.getFirst()));
            	}
            }
        });
        
        Button p1X = new Button();
        p1X.setGraphic(new ImageView(SwingFXUtils.toFXImage(redX, null)));
        p1X.setMaxSize(75, 75);
        p1X.setMinSize(75, 75);
        p1X.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent arg0) { 
            	if (!(p1Name.getText().equals(""))) {
            		p1Name.setText("");
            		profiles.addToFront(players[0]);
            		players[0] = null;
            		turn = 0;
            	}
            }
        });
        
        Button p2X = new Button();
        p2X.setGraphic(new ImageView(SwingFXUtils.toFXImage(redX, null)));
        p2X.setMaxSize(75, 75);
        p2X.setMinSize(75, 75);
        p2X.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent arg0) { 
            	if (!(p2Name.getText().equals(""))) {
            		p2Name.setText("");
            		profiles.addToFront(players[1]);
            		players[1] = null;
            		
            		turn = (players[0] == null) ? 0 : 1;
            	}
            }
        });
	
    	Scene scene = new Scene(new Group());
    	pane.add(left, 0, 0, 1, 2);
    	pane.add(center, 1, 0, 1, 2);
    	pane.add(right, 2, 0, 1, 2);
    	pane.add(info, 0, 2, 3, 3);
    	pane.add(p1, 3, 0);
    	pane.add(p2, 3, 1);
    	pane.add(p1Name, 4, 0);
    	pane.add(p2Name, 4, 1);
    	pane.add(p1X, 5, 0);
    	pane.add(p2X, 5, 1);
    	pane.add(startButton, 3, 3, 3, 1);
    	((Group)scene.getRoot()).getChildren().add(pane);

    	stage.setScene(scene);
    	stage.centerOnScreen();
    	stage.sizeToScene();
    	stage.show();
		
		
	}

	//popup window if file data not found
	public void filePopup() {
		Label label = new Label("Player save data not found in "+path+".");
		Label label2 = new Label("New save data will be written after a game.");
		label.setStyle("-fx-font: 16 oblique;");
		label2.setStyle("-fx-font: 16 oblique;");
		Stage stage2 = new Stage();
		Scene scene2 = new Scene(new Group());
		Button go = new Button("Ok");
		go.setStyle("-fx-font: 32 oblique;");
		go.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				stage2.close();
			}         	
		});
		VBox v = new VBox(label, label2, go);
		v.setAlignment(Pos.CENTER);
		((Group)scene2.getRoot()).getChildren().add(v);
		stage2.initModality(Modality.WINDOW_MODAL);
		stage2.initOwner(stage);
		stage2.setScene(scene2);
		stage2.showAndWait();
	}
	
	//popup window for creating a new profile
	public void popup() {
		Label prompt = new Label("Enter your name");
		prompt.setStyle("-fx-font: 16 oblique;");
		Stage stage2 = new Stage();
		Scene scene2 = new Scene(new Group());
		TextField textField = new TextField();
		Button go = new Button("Create new profile");
		go.setStyle("-fx-font: 16 oblique;");
		go.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				newName = textField.getText();
				newName.replaceAll(" ", "");
				if (newName.length() < 1)
					newName = "";
				
				stage2.close();
			}         	
		});
		stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				newName = null;
			}
        });
		VBox v = new VBox(prompt, textField, go);
		((Group)scene2.getRoot()).getChildren().add(v);
		
		stage2.initModality(Modality.WINDOW_MODAL);
		stage2.initOwner(stage);
		stage2.setScene(scene2);
		stage2.showAndWait();
	}

	//popup window for changing settings
	public void options() {
		try {
			loadProfiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Label sound = new Label("Music");
		sound.setMinSize(160, 80);
		sound.setStyle("-fx-font: 36 oblique;");
		
		Button soundButton = new Button(musicOn);
		soundButton.setStyle("-fx-font: 36 oblique;");
		soundButton.setMinSize(160, 80);
		soundButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				if (soundButton.getText().compareTo("On") == 0) {
					musicOn = "Off";
					backgroundMusic(false);
				}else {
					musicOn = "On";
					backgroundMusic(true);
				}
				soundButton.setText(musicOn);
			}         	
		});
		
		Label delete = new Label("Delete Profile");
		delete.setMinSize(160, 80);
		delete.setStyle("-fx-font: 36 oblique;");
		MenuButton menu = new MenuButton("Select", null);
		menu.setMinSize(160, 80);
		menu.setStyle("-fx-font: 16 oblique;");
		for (Profile prof: profiles) {
			if (prof.getName().compareTo("Add New") != 0) {
				MenuItem m = new MenuItem(prof.getName());
				m.setStyle("-fx-font: 16 oblique;");
				m.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent arg0) {
						menu.setText(prof.getName());
					}         	
				});
				menu.getItems().add(m);
			}
		}
		//menu.set
		Button deleteButton = new Button("Delete");
		deleteButton.setStyle("-fx-font: 36 oblique;");
		deleteButton.setMinSize(160, 80);
		deleteButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				if (menu.getText().compareTo("Select") != 0) {
					profiles.remove(menu.getText());
					storeProfiles();
					for (int i = 0; i < menu.getItems().size(); i++) {
						if (menu.getItems().get(i).getText().compareTo(menu.getText()) == 0) 
							menu.getItems().remove(i);
					}
				}
			}         	
		});
		
		TextArea credits = new TextArea("Game developed by Matthew Vorsteg\nArt by Matt Vorsteg and Judy Tram, Music by Matt "
				+ "Vorsteg\nSpecial thanks to Matt Graber and Tim Henderson");
		credits.setStyle("-fx-font: 16 oblique;");
		Stage stage2 = new Stage();
		Scene scene2 = new Scene(new Group());
		
		stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				
			}
        });
		HBox h1 = new HBox(sound, soundButton);
		h1.setAlignment(Pos.CENTER);
		HBox h2 = new HBox(delete, menu, deleteButton);
		h2.setAlignment(Pos.CENTER);
		VBox v = new VBox(h1, h2, credits);
		v.setAlignment(Pos.CENTER);
		((Group)scene2.getRoot()).getChildren().add(v);
		stage2.initModality(Modality.WINDOW_MODAL);
		stage2.initOwner(stage);
		stage2.setScene(scene2);
		stage2.showAndWait();
	}
	
	//brings up the menu to select a symbol and color
	public void customize() {
		turn = 0;
		BufferedImage lockTemp;
		try {
			lockTemp = ImageIO.read(getClass().getResourceAsStream("/ttt/Padlock.png"));
		}catch (Exception e) {
			lockTemp = null;
		}
		final BufferedImage lock = lockTemp;
		BufferedImage[][] arr = new BufferedImage[2][50];
		Color[] colors = new Color[] {Color.BLACK, Color.RED, Color.BLUE, Color.PINK, new Color(161,16,183), 
				Color.ORANGE, new Color(84,232,237), new Color(114,25,0), new Color(221,207,46), Color.WHITE};
		int[] levelRequirements = new int[] {1, 1, 2, 4, 5, 6, 7, 9, 10, 12, 
				13, 14, 15, 17, 18, 19, 20, 20, 24, 25,
				26, 28, 30, 31, 32, 35, 38, 40, 40, 40,
				1, 2, 3, 8, 11, 16, 20, 22, 23, 36, 40};
		String[] names = new String[] {"X", "O", "Delta", "Square", "Pentagon", "Hexagon", "Lucky 7", "Null Symbol", 
				"Star", "Tie", "Trumpet", "Present", "Heart", "Solid Heart", "Mr. TP", "Smiley Face", "Pizza", "Bert", "Eugene", "Pig", "Eye", 
				"Ghosty Feller", "Gear" , "Snowman", "Puzzle Piece", "Chess King", "Vorstegasauras", "Mr. Placeholder", "The Filthy Floater", "Namreh"
		};
		Button[] bigButtons = new Button[2];
		GridPane littlePane = new GridPane();
		GridPane pane = new GridPane();
		Label info = new Label(players[turn].getName()+", pick a symbol and color");
		info.setAlignment(Pos.CENTER);
		info.setMinWidth(340);
		
		//adds the player buttons to the pane
		for (int i=0; i<2; i++) {
			final Button bigButton = new Button();
			bigButton.setMinSize(180, 180);
			bigButton.setMaxSize(180, 180);
			
			Label name = new Label();
			name.setText(players[i].getName());
			bigButtons[i] = bigButton;
			littlePane.add(name, 2*i, 0, 1, 1);
			littlePane.add(bigButtons[i], 2*i, 1, 1, 1);
		}
		littlePane.add(info, 1, 1, 1, 1);
		//determines behavior when clicked, to ultimately set the player's symbol to the selection
		bigButtons[0].setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) { 
            	if (bigButtons[0].getGraphic() != null) {
            		turn = 1;
            		info.setText(players[turn].getName()+", pick your symbol and color");
            		try {
            			final int p1Sym = symb;
            			int i=0;
            				for (int r = 0; r < 3; r++) {
            					for (int c = 0; c < 10; c++) {
            						try {
            							arr[0][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol"+(i+1)+".png"));
            							arr[1][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol"+(i+1)+"B.png"));
            						}catch (Exception e) {
            							arr[0][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/SymbolPlaceHolder.png"));
            							arr[1][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/SymbolPlaceHolderB.png"));
            						}
            						Button button = new Button();
            						button.setMinSize(70, 70);
            						button.setMaxSize(70, 70);
            						final int i2 = i;
            						if (players[turn].getLevel() >= levelRequirements[i]) {
            							
            							button.setGraphic(new ImageView(SwingFXUtils.toFXImage(arr[0][i], null)));
            							
            							button.setOnAction(new EventHandler<ActionEvent>(){
            					            public void handle(ActionEvent arg0) { 
            					            	if (p1Sym != i2) {
	            					            	symb = i2;
	            					            	info.setText(players[turn].getName()+" is "+names[i2]);
	            					            	bigButtons[turn].setGraphic(new ImageView(SwingFXUtils.toFXImage(arr[1][i2], null)));
	            					            	symbols[turn+1] = arr[0][i2];
	            					            	symbols[turn+3] = arr[1][i2];
            					            	}else
            					            		info.setText(players[turn-1].getName()+" is already "+names[i2]);
            					            }
            					        });
            						}else {
            							button.setGraphic(new ImageView(SwingFXUtils.toFXImage(lock, null)));
            							button.setOnAction(new EventHandler<ActionEvent>(){
            					            public void handle(ActionEvent arg0) { 
            					            	info.setText("This symbol unlocks at level "+levelRequirements[i2]);
            					            }
            					        });
            						}
            						pane.add(button, c, r+2);
            						i++;
            					}
            				}
            				i=0;
            				for (int r = 10; r < 12; r++) {
            					for (int c = 0; c < 10; c+=2) {
            						
            						Button button = new Button();
            						button.setMinSize(140, 70);
            						button.setMaxSize(140, 70);
            						
            						final int i2 = i;
            						if (players[turn].getLevel() >= levelRequirements[i+30]) {
            							button.setStyle("-fx-background-color: rgb("+colors[i].getRed()+","+colors[i].getGreen()+","+colors[i].getBlue()+");");
            							button.setOnAction(new EventHandler<ActionEvent>(){
            					            public void handle(ActionEvent arg0) { 
            					            	if (symb < 28) {
	            					            	colorChange(symbols[turn+1], colors[i2]);
	            					            	colorChange(symbols[turn+3], colors[i2]);
	            					            	bigButtons[turn].setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[turn+3], null)));
	            					            }
            					            	else
            					            		info.setText(names[symb]+" can't be recolored");
            					            }
            					        });
            						}else {
            							button.setGraphic(new ImageView(SwingFXUtils.toFXImage(lock, null)));
            							button.setOnAction(new EventHandler<ActionEvent>(){
            					            public void handle(ActionEvent arg0) { 
            					            	info.setText("This color unlocks at level "+levelRequirements[i2+30]);
            					            }
            					        });
            						}
            						pane.add(button, c, r+2, 2, 1);
            						i++;
            					}
            				
            			}
            		}catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
            }
        });
		bigButtons[1].setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) { 
            	if (bigButtons[1].getGraphic() != null) {
            		try {
						gameStart();
					} catch (Exception e) {
						e.printStackTrace();
					}
            	}
            }
        });
		
		
		try {
			
			int i=turn;
				for (int r = 0; r < 3; r++) {
					for (int c = 0; c < 10; c++) {
						try {
							arr[0][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol"+(i+1)+".png"));
							arr[1][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol"+(i+1)+"B.png"));
						}catch (Exception e) {
							arr[0][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/SymbolPlaceHolder.png"));
							arr[1][i] = ImageIO.read(getClass().getResourceAsStream("/ttt/SymbolPlaceHolderB.png"));
						}
						Button button = new Button();
						button.setMinSize(70, 70);
						button.setMaxSize(70, 70);
						final int i2 = i;
						if (players[turn].getLevel() >= levelRequirements[i]) {
							button.setGraphic(new ImageView(SwingFXUtils.toFXImage(arr[0][i], null)));
							
							button.setOnAction(new EventHandler<ActionEvent>(){
					            public void handle(ActionEvent arg0) { 
					            	symb = i2;
					            	info.setText(players[turn].getName()+" is "+names[i2]);
					            	bigButtons[turn].setGraphic(new ImageView(SwingFXUtils.toFXImage(arr[1][i2], null)));
					            	symbols[turn+1] = arr[0][i2];
					            	symbols[turn+3] = arr[1][i2];
					            }
					        });
						}else {
							button.setGraphic(new ImageView(SwingFXUtils.toFXImage(lock, null)));
							button.setOnAction(new EventHandler<ActionEvent>(){
					            public void handle(ActionEvent arg0) { 
					            	info.setText("This symbol unlocks at level "+levelRequirements[i2]);
					            	
					            }
					        });
						}
						pane.add(button, c, r+2);
						i++;
					}
				}
				i=0;
				for (int r = 10; r < 12; r++) {
					for (int c = 0; c < 10; c+=2) {
						
						Button button = new Button();
						button.setMinSize(140, 70);
						button.setMaxSize(140, 70);
						
						final int i2 = i;
						if (players[turn].getLevel() >= levelRequirements[i+30]) {
							button.setStyle("-fx-background-color: rgb("+colors[i].getRed()+","+colors[i].getGreen()+","+colors[i].getBlue()+");");
							button.setOnAction(new EventHandler<ActionEvent>(){
					            public void handle(ActionEvent arg0) { 
					            	if (symb < 28) {
    					            	colorChange(symbols[turn+1], colors[i2]);
    					            	colorChange(symbols[turn+3], colors[i2]);
    					            	bigButtons[turn].setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[turn+3], null)));
    					            }
					            	else
					            		info.setText(names[symb]+" can't be recolored");
					            }
					        });
						}else {
							button.setGraphic(new ImageView(SwingFXUtils.toFXImage(lock, null)));
							button.setOnAction(new EventHandler<ActionEvent>(){
					            public void handle(ActionEvent arg0) { 
					            	info.setText("This color unlocks at level "+levelRequirements[i2+30]);
					            }
					        });
						}
						pane.add(button, c, r+2, 2, 1);
						i++;
					}
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		VBox v = new VBox(littlePane, pane);
		
		Scene scene = new Scene(new Group());
		((Group)scene.getRoot()).getChildren().add(v);
		stage.setScene(scene);
	}
	
	//method required by Application
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.setTitle("Tic Tac Toe Ultimate: Platinum Edition");
        stage.setWidth(1400);
        stage.setHeight(810);
		mainMenu();
		
	}
	
	//begins a game of tic tac toe
	public void gameStart() throws Exception {
		
		turn = coinToss();
		
		Image fxGrid = SwingFXUtils.toFXImage(grid, null);
		Image fxEmpty = SwingFXUtils.toFXImage(symbols[0], null);
		
		//data for the GridPane
		int verSpaceBetweenNodes = 8, horSpaceBetweenNodes = 8;
		int paneBorderTop = 8, paneBorderRight = 8;
		int paneBorderBottom = 8, paneBorderLeft = 8;
		
		
		//create the GridPane
		GridPane pane = new GridPane();
		pane.setStyle("-fx-background-color: black;");
		pane.setHgap(horSpaceBetweenNodes);
		pane.setVgap(verSpaceBetweenNodes);
		pane.setPadding(new Insets(paneBorderTop, paneBorderRight, 
					    paneBorderBottom, paneBorderLeft));
		
		 Label p1 = new Label();
		 p1.setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[1], null)));
		 p1.setText(players[0].getName()+"\nLvl "+players[0].getLevel());
		 p1.setStyle("-fx-font: 20 oblique; -fx-background-color: #e7e7e7; ");
		 p1.setMinSize(180, 70);
		 if (turn == 1) {
			 p1.setStyle("-fx-font: 20 oblique; -fx-background-color: GREEN; ");
		 }
		 pane.add(p1, 0, 0, 3, 1);
		 
		 Label p2 = new Label();
		 p2.setContentDisplay(ContentDisplay.RIGHT);
		 p2.setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[2], null)));
		 p2.setText(players[1].getName()+"\nLvl "+players[1].getLevel());
		 p2.setStyle("-fx-font: 20 oblique; -fx-background-color: #e7e7e7; ");
		 p2.setAlignment(Pos.BASELINE_RIGHT);
		 p2.setMinSize(180, 70);
		 if (turn == 2) {
			 p2.setStyle("-fx-font: 20 oblique; -fx-background-color: GREEN; ");
		 }
		 pane.add(p2, 5, 0);
		
		
		final Button button3 = new Button();
		button3.setText("Press when ready");
		button3.setVisible(false);
		button3.setMinSize(360, 70);
		button3.setStyle("-fx-font: 36 oblique; ");
		button3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	if (selected) {
            		button3.setVisible(false);
            		selected = false;
            		makeMove(turn, coords[0], coords[1]);
            		if (turn == 1) {
           				p1.setStyle("-fx-font: 20 oblique; -fx-background-color: GREEN; ");
           				p2.setStyle("-fx-font: 20 oblique; -fx-background-color: #e7e7e7; ");
            		}
            		else {
            			p1.setStyle("-fx-font: 20 oblique; -fx-background-color: #e7e7e7; ");
            			p2.setStyle("-fx-font: 20 oblique; -fx-background-color: GREEN; ");
            		}
            	}
            }
        });
		pane.add(button3, 2, 0, 2, 1);
		
		for (int r=0; r<3; r++) {
			for (int c=0; c<3; c++) {
				final int x=r; 
				final int y=c;
				final Button button1 = new Button();
				button1.setMinSize(180, 170);
				button1.setGraphic(new ImageView(fxGrid));
				button1.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                    	if (choosingBlock && board.getBlock(x, y).isEmpty()) {
                    		
                    		choosingBlock = false;
                    		loadBlock(x,y);
                    	}
                    }
                });
				final Button button2 = new Button();
				button2.setGraphic(new ImageView(fxEmpty));
				button2.setMinSize(180, 170);
				button2.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                    	if (currBlock != null && currBlock.getCell(x, y).isEmpty()){
                    		if (selected) 
                    			removeSymbol(coords[0], coords[1]);
                    		else {
                    			selected = true;
                    			button3.setVisible(true);
                    		}
                    		coords[0] = x;
                    		coords[1] = y;
                    		button2.setGraphic(new ImageView(SwingFXUtils.toFXImage(symbols[turn], null)));

                    	}
                    }
                });
	
                leftGrid[r][c] = button1;
                rightGrid[r][c] = button2;
                pane.add(leftGrid[r][c], r, c+2);
                pane.add(rightGrid[r][c], r+3, c+2);
                
			}
		}
		
		
		
		
		//create the scene
		Scene scene = new Scene(new Group());
        

        //create the vbox
        VBox vbox = new VBox();
        vbox.setLayoutX(20);
        vbox.setLayoutY(20);
		
		vbox.getChildren().add(pane);
        vbox.setSpacing(10);
        ((Group)scene.getRoot()).getChildren().add(vbox);

        stage.setScene(scene);
        stage.show();
		
	}

	//resets the board for a new game
	public void reset() {
		board = new Board();
		exp[0] = 0;
		exp[1] = 0;
		choosingBlock = true;
		symb = 0;
		freeBlocks = 9;
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				try {
					graphics[r][c] = ImageIO.read(getClass().getResourceAsStream("/ttt/Grid.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//brings up the title screen menu
	public void mainMenu() throws Exception{
		backgroundMusic(true);
		ImageView title = new ImageView(SwingFXUtils.toFXImage(ImageIO.read(getClass().getResourceAsStream("/ttt/Main.png")), null));
		VBox v = new VBox();
		Scene scene = new Scene(new Group());
		((Group)scene.getRoot()).getChildren().add(v);
		
		Button start = new Button("Start");
		start.setMinSize(200, 75);
		start.setStyle("-fx-font: 36 oblique;");
		start.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) { 
            	try {
					loadMenu();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
		
		Button options = new Button();
		try {
			options.setGraphic(new ImageView(SwingFXUtils.toFXImage(ImageIO.read(getClass().getResourceAsStream("/ttt/Symbol23.png")), null)));
		}catch(Exception e) {
			e.printStackTrace();
		}
		options.setMinSize(75,75);
		options.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) { 
            	options();
            }
        });
		HBox h = new HBox();
		h.getChildren().add(start);
		h.getChildren().add(options);
		h.setAlignment(Pos.CENTER);
		v.setAlignment(Pos.CENTER);
		h.setStyle("-fx-background-color: black");
		v.setStyle("-fx-background-color: black");
		v.getChildren().add(title);
		v.getChildren().add(h);
		
        stage.setScene(scene);
        stage.show();
	}
	
	//enables/disables the background music
	public void backgroundMusic(boolean signal) {
		if (signal) {
			clip.setFramePosition(0);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}else
			clip.stop();
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
