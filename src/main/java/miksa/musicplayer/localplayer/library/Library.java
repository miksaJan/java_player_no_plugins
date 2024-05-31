package miksa.musicplayer.localplayer.library;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import miksa.generics.AlbumString;
import miksa.generics.ArtistString;
import miksa.generics.LibraryObserver;
import miksa.musicplayer.localplayer.tracks.LocalSoundTrack;

public class Library {
	private List<LocalSoundTrack> library;
	private final List<LibraryObserver> observerList;
	private final LibraryLoader loader;
	
	public Library() {
		library = new ArrayList<>();
		observerList = new ArrayList<>();
		loader = new LibraryLoader();
		relaodLibrary();
	}
	
	public void relaodLibrary() {
		loader.loadDirectories();
		Thread thread = new Thread(() -> {
			library = loader.relaodLibraryFromDirectories();
			fireLibraryChanged();
		});
		thread.start();
	}
	
	public List<ArtistString> getAllArtists() {
		List<ArtistString> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			try {
				ArtistString artist = track.getArtist();
				if(!output.contains(artist))
					output.add(artist);
			} catch (Exception e) {
				System.err.println(track.getUrl());
				e.printStackTrace();
			}
			
		}
		output = output.stream().sorted((o1,o2) -> o1.getTextRepresentation().compareTo(o2.getTextRepresentation())).collect(Collectors.toList());
		return output;
	}
	
	public List<AlbumString> getAllAlbums() {
		List<AlbumString> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			AlbumString album = track.getAlbum();
			if(!output.contains(album))
				output.add(album);
		}
		output = output.stream().sorted((o1,o2) -> o1.getTextRepresentation().compareTo(o2.getTextRepresentation())).collect(Collectors.toList());
		return output;
	}
	public List<LocalSoundTrack> getAllTracks() {
		return library;
	}
	public List<AlbumString> getAllAlbumsOfArtist(ArtistString artist) {
		List<AlbumString> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			if(track.getArtist().equals(artist)) {
				AlbumString album = track.getAlbum();
				if(!output.contains(album)) {
					output.add(album);
				}
			}
		}
		output = output.stream().sorted((o1,o2) -> o1.getTextRepresentation().compareTo(o2.getTextRepresentation())).collect(Collectors.toList());
		return output;
	}
	public List<LocalSoundTrack> getAllTracksOfArtist(ArtistString artist) {
		List<LocalSoundTrack> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			if(track.getArtist().equals(artist.getTextRepresentation())) {
				output.add(track);
			}
		}
		return output;
	}
	public List<LocalSoundTrack> getAllTracksOfAlbum(AlbumString album) {
		List<LocalSoundTrack> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			if(track.getAlbum().equals(album)) {
				output.add(track);
			}
		}
		return output;
	}
	
	//observer subject methods
	public void addObserver(LibraryObserver observer) {
		this.observerList.add(observer);
	}
	
	public void removeObserver(LibraryObserver observer) {
		this.observerList.remove(observer);
	}
	
	public void fireLibraryChanged() {
		clearObservers();
		for(LibraryObserver observer : observerList) {
			observer.notifyLibraryChanged(this);
		}
	}
	
	private void clearObservers() {
		int i = 0;
		while(i < observerList.size()) {
			if(observerList.get(i) == null)
				removeObserver(observerList.get(i));
			else
				i++;
		}
	}

	public List<ArtistString> searchArtists(String querry) {
		List<ArtistString> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			ArtistString artist = track.getArtist();
			if(artist.getTextRepresentation().contains(querry) && !output.contains(artist))
				output.add(artist);
		}
		output = output.stream().sorted((o1,o2) -> o1.getTextRepresentation().compareTo(o2.getTextRepresentation())).collect(Collectors.toList());
		return output;
	}

	public List<AlbumString> searchAlbums(String querry) {
		List<AlbumString> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			AlbumString album = track.getAlbum();
			if(album.getTextRepresentation().contains(querry) && !output.contains(album))
				output.add(album);
		}
		output = output.stream().sorted((o1,o2) -> o1.getTextRepresentation().compareTo(o2.getTextRepresentation())).collect(Collectors.toList());
		return output;
	}

	public List<LocalSoundTrack> searchTracks(String querry) {
		List<LocalSoundTrack> output = new ArrayList<>();
		for(LocalSoundTrack track : library) {
			String title = track.getTitle();
			if(title.contains(querry))
				output.add(track);
		}
		return output;
	}
}
