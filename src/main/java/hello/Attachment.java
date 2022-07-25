package hello;

public class Attachment {

	private String Name;
	private String ContentType;

	private byte[] ContentBytes;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getContentType() {
		return ContentType;
	}

	public void setContentType(String contentType) {
		ContentType = contentType;
	}

	public byte[] getContentBytes() {
		return ContentBytes;
	}

	public void setContentBytes(byte[] contentBytes) {
		ContentBytes = contentBytes;
	}

}
