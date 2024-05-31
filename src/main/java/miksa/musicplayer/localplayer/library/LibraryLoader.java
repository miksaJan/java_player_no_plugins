package miksa.musicplayer.localplayer.library;

import java.util.ArrayList;
import java.util.List;

import miksa.musicplayer.localplayer.DirectoryLoader;
import miksa.musicplayer.localplayer.tracks.LocalSoundTrack;
import miksa.musicplayer.preferences.PreferencesAPI;

public class LibraryLoader {
	private final PreferencesAPI settings = new PreferencesAPI();
	private List<String> directories = new ArrayList<>();
	private DirectoryLoader dl = new DirectoryLoader();
	
	public void loadDirectories() {
		this.directories.clear();
		this.directories.addAll(settings.readLibraryPaths());
	}
	
	public List<LocalSoundTrack> relaodLibraryFromDirectories() {
		List<LocalSoundTrack> library = new ArrayList<>();
		for(String dir : directories) {
			List<LocalSoundTrack> tempList = dl.loadFromDirectory(dir, true);
			library.addAll(tempList);
		}
		return library;
	}

	public List<String> getPaths() {
		return this.directories;
	}
}
