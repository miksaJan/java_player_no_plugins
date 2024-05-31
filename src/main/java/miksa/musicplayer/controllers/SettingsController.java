package miksa.musicplayer.controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import miksa.musicplayer.preferences.PreferencesAPI;

public class SettingsController {
	
	private Stage settingsStage;
	private final PreferencesAPI settings = new PreferencesAPI();
	
	//settings
	@FXML ListView<String> lvLibPaths, lvPluginPaths;
	
	public void initialize() {
		//load settings
		lvLibPaths.getItems().addAll(settings.readLibraryPaths());
		lvPluginPaths.getItems().addAll(settings.readPluginPaths());
	}
	
    public Stage getSettingsStage() {
		return settingsStage;
	}

	public void setSettingsStage(Stage settingsStage) {
		this.settingsStage = settingsStage;
	}
    
    @FXML private void btnSubmit_Click() {
    	settings.writeLibraryPaths(lvLibPaths.getItems());
    	settings.writePluginPaths(lvPluginPaths.getItems());
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Upozornění");
    	alert.setHeaderText("Upozornění");
    	alert.setContentText("Změny nastavení se projeví až po restartu programu");
    	alert.show();
    	settingsStage.close();
    }

    @FXML private void btnCancel_Click() {
    	settingsStage.close();
    }
    
    @FXML private void btnLibAdd_Click() {
    	DirectoryChooser dc = new DirectoryChooser();
    	File f = dc.showDialog(settingsStage);
    	if(f == null)
    		return;
    	if(!lvLibPaths.getItems().contains(f.getAbsolutePath())) {
    		lvLibPaths.getItems().add(f.getAbsolutePath());
    	}
    }

    @FXML private void btnLibRemove_Click() {
    	String path = lvLibPaths.getSelectionModel().getSelectedItem();
    	if(path != null) {
    		lvLibPaths.getItems().remove(path);
    	}
    }
    
    @FXML private void btnPluginsAdd_Click() {
    	FileChooser.ExtensionFilter filter = new ExtensionFilter("Soubor JAR (*.jar)", "*.jar");
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().add(filter);
    	File f = fc.showOpenDialog(settingsStage);
    	if(!lvPluginPaths.getItems().contains(f.getAbsolutePath())) {
    		lvPluginPaths.getItems().add(f.getAbsolutePath());
    	}
    } 
    
    @FXML private void btnPluginsRemove_Click() {
    	String path = lvPluginPaths.getSelectionModel().getSelectedItem();
    	if(path != null) {
    		lvPluginPaths.getItems().remove(path);
    	}
    }
}