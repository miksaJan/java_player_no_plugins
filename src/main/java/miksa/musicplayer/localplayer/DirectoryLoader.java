package miksa.musicplayer.localplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import miksa.musicplayer.localplayer.tracks.LocalSoundTrack;

public class DirectoryLoader {
	private LocalSoundTrackFactory factory = new LocalSoundTrackFactory();
	
	public List<LocalSoundTrack> loadFromDirectory(String url, boolean recursive) {
		List<LocalSoundTrack> list = new ArrayList<>();
		SearchInDirectory(url, recursive, list);
		return list;
	}
	
	private void SearchInDirectory(String url,boolean recursive,List<LocalSoundTrack> outputList) {
		//https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
		//https://stackoverflow.com/questions/13634576/javafx-filechooser-how-to-set-file-filters
		File listOfFiles[] = new File(url).listFiles();
		if(listOfFiles == null)
			return;
		
		for(int i = 0; i < listOfFiles.length;i++) {
			if(listOfFiles[i].isFile() && listOfFiles[i].exists()) {
				LocalSoundTrack track = factory.createSoundTrackFromFile(listOfFiles[i]);
				if(track != null) {
					outputList.add(track);
				}
			}
			else if(listOfFiles[i].isDirectory() && recursive) {
				SearchInDirectory(listOfFiles[i].getAbsolutePath(), recursive, outputList);
			}
		}
	}
}
