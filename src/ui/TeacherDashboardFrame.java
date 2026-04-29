package ui;

import model.OperationResult;
import model.TeacherAssignmentRecord;
import model.TeacherClassRecord;
import model.TeacherDashboardData;
import model.TeacherNoticeRecord;
import model.TeacherStudentRecord;
import model.TeacherSubmissionReviewRecord;
import service.TeacherDashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeacherDashboardFrame extends JFrame {
    private static final Color NAV_BG = new Color(38, 46, 56);
    private static final Color NAV_BUTTON_BG = new Color(52, 63, 76);
    private static final Color NAV_BUTTON_ACTIVE = new Color(239, 193, 67);
    private static final Color CONTENT_BG = new Color(244, 247, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(28, 36, 46);
    private static final Color TEXT_MUTED = new Color(110, 122, 136);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel centerCards = new JPanel(cardLayout);
    private final JLabel statusBar = new JLabel(" Ready");
    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();

    private final List<TeacherClassRecord> classRecords = new ArrayList<>();
    private final List<TeacherStudentRecord> studentRecords = new ArrayList<>();
    private final List<TeacherAssignmentRecord> assignmentRecords = new ArrayList<>();
    private final List<TeacherNoticeRecord> noticeRecords = new ArrayList<>();
    private final List<TeacherSubmissionReviewRecord> reviewRecords = new ArrayList<>();

    private final String teacherPrn;
    private final String teacherName;
    private final String departmentName;
    private final String sourceMessage;

    private DefaultTableModel reviewTableModel;
    private DefaultTableModel attendanceTableModel;
    private DefaultTableModel marksTableModel;
    private DefaultTableModel assignmentTableModel;
    private DefaultTableModel noticeTableModel;
    private JTextField assignmentTitleField;
    private JTextArea assignmentDescriptionArea;
    private JTextArea noticeArea;
    private JComboBox<String> attendanceClassCombo;
    private JComboBox<String> attendanceSubjectCombo;
    private JComboBox<String> marksClassCombo;
    private JComboBox<String> marksSubjectCombo;

    public TeacherDashboardFrame() {
        this(null);
    }

    public TeacherDashboardFrame(String preferredTeacherPrn) {
        TeacherDashboardData dashboardData = TeacherDashboardService.loadDashboard(preferredTeacherPrn);
        teacherPrn = dashboardData.teacherPrn;
        teacherName = dashboardData.teacherName;
        departmentName = dashboardData.department;
        sourceMessage = dashboardData.sourceMessage;

        classRecords.addAll(dashboardData.classRecords);
        studentRecords.addAll(dashboardData.studentRecords);
        assignmentRecords.addAll(dashboardData.assignmentRecords);
        noticeRecords.addAll(dashboardData.noticeRecords);
        reviewRecords.addAll(dashboardData.reviewRecords);

        setTitle("Teacher Dashboard");
        setSize(1260, 760);
        setMinimumSize(new Dimension(1120, 700));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(CONTENT_BG);
        setLayout(new BorderLayout());

        add(createNavigationPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        showCard("Overview");
        statusBar.setText(" " + sourceMessage);
        setLocationRelativeTo(null);
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(NAV_BG);
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBorder(new EmptyBorder(24, 18, 24, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Teacher Portal");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel teacherLabel = new JLabel(teacherName + " - " + departmentName);
        teacherLabel.setForeground(new Color(189, 200, 213));
        teacherLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        teacherLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(teacherLabel);
        panel.add(Box.createVerticalStrut(26));
        panel.add(createMenuButton("Overview"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Reviews"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Classes"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Attendance"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Marks"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Assignments"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Notices"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Students"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Settings"));
        panel.add(Box.createVerticalGlue());
        panel.add(createLogoutButton());

        return panel;
    }

    private JButton createMenuButton(String cardKey) {
        JButton button = new JButton(cardKey);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setPreferredSize(new Dimension(200, 44));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setBackground(NAV_BUTTON_BG);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.addActionListener(event -> showCard(cardKey));
        menuButtons.put(cardKey, button);
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("Logout");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setPreferredSize(new Dimension(200, 44));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setBackground(new Color(201, 84, 84));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.addActionListener(event -> AppNavigator.openLoginWindow(this));
        return button;
    }

    private JPanel createCenterPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(24, 24, 24, 24));

        centerCards.setOpaque(false);
        centerCards.add(createOverviewPanel(), "Overview");
        centerCards.add(createReviewPanel(), "Reviews");
        centerCards.add(createClassesPanel(), "Classes");
        centerCards.add(createAttendancePanel(), "Attendance");
        centerCards.add(createMarksPanel(), "Marks");
        centerCards.add(createAssignmentsPanel(), "Assignments");
        centerCards.add(createNoticesPanel(), "Notices");
        centerCards.add(createStudentsPanel(), "Students");
        centerCards.add(createSettingsPanel(), "Settings");

        wrapper.add(centerCards, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createOverviewPanel() {
        JPanel page = createPageShell("Overview", "Connected teacher summary");
        JPanel stats = new JPanel(new GridLayout(2, 2, 16, 16));
        stats.setOpaque(false);
        stats.add(createStatCard("Teacher", teacherName, teacherPrn == null || teacherPrn.isBlank() ? "No PRN found" : teacherPrn));
        stats.add(createStatCard("Students", String.valueOf(studentRecords.size()), "Loaded from users table"));
        stats.add(createStatCard("Pending Reviews", String.valueOf(countPendingReviews()), "Project and certificate approvals"));
        stats.add(createStatCard("Assignments", String.valueOf(assignmentRecords.size()), "Visible in teacher dashboard"));
        page.add(stats, BorderLayout.CENTER);
        return page;
    }

    private JPanel createReviewPanel() {
        JPanel page = createPageShell("Submission Reviews", "Approve or decline student uploads");

        reviewTableModel = new DefaultTableModel(new String[]{"Student PRN", "Student Name", "Class", "Title", "Type", "Status", "Submitted"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reloadReviewTable();

        JTable table = createTable(reviewTableModel);
        JButton approveButton = createActionButton("Approve");
        approveButton.addActionListener(event -> reviewSelectedSubmission(table, true));

        JButton declineButton = createActionButton("Decline");
        declineButton.addActionListener(event -> reviewSelectedSubmission(table, false));

        JButton fileButton = createActionButton("Open File");
        fileButton.addActionListener(event -> openSelectedSubmissionFile(table));

        JButton refreshButton = createActionButton("Refresh");
        refreshButton.addActionListener(event -> refreshReviews());

        JPanel actions = new JPanel(new GridLayout(1, 4, 12, 0));
        actions.setOpaque(false);
        actions.add(approveButton);
        actions.add(declineButton);
        actions.add(fileButton);
        actions.add(refreshButton);

        page.add(createPanelCard("Review Queue", createTableSection(table, actions)), BorderLayout.CENTER);
        return page;
    }

    private JPanel createClassesPanel() {
        JPanel page = createPageShell("Classes", "Classes mapped to this teacher");
        DefaultTableModel model = new DefaultTableModel(new String[]{"Class", "Subject", "Semester"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (TeacherClassRecord record : classRecords) {
            model.addRow(new Object[]{record.className, record.subject, record.semester});
        }
        page.add(createPanelCard("Assigned Classes", createTableSection(createTable(model), null)), BorderLayout.CENTER);
        return page;
    }

    private JPanel createAttendancePanel() {
        JPanel page = createPageShell("Attendance", "Save class attendance to Supabase");
        attendanceClassCombo = new JComboBox<>(buildClassNames());
        attendanceSubjectCombo = new JComboBox<>(buildSubjects());

        attendanceTableModel = new DefaultTableModel(new String[]{"Student PRN", "Student Name", "Present"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 2 ? Boolean.class : String.class;
            }
        };
        for (TeacherStudentRecord record : studentRecords) {
            attendanceTableModel.addRow(new Object[]{record.id, record.name, record.present});
        }

        JPanel filters = createLabeledFieldRow(
                createFieldBlock("Class", attendanceClassCombo),
                createFieldBlock("Subject", attendanceSubjectCombo)
        );
        JButton saveButton = createActionButton("Save Attendance");
        saveButton.addActionListener(event -> saveAttendance());
        page.add(createPanelCard("Attendance Register", createTableSection(createTable(attendanceTableModel), saveButton, filters)), BorderLayout.CENTER);
        return page;
    }

    private JPanel createMarksPanel() {
        JPanel page = createPageShell("Marks", "Save student marks to Supabase");
        marksClassCombo = new JComboBox<>(buildClassNames());
        marksSubjectCombo = new JComboBox<>(buildSubjects());

        marksTableModel = new DefaultTableModel(new String[]{"Student PRN", "Student Name", "Assignment Marks", "Exam Marks"}, 0);
        for (TeacherStudentRecord record : studentRecords) {
            marksTableModel.addRow(new Object[]{record.id, record.name, record.assignmentMarks, record.examMarks});
        }

        JPanel filters = createLabeledFieldRow(
                createFieldBlock("Class", marksClassCombo),
                createFieldBlock("Subject", marksSubjectCombo)
        );
        JButton saveButton = createActionButton("Save Marks");
        saveButton.addActionListener(event -> saveMarks());
        page.add(createPanelCard("Marks Sheet", createTableSection(createTable(marksTableModel), saveButton, filters)), BorderLayout.CENTER);
        return page;
    }

    private JPanel createAssignmentsPanel() {
        JPanel page = createPageShell("Assignments", "Post assignments for your students");

        assignmentTitleField = new JTextField();
        assignmentDescriptionArea = new JTextArea(4, 20);
        assignmentDescriptionArea.setLineWrap(true);
        assignmentDescriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(createFieldBlock("Title", assignmentTitleField));
        form.add(Box.createVerticalStrut(12));
        form.add(createFieldBlock("Description", new JScrollPane(assignmentDescriptionArea)));
        form.add(Box.createVerticalStrut(12));
        JButton postButton = createActionButton("Post Assignment");
        postButton.addActionListener(event -> postAssignment());
        form.add(postButton);

        assignmentTableModel = new DefaultTableModel(new String[]{"Title", "Description", "Due Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reloadAssignmentTable();

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.add(createPanelCard("New Assignment", form));
        body.add(createPanelCard("Posted Assignments", createTableSection(createTable(assignmentTableModel), null)));
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JPanel createNoticesPanel() {
        JPanel page = createPageShell("Notices", "Post notices that students can read");

        noticeArea = new JTextArea(6, 20);
        noticeArea.setLineWrap(true);
        noticeArea.setWrapStyleWord(true);

        JButton postButton = createActionButton("Post Notice");
        postButton.addActionListener(event -> postNotice());

        JPanel form = new JPanel(new BorderLayout(0, 12));
        form.setOpaque(false);
        form.add(new JScrollPane(noticeArea), BorderLayout.CENTER);
        form.add(postButton, BorderLayout.SOUTH);

        noticeTableModel = new DefaultTableModel(new String[]{"Date", "Notice"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reloadNoticeTable();

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.add(createPanelCard("Compose Notice", form));
        body.add(createPanelCard("Recent Notices", createTableSection(createTable(noticeTableModel), null)));
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JPanel createStudentsPanel() {
        JPanel page = createPageShell("Students", "All students and their submissions");
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(CARD_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        
        // Tab 1: Student Directory
        DefaultTableModel model = new DefaultTableModel(new String[]{"PRN", "Name", "Class", "Attendance", "Assignment Marks", "Exam Marks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (TeacherStudentRecord record : studentRecords) {
            model.addRow(new Object[]{
                    record.id,
                    record.name,
                    record.className,
                    record.attendancePercentage,
                    record.assignmentMarks,
                    record.examMarks
            });
        }
        JPanel studentTable = createTableSection(createTable(model), null);
        tabbedPane.addTab("Student Directory", createPanelCard("All Students", studentTable));
        
        // Tab 2: Student Submissions by PRN
        DefaultTableModel submissionModel = new DefaultTableModel(new String[]{"Student PRN", "Student Name", "Title", "Type", "Status", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (TeacherSubmissionReviewRecord review : reviewRecords) {
            submissionModel.addRow(new Object[]{
                    review.studentPrn,
                    review.studentName,
                    review.title,
                    review.type,
                    review.status,
                    review.createdAt
            });
        }
        
        JPanel submissionTable = createTableSection(createTable(submissionModel), null);
        tabbedPane.addTab("Student Submissions", createPanelCard("All Submissions", submissionTable));
        
        page.add(tabbedPane, BorderLayout.CENTER);
        return page;
    }

    private JPanel createSettingsPanel() {
        JPanel page = createPageShell("Settings", "Update teacher credentials");

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel teacherInfo = new JLabel("Signed in as " + teacherName + " (" + teacherPrn + ")");
        teacherInfo.setFont(new Font("SansSerif", Font.BOLD, 18));
        teacherInfo.setForeground(TEXT_PRIMARY);
        teacherInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField currentPassword = new JPasswordField();
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();

        content.add(teacherInfo);
        content.add(Box.createVerticalStrut(16));
        content.add(createFieldBlock("Current Password", currentPassword));
        content.add(Box.createVerticalStrut(12));
        content.add(createFieldBlock("New Password", newPassword));
        content.add(Box.createVerticalStrut(12));
        content.add(createFieldBlock("Confirm Password", confirmPassword));
        content.add(Box.createVerticalStrut(16));

        JButton updateButton = createActionButton("Update Password");
        updateButton.addActionListener(event -> updatePassword(currentPassword, newPassword, confirmPassword));
        content.add(updateButton);

        page.add(createPanelCard("Profile Settings", content), BorderLayout.CENTER);
        return page;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(226, 232, 240));
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusBar.setForeground(TEXT_PRIMARY);
        panel.add(statusBar, BorderLayout.WEST);
        return panel;
    }

    private JPanel createPageShell(String titleText, String subtitleText) {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setOpaque(false);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        page.add(header, BorderLayout.NORTH);
        return page;
    }

    private JPanel createStatCard(String titleText, String valueText, String detailText) {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.PLAIN, 14));
        title.setForeground(TEXT_MUTED);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("SansSerif", Font.BOLD, 28));
        value.setForeground(TEXT_PRIMARY);

        JLabel detail = new JLabel(detailText);
        detail.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detail.setForeground(TEXT_MUTED);

        body.add(title);
        body.add(Box.createVerticalStrut(10));
        body.add(value);
        body.add(Box.createVerticalStrut(8));
        body.add(detail);

        return createPanelCard("", body);
    }

    private JPanel createPanelCard(String titleText, Component content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        if (titleText != null && !titleText.isBlank()) {
            JLabel title = new JLabel(titleText);
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            title.setForeground(TEXT_PRIMARY);
            title.setBorder(new EmptyBorder(0, 0, 14, 0));
            card.add(title, BorderLayout.NORTH);
        }
        if (content != null) {
            card.add(content, BorderLayout.CENTER);
        }
        return card;
    }

    private JPanel createTableSection(JTable table, Component actionComponent) {
        return createTableSection(table, actionComponent, null);
    }

    private JPanel createTableSection(JTable table, Component actionComponent, Component topComponent) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        if (topComponent != null) {
            panel.add(topComponent, BorderLayout.NORTH);
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        if (actionComponent != null) {
            panel.add(actionComponent, BorderLayout.SOUTH);
        }
        return panel;
    }

    private JPanel createFieldBlock(String labelText, Component field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);
        return panel;
    }

    private JPanel createLabeledFieldRow(JPanel firstField, JPanel secondField) {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.add(firstField);
        row.add(secondField);
        return row;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(TEXT_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(12, 14, 12, 14));
        return button;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(255, 241, 194));
        table.setSelectionForeground(TEXT_PRIMARY);
        return table;
    }

    private void postAssignment() {
        String title = assignmentTitleField.getText().trim();
        String description = assignmentDescriptionArea.getText().trim();
        if (title.isEmpty() || description.isEmpty()) {
            statusBar.setText(" Enter assignment title and description");
            return;
        }

        OperationResult result = TeacherDashboardService.postAssignment(teacherPrn, title, description);
        statusBar.setText(" " + result.message);
        if (!result.success) {
            return;
        }

        TeacherAssignmentRecord record = new TeacherAssignmentRecord();
        record.title = title;
        record.description = description;
        record.dueDate = java.time.LocalDate.now().plusDays(7).toString();
        assignmentRecords.add(0, record);
        reloadAssignmentTable();
        assignmentTitleField.setText("");
        assignmentDescriptionArea.setText("");
    }

    private void postNotice() {
        String text = noticeArea.getText().trim();
        if (text.isEmpty()) {
            statusBar.setText(" Write a notice before posting");
            return;
        }

        OperationResult result = TeacherDashboardService.postNotice(teacherPrn, text);
        statusBar.setText(" " + result.message);
        if (!result.success) {
            return;
        }

        TeacherNoticeRecord record = new TeacherNoticeRecord();
        record.date = java.time.LocalDate.now().toString();
        record.text = text;
        noticeRecords.add(0, record);
        reloadNoticeTable();
        noticeArea.setText("");
    }

    private void saveAttendance() {
        List<TeacherStudentRecord> records = new ArrayList<>();
        for (int row = 0; row < attendanceTableModel.getRowCount(); row++) {
            TeacherStudentRecord record = new TeacherStudentRecord();
            record.id = String.valueOf(attendanceTableModel.getValueAt(row, 0));
            record.name = String.valueOf(attendanceTableModel.getValueAt(row, 1));
            Object presentValue = attendanceTableModel.getValueAt(row, 2);
            record.present = presentValue instanceof Boolean && (Boolean) presentValue;
            records.add(record);
        }

        OperationResult result = TeacherDashboardService.saveAttendance(
                teacherPrn,
                String.valueOf(attendanceClassCombo.getSelectedItem()),
                String.valueOf(attendanceSubjectCombo.getSelectedItem()),
                records
        );
        statusBar.setText(" " + result.message);
    }

    private void saveMarks() {
        List<TeacherStudentRecord> records = new ArrayList<>();
        for (int row = 0; row < marksTableModel.getRowCount(); row++) {
            TeacherStudentRecord record = new TeacherStudentRecord();
            record.id = String.valueOf(marksTableModel.getValueAt(row, 0));
            record.name = String.valueOf(marksTableModel.getValueAt(row, 1));
            record.assignmentMarks = parseIntValue(marksTableModel.getValueAt(row, 2));
            record.examMarks = parseIntValue(marksTableModel.getValueAt(row, 3));
            records.add(record);
        }

        OperationResult result = TeacherDashboardService.saveMarks(
                teacherPrn,
                String.valueOf(marksClassCombo.getSelectedItem()),
                String.valueOf(marksSubjectCombo.getSelectedItem()),
                records
        );
        statusBar.setText(" " + result.message);
    }

    private void updatePassword(JPasswordField currentPassword, JPasswordField newPassword, JPasswordField confirmPassword) {
        if (new String(currentPassword.getPassword()).trim().isEmpty()) {
            statusBar.setText(" Enter current password before updating");
            return;
        }

        OperationResult result = TeacherDashboardService.updatePassword(
                teacherPrn,
                new String(newPassword.getPassword()).trim(),
                new String(confirmPassword.getPassword()).trim()
        );
        statusBar.setText(" " + result.message);
    }

    private void reviewSelectedSubmission(JTable table, boolean approve) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= reviewRecords.size()) {
            statusBar.setText(" Select a submission first");
            return;
        }

        TeacherSubmissionReviewRecord review = reviewRecords.get(selectedRow);
        OperationResult result = TeacherDashboardService.reviewSubmission(review, approve, Math.max(1, studentRecords.size()));
        statusBar.setText(" " + result.message);
        if (result.success) {
            review.status = approve ? "approved" : "rejected";
            reloadReviewTable();
        }
    }

    private void openSelectedSubmissionFile(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= reviewRecords.size()) {
            statusBar.setText(" Select a submission first");
            return;
        }
        String fileUrl = reviewRecords.get(selectedRow).fileUrl;
        if (fileUrl == null || fileUrl.isBlank()) {
            statusBar.setText(" No file URL found for this submission");
            return;
        }

        try {
            Desktop.getDesktop().browse(URI.create(fileUrl));
            statusBar.setText(" Opened submission file");
        } catch (Exception exception) {
            statusBar.setText(" Could not open file URL");
        }
    }

    private void refreshReviews() {
        TeacherDashboardData refreshed = TeacherDashboardService.loadDashboard(teacherPrn);
        reviewRecords.clear();
        reviewRecords.addAll(refreshed.reviewRecords);
        reloadReviewTable();
        statusBar.setText(" Review queue refreshed");
    }

    private void reloadReviewTable() {
        if (reviewTableModel == null) {
            return;
        }
        reviewTableModel.setRowCount(0);
        for (TeacherSubmissionReviewRecord review : reviewRecords) {
            reviewTableModel.addRow(new Object[]{
                    review.studentPrn,
                    review.studentName,
                    review.className,
                    review.title,
                    review.type,
                    review.status,
                    formatDate(review.createdAt)
            });
        }
    }

    private void reloadAssignmentTable() {
        if (assignmentTableModel == null) {
            return;
        }
        assignmentTableModel.setRowCount(0);
        for (TeacherAssignmentRecord record : assignmentRecords) {
            assignmentTableModel.addRow(new Object[]{record.title, record.description, record.dueDate});
        }
    }

    private void reloadNoticeTable() {
        if (noticeTableModel == null) {
            return;
        }
        noticeTableModel.setRowCount(0);
        for (TeacherNoticeRecord record : noticeRecords) {
            noticeTableModel.addRow(new Object[]{record.date, record.text});
        }
    }

    private void showCard(String cardKey) {
        cardLayout.show(centerCards, cardKey);
        highlightMenuButton(cardKey);
        statusBar.setText(" Switched to " + cardKey);
    }

    private void highlightMenuButton(String activeCardKey) {
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            boolean active = entry.getKey().equals(activeCardKey);
            JButton button = entry.getValue();
            button.setBackground(active ? NAV_BUTTON_ACTIVE : NAV_BUTTON_BG);
            button.setForeground(active ? TEXT_PRIMARY : Color.WHITE);
        }
    }

    private String[] buildClassNames() {
        List<String> values = new ArrayList<>();
        for (TeacherClassRecord record : classRecords) {
            if (record.className != null && !record.className.isBlank() && !values.contains(record.className)) {
                values.add(record.className);
            }
        }
        if (values.isEmpty()) {
            values.add("No class data");
        }
        return values.toArray(new String[0]);
    }

    private String[] buildSubjects() {
        List<String> values = new ArrayList<>();
        for (TeacherClassRecord record : classRecords) {
            if (record.subject != null && !record.subject.isBlank() && !values.contains(record.subject)) {
                values.add(record.subject);
            }
        }
        if (values.isEmpty()) {
            values.add("No subject data");
        }
        return values.toArray(new String[0]);
    }

    private int countPendingReviews() {
        int count = 0;
        for (TeacherSubmissionReviewRecord review : reviewRecords) {
            if (review.status == null || review.status.isBlank() || "pending".equalsIgnoreCase(review.status)) {
                count++;
            }
        }
        return count;
    }

    private int parseIntValue(Object value) {
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String formatDate(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.contains("T") ? raw.substring(0, raw.indexOf('T')) : raw;
    }
}
