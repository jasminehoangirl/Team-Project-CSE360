// Email.java
import java.time.LocalDateTime;
import java.util.Objects;

public class Email {
    private int id;
    private String fromUser;
    private String toUser;
    private String subject;
    private String content;
    private LocalDateTime sentDate;
    private EmailStatus status;
    private boolean isRead;

    public enum EmailStatus {
        SENT,
        DRAFT,
        DELETED
    }

    // Constructors
    public Email() {
        this.sentDate = LocalDateTime.now();
        this.status = EmailStatus.DRAFT;
        this.isRead = false;
    }

    public Email(String fromUser, String toUser, String subject, String content) {
        this();
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.subject = subject;
        this.content = content;
    }

    public Email(int id, String fromUser, String toUser, String subject, String content,
                LocalDateTime sentDate, EmailStatus status, boolean isRead) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.subject = subject;
        this.content = content;
        this.sentDate = sentDate;
        this.status = status;
        this.isRead = isRead;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFromUser() { return fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }

    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSentDate() { return sentDate; }
    public void setSentDate(LocalDateTime sentDate) { this.sentDate = sentDate; }

    public EmailStatus getStatus() { return status; }
    public void setStatus(EmailStatus status) { this.status = status; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    // Utility methods
    public boolean isValid() {
        return fromUser != null && !fromUser.trim().isEmpty() &&
               toUser != null && !toUser.trim().isEmpty() &&
               subject != null && !subject.trim().isEmpty() &&
               content != null;
    }

    public Email createReply() {
        Email reply = new Email();
        reply.setToUser(this.fromUser);
        reply.setSubject("Re: " + this.subject);
        reply.setContent("\n\n-------- Original Message --------\n" +
                        "From: " + this.fromUser + "\n" +
                        "Date: " + this.sentDate + "\n" +
                        "Subject: " + this.subject + "\n\n" +
                        this.content);
        return reply;
    }

    public Email createForward() {
        Email forward = new Email();
        forward.setSubject("Fwd: " + this.subject);
        forward.setContent("\n\n-------- Forwarded Message --------\n" +
                          "From: " + this.fromUser + "\n" +
                          "Date: " + this.sentDate + "\n" +
                          "Subject: " + this.subject + "\n\n" +
                          this.content);
        return forward;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return id == email.id &&
               isRead == email.isRead &&
               Objects.equals(fromUser, email.fromUser) &&
               Objects.equals(toUser, email.toUser) &&
               Objects.equals(subject, email.subject) &&
               Objects.equals(content, email.content) &&
               Objects.equals(sentDate, email.sentDate) &&
               status == email.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromUser, toUser, subject, content, sentDate, status, isRead);
    }

    @Override
    public String toString() {
        return "Email{" +
               "id=" + id +
               ", from='" + fromUser + '\'' +
               ", to='" + toUser + '\'' +
               ", subject='" + subject + '\'' +
               ", sentDate=" + sentDate +
               ", status=" + status +
               ", isRead=" + isRead +
               '}';
    }
}
