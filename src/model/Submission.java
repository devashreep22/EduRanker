package model;

public class Submission {
    public String title;
    public String type;
    public String file_url;
    public String status;

    public Submission() {
    }

    public Submission(String title, String type, String file_url, String status) {
        this.title = title;
        this.type = type;
        this.file_url = file_url;
        this.status = status;
    }

    public boolean isApproved() {
        return "approved".equalsIgnoreCase(status);
    }
}
