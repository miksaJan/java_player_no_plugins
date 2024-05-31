package miksa.musicplayer.localplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import miksa.generics.PlaybackQueueObserver;
import miksa.musicplayer.localplayer.tracks.LocalSoundTrack;

public class PlaybackQueue {
	private final List<LocalSoundTrack> list = new ArrayList<>();
	private int selectedTrackIndex = 0;
	private final Random generator = new Random();
	private final List<PlaybackQueueObserver> observerList = new ArrayList<>();
	private LocalSoundTrack selectedTrack;
	
	public void add(LocalSoundTrack track) {
		this.list.add(track);
		fireQueueChanged();
	}
	
	public void addAll(List<LocalSoundTrack> tracks) {
		for(int i = 0; i < tracks.size();i++) {
			//filter invalid tracks
			if(tracks.get(i) == null) {
				tracks.remove(i);
				i--;
			}
			else {
				add(tracks.get(i));
			}
		}
		fireQueueChanged();
	}
	
	public void remove(LocalSoundTrack track) {
		list.remove(track);
		findIndexOfSelectedTrack();
		fireQueueChanged();
	}
	
	public void removeAt(int index) {
		list.remove(index);
		findIndexOfSelectedTrack();
		fireQueueChanged();
	}
	
	public void clear() {
		list.clear();
		selectedTrackIndex = 0;
		findIndexOfSelectedTrack();
		fireQueueChanged();
	}
	
	public void select(int index) {
		selectedTrackIndex = index;
		selectedTrackIndexChanged();
	}
	
	public void selectPrev() {
		if(selectedTrackIndex > 0) {
			select(selectedTrackIndex-1);
			selectedTrackIndexChanged();
		}	
	}
	
	public LocalSoundTrack getCurrentlyPlayingTrack() {
		if(selectedTrackIndex < list.size())
			return list.get(selectedTrackIndex);
		else return null;
	}
	
	public void selectNext() {
		if(hasNext()) {
			selectedTrackIndex++;
			selectedTrackIndexChanged();
		}
	}
	
	public void selectShuffledNext() {
		selectedTrackIndex = generator.nextInt(list.size());
		selectedTrackIndexChanged();
	}
	
	public boolean hasNext() {
		return selectedTrackIndex+1 < list.size();
	}
	
	public List<LocalSoundTrack> getList() {
		return this.list;
	}
	
	private void selectedTrackIndexChanged() {
		selectedTrack = getCurrentlyPlayingTrack();
	}
	
	private void findIndexOfSelectedTrack() {
		int index = list.indexOf(selectedTrack);
		if(index < 0) {
			selectedTrackIndex = 0;
			selectedTrackIndexChanged();
			fireSelectedTrackRemoved();
		} else {
			selectedTrackIndexChanged();
			selectedTrackIndex = index;
		}
	}
	
	//observer subject methods
	public void addObserver(PlaybackQueueObserver observer) {
		this.observerList.add(observer);
	}
	public void removeObserver(PlaybackQueueObserver observer) {
		this.observerList.remove(observer);
	}
	public void fireQueueChanged() {
		clearObservers();
		for(PlaybackQueueObserver observer : observerList) {
			observer.notifyPlaybackQueueChanged(this);
		}
	}
	private void fireSelectedTrackRemoved() {
		clearObservers();
		for(PlaybackQueueObserver observer : observerList) {
			observer.playedTrackRemoved();
		}
	}
	
	public void clearObservers() {
		int i = 0;
		while(i < observerList.size()) {
			if(observerList.get(i) == null)
				removeObserver(observerList.get(i));
			else
				i++;
		}
	}
}