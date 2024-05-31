package miksa.musicplayer.localplayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.flac.FlacTagReader;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.audio.wav.WavFileReader;
import org.jaudiotagger.audio.wav.WavTagReader;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v24FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.wav.WavTag;

import javafx.scene.image.Image;
import miksa.generics.AlbumString;
import miksa.generics.ArtistString;
import miksa.musicplayer.localplayer.tracks.FlacSoundTrack;
import miksa.musicplayer.localplayer.tracks.LocalSoundTrack;

//MP3 specs reference https://www.datavoyage.com/mpgscript/mpeghdr.htm

public class LocalSoundTrackFactory {
	private String allowedTypes[] = {"mp3","wav","flac"}; 
	private MP3FileReader mp3filereader = new MP3FileReader();
	private FlacFileReader flacfilereader = new FlacFileReader();
	private WavFileReader wavfilereader = new WavFileReader();
	
	public LocalSoundTrack createSoundTrackFromFile(File file) {
		String filename = file.getName();
		if(isSoundFile(filename)) {
			String filetype = getFileType(filename).toUpperCase();
			if(filetype.equals("MP3")) {
				return createMP3File(file);
			}
			if(filetype.equals("WAV")) {
				return createWAVFile(file);
			}
			if(filetype.equals("FLAC")) {
				return createFlacFile(file);
			}
		}
		return null;
	}

	private String getFileType(String filename) {
		int index = filename.lastIndexOf('.');
		if(index < 0)
			return "";
		return filename.substring(index+1);
	}
	
