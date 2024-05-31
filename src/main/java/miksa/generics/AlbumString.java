package miksa.generics;

public class AlbumString {
	private String textRepresentation;
	
	public AlbumString(String textRepresentation) {
		super();
		this.textRepresentation = textRepresentation;
	}

	public String getTextRepresentation() {
		return textRepresentation;
	}

	public void setTextRepresentation(String textRepresentation) {
		this.textRepresentation = textRepresentation;
	}

	@Override
	public final String toString() {
		return this.textRepresentation;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof AlbumString) {
			AlbumString tmp = (AlbumString) object;
			return textRepresentation.equals(tmp.getTextRepresentation());
		}
		else if(object instanceof String) {
			return textRepresentation.equals(object.toString());
		}
		else return false;
	}
}
