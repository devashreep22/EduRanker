package model;

import java.util.ArrayList;
import java.util.List;

public class DashboardData {
    public String studentName;
    public String className;
    public String headline;
    public String primaryGuideTitle;
    public String primaryGuideText;
    public String secondaryGuideTitle;
    public String secondaryGuideText;
    public int rank;
    public int totalStudents;
    public int percentile;
    public int academicsProgress;
    public int codingProgress;
    public int clubsProgress;
    public List<Integer> monthlyProgress;

    public DashboardData() {
        this.studentName = "Student";
        this.className = "Class 11";
        this.headline = "AI Guide";
        this.primaryGuideTitle = "Improve Coding";
        this.primaryGuideText = "Focus on data structures and algorithms";
        this.secondaryGuideTitle = "Update Profile";
        this.secondaryGuideText = "Add 3 new projects to reach Top 10";
        this.rank = 12;
        this.totalStudents = 150;
        this.percentile = 92;
        this.academicsProgress = 72;
        this.codingProgress = 86;
        this.clubsProgress = 45;
        this.monthlyProgress = new ArrayList<>();
        this.monthlyProgress.add(28);
        this.monthlyProgress.add(22);
        this.monthlyProgress.add(35);
        this.monthlyProgress.add(24);
        this.monthlyProgress.add(33);
        this.monthlyProgress.add(21);
        this.monthlyProgress.add(31);
    }
}
