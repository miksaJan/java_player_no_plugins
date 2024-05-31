package miksa.musicplayer.localplayer.tracks;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.DataFormatException;

import javafx.scene.media.Media;
import miksa.musicplayer.localplayer.flacdecoder.FlacDecoder;

public class FlacSoundTrack extends LocalSoundTrack {
	private File decodedFile;
	public FlacSoundTrack(String url, String filename, String filetype) {
		super(url, null, filename, filetype);
	}
	
	@Override
	public Media getMedia() {
		if(decodedFile == null || !(decodedFile.exists())) {
			try {
				decodedFile = FlacDecoder.decodeFile(new File(this.url.toString()));
			} catch (DataFormatException | IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return new Media(decodedFile.toURI().toString());
	}
}
