package miksa.musicplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import miksa.musicplayer.controllers.PrimaryController;

import java.io.File;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
    	FXMLLoader fxml = new FXMLLoader(getClass().getResource("primary_layout.fxml"));
    	try {
    		scene = new Scene(fxml.load());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
        scene.getStylesheets().add(App.class.getResource("main_window_style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.setTitle("HMplayer");
        stage.setOnCloseRequest(event -> stop());
        PrimaryController controller = fxml.getController();
        controller.setPrimarystage(stage);
    }
    
    public void stop() {
    	String tempDirPath = System.getProperty("java.io.tmpdir");
		File tempdir = new File(tempDirPath+"HMplayer");
		System.out.println(tempdir.getAbsolutePath());
		//clean after program closes
		if(tempdir.exists()) {
			for(File file : tempdir.listFiles()) {
				if(file.isFile()) {
					file.delete();
				}
			}
		}
		System.exit(0);
    }
    
    public static void run(String[] args) {
        launch();
    }
}