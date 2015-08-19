
/**
 * aplikasi ini ditulis oleh Mohammad Fajar berdasarkan contoh-contoh yang diberikan
 * untuk library javaFX (http://oracle.com)
 * Barang siapa yang ingin mengambil keuntungan 
 * dari source code aplikasi ini, wajib memberikan atribusi kepada 
 * penulis (fajar.kasimbar@gmail.com)
 * Dan jika keuntungan tersebut berupa keuntungan material (uang dan semisalnya)
 * maka harus memperoleh izin tertulis dari penulis. 
 * 
 */


package fjr.com.test.music.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;


import javafx.scene.media.AudioSpectrumListener;
import javafx.util.Duration;


public class CubicEqualizer extends Application{

	HashMap<Cube, Integer> map = new HashMap<Cube, Integer>(); 
	ArrayList<Cube>  listOfCube = new ArrayList<>(); 
	
	Rectangle fillRectangle; 
	
	
	MediaPlayer player; 
	Media media; 
	
	
	Random r = new Random(); 
	
	Color cc[] =  new Color[]{Color.RED, Color.BLACK, Color.MAGENTA, 
			Color.MEDIUMAQUAMARINE, Color.PALEGOLDENROD, Color.WHEAT, 
			Color.PALEGREEN, Color.WHEAT, Color.ALICEBLUE, Color.KHAKI, Color.MEDIUMBLUE, 
			Color.MINTCREAM, Color.MEDIUMPURPLE, Color.MEDIUMSLATEBLUE, Color.AQUA, Color.CADETBLUE, Color.CHOCOLATE }; 
	
	
	String uri; 
	
	
	Duration durasi; 
	
	
	boolean PLAY_AUDIO = true; 
	
	AudioSpectrumListener audioSpectrumListener;
	
	
	double INIT_HEIGHT  = 0.0; 
	
	ListView<Text> listOfViewMusic; 
	
	
	Text currentTrack; 
	
	
	Slider timeSlider; 
	
	boolean pauseState = false; 
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Group g1 = new Group();

		Group root = new Group();

		primaryStage.setScene(new Scene(root, 400, 400));
		primaryStage.show();

		creatBall(g1);
		g1.getTransforms().addAll(new Translate(30, 130));

		
		VBox vbox1 = new VBox();
		vbox1.setTranslateX(10);
		vbox1.setTranslateY(10);
		vbox1.setSpacing(10);

		final Slider slider1 = new Slider();
		slider1.setOrientation(Orientation.HORIZONTAL);
		slider1.setPrefWidth(100);
		slider1.setMin(0.);
		slider1.setMax(1.);
		slider1.setValue(INIT_HEIGHT);

