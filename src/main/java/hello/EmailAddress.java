package hello;

public class EmailAddress {

	private String Name;

	@Override
	public String toString() {
		return "EmailAddress{" +
				"Name='" + Name + '\'' +
				", Address='" + Address + '\'' +
				'}';
	}

	private String Address;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

}