	private boolean isSoundFile(String filename) {
		String extension = getFileType(filename);
		for(int i = 0; i < allowedTypes.length;i++) {
			if(extension.toLowerCase().equals(allowedTypes[i]))
				return true;
		}
		return false;
	}
	//Zdroj - https://openjfx.io/javadoc/16/javafx.media/javafx/scene/media/package-summary.html
	// https://www.datavoyage.com/mpgscript/mpeghdr.htm
	private LocalSoundTrack createMP3File(File file) {
		LocalSoundTrack st = new LocalSoundTrack(file.getAbsolutePath(), file.toURI().toString(), file.getName(), "MP3");
		
		MP3File audiofile;
		try {
			audiofile = (MP3File) (mp3filereader.read(file));
			int duration = audiofile.getAudioHeader().getTrackLength();
			st.setDuration(duration);
			st.setDurationStr(this.createTimeFormat(duration));
		} catch (Exception e2) {
			e2.printStackTrace();
			return st;
		}
		
		//reload MP3 tags
		ID3v24Tag tag = audiofile.getID3v2TagAsv24();
		String artist = tag.getFirst(ID3v24FieldKey.ARTIST);
		if(!artist.equals(""))
			st.setArtist(new ArtistString(artist));
		String album = tag.getFirst(ID3v24FieldKey.ALBUM);
		if(!album.equals(""))
			st.setAlbum(new AlbumString(album));
		String title = tag.getFirst(ID3v24FieldKey.TITLE);
		if(!title.equals(""))
			st.setTitle(title);
		try {
			st.setTracknumber(Integer.parseInt(tag.getFirst(ID3v24FieldKey.TRACK)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		try {
			st.setYear(Integer.parseInt(tag.getFirst(ID3v24FieldKey.YEAR)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		try {
			st.setDiscnumber(Integer.parseInt(tag.getFirst(ID3v24FieldKey.DISC_NO)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		//write image
		Thread thread = new Thread(() -> loadImage(tag, st));
		thread.start();
		return st;
	}
	
	private void loadImage(ID3v24Tag tag, LocalSoundTrack output) {
		//write image
		Artwork artwork = tag.getFirstArtwork();
		if(artwork != null) {
			InputStream is = new ByteArrayInputStream(artwork.getBinaryData());
			output.setAlbumArt(new Image(is));
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private FlacSoundTrack createFlacFile(File file) {
		FlacSoundTrack st = new FlacSoundTrack(file.getAbsolutePath(), file.getName(), "FLAC");
		
		//read flac tags https://www.jthink.net/jaudiotagger/
		/*
		Fields:
		VENDOR
		TITLE
		ARTIST
		ALBUMARTIST
		ALBUM
		DATE (YEAR)
		TRACKNUMBER
		DISCNUMBER
		 */
		
		//Get duration
		int duration = 0;
		try {
			duration = flacfilereader.read(file).getAudioHeader().getTrackLength();
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException
				| InvalidAudioFrameException e1) {
			e1.printStackTrace();
		}
		
		st.setDuration(duration);
		st.setDurationStr(this.createTimeFormat(duration));
		
		//Get ID3 tags
		FlacTagReader ft = new FlacTagReader();
		FlacTag tag;
		try {
			tag = ft.read(file.toPath());
		} catch (CannotReadException | IOException e) {
			e.printStackTrace();
			return null;
		}
		
		String artist = tag.getFirst("ARTIST");
		if(!artist.equals(""))
			st.setArtist(new ArtistString(artist));
		
		String album = tag.getFirst("ALBUM");
		if(!album.equals(""))
			st.setAlbum(new AlbumString(album));
		
		String title = tag.getFirst("TITLE");
		if(!title.equals(""))
			st.setTitle(title);
		try {	
			st.setTracknumber(Integer.parseInt(tag.getFirst("TRACKNUMBER").toString()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		try {	
			st.setDiscnumber(Integer.parseInt(tag.getFirst("DISCNUMBER").toString()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		try {
			st.setYear(Integer.parseInt(tag.getFirst("DATE")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		//write image - the slowest part, the image is not needed immediately
		Thread thread = new Thread(() -> loadImage(tag, st));
		thread.start();
		
		return st;
	}
	
	private void loadImage(FlacTag tag, FlacSoundTrack output) {
		List<MetadataBlockDataPicture> images = tag.getImages();
		if(images.size() > 0) {
			MetadataBlockDataPicture image = tag.getImages().get(0);
			InputStream is = new ByteArrayInputStream(image.getImageData());
			output.setAlbumArt(new Image(is));
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private LocalSoundTrack createWAVFile(File file) {
		LocalSoundTrack st = new LocalSoundTrack(file.getAbsolutePath(), file.toURI().toString(), file.getName(), "WAV");
		AudioFile audiofile;
		//get duration
		try {
			audiofile = wavfilereader.read(file);
			int duration = audiofile.getAudioHeader().getTrackLength();
			st.setDuration(duration);
			st.setDurationStr(this.createTimeFormat(duration));
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e1) {
			e1.printStackTrace();
		}
		
		//load tags
		try {
			
			/*
			WAV tags
			TDRC Year
			TRCK Track number
			TALB album
			TIT2 title
			TPE1 artist
			 */
			
			WavTag tag = new WavTagReader("").read(file.toPath());
			
			String artist = tag.getFirst("TPE1");
			if(!artist.equals(""))
				st.setArtist(new ArtistString(artist));
			
			String album = tag.getFirst("TALB");
			if(!album.equals(""))
				st.setAlbum(new AlbumString(album));
			
			String title = tag.getFirst("TIT2");
			if(!title.equals(""))
				st.setTitle(title);
			
			try {
				st.setTracknumber(Integer.parseInt(tag.getFirst("TRCK").toString()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			try {
				String year = tag.getFirst("TDRC");
				st.setYear(Integer.parseInt(year));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			//write image
			Artwork artwork = tag.getFirstArtwork();
			if(artwork != null) {
				InputStream is = new ByteArrayInputStream(artwork.getBinaryData());
				st.setAlbumArt(new Image(is));
				is.close();
			}
		} catch (CannotReadException | IOException e) {
			e.printStackTrace();
		}
		return st;
	}
	
	private String createTimeFormat(double secondsInput) {
		String output = "";
		//compute minutes
		int minutes = (int)secondsInput / 60;
		if(minutes < 10)
			output = "0"; 
		output += minutes+":";
		//compute seconds
		int seconds = (int)secondsInput % 60;
		if(seconds < 10) 
			output += "0"+seconds; 
		else 
			output += seconds+"";
		return output;
	}
}