		slider1.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				for (int i = 0; i < listOfCube.size(); i++) {
					Cube c = listOfCube.get(i);
					Scale sc = c.scale;
					sc.setY(slider1.getValue());
					c.koreksiHeight();
				}
			}
		});

		
		Button buttonPlay = new Button("PLAY"); 
		buttonPlay.setPrefWidth(100);
		buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(listOfMusic != null && listOfMusic.size() > 0){
					if(currentTrack != null){
						String path = currentTrack.getId();   
						uri = new File(path).toURI().toString(); 
						startAudio();

						if(pauseState){
							pauseState = false; 
						}
					}
				}
			}
		});
		
		vbox1.getChildren().add(buttonPlay); 
		
		Button buttonPause = new Button("PAUSE"); 
		buttonPause.setPrefWidth(100);
		buttonPause.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				if(player != null){
					player.pause();
					pauseState = true; 
				}
			}
		});
		
		
		
		vbox1.getChildren().add(buttonPause); 
		
		
		
		Rectangle r = new Rectangle();
		r.setWidth(30);
		r.setHeight(30);
		r.setFill(Color.BLACK);

		fillRectangle = r;

		
		HBox hbox1 = new HBox();
		hbox1.setSpacing(10);
		
		ListView<Text> listView1 = new ListView<>(); 
		listView1.setPrefSize(200, 100);

		listOfViewMusic = listView1;
		
		VBox vbox2 = new VBox(); 
		vbox2.setSpacing(10);
		vbox2.setTranslateX(10);
		vbox2.setTranslateY(10); 
		
		timeSlider = new Slider(); 
		timeSlider.setOrientation(Orientation.HORIZONTAL); 
		timeSlider.setPrefWidth(200); 
		
		timeSlider.valueProperty().addListener(new InvalidationListener() {			
			@Override
			public void invalidated(Observable arg0) {
				if( timeSlider.isValueChanging()){
					if(player != null){
						Duration dd = player.getMedia().getDuration(); 
						double currentTime = dd.toMillis() * (timeSlider.getValue() / timeSlider.getMax());
						player.seek(new Duration(currentTime)); 
						updateValue();
					}
					
				}
			}
		});
		
		
		vbox2.getChildren().addAll(listOfViewMusic , timeSlider); 
		hbox1.getChildren().addAll(vbox1, vbox2); 

		
		StackPane pane = new StackPane();
		
		AnchorPane pp = new AnchorPane();
		pp.getChildren().add(g1); 
	
		pane.getChildren().addAll(pp, hbox1); 
		root.getChildren().addAll(pane);

		
		uri = CubicEqualizer.class.getResource("test.mp3").toExternalForm();

		audioSpectrumListener = new AudioSpectrumListener() {
			@Override
			public void spectrumDataUpdate(double timestamp, double duration,
					float[] magnitudes, float[] phases) {
				
				for(int i = 0; i< listOfCube.size(); i++){
					Cube cc = listOfCube.get(i);
					cc.scale.setY((magnitudes[i] + 60)/ 18.0);
					cc.koreksiHeight();
				}
				
			}
		};
		setListViewProperty(listView1);
	}
	
	
	List<Text> listOfMusic = new ArrayList<>(); 
	boolean dragNDropState = false;
	
	ArrayList<Text> listOfTrackToDelete = new ArrayList<>(); 
	
	private void setListViewProperty(final ListView<Text> node){
		
		node.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db  = event.getDragboard(); 
				if(event.getGestureSource() != node){
					if(db.hasFiles()){
						dragNDropState = true; 
						event.acceptTransferModes(TransferMode.LINK); 
					}
				}
				event.consume();
			}
		});
		
		node.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				dragNDropState = false; 
				event.consume();
			}
		});
		
		
		node.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard(); 
				if(dragNDropState){
					
					A: for(File f: db.getFiles()){
						String name = f.getName(); 
						String path = f.getAbsolutePath(); 
						String[] split = path.split("\\."); 
						if(split.length == 2){
							String extension = split[1];
							if(!extension.equalsIgnoreCase("mp3")){
								continue;
							}
						}
						
						final Text t = new Text(); 
						t.setText(name);
						t.setId(path);
					
						// cek jika track sudah ada di dalam list, maka lompati 
						for(int i=0; i< listOfMusic.size(); i++){
							if(t.getId().equals(listOfMusic.get(i).getId())){
								continue A; 
							}
						}
								
						listOfMusic.add(t); 
						
						t.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent mouseEvent) {
								if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
									if(mouseEvent.getClickCount() == 2){
										
										if(pauseState){
											pauseState = false;
										}
										
										String filePath = t.getId(); 
										uri = new File(filePath).toURI().toString();
										startAudio();
										currentTrack = t;
									}
								}
							}
						});
					}
					
					listOfViewMusic.setItems(FXCollections.observableArrayList(listOfMusic)); 
					event.setDropCompleted(true); 
					
				}
				event.consume();
			}
		});
		
		
		// hapus item dalam listview 
		node.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		node.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				ObservableList<Text> selected_item = 
						node.getSelectionModel().getSelectedItems();
				listOfTrackToDelete.clear();
				for (Text t : selected_item) {
					listOfTrackToDelete.add(t);
				}
			}
		});
		
		node.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.DELETE)){
					if(listOfTrackToDelete.size() >0 && listOfMusic.size() > 0){
						if(listOfMusic.size() >= listOfTrackToDelete.size()){
							listOfMusic.removeAll(listOfTrackToDelete); 
							listOfViewMusic.setItems(FXCollections.observableArrayList(listOfMusic)); 
						}
					}
				}else if(event.getCode() == KeyCode.A && event.isControlDown() ){
//					System.out.println("selek semua file"); 
					ObservableList<Text> selected_item = 
							node.getSelectionModel().getSelectedItems();
					listOfTrackToDelete.clear();
					for (Text t : selected_item) {
						listOfTrackToDelete.add(t);
					}
					System.out.println(listOfTrackToDelete.size()); 
				}
			}
		});
		
		
		

		
		
		
	}

	  private void startAudio() {
	        if (PLAY_AUDIO) {
	           if(!pauseState){
	        	   getMediaPlayer().play();
	           }else{
	        	   if(player != null){
	        		   player.play();
	        	   }
	           }
	        }
	    }
	
	  
	private void updateValue() {
		
		if (player != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					double total = player.getMedia().getDuration().toMillis();
					double now = player.getCurrentTime().toMillis();
					double rasio = (now / total) * 100;
					timeSlider.setValue(rasio);
				}
			});

		}
	}	  
	
	private MediaPlayer getMediaPlayer(){
		
		media = new Media(uri); 
		if(player == null){
			player = new MediaPlayer(media); 
		}else{
			player.stop();
			player = new MediaPlayer(media); 
		}
		
		player.setAudioSpectrumListener(audioSpectrumListener);
		
		player.currentTimeProperty().addListener(
				new ChangeListener<Duration>() {
					@Override
					public void changed(
							ObservableValue<? extends Duration> observable, 
							Duration oldValue, 
							Duration newValue) {
						updateValue();
					}
				});

		
		// mainkan lagu berikutnya jika lagu ini sudah selesai.... 
		player.setOnEndOfMedia(new Runnable() {
			
			@Override
			public void run() {
				if(listOfMusic == null){
					return; 
				}
				if(listOfMusic.size() <= 0){
					return; 
				}
				Text tt; 
				int index  = listOfMusic.indexOf(currentTrack);
				int index_plus_satu = index + 1; 

				
				if(index_plus_satu < listOfMusic.size()){
					tt = listOfMusic.get(index_plus_satu);
				}else{
					tt = listOfMusic.get(0); 
				}
				currentTrack = tt; 
				uri = new File(tt.getId()).toURI().toString(); 
				startAudio();

			}
		});

		return player; 
	}
	

	
	private Color getRandomColor(){
		int m = r.nextInt(cc.length);  
		return cc[m]; 
	}
	
	
	private void creatBall(Group root){
		VBox v = new VBox();
		v.setSpacing(5);
		Color color = Color.RED;
		for(int i=0 ; i< 10 ;  i++){
			HBox b = new HBox(); 
			b.setSpacing(5);
			if(i == 5){
				color = Color.WHITESMOKE;
			}
			for(int j=0; j< 10 ; j++){
				  Cube c = new Cube(20, 90 , color , 1 );
			        c.rx.setAngle(90);
			        listOfCube.add(c);
			        Xform f = new Xform(); 
			        f.getChildren().add(c); 
			        b.getChildren().add(f); 
			}
			v.getChildren().add(b);
		}
	        
		Xform m = new Xform(); 
		m.getChildren().add(v); 
		
		m.rx.setAngle(90);
		
		
		Xform m1= new Xform();
		m1.getChildren().add(m); 
		m1.ry.setAngle(25);
		m1.setTranslateY(150); 
		
		
		Xform m2 = new Xform(); 
		m2.getChildren().add(m1); 
		m2.rx.setAngle(-23); 
		m2.toBack();
		root.getChildren().add(m2) ; 
		
		
//		int ii=0; 
//		for(Cube c : listOfCube){
//			map.put(c, ii++); 
//		}
//		
//		while(map.size() < listOfCube.size()){
//			int index = r.nextInt(listOfCube.size());
//			Cube cc = listOfCube.get(index);
//			if(map.get(cc) == null ){
//				map.put(cc, index); 
//			}
//		}
		
//		Collections.shuffle(listOfCube);
		
	}
	
	
	   class Xform extends Group {
	        final Rotate rx = new Rotate(0, Rotate.X_AXIS);
	        final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
	        final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
	        public Xform() { 
	            super(); 
	            getTransforms().addAll(rz, ry, rx); 
	        }
	    }

	 
	
	 public class Cube extends Group {
		 
	        final Rotate rx = new Rotate(0,Rotate.X_AXIS);
	        final Rotate ry = new Rotate(0,Rotate.Y_AXIS);
	        final Rotate rz = new Rotate(0,Rotate.Z_AXIS);
	        
	        final Scale scale = new Scale(); 
	        
	        final Translate yy = new Translate(); 
	        
	        Rectangle rectangleAcuan; 
	        
	        Rectangle topRectangle;
	        
	        public Cube(final  double width, final double height , final Color color, final double shade) {
	            getTransforms().addAll(rz, ry, rx);
	            setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent arg0) {
						fillRectangle.setFill(color);
					}
				});
	            
	           
	            Rectangle backRec = new Rectangle(); // depan 
	            		backRec.setWidth(width);
	            		backRec.setHeight(height);
	            		backRec.setFill(color.deriveColor(0.0, 1.0, (1 - 0.5*shade), 1.0));
	            		backRec.setTranslateX(-0.5*width);
	                    backRec.setTranslateY(-0.5*width);
	                    backRec.setTranslateZ(0.5*width);
	                    backRec.getTransforms().add(scale);
