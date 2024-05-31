package miksa.generics;

import miksa.musicplayer.localplayer.LocalPlayer;

public interface PlayerObserver {
	void notifyPlayerStarted(LocalPlayer sender);
	void notifyPlayerPaused(LocalPlayer sender);
	void notifyPlayerStoped(LocalPlayer sender);
	void notifyPlayerMediaChanged(LocalPlayer sender);
	void notifyPlayerVolumeChanged(LocalPlayer sender);
	void notifyPlayerTimeChanged(LocalPlayer sender);
	void notifyPlaybackModeChanged(LocalPlayer sender);
}
