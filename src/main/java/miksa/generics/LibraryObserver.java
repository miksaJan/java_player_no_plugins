package miksa.generics;

import miksa.musicplayer.localplayer.library.Library;

public interface LibraryObserver {
	void notifyLibraryChanged(Library library);
}
