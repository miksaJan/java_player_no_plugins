package miksa.generics;

import miksa.musicplayer.localplayer.PlaybackQueue;

public interface PlaybackQueueObserver {
	<T extends GenericSoundTrack> void notifyPlaybackQueueChanged(PlaybackQueue queue);
	void playedTrackRemoved();
}
