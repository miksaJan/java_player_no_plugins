package miksa.musicplayer.preferences;

import java.util.List;

public class PreferencesAPI {
	private PreferenceLoader loader = new PreferenceLoader();
	
	public void writeLibraryPaths(List<String> paths) {
		loader.writeStringArray(paths, "library-paths");
	}
	
	public List<String> readLibraryPaths() {
		return loader.getStringArray("library-paths");
	}
	
	public void writePluginPaths(List<String> paths) {
		loader.writeStringArray(paths, "plugin-paths");
	}
	
	public List<String> readPluginPaths() {
		return loader.getStringArray("plugin-paths");
	}
}
