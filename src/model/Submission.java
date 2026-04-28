package model;

public class Submission {
    public String id;
    public String studentPrn;
    public String studentName;
    public String title;
    public String type;
    public String description;
    public String file_url;
    public String status;
    public String createdAt;
    public String updatedAt;
    public String user_id;

    public Submission() {
    }

    public Submission(String title, String type, String file_url, String status) {
        this.title = title;
        this.type = type;
        this.file_url = file_url;
        this.status = status;
    }

    public Submission(String title, String type, String file_url, String status, String user_id) {
        this.title = title;
        this.type = type;
        this.file_url = file_url;
        this.status = status;
        this.user_id = user_id;
    }

    public boolean isApproved() {
        return "approved".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return status == null || status.isBlank() || "pending".equalsIgnoreCase(status);
    }
}
