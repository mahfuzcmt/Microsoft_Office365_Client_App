package hello;

import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailMessages {

	private String Id;

	private String ReplyTo;

	private String Subject;

	private String Body;

	private Boolean HasAttachments;
	private Boolean HasRead;// read email

	private Calendar CreatedDateTime;
	private Calendar ReceivedDateTime;

	private EmailAddress Sender;

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

	public String getBody() {
		return Body;
	}

	public void setBody(String body) {
		Body = body;
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

	public EmailAddress getSender() {
		return Sender;
	}

	public void setSender(EmailAddress sender) {
		Sender = sender;
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
		return "EmailMessages{" +
				"Id='" + Id + '\'' +
				", ReplyTo='" + ReplyTo + '\'' +
				", Subject='" + Subject + '\'' +
				", Body='" + Body + '\'' +
				", HasAttachments=" + HasAttachments +
				", HasRead=" + HasRead +
				", CreatedDateTime=" + CreatedDateTime +
				", ReceivedDateTime=" + ReceivedDateTime +
				", Sender=" + Sender +
				", ToRecipients=" + ToRecipients +
				", CcRecipients=" + CcRecipients +
				", BccRecipients=" + BccRecipients +
				", Attachments=" + Attachments +
				'}';
	}
}
