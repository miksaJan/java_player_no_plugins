package miksa.musicplayer.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class PreferenceLoader {
	private Preferences preferences = Preferences.userRoot().node(this.getClass().getName());
	
	public List<String> getStringArray(String prefname) {
		String input = preferences.get(prefname, "");
		if(input.equals(""))
			return new ArrayList<>();
		else {
			String arr[] = input.split("\n");
			List<String> output = new ArrayList<>();
			for(String path : arr) {
				output.add(path);
				System.out.println(path);
			}
			return output;
		}
		
	}
	
	public void writeStringArray(List<String> array, String prefname) {
		StringBuilder output = new StringBuilder();
		for(String path : array) {
			output.append(path);
			output.append("\n");
		}
		preferences.put(prefname, output.toString());
	}
}
