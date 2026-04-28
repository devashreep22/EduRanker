# EduRanker Backend Setup Guide

## Supabase Configuration

**Project ID:** sjqbrvdjafdhxdmvysyv
**URL:** https://sjqbrvdjafdhxdmvysyv.supabase.co
**Status:** ✅ API Credentials Configured

---

## Database Setup Instructions

### Step 1: Open Supabase SQL Editor
1. Go to your dashboard: https://supabase.com/dashboard/project/sjqbrvdjafdhxdmvysyv/editor/17560
2. Click on **SQL Editor** (on the left sidebar)
3. Click **New Query**

---

### Step 2: Create Tables

#### Table 1: Users Table
Copy and paste this SQL into your Supabase editor:

```sql
-- Create users table
CREATE TABLE IF NOT EXISTS users (
  prn TEXT PRIMARY KEY,
  password TEXT NOT NULL,
  name TEXT NOT NULL,
  email TEXT UNIQUE,
  role TEXT NOT NULL DEFAULT 'student', -- 'student', 'teacher', 'admin'
  class_name TEXT,
  department TEXT,
  
  -- Dashboard fields
  headline TEXT DEFAULT 'Welcome to EduRanker',
  primary_guide_title TEXT DEFAULT 'Get Started',
  primary_guide_text TEXT DEFAULT 'Upload your achievements and track your progress',
  secondary_guide_title TEXT DEFAULT 'Stay Engaged',
  secondary_guide_text TEXT DEFAULT 'Participate in workshops and competitions',
  
  -- Performance metrics
  current_rank INTEGER DEFAULT 0,
  total_students INTEGER DEFAULT 1,
  percentile INTEGER DEFAULT 0,
  cgpa DECIMAL(3,2) DEFAULT 0,
  
  -- Progress tracking
  academics_progress INTEGER DEFAULT 0,
  coding_progress INTEGER DEFAULT 0,
  clubs_progress INTEGER DEFAULT 0,
  
  -- Monthly progress (JSON array)
  monthly_progress JSON,
  
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Insert sample users for testing
INSERT INTO users (prn, password, name, email, role, class_name, department, current_rank, total_students, percentile, cgpa) 
VALUES 
  ('101', 'password123', 'Saniya', 'saniya@example.com', 'student', 'Computer Engineering', 'Computer Science', 3, 150, 95, 8.5),
  ('102', 'password123', 'John Doe', 'john@example.com', 'student', 'Computer Engineering', 'Computer Science', 5, 150, 90, 8.2),
  ('TEACHER1', 'teacher123', 'Prof. Smith', 'smith@example.com', 'teacher', '', 'Computer Science', 0, 0, 0, 0)
ON CONFLICT (prn) DO NOTHING;
```

#### Table 2: Submissions Table
```sql
CREATE TABLE IF NOT EXISTS submissions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_prn TEXT NOT NULL,
  title TEXT NOT NULL,
  type TEXT NOT NULL, -- 'project', 'certificate', 'workshop'
  description TEXT,
  status TEXT DEFAULT 'pending', -- 'pending', 'approved', 'rejected'
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (student_prn) REFERENCES users(prn) ON DELETE CASCADE
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_submissions_student ON submissions(student_prn);

-- Insert sample submissions
INSERT INTO submissions (student_prn, title, type, description, status) 
VALUES 
  ('101', 'AI Project Demo', 'project', 'Machine Learning classification project', 'approved'),
  ('101', 'Python Certification', 'certificate', 'Python Advanced Course Certificate', 'approved'),
  ('101', 'Web Dev Workshop', 'workshop', 'Full Stack Web Development Workshop', 'pending'),
  ('101', 'Mobile App', 'project', 'Android Development Project', 'approved'),
  ('101', 'Advanced Java Course', 'certificate', 'Java Advanced Programming Certificate', 'pending'),
  ('101', 'Data Science Workshop', 'workshop', 'Data Science and Analytics Workshop', 'approved'),
  ('101', 'Blockchain Project', 'project', 'Blockchain Implementation Project', 'rejected'),
  ('101', 'AWS Certification', 'certificate', 'AWS Solutions Architect Certificate', 'approved'),
  ('101', 'Cloud Computing Workshop', 'workshop', 'Cloud Computing Basics Workshop', 'approved'),
  ('101', 'React JS Project', 'project', 'React JS Frontend Project', 'approved');
```

