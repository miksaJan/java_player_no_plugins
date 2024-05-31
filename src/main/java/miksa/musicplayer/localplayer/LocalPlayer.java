package miksa.musicplayer.localplayer;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import miksa.generics.PlaybackMode;
import miksa.generics.PlaybackQueueObserver;
import miksa.generics.PlayerObserver;
import miksa.generics.PlayerStatus;
import miksa.musicplayer.localplayer.library.Library;
import miksa.musicplayer.localplayer.tracks.LocalSoundTrack;

public class LocalPlayer implements PlaybackQueueObserver {
	private final List<PlayerObserver> observerList = new ArrayList<>();
	private MediaPlayer mediaPlayer;
	private final PlaybackQueue queue = new PlaybackQueue();
	private PlaybackMode playbackMode;
	private final Library library = new Library();
	
	private Thread playNotifyThread;
	private boolean playNotifyThreadRun = false;
	private double lastVolumeSet = 0;
	
	public LocalPlayer() {
		getQueue().addObserver(this);
		setPlaybackMode(PlaybackMode.NORMAL);
	}
	
	private void loadSelectedFileFromQueue() {
		if(mediaPlayer != null) {
			stop();
		}
		LocalSoundTrack track = getQueue().getCurrentlyPlayingTrack();
		Media media = track.getMedia();
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setVolume(lastVolumeSet);
		
		//satus on events
		mediaPlayer.setOnReady(() -> { 
			fireMediaChanged();
			printPlayerStatus();
		});
		mediaPlayer.setOnPlaying(() -> printPlayerStatus());
		mediaPlayer.setOnHalted(() -> printPlayerStatus());
		mediaPlayer.setOnEndOfMedia(() -> reachedEndOfTrack());
	}
	
	public void play() {
		if(mediaPlayer == null) {
			loadSelectedFileFromQueue();
		}
		mediaPlayer.play();
		timeStart();
		firePlayStarted();
	}
	
	public void stop() {
		if(mediaPlayer == null)
			return;
		mediaPlayer.stop();
		timeStop();
		mediaPlayer.dispose();
		mediaPlayer = null;
		firePlayStopped();
	}
	
	public void pause() {
		if(mediaPlayer == null)
			return;
		mediaPlayer.pause();
		timeStop();
		firePlayPaused();
	}
	
	public void playpause() {
		if(mediaPlayer == null || mediaPlayer.getStatus() != Status.PLAYING)
			play();
		else
			pause();
	}
	
	public void prev() {
		getQueue().selectPrev();
		loadSelectedFileFromQueue();
		play();
	}
	
	public void next() {
		if(playbackMode == PlaybackMode.SHUFFLE)
			getQueue().selectShuffledNext();
		else
			getQueue().selectNext();
		loadSelectedFileFromQueue();
		play();
	}
	
	public void playAtQueueIndex(int index) {
		getQueue().select(index);
		loadSelectedFileFromQueue();
		play();
	}
	
	public void seek(double seconds) {
		if(mediaPlayer != null) {
			mediaPlayer.seek(new Duration(seconds*1000));
			fireTimeChanged();
		}
	}

	public MediaPlayer getMediaPlayer() {
		return this.mediaPlayer;
	}
	
	public PlaybackQueue getQueue() {
		return queue;
	}

	public void setVolume(double volume) {
		lastVolumeSet = volume/10;
		if(mediaPlayer != null)
			mediaPlayer.setVolume(lastVolumeSet);
	}
	
	public void setPlaybackMode(PlaybackMode playbackMode) {
		this.playbackMode = playbackMode;
		firePlaybackModeChanged();
	}
	
	public PlaybackMode getPlaybackMode() {
		return this.playbackMode;
	}
	
	private void reachedEndOfTrack() {
		stop();
		// if player mode is REPEAT ONE, do nothing
		if(playbackMode == PlaybackMode.REPEAT_ALL) {
			if(getQueue().hasNext())
				getQueue().selectNext();
			else
				getQueue().select(0);
		}
		else if(playbackMode == PlaybackMode.NORMAL) {
			if(getQueue().hasNext())
				getQueue().selectNext();
			else {
				stop();
				return;
			}
		}
		else if(playbackMode == PlaybackMode.SHUFFLE)
			getQueue().selectShuffledNext();
		loadSelectedFileFromQueue();
		play();
	}
	
	//observer methods
	public void addObserver(PlayerObserver observer) {
		observerList.add(observer);
	}
	
	public void removeObserver(PlayerObserver observer) {
		observerList.remove(observer);
	}
	
	public void firePlayStarted() {
		for(int i = 0; i < observerList.size();i++) {
			observerList.get(i).notifyPlayerStarted(this);
		}
	}
	
	public void firePlayPaused() {
		for(int i = 0; i < observerList.size();i++) {
			observerList.get(i).notifyPlayerPaused(this);
		}
	}
	
	public void firePlayStopped() {
		for(int i = 0; i < observerList.size();i++) {
			observerList.get(i).notifyPlayerStoped(this);
		}
	}
	
	public void fireMediaChanged() {
		for(int i = 0; i < observerList.size();i++) {
			observerList.get(i).notifyPlayerMediaChanged(this);
		}
	}
	
	public void fireTimeChanged() {
		for(int i = 0; i < observerList.size();i++) {
			observerList.get(i).notifyPlayerTimeChanged(this);
		}
	}
	
	public void firePlaybackModeChanged() {
		for(int i = 0; i < observerList.size();i++) {
			observerList.get(i).notifyPlaybackModeChanged(this);
		}
	}

	//update elapsed time on GUI
	private void timeUpdate() {
		System.out.println("Thread running");
		while(playNotifyThreadRun && !Thread.interrupted()) {
			if(mediaPlayer != null)
				fireTimeChanged();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void timeStart() {
		playNotifyThreadRun = true;
		playNotifyThread = new Thread(() -> timeUpdate());
		playNotifyThread.start();
	}
	private void timeStop() {
		playNotifyThreadRun = false;
	}
	
	private void printPlayerStatus() {
		if(mediaPlayer != null)
			System.out.println("Player status changed to:" + mediaPlayer.getStatus().toString());
	}

	@Override
	public void notifyPlaybackQueueChanged(PlaybackQueue queue) {
		//this isn't interesting for me
	}

	@Override
	public void playedTrackRemoved() {
		stop();
	}

	public Library getLibrary() {
		return library;
	}

	public int getCurrentTime() {
		if(mediaPlayer != null)
			return (int)mediaPlayer.getCurrentTime().toSeconds();
		else return 0;
	}

	public PlayerStatus getStatus() {
		if(mediaPlayer != null) {
			if(mediaPlayer.getStatus() == Status.PLAYING)
				return PlayerStatus.PLAYING;
			else if(mediaPlayer.getStatus() == Status.PAUSED) {
				return PlayerStatus.PAUSED;
			}
		}
		return PlayerStatus.STOPPED;
	}

	public void playTrack(LocalSoundTrack track) {
		stop();
		getQueue().clear();
		queue.add((LocalSoundTrack)track);
		playpause();
	}
	
	@Override
	public String toString() {
		return "Lokální přehrávač";
	}
}