//	                    backRec.setOpacity(0.5);
	            		
	                    
	                    DropShadow effect = new DropShadow();
	                    effect.setRadius(0);
	                    effect.setOffsetY(1);
	                    
	               topRectangle =  new Rectangle(); // atas 
	                    topRectangle. setWidth(width);
	                    topRectangle.setHeight(width); 
	                    topRectangle.setFill(color.deriveColor(0.0, 1.0, (1 - 0.4*shade), 1.0));
	                    topRectangle.setTranslateX(-0.5*width); 
	                    topRectangle.setTranslateY(0); 
	                    topRectangle.setRotationAxis(Rotate.X_AXIS); 
	                    topRectangle.setRotate(90); 
//	                    topRectangle.setEffect(effect);
//	                    topRectangle.setOpacity(0.4);

	               
	               Xform ffTop = new Xform(); 
	               ffTop.getChildren().add(topRectangle); 
	               ffTop.getTransforms().add(yy); 
	               
	               Rectangle rightRec = new Rectangle();  // kanan
		                    rightRec.setWidth(width); 
		                    rightRec.setHeight(height); 
		                    rightRec.setFill(color.deriveColor(0.0, 1.0, (1 - 0.3 * shade ), 1.0));
		                    rightRec.setTranslateX(-1*width); 
		                    rightRec.setTranslateY(-0.5*width); 
		                    rightRec.setRotationAxis(Rotate.Y_AXIS); 
		                    rightRec.getTransforms().add(scale); 
		                    rightRec.setRotate(90); 