#### Table 3: Achievements Table
```sql
CREATE TABLE IF NOT EXISTS achievements (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_prn TEXT NOT NULL,
  title TEXT NOT NULL,
  category TEXT NOT NULL, -- 'project', 'certificate', 'workshop'
  description TEXT,
  achieved_date DATE,
  certificate_url TEXT,
  badge_earned BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (student_prn) REFERENCES users(prn) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_achievements_student ON achievements(student_prn);

-- Insert sample achievements
INSERT INTO achievements (student_prn, title, category, achieved_date, badge_earned) 
VALUES 
  ('101', 'AI Project Demo', 'project', '2026-04-15', true),
  ('101', 'Python Certification', 'certificate', '2026-04-10', true),
  ('101', 'Web Dev Workshop', 'workshop', '2026-04-20', true),
  ('101', 'Mobile App', 'project', '2026-03-28', true),
  ('101', 'Advanced Java Course', 'certificate', '2026-04-25', false),
  ('101', 'Data Science Workshop', 'workshop', '2026-03-15', true),
  ('101', 'AWS Certification', 'certificate', '2026-04-01', true),
  ('101', 'Cloud Computing Workshop', 'workshop', '2026-03-10', true),
  ('101', 'React JS Project', 'project', '2026-04-05', true);
```

#### Table 4: Performance Records Table (Optional - for tracking history)
```sql
CREATE TABLE IF NOT EXISTS performance_records (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_prn TEXT NOT NULL,
  month DATE,
  academics_score INTEGER,
  coding_score INTEGER,
  clubs_score INTEGER,
  created_at TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (student_prn) REFERENCES users(prn) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_performance_student ON performance_records(student_prn);
```

---

### Step 3: Enable Row Level Security (RLS)

```sql
-- Enable RLS on all tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE submissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE achievements ENABLE ROW LEVEL SECURITY;
ALTER TABLE performance_records ENABLE ROW LEVEL SECURITY;

-- Create policy to allow public access (for now - change later for security)
CREATE POLICY "Enable read access for all users" ON users FOR SELECT USING (true);
CREATE POLICY "Enable read access for submissions" ON submissions FOR SELECT USING (true);
CREATE POLICY "Enable read access for achievements" ON achievements FOR SELECT USING (true);
CREATE POLICY "Enable read access for performance" ON performance_records FOR SELECT USING (true);

-- Allow inserts for submissions
CREATE POLICY "Enable insert for submissions" ON submissions FOR INSERT WITH CHECK (true);

-- Allow updates for submissions (teacher/admin only - implement in Java)
CREATE POLICY "Enable update for submissions" ON submissions FOR UPDATE USING (true);
```

---

### Step 4: How to Run in Supabase

1. **Login** to https://supabase.com/dashboard
2. **Select your project** (sjqbrvdjafdhxdmvysyv)
3. **Go to SQL Editor** → Click "New Query"
4. **Copy and paste** each SQL block above
5. **Click Run** (green play button)
6. Repeat for each table

---

## Java Backend Integration

The following Java services are now connected:

### 1. **AuthService** - Login
```java
User user = AuthService.login("101", "password123");
```

### 2. **DashboardService** - Load dashboard data
```java
DashboardData data = DashboardService.loadDashboard(user);
```

### 3. **SubmissionService** - NEW! Submissions & Achievements
```java
// Get all submissions for a user
List<Map<String, String>> submissions = SubmissionService.getSubmissions("101");

// Get submission stats
Map<String, Integer> stats = SubmissionService.getSubmissionStats("101");
// Returns: {total: 10, approved: 7, pending: 2, rejected: 1}

// Get achievements
List<Map<String, String>> achievements = SubmissionService.getAchievements("101");

// Get achievement counts
Map<String, Integer> counts = SubmissionService.getAchievementCounts("101");
// Returns: {projects: 3, certificates: 3, workshops: 3}

// Create new submission
SubmissionService.createSubmission("101", "My Project", "project", "Description here");

// Update submission status (teacher/admin)
SubmissionService.updateSubmissionStatus(submissionId, "approved");
```

---

## API Endpoints Available

All requests go to: `https://sjqbrvdjafdhxdmvysyv.supabase.co`

### Users
- GET `/rest/v1/users?select=*`
- GET `/rest/v1/users?select=*&prn=eq.101`

### Submissions
- GET `/rest/v1/submissions?select=*&student_prn=eq.101`
- POST `/rest/v1/submissions` (requires auth)
- PATCH `/rest/v1/submissions?id=eq.{id}` (teacher/admin)
- DELETE `/rest/v1/submissions?id=eq.{id}` (teacher/admin)

### Achievements
- GET `/rest/v1/achievements?select=*&student_prn=eq.101`
- POST `/rest/v1/achievements` (requires auth)

---

## Test Credentials

**Student Login:**
- PRN: `101`
- Password: `password123`
- Name: Saniya

**Teacher Login:**
- PRN: `TEACHER1`
- Password: `teacher123`

---

## Troubleshooting

### Error: "API Error 401"
- Check API Key in `Config.java`
- Verify RLS policies are enabled

### Error: "Table does not exist"
- Run the SQL scripts in Supabase SQL Editor
- Wait 2-3 seconds after each query completes

### No data showing
- Check that sample data INSERT statements ran successfully
- Verify the API endpoint syntax

---

## Next Steps

1. ✅ Run SQL scripts in Supabase
2. ✅ Update your Java code to use SubmissionService
3. 📋 Create UI components that call these services
4. 🔒 Implement proper authentication and RLS policies
5. 📊 Add more dashboard visualizations

