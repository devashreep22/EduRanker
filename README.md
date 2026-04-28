# 🎓 EduRanker – Student Performance Ranking System

EduRanker is a desktop-based application developed using **Java Swing (GUI)** and **Supabase (Backend)** to manage, evaluate, and rank students based on academic and extracurricular achievements.

This project demonstrates the use of **Object-Oriented Programming (OOP)** concepts in building a real-world system with role-based access.

---

## 📌 Project Overview

EduRanker provides a centralized system where:

- Students can upload achievements (projects, certificates, workshops)
- Teachers verify and approve submissions
- The system calculates rankings automatically

This ensures:
- Fair evaluation
- Transparency
- Efficient data management

---

## 🎯 Objectives

- Apply OOP principles in a practical application
- Develop a GUI using Java Swing
- Integrate cloud backend using Supabase
- Implement role-based login system
- Create an automated ranking system

---

## 👥 User Roles

### 👨‍🎓 Student
- Login using PRN and Password
- View personal dashboard
- Upload:
  - Projects
  - Certificates
  - Workshops
- Track submission status (Pending / Approved)
- View rank and performance metrics

---

### 👨‍🏫 Teacher (Admin)
- Login access
- View all student submissions
- Approve / Reject submissions
- View performance and rankings of students

---

## ⚙️ System Workflow

### 🔐 Login Flow
User enters PRN & Password → System verifies from Supabase → Role identified → Dashboard opened

---

### 📤 Submission Flow
Student uploads data → Stored in database → Status set to "Pending"

---

### ✅ Approval Flow
Teacher reviews submissions → Approves or rejects → Status updated

---

### 🏆 Ranking Flow
Approved data fetched → Score calculated → Students sorted → Rank assigned

---

## 🧠 Ranking Algorithm

Score is calculated using:

Score =  
(CGPA × 10) +  
(Projects × 5) +  
(Certificates × 3) +  
(Workshops × 2) +  
(Coding Score × 5) +  
(Teacher Rating × 4)

Students are ranked based on highest score.

---

## 🏗️ Tech Stack

| Layer       | Technology |
|------------|----------|
| Frontend   | Java Swing |
| Backend    | Supabase (PostgreSQL + REST API) |
| Language   | Java |
| Library    | Gson (JSON Parsing) |

---

## 📂 Project Structure

EduRanker/
│
├── lib/
│ └── gson-2.10.1.jar
│
├── src/
│ ├── Main.java
│ ├── TestApi.java
│
│ ├── util/
│ │ ├── Config.java
│ │ ├── ApiClient.java
│
│ ├── model/
│ │ ├── User.java
│
│ ├── service/
│ │ ├── AuthService.java
│
│ ├── ui/
│ ├── LoginFrame.java
│ ├── StudentDashboard.java
│ ├── TeacherDashboard.java



---

## 🔗 Supabase Setup

1. Create a project in Supabase
2. Create tables:

### users
- prn
- password
- role
- name

### students
- cgpa
- coding_score
- teacher_rating

### submissions
- type
- title
- file_url
- status

3. Disable Row Level Security (RLS)
4. Insert sample data

---

## ⚙️ Configuration

Update `Config.java`:

```java
public class Config {
    public static final String SUPABASE_URL = "https://your-project-id.supabase.co";
    public static final String API_KEY = "your-anon-public-key";
}
