package hello;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagesList {

	private List<EmailMessages> value;

	public List<EmailMessages> getValue() {
		return value;
	}

	public void setValue(List<EmailMessages> value) {
		this.value = value;
	}

}
