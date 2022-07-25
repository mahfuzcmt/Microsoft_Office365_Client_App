package hello;

import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailMessages {

	private String Id;
	private String Sender;

	private String ReplyTo;

	private String Subject;

	private Boolean HasAttachments;
	private Boolean HasRead;// read email

	private Calendar CreatedDateTime;
	private Calendar ReceivedDateTime;

	private List<EmailAddress> ToRecipients;
	private List<EmailAddress> CcRecipients;
	private List<EmailAddress> BccRecipients;

	private List<Attachment> Attachments;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getSender() {
		return Sender;
	}

	public void setSender(String sender) {
		Sender = sender;
	}

	public String getReplyTo() {
		return ReplyTo;
	}

	public void setReplyTo(String replyTo) {
		ReplyTo = replyTo;
	}

	public String getSubject() {
		return Subject;
	}

	public void setSubject(String subject) {
		Subject = subject;
	}

	public Boolean getHasAttachments() {
		return HasAttachments;
	}

	public void setHasAttachments(Boolean hasAttachments) {
		HasAttachments = hasAttachments;
	}

	public Boolean getHasRead() {
		return HasRead;
	}

	public void setHasRead(Boolean hasRead) {
		HasRead = hasRead;
	}

	public Calendar getCreatedDateTime() {
		return CreatedDateTime;
	}

	public void setCreatedDateTime(Calendar createdDateTime) {
		CreatedDateTime = createdDateTime;
	}

	public Calendar getReceivedDateTime() {
		return ReceivedDateTime;
	}

	public void setReceivedDateTime(Calendar receivedDateTime) {
		ReceivedDateTime = receivedDateTime;
	}

	public List<EmailAddress> getToRecipients() {
		return ToRecipients;
	}

	public void setToRecipients(List<EmailAddress> toRecipients) {
		ToRecipients = toRecipients;
	}

	public List<EmailAddress> getCcRecipients() {
		return CcRecipients;
	}

	public void setCcRecipients(List<EmailAddress> ccRecipients) {
		CcRecipients = ccRecipients;
	}

	public List<EmailAddress> getBccRecipients() {
		return BccRecipients;
	}

	public void setBccRecipients(List<EmailAddress> bccRecipients) {
		BccRecipients = bccRecipients;
	}

	public List<Attachment> getAttachments() {
		return Attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		Attachments = attachments;
	}

	@Override
	public String toString() {
		return "EmailMessages [Id=" + Id + ", Sender=" + Sender + ", ReplyTo=" + ReplyTo + ", Subject=" + Subject
				+ ", HasAttachments=" + HasAttachments + ", HasRead=" + HasRead + ", CreatedDateTime=" + CreatedDateTime
				+ ", ReceivedDateTime=" + ReceivedDateTime + ", ToRecipients=" + ToRecipients + ", CcRecipients="
				+ CcRecipients + ", BccRecipients=" + BccRecipients + ", Attachments=" + Attachments + "]";
	}

}