//		                    rightRec.setOpacity(0.5); 
		                    


	               
	               Rectangle leftRec = new Rectangle(); // kiri
		                    leftRec.setWidth(width); 
		                    leftRec.setHeight(height); 
		                    leftRec.setFill(color.deriveColor(0.0, 1.0, (1 - 0.2*shade), 1.0)); 
		                    leftRec.setTranslateX(0);
		                    leftRec.getTransforms().add(scale); 
		                    leftRec.setTranslateY(-0.5*width); 
		                    leftRec.setRotationAxis(Rotate.Y_AXIS); 
		                    leftRec.setRotate(90); 
//		                    leftRec.setOpacity(0.5); 
		                    
	            
	               Rectangle bottomRect = new Rectangle(); // bawah  
		                    bottomRect.setWidth(width); 
		                    bottomRect.setHeight(width); 
		                    bottomRect.setFill(color.deriveColor(0.0, 1.0, (1 - 0.1*shade), 1.0)); 
		                    bottomRect.setTranslateX(-0.5*width); 
		                    bottomRect.setTranslateY(-1*width); 
		                    bottomRect.setRotationAxis(Rotate.X_AXIS); 
		                    bottomRect.setRotate(90); 
		                    bottomRect.setOpacity(0.4); 
	               
	            
	                rectangleAcuan =  new Rectangle();  // belakang 
	                    rectangleAcuan.setWidth(width); 
	                    rectangleAcuan.setHeight(height); 
	                    rectangleAcuan.setFill(color); 
	                    rectangleAcuan.setTranslateX(-0.5*width); 
	                    rectangleAcuan.getTransforms().add(scale); 
	                    rectangleAcuan.setTranslateY(-0.5*width); 
	                    rectangleAcuan.setTranslateZ(-0.5*width); 
//	                    rectangleAcuan.setOpacity(0.5);


	            getChildren().addAll(backRec
	            		,ffTop
	            		,rightRec
	            		,leftRec
	            		,bottomRect
	            		,rectangleAcuan );
	            
	            scale.setY(INIT_HEIGHT);
	            koreksiHeight();

	        }
	        

	        // sesuaikan posisi rectangle atas sesuai dengan ketinggian rectangle kanan, kiri, depan, belakang
	        final void  koreksiHeight(){
	        	Rectangle r = rectangleAcuan;
	        	double m = scale.getY() * r.getHeight() - r.getWidth();
	            yy.setY(m);	        	
	        }

	    }


	public static void main(String[] args){
		launch(args); 
	}
}
