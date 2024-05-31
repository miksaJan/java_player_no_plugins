package miksa.musicplayer.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import miksa.generics.AlbumString;
import miksa.generics.ArtistString;
import miksa.generics.GenericSoundTrack;
import miksa.generics.LibraryObserver;
import miksa.generics.PlaybackMode;
import miksa.generics.PlaybackQueueObserver;
import miksa.generics.PlayerObserver;
import miksa.musicplayer.App;
import miksa.musicplayer.localplayer.DirectoryLoader;
import miksa.musicplayer.localplayer.LocalPlayer;
import miksa.musicplayer.localplayer.LocalSoundTrackFactory;
import miksa.musicplayer.localplayer.PlaybackQueue;
import miksa.musicplayer.localplayer.library.Library;
import miksa.musicplayer.localplayer.tracks.LocalSoundTrack;

public class PrimaryController implements PlayerObserver, PlaybackQueueObserver,LibraryObserver {
	@FXML private Button btnStart,btnSearch;
	@FXML private ToggleButton tbtnLibraryModeArtists,tbtnLibraryModeAlbums,tbtnLibraryModeTracks;
	@FXML private ImageView btnPlayPauseImage,ivSidebarArt;
	@FXML private Label lbNowPlayDuration,lbNowPlayActualTime,lbSidebarTitle,lbSidebarArtist,lbSidebarAlbum,lbSidebarFormatSource;
	@FXML private Slider sliderVolume,sliderProgress;
	@FXML private TableView<LocalSoundTrack> tvQueue,tvLibraryTracks;
	//@FXML private ComboBox<GenericPlayer> cbPlayerSelect;
	@FXML private AnchorPane apPluginSpace;
	@FXML private RadioButton rbLibraryUser,rbLibrarySearch;
	//search bar
	@FXML private HBox hbSearch;
	@FXML private TextField tfSearch;
	
	//queue columns
	@FXML private TableColumn<GenericSoundTrack, String> tcQueueName,tcQueueArtist,tcQueueAlbum,tcQueueFormat,tcQueueDuration,tcQueueSource,tcQueuePath;
	@FXML private TableColumn<GenericSoundTrack, Integer> tcQueueYear,tcQueueDiscNum,tcQueueTrackNum;
	//library columns
	@FXML private TableColumn<GenericSoundTrack, String> tcLibArtist,tcLibAlbum,tcLibTitle,tcLibDuration,tcLibFormat,tcLibPath;
	@FXML private TableColumn<GenericSoundTrack, Integer> tcLibYear,tcLibDiscNum,tcLibTrackNum;
	
	@FXML private ListView<ArtistString> lvLibraryArtists;
	@FXML private ListView<AlbumString> lvLibraryAlbums;
	@FXML private MenuItem miPlay,miPause,miStop,miPrev,miNext,cmQueuePlay,cmQueueRemoveSelected,cmLibAddToQueue,cmLibPlay;
	@FXML private CheckMenuItem cmiNormal,cmiRepeatOne,cmiRepeatAll,cmiShuffle;
	
	private LocalPlayer player;
	//private PlayerManager playerManager = new PlayerManager(apPluginSpace);
	
	private Stage primarystage;
	private final DirectoryLoader directoryLoader = new DirectoryLoader();
	private final LocalSoundTrackFactory factory = new LocalSoundTrackFactory();
	private final String version = "1.0.3";
	
	private boolean seeking = false;
	
	//test variables
	private boolean libraryLoadFinished = false;
	
