package miksa.generics;

public class ArtistString {
	private final String textRepresentation;
	
	public ArtistString(String textRepresentation) {
		this.textRepresentation = textRepresentation;
	}

	public String getTextRepresentation() {
		return textRepresentation;
	}

	@Override
	public final String toString() {
		return this.textRepresentation;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof ArtistString) {
			ArtistString tmp = (ArtistString) object;
			return textRepresentation.equals(tmp.getTextRepresentation());
		}
		else if(object instanceof String) {
			return textRepresentation.equals(object.toString());
		}
		else return false;
	}
}
