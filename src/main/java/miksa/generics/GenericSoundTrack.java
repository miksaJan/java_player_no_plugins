package miksa.generics;

import javafx.scene.image.Image;

public class GenericSoundTrack {
	//metadata
	protected String url;
	protected String uri;
	protected int discnumber;
	protected ArtistString artist = new ArtistString("Unknown artist");
	protected AlbumString album = new AlbumString("Unknown album");
	protected String title = "";
	protected int tracknumber = 0;
	protected int year = 0;
	protected double duration = 0;
	protected String durationStr = "";
	protected Image albumArt = null;
	protected String filetype = "";
	protected String source = "";
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public int getDiscnumber() {
		return discnumber;
	}
	public void setDiscnumber(int discnumber) {
		this.discnumber = discnumber;
	}
	public ArtistString getArtist() {
		return artist;
	}
	public void setArtist(ArtistString artist) {
		this.artist = artist;
	}
	public AlbumString getAlbum() {
		return album;
	}
	public void setAlbum(AlbumString album) {
		this.album = album;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTracknumber() {
		return tracknumber;
	}
	public void setTracknumber(int tracknumber) {
		this.tracknumber = tracknumber;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getDurationStr() {
		return durationStr;
	}
	public void setDurationStr(String durationStr) {
		this.durationStr = durationStr;
	}
	public Image getAlbumArt() {
		return albumArt;
	}
	public void setAlbumArt(Image albumArt) {
		this.albumArt = albumArt;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
}