	@FXML private void initialize() {
		player = new LocalPlayer();

		player.addObserver(this);
		player.getQueue().addObserver(this);
		player.getLibrary().addObserver(this);

		setVolume();
		sliderVolume.valueProperty().addListener(t -> setVolume());
		
		//library mode selector init
		rbLibraryUser.selectedProperty().addListener((obs,oldval,newval) -> rbLibraryModeCheckedChanged());
		rbLibrarySearch.selectedProperty().addListener((obs,oldval,newval) -> rbLibraryModeCheckedChanged());
		hbSearch.setVisible(false);
		
		//init library
		lvLibraryArtists.getSelectionModel().selectedItemProperty().addListener(t -> libraryArtist_IndexChanged());
		lvLibraryAlbums.getSelectionModel().selectedItemProperty().addListener(t -> libraryAlbum_IndecChanged());
		
		//init queue table
		tcQueueTrackNum.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,Integer>("tracknumber"));
		tcQueueDiscNum.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,Integer>("discnumber"));
		tcQueueName.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("title"));
		tcQueueArtist.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("artist"));
		tcQueueAlbum.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("album"));
		tcQueueDuration.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("durationStr"));
		tcQueueFormat.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("filetype"));
		tcQueuePath.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("url"));
		tcQueueYear.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,Integer>("year"));
		tcQueueSource.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("source"));
		
		//init library table
		tcLibTrackNum.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,Integer>("tracknumber"));
		tcLibDiscNum.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,Integer>("discnumber"));
		tcLibTitle.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("title"));
		tcLibArtist.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("artist"));
		tcLibAlbum.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("album"));
		tcLibDuration.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("durationStr"));
		tcLibFormat.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("filetype"));
		tcLibPath.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,String>("url"));
		tcLibYear.setCellValueFactory(new PropertyValueFactory<GenericSoundTrack,Integer>("year"));
	}
	
    private void rbLibraryModeCheckedChanged() {
		hbSearch.setVisible(rbLibrarySearch.isSelected());
		tbtnLibraryModeArtists.setSelected(true);
		if(rbLibraryUser.isSelected()) {
			if(tbtnLibraryModeArtists.isSelected())
				refreshLibraryArtists();
			else if(tbtnLibraryModeAlbums.isSelected())
				refreshLibraryAlbums();
			else if(tbtnLibraryModeTracks.isSelected())
				refreshLibraryTracks();
		} else {
			lvLibraryArtists.setVisible(true);
			lvLibraryArtists.getItems().clear();
			lvLibraryAlbums.setVisible(true);
			lvLibraryAlbums.getItems().clear();
			tvLibraryTracks.getItems().clear();
		}
	}

	private void libraryAlbum_IndecChanged() {
    	tvLibraryTracks.getItems().clear();
    	AlbumString album = lvLibraryAlbums.getSelectionModel().getSelectedItem();
		List<LocalSoundTrack> tracks = player.getLibrary().getAllTracksOfAlbum(album);
		tvLibraryTracks.getItems().addAll(tracks);
	}

	private void libraryArtist_IndexChanged() {
		lvLibraryAlbums.getItems().clear();
		ArtistString artist = lvLibraryArtists.getSelectionModel().getSelectedItem();
		List<AlbumString> albums = player.getLibrary().getAllAlbumsOfArtist(artist);
		lvLibraryAlbums.getItems().addAll(albums);
	}

	@FXML private void btnStartClick() {
    	player.playpause();
    }
    
    @FXML private void btnStopClick() {
    	player.stop();
    }
    
    @FXML private void btnNextClick() {
    	player.next();
    }
    
    @FXML private void btnPrevClick() {
    	player.prev();
    }
    
    @FXML private void btnRemoveAllClick() {
    	player.getQueue().clear();
    }
    
    @FXML private void btnSearch_Click() {
    	String querry = tfSearch.getText();
    	if(tbtnLibraryModeArtists.isSelected()) {
    		lvLibraryArtists.getItems().clear();
    		lvLibraryArtists.getItems().addAll(player.getLibrary().searchArtists(querry));
    	} else if(tbtnLibraryModeAlbums.isSelected()) {
    		lvLibraryAlbums.getItems().clear();
    		lvLibraryAlbums.getItems().addAll(player.getLibrary().searchAlbums(querry));
    	} else {
    		tvLibraryTracks.getItems().clear();
    		tvLibraryTracks.getItems().addAll(player.getLibrary().searchTracks(querry));
    	}
    }
    
    @FXML private void openFileClick() {
    	//https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
    	FileChooser fc = new FileChooser();
    	//filters
    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 soubory (*.mp3)", "*.mp3");
    	fc.getExtensionFilters().add(extFilter);
    	extFilter = new FileChooser.ExtensionFilter("WAV soubory (*.wav)", "*.wav");
    	fc.getExtensionFilters().add(extFilter);
    	extFilter = new FileChooser.ExtensionFilter("FLAC soubory (*.flac)", "*.flac");
    	fc.getExtensionFilters().add(extFilter);
    	File f = fc.showOpenDialog(primarystage);
    	if(f != null) {
        	PlaybackQueue queue = (PlaybackQueue) player.getQueue();
        	queue.add(factory.createSoundTrackFromFile(f));
    	}
    }
    
    @FXML private void btnAddAllToQueue_Click() {
    	player.getQueue().addAll(tvLibraryTracks.getItems());
    }
    
    @FXML private void btnPlayAll_Click() {
    	if(tvLibraryTracks.getItems().size() > 0) {
    		player.getQueue().clear();
    		player.getQueue().addAll(tvLibraryTracks.getItems());
    		player.playpause();
    	}
    }
   
    @FXML private void miOpenFolderClick() {
    	DirectoryChooser dc = new DirectoryChooser();
    	File f = dc.showDialog(primarystage);
    	if(f == null) 
    		return;
    	List<LocalSoundTrack> list = directoryLoader.loadFromDirectory(f.getAbsolutePath(), false);
    	player.getQueue().addAll(list);
    }
    
    @FXML private void miSetPlaybackModeNormalClick() {
    	player.setPlaybackMode(PlaybackMode.NORMAL);
    }
    @FXML private void miSetPlaybackModeRepeatOneClick() {
    	player.setPlaybackMode(PlaybackMode.REPEAT_ONE);
    }
    @FXML private void miSetPlaybackModeRepeatAllClick() {
    	player.setPlaybackMode(PlaybackMode.REPEAT_ALL);
    }
    @FXML private void miSetPlaybackModeShuffleClick() {
    	player.setPlaybackMode(PlaybackMode.SHUFFLE);
    }
    
    @FXML private void miClose_Click() {
    	primarystage.close();
    }
    
    @FXML private void miOpenSettigns_Click() {
    	Stage settings = new Stage();
    	FXMLLoader fxml = new FXMLLoader(App.class.getResource("settings.fxml"));
    	try {
    		settings.setScene(new Scene(fxml.load()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
    	SettingsController controller = (SettingsController) fxml.getController();
    	controller.setSettingsStage(settings);
    	settings.setTitle("HMplayer - nastavení");
    	settings.show();
    }
    
    @FXML private void miAbout_Click() {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("O aplikaci");
    	alert.setHeaderText("Hudební přehrávač");
    	alert.setContentText("Autor:\tJan Miksa");
    	alert.show();
    }
    
    @FXML private void cmQueuePlay_Click() {
    	player.playAtQueueIndex(tvQueue.getSelectionModel().getSelectedIndex());
    }
    
    @FXML private void cmQueueRemoveSelected_Click() {
    	LocalSoundTrack st = tvQueue.getItems().get(tvQueue.getSelectionModel().getSelectedIndex());
    	player.getQueue().remove(st);
    }
    
    @FXML private void cmLibAddToQueue_Click() {
    	player.getQueue().add(tvLibraryTracks.getSelectionModel().getSelectedItem());
    }
    
    @FXML private void cmLibPlay_Click() {
    	player.playTrack(tvLibraryTracks.getSelectionModel().getSelectedItem());
    }
    
    @FXML private void seekStart() {
    	seeking = true;
    }
    
    @FXML private void seekStop() {
    	seeking = false;
    	playbackSeek();
    }

	@Override
	public void notifyPlayerStarted(LocalPlayer sender) {
		Platform.runLater(() -> {
			btnPlayPauseImage.setImage(new Image(getClass().getResource("icons/pause.png").toString()));
			miPlay.setDisable(true);
			miPause.setDisable(false);
			miStop.setDisable(false);
		});
	}

	@Override
	public void notifyPlayerPaused(LocalPlayer sender) {
		Platform.runLater(() -> {
			btnPlayPauseImage.setImage(new Image(getClass().getResource("icons/play.png").toString()));
			miPlay.setDisable(false);
			miPause.setDisable(true);
			miStop.setDisable(false);
		});
	}

	@Override
	public void notifyPlayerStoped(LocalPlayer sender) {
			Platform.runLater(() -> {
				btnPlayPauseImage.setImage(new Image(getClass().getResource("icons/play.png").toString()));
				miPlay.setDisable(false);
				miPause.setDisable(true);
				miStop.setDisable(true);
				if(primarystage != null)
					primarystage.setTitle("HMplayer");
				//clear sidebar
				ivSidebarArt.setImage(null);
				lbSidebarTitle.setText("");
				lbSidebarArtist.setText("");
				lbSidebarAlbum.setText("");
				lbSidebarFormatSource.setText("");
				//duration set
				lbNowPlayDuration.setText("--:--");
				sliderProgress.setMax(0);
			});
	}
	
	@Override
	public void notifyPlayerMediaChanged(LocalPlayer sender) {
			GenericSoundTrack st = sender.getQueue().getCurrentlyPlayingTrack();
			Platform.runLater(() -> {
				if(st == null) {
					ivSidebarArt.setImage(null);
					lbSidebarTitle.setText("");
					lbSidebarArtist.setText("");
					lbSidebarAlbum.setText("");
					lbSidebarFormatSource.setText("");
					//duration set
					lbNowPlayDuration.setText("--:--");
					sliderProgress.setMax(0);
				} else {
					//update sidebar
					ivSidebarArt.setImage(st.getAlbumArt());
					lbSidebarTitle.setText(st.getTitle());
					lbSidebarArtist.setText(st.getArtist().getTextRepresentation());
					String albumText = st.getAlbum().getTextRepresentation();
					int year = st.getYear();
					if(year != 0)
						albumText += " ("+year+")";
					lbSidebarAlbum.setText(albumText);
					//file type - source line
					String source = st.getSource();
					String filetype = st.getFiletype();
					String result = "";
					
					if(source == null || filetype == null || source.equals("") || filetype.equals(""))
						result = filetype + source;
					else
						result = filetype + " - " + source;
					
					lbSidebarFormatSource.setText(result);
					primarystage.setTitle(st.getTitle() + " - HMplayer");
					
					//duration set
					lbNowPlayDuration.setText(st.getDurationStr());
					sliderProgress.setMax(st.getDuration());
				}
			});
	}

	@Override
	public void notifyPlayerTimeChanged(LocalPlayer sender) {
		if(seeking)
			return;
		
			int secondsInput = (int)sender.getCurrentTime();
			Platform.runLater(() -> {
				lbNowPlayActualTime.setText(createTimeFormat(secondsInput));
				sliderProgress.setValue(secondsInput);
			});
	}

	@Override
	public void notifyPlaybackModeChanged(LocalPlayer sender) {
		cmiNormal.setSelected(sender.getPlaybackMode() == PlaybackMode.NORMAL);
		cmiRepeatOne.setSelected(sender.getPlaybackMode() == PlaybackMode.REPEAT_ONE);
		cmiRepeatAll.setSelected(sender.getPlaybackMode() == PlaybackMode.REPEAT_ALL);
		cmiShuffle.setSelected(sender.getPlaybackMode() == PlaybackMode.SHUFFLE);
	}

	private void setVolume() {
		//Volume range 0-10
		player.setVolume(sliderVolume.getValue());
	}

	private String createTimeFormat(int secondsInput) {
		String output = "";
		int minutes = secondsInput / 60;
		if(minutes < 10)
			output = "0";
		output += minutes+":";
		
		int seconds = secondsInput % 60;
		if(seconds < 10) 
			output += "0"+seconds; 
		else 
			output += seconds+"";
		return output;
	}

	@FXML private void playbackSeek() {
		player.seek(sliderProgress.getValue());
	}

	//change between artists, albums and tracks mode
	@FXML private void libraryModeChange() {
		lvLibraryArtists.getItems().clear();
		lvLibraryAlbums.getItems().clear();
		tvLibraryTracks.getItems().clear();
		if(tbtnLibraryModeArtists.isSelected()) {
			lvLibraryArtists.setVisible(true);
			lvLibraryAlbums.setVisible(true);
			if(rbLibraryUser.isSelected())
				refreshLibraryArtists();
		} else if(tbtnLibraryModeAlbums.isSelected()) {
			lvLibraryArtists.setVisible(false);
			lvLibraryAlbums.setVisible(true);
			if(rbLibraryUser.isSelected())
				refreshLibraryAlbums();
		} else if(tbtnLibraryModeTracks.isSelected()) {
			lvLibraryArtists.setVisible(false);
			lvLibraryAlbums.setVisible(false);
			if(rbLibraryUser.isSelected())
				refreshLibraryTracks();
		}
	}
	
	private void refreshLibraryArtists() {
		lvLibraryArtists.getItems().clear();
		lvLibraryArtists.getItems().addAll(player.getLibrary().getAllArtists());
	}
	
	private void refreshLibraryAlbums() {
		lvLibraryAlbums.getItems().clear();
		lvLibraryAlbums.getItems().addAll(player.getLibrary().getAllAlbums());
	}
	
	private void refreshLibraryTracks() {
		tvLibraryTracks.getItems().clear();
		tvLibraryTracks.getItems().addAll(player.getLibrary().getAllTracks());
	}
	
	
	@Override
	public <T extends GenericSoundTrack> void notifyPlaybackQueueChanged(PlaybackQueue queue) {
		List<LocalSoundTrack> queueTmp = queue.getList();
		tvQueue.getItems().clear();
	    tvQueue.getItems().addAll(queueTmp);
	}

	@Override
	public void notifyLibraryChanged(Library genericLibrary) {
		if(rbLibraryUser.isSelected()) {
			if(tbtnLibraryModeArtists.isSelected())
				refreshLibraryArtists();
			else if(tbtnLibraryModeAlbums.isSelected())
				refreshLibraryAlbums();
			else
				refreshLibraryTracks();
		}
		libraryLoadFinished = true;	
	}

	@Override
	public void playedTrackRemoved() {
		//this is not interesting for me...
	}

	public Stage getPrimarystage() {
		return primarystage;
	}

	public void setPrimarystage(Stage primarystage) {
		this.primarystage = primarystage;
	}
	
	//test methods
	public boolean isLibraryLoadFinished() {
		return libraryLoadFinished;
	}

	@Override
	public void notifyPlayerVolumeChanged(LocalPlayer sender) {
		// TODO Auto-generated method stub
		this.sliderVolume.setValue(sender.getMediaPlayer().getVolume());
	}
}