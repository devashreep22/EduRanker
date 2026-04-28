package model;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardData {
    public String teacherPrn;
    public String teacherName;
    public String department;
    public boolean profileLoaded;
    public boolean anySupabaseDataLoaded;
    public String sourceMessage;
    public List<TeacherClassRecord> classRecords;
    public List<TeacherStudentRecord> studentRecords;
    public List<TeacherAssignmentRecord> assignmentRecords;
    public List<TeacherNoticeRecord> noticeRecords;
    public List<TeacherSubmissionReviewRecord> reviewRecords;

    public TeacherDashboardData() {
        this.teacherPrn = "";
        this.teacherName = "Teacher";
        this.department = "Department";
        this.profileLoaded = false;
        this.anySupabaseDataLoaded = false;
        this.sourceMessage = "Using sample data";
        this.classRecords = new ArrayList<>();
        this.studentRecords = new ArrayList<>();
        this.assignmentRecords = new ArrayList<>();
        this.noticeRecords = new ArrayList<>();
        this.reviewRecords = new ArrayList<>();
    }
}
