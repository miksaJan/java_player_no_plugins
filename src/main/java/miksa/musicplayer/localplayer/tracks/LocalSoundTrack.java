package miksa.musicplayer.localplayer.tracks;

import javafx.scene.media.Media;
import miksa.generics.GenericSoundTrack;

public class LocalSoundTrack extends GenericSoundTrack {
	protected final String filename;
	protected Media media;
	
	public LocalSoundTrack(String url, String uri, String filename, String filetype) {
		this.url = url;
		this.uri = uri;
		this.filename = filename;
		this.filetype = filetype;
		this.title = this.filename;
		this.source = "Lokální";
	}

	public Media getMedia() {
		if(media == null)
			media = new Media(uri); 
		return this.media;
	}

	public String getFilename() {
		return filename;
	}

	@Override
	public String toString() {
		return this.filename;
	}
}
