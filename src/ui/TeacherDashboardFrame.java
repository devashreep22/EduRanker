package ui;

import model.OperationResult;
import model.TeacherAssignmentRecord;
import model.TeacherClassRecord;
import model.TeacherDashboardData;
import model.TeacherNoticeRecord;
import model.TeacherStudentRecord;
import service.TeacherDashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeacherDashboardFrame extends JFrame {
    private static final Color NAV_BG = new Color(38, 46, 56);
    private static final Color NAV_BUTTON_BG = new Color(52, 63, 76);
    private static final Color NAV_BUTTON_ACTIVE = new Color(239, 193, 67);
    private static final Color NAV_BUTTON_HOVER = new Color(68, 81, 96);
    private static final Color CONTENT_BG = new Color(244, 247, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(28, 36, 46);
    private static final Color TEXT_MUTED = new Color(110, 122, 136);
    private static final Color ACCENT = new Color(239, 193, 67);

    private final CardLayout cardLayout;
    private final JPanel centerCards;
    private final JLabel statusBar;
    private final Map<String, JButton> menuButtons;
    private final List<ClassRecord> classRecords;
    private final List<StudentRecord> studentRecords;
    private final List<AssignmentRecord> assignmentRecords;
    private final List<NoticeRecord> noticeRecords;
    private final String teacherPrn;
    private final String teacherName;
    private final String departmentName;
    private final String sourceMessage;

    private JComboBox<String> attendanceClassCombo;
    private JComboBox<String> attendanceSubjectCombo;
    private JComboBox<String> marksClassCombo;
    private JComboBox<String> marksSubjectCombo;
    private DefaultTableModel attendanceTableModel;
    private DefaultTableModel marksTableModel;
    private JTextField assignmentTitleField;
    private JTextArea assignmentDescriptionArea;
    private DefaultTableModel assignmentTableModel;
    private JTextArea noticeArea;
    private DefaultTableModel noticeTableModel;
    private JTextField studentSearchField;
    private TableRowSorter<DefaultTableModel> studentSorter;

    public TeacherDashboardFrame() {
        this(null);
    }

    public TeacherDashboardFrame(String preferredTeacherPrn) {
        this.cardLayout = new CardLayout();
        this.centerCards = new JPanel(cardLayout);
        this.statusBar = new JLabel(" Ready");
        this.menuButtons = new LinkedHashMap<>();
        TeacherDashboardData dashboardData = TeacherDashboardService.loadDashboard(preferredTeacherPrn);
        this.teacherPrn = dashboardData.teacherPrn;
        this.teacherName = dashboardData.teacherName;
        this.departmentName = dashboardData.department;
        this.sourceMessage = dashboardData.sourceMessage;
        this.classRecords = buildClassRecords(dashboardData.classRecords);
        this.studentRecords = buildStudentRecords(dashboardData.studentRecords);
        this.assignmentRecords = buildAssignmentRecords(dashboardData.assignmentRecords);
        this.noticeRecords = buildNoticeRecords(dashboardData.noticeRecords);

        setTitle("Teacher Dashboard");
        setSize(1200, 700);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(CONTENT_BG);

        add(createNavigationPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightSidebar(), BorderLayout.EAST);
        add(createStatusBar(), BorderLayout.SOUTH);

        showCard("Dashboard");
        statusBar.setText(" " + sourceMessage);
        setLocationRelativeTo(null);
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(NAV_BG);
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(new EmptyBorder(24, 20, 24, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Teacher Portal");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(sourceMessage);
        subtitle.setForeground(new Color(189, 200, 213));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(26));

        panel.add(createMenuButton("Dashboard", "Dashboard"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("My Classes", "Classes"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Attendance", "Attendance"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Marks Entry", "Marks"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Assignments", "Assignments"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Notices", "Notices"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Student List", "Students"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMenuButton("Settings", "Settings"));
        panel.add(Box.createVerticalGlue());
        panel.add(createMenuButton("Logout", "Logout"));

        return panel;
    }

    private JButton createMenuButton(String text, String cardKey) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setPreferredSize(new Dimension(210, 44));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setBackground(NAV_BUTTON_BG);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.addActionListener(event -> {
            System.out.println(text + " button clicked");
            if ("Logout".equals(cardKey)) {
                statusBar.setText(" Logout clicked");
                JOptionPane.showMessageDialog(this, "Logout action triggered.");
                return;
            }
            showCard(cardKey);
        });
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                if (button != menuButtons.get(getCurrentCardKey())) {
                    button.setBackground(NAV_BUTTON_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                if (button != menuButtons.get(getCurrentCardKey())) {
                    button.setBackground(NAV_BUTTON_BG);
                }
            }
        });
        menuButtons.put(cardKey, button);
        return button;
    }

    private JPanel createCenterPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(24, 24, 24, 24));

        centerCards.setOpaque(false);
        centerCards.add(createDashboardHomePanel(), "Dashboard");
        centerCards.add(createMyClassesPanel(), "Classes");
        centerCards.add(createAttendancePanel(), "Attendance");
        centerCards.add(createMarksEntryPanel(), "Marks");
        centerCards.add(createAssignmentsPanel(), "Assignments");
        centerCards.add(createNoticesPanel(), "Notices");
        centerCards.add(createStudentListPanel(), "Students");
        centerCards.add(createSettingsPanel(), "Settings");

        wrapper.add(centerCards, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createRightSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBackground(new Color(233, 238, 245));
        panel.setBorder(new EmptyBorder(24, 18, 24, 18));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel profileTitle = new JLabel("Profile");
        profileTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        profileTitle.setForeground(TEXT_PRIMARY);
        profileTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel profilePhoto = new JLabel(buildTeacherInitials(), SwingConstants.CENTER);
        profilePhoto.setOpaque(true);
        profilePhoto.setBackground(ACCENT);
        profilePhoto.setForeground(TEXT_PRIMARY);
        profilePhoto.setFont(new Font("SansSerif", Font.BOLD, 26));
        profilePhoto.setMaximumSize(new Dimension(96, 96));
        profilePhoto.setPreferredSize(new Dimension(96, 96));
        profilePhoto.setMinimumSize(new Dimension(96, 96));
        profilePhoto.setBorder(new EmptyBorder(30, 30, 30, 30));
        profilePhoto.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel teacherName = new JLabel(this.teacherName);
        teacherName.setFont(new Font("SansSerif", Font.BOLD, 18));
        teacherName.setForeground(TEXT_PRIMARY);
        teacherName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel department = new JLabel(this.departmentName);
        department.setFont(new Font("SansSerif", Font.PLAIN, 14));
        department.setForeground(TEXT_MUTED);
        department.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton quickLogout = new JButton("Logout Shortcut");
        quickLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        quickLogout.setFocusPainted(false);
        quickLogout.setBackground(new Color(210, 84, 84));
        quickLogout.setForeground(Color.WHITE);
        quickLogout.setBorder(new EmptyBorder(12, 16, 12, 16));
        quickLogout.addActionListener(event -> {
            System.out.println("Logout shortcut clicked");
            statusBar.setText(" Logout shortcut clicked");
        });

        content.add(profileTitle);
        content.add(Box.createVerticalStrut(24));
        content.add(profilePhoto);
        content.add(Box.createVerticalStrut(18));
        content.add(teacherName);
        content.add(Box.createVerticalStrut(6));
        content.add(department);
        content.add(Box.createVerticalStrut(24));
        content.add(createPanelCard("Today", createSidebarInfo(
                "09:00 AM - SY BSc",
                "11:00 AM - TY BSc",
                "02:00 PM - Attendance Review"
        )));
        content.add(Box.createVerticalStrut(18));
        content.add(quickLogout);
        content.add(Box.createVerticalGlue());

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSidebarInfo(String lineOne, String lineTwo, String lineThree) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createMutedLabel(lineOne));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createMutedLabel(lineTwo));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createMutedLabel(lineThree));
        return panel;
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

    private JPanel createDashboardHomePanel() {
        JPanel panel = createPageShell("Welcome, Teacher", "Your teaching snapshot for today");
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 18, 18));
        statsGrid.setOpaque(false);
        statsGrid.add(createStatCard("Total Students", String.valueOf(studentRecords.size()), "Across all assigned classes"));
        statsGrid.add(createStatCard("Classes Assigned", String.valueOf(classRecords.size()), "Currently mapped in Supabase"));
        statsGrid.add(createStatCard("Pending Assignments", String.valueOf(assignmentRecords.size()), "Assignment rows available"));
        statsGrid.add(createStatCard("Average Attendance", averageAttendance() + "%", "Computed from loaded student data"));
        panel.add(statsGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMyClassesPanel() {
        JPanel panel = createPageShell("My Classes", "Classes and subjects under your guidance");

        String[] columns = {"Class Name", "Subject", "Semester"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (ClassRecord record : classRecords) {
            model.addRow(new Object[]{record.className, record.subject, record.semester});
        }

        JTable table = createTable(model);
        JButton openClassButton = createActionButton("Open Class");
        openClassButton.addActionListener(event -> {
            System.out.println("Open Class clicked");
            statusBar.setText(" Open Class clicked");
        });

        panel.add(createPanelCard("Assigned Classes", createTableSection(table, openClassButton, null)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = createPageShell("Attendance", "Track and save class-wise attendance");

        attendanceClassCombo = new JComboBox<>(buildClassNames());
        attendanceSubjectCombo = new JComboBox<>(buildSubjects());

        JPanel filters = createLabeledFieldColumn(
                createFieldBlock("Select Class", attendanceClassCombo),
                createFieldBlock("Select Subject", attendanceSubjectCombo)
        );

        String[] columns = {"Student ID", "Name", "Present"};
        attendanceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        for (StudentRecord record : studentRecords) {
            attendanceTableModel.addRow(new Object[]{record.id, record.name, record.present});
        }

        JTable table = createTable(attendanceTableModel);
        JButton saveButton = createActionButton("Save Attendance");
        saveButton.addActionListener(event -> {
            System.out.println("Save Attendance clicked");
            saveAttendance();
        });
        JButton reportButton = createActionButton("View Report");
        reportButton.addActionListener(event -> {
            System.out.println("View Report clicked");
            statusBar.setText(" Viewing attendance report");
        });

        panel.add(createPanelCard("Attendance Register", createTableSection(table, saveButton, reportButton, filters)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMarksEntryPanel() {
        JPanel panel = createPageShell("Marks Entry", "Enter coursework and exam scores");

        marksClassCombo = new JComboBox<>(buildClassNames());
        marksSubjectCombo = new JComboBox<>(buildSubjects());

        JPanel filters = createLabeledFieldColumn(
                createFieldBlock("Class", marksClassCombo),
                createFieldBlock("Subject", marksSubjectCombo)
        );

        String[] columns = {"Student ID", "Name", "Assignment Marks", "Exam Marks"};
        marksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3;
            }
        };

        for (StudentRecord record : studentRecords) {
            marksTableModel.addRow(new Object[]{record.id, record.name, record.assignmentMarks, record.examMarks});
        }

        JTable table = createTable(marksTableModel);
        JButton saveMarksButton = createActionButton("Save Marks");
        saveMarksButton.addActionListener(event -> {
            System.out.println("Save Marks clicked");
            saveMarks();
        });
        JButton calculateButton = createActionButton("Calculate Total");
        calculateButton.addActionListener(event -> {
            System.out.println("Calculate Total clicked");
            statusBar.setText(" Class total: " + calculateTotalMarks());
        });

        panel.add(createPanelCard("Marks Sheet", createTableSection(table, saveMarksButton, calculateButton, filters)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = createPageShell("Assignments", "Create and post assignments for your students");

        assignmentTitleField = new JTextField();
        assignmentDescriptionArea = new JTextArea(4, 20);
        assignmentDescriptionArea.setLineWrap(true);
        assignmentDescriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(createFieldBlock("Title", assignmentTitleField));
        form.add(Box.createVerticalStrut(14));
        form.add(createFieldBlock("Description", new JScrollPane(assignmentDescriptionArea)));
        form.add(Box.createVerticalStrut(14));

        JButton uploadButton = createActionButton("Upload File");
        uploadButton.addActionListener(event -> {
            System.out.println("Upload File clicked");
            statusBar.setText(" Upload File clicked");
        });
        JButton postButton = createActionButton("Post Assignment");
        postButton.addActionListener(event -> postAssignment());

        JPanel actionGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        actionGrid.setOpaque(false);
        actionGrid.add(uploadButton);
        actionGrid.add(postButton);
        form.add(actionGrid);

        assignmentTableModel = new DefaultTableModel(new String[]{"Title", "Description", "Due Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadAssignments();
        JTable table = createTable(assignmentTableModel);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(createPanelCard("Assignment Composer", form));
        body.add(Box.createVerticalStrut(16));
        body.add(createPanelCard("Assignment History", createTableSection(table, null, null)));

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createNoticesPanel() {
        JPanel panel = createPageShell("Notices", "Share important announcements with students");

        noticeArea = new JTextArea(5, 20);
        noticeArea.setLineWrap(true);
        noticeArea.setWrapStyleWord(true);
        JButton postNoticeButton = createActionButton("Post Notice");
        postNoticeButton.addActionListener(event -> postNotice());

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(createFieldBlock("Write Notice", new JScrollPane(noticeArea)));
        form.add(Box.createVerticalStrut(14));
        form.add(postNoticeButton);

        noticeTableModel = new DefaultTableModel(new String[]{"Date", "Notice"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loadNotices();

        JTable table = createTable(noticeTableModel);
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(createPanelCard("Notice Board", form));
        body.add(Box.createVerticalStrut(16));
        body.add(createPanelCard("Notice History", createTableSection(table, null, null)));

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentListPanel() {
        JPanel panel = createPageShell("Student List", "Search and review student performance details");

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Class", "Attendance %"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (StudentRecord record : studentRecords) {
            model.addRow(new Object[]{record.id, record.name, record.className, record.attendancePercentage});
        }

        JTable table = createTable(model);
        studentSorter = new TableRowSorter<>(model);
        table.setRowSorter(studentSorter);

        studentSearchField = new JTextField();
        studentSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                applyStudentFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                applyStudentFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                applyStudentFilter();
            }
        });

        JPanel topArea = new JPanel();
        topArea.setOpaque(false);
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.add(createFieldBlock("Search Student", studentSearchField));

        panel.add(createPanelCard("Student Directory", createTableSection(table, null, null, topArea)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = createPageShell("Settings", "Manage your profile and security");

        JPasswordField currentPassword = new JPasswordField();
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();
        JButton updateButton = createActionButton("Update Profile");
        updateButton.addActionListener(event -> updatePassword(currentPassword, newPassword, confirmPassword));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(createFieldBlock("Current Password", currentPassword));
        form.add(Box.createVerticalStrut(14));
        form.add(createFieldBlock("New Password", newPassword));
        form.add(Box.createVerticalStrut(14));
        form.add(createFieldBlock("Confirm Password", confirmPassword));
        form.add(Box.createVerticalStrut(18));
        form.add(updateButton);

        panel.add(createPanelCard("Security Settings", form), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPageShell(String titleText, String subtitleText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createStatCard(String titleText, String valueText, String detailText) {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.PLAIN, 14));
        title.setForeground(TEXT_MUTED);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("SansSerif", Font.BOLD, 34));
        value.setForeground(TEXT_PRIMARY);

        JLabel detail = new JLabel(detailText);
        detail.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detail.setForeground(TEXT_MUTED);

        content.add(title);
        content.add(Box.createVerticalStrut(14));
        content.add(value);
        content.add(Box.createVerticalStrut(10));
        content.add(detail);

        return createPanelCard("", content);
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

    private JPanel createTableSection(JTable table, JButton primaryButton, JButton secondaryButton) {
        return createTableSection(table, primaryButton, secondaryButton, null);
    }

    private JPanel createTableSection(JTable table, JButton primaryButton, JButton secondaryButton, Component topContent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        if (topContent != null) {
            panel.add(topContent, BorderLayout.NORTH);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(12, 0, 12, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        if (primaryButton != null || secondaryButton != null) {
            JPanel actions = new JPanel();
            actions.setOpaque(false);
            if (primaryButton != null && secondaryButton != null) {
                actions.setLayout(new GridLayout(1, 2, 12, 0));
                actions.add(primaryButton);
                actions.add(secondaryButton);
            } else {
                actions.setLayout(new GridLayout(1, 1, 0, 0));
                actions.add(primaryButton != null ? primaryButton : secondaryButton);
            }
            panel.add(actions, BorderLayout.SOUTH);
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

        if (field instanceof JComponent component) {
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);
        return panel;
    }

    private JPanel createLabeledFieldColumn(JPanel firstField, JPanel secondField) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.setLayout(new GridLayout(1, 2, 16, 0));
        panel.add(firstField);
        panel.add(secondField);
        return panel;
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

    private JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(235, 239, 245));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(255, 241, 194));
        table.setSelectionForeground(TEXT_PRIMARY);
        return table;
    }

    private void postAssignment() {
        String title = assignmentTitleField.getText().trim();
        String description = assignmentDescriptionArea.getText().trim();

        if (title.isEmpty() || description.isEmpty()) {
            statusBar.setText(" Please enter assignment title and description");
            return;
        }

        OperationResult result = TeacherDashboardService.postAssignment(teacherPrn, title, description);
        AssignmentRecord record = new AssignmentRecord(title, description, "Next Monday");
        assignmentRecords.add(0, record);
        assignmentTableModel.insertRow(0, new Object[]{record.title, record.description, record.dueDate});
        assignmentTitleField.setText("");
        assignmentDescriptionArea.setText("");
        System.out.println("Post Assignment clicked");
        statusBar.setText(" " + result.message);
    }

    private void postNotice() {
        String noticeText = noticeArea.getText().trim();

        if (noticeText.isEmpty()) {
            statusBar.setText(" Please write a notice before posting");
            return;
        }

        OperationResult result = TeacherDashboardService.postNotice(teacherPrn, noticeText);
        NoticeRecord record = new NoticeRecord("Today", noticeText);
        noticeRecords.add(0, record);
        noticeTableModel.insertRow(0, new Object[]{record.date, record.text});
        noticeArea.setText("");
        System.out.println("Post Notice clicked");
        statusBar.setText(" " + result.message);
    }

    private void loadAssignments() {
        for (AssignmentRecord record : assignmentRecords) {
            assignmentTableModel.addRow(new Object[]{record.title, record.description, record.dueDate});
        }
    }

    private void loadNotices() {
        for (NoticeRecord record : noticeRecords) {
            noticeTableModel.addRow(new Object[]{record.date, record.text});
        }
    }

    private void applyStudentFilter() {
        String text = studentSearchField.getText().trim();
        if (text.isEmpty()) {
            studentSorter.setRowFilter(null);
        } else {
            studentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
        }
        statusBar.setText(" Filtering student list");
    }

    private void showCard(String cardKey) {
        cardLayout.show(centerCards, cardKey);
        highlightMenuButton(cardKey);
        statusBar.setText(" Switched to " + cardKey);
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

    private int calculateTotalMarks() {
        int total = 0;
        for (int row = 0; row < marksTableModel.getRowCount(); row++) {
            total += parseIntValue(marksTableModel.getValueAt(row, 2));
            total += parseIntValue(marksTableModel.getValueAt(row, 3));
        }
        return total;
    }

    private void updatePassword(JPasswordField currentPassword, JPasswordField newPassword, JPasswordField confirmPassword) {
        System.out.println("Update Profile clicked");
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

    private void highlightMenuButton(String activeCardKey) {
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            boolean active = entry.getKey().equals(activeCardKey);
            JButton button = entry.getValue();
            button.setBackground(active ? NAV_BUTTON_ACTIVE : NAV_BUTTON_BG);
            button.setForeground(active ? TEXT_PRIMARY : Color.WHITE);
        }
        centerCards.putClientProperty("activeCard", activeCardKey);
    }

    private String getCurrentCardKey() {
        Object value = centerCards.getClientProperty("activeCard");
        return value == null ? "Dashboard" : value.toString();
    }

    private String[] buildClassNames() {
        List<String> values = new ArrayList<>();
        for (ClassRecord record : classRecords) {
            values.add(record.className);
        }
        return values.toArray(new String[0]);
    }

    private String[] buildSubjects() {
        List<String> values = new ArrayList<>();
        for (ClassRecord record : classRecords) {
            if (!values.contains(record.subject)) {
                values.add(record.subject);
            }
        }
        return values.toArray(new String[0]);
    }

    private List<ClassRecord> createSampleClasses() {
        List<ClassRecord> records = new ArrayList<>();
        records.add(new ClassRecord("SY BSc CS - A", "Data Structures", "Semester 3"));
        records.add(new ClassRecord("SY BSc CS - B", "OOP with Java", "Semester 3"));
        records.add(new ClassRecord("TY BSc CS - A", "Database Systems", "Semester 5"));
        records.add(new ClassRecord("TY BSc CS - B", "Software Engineering", "Semester 5"));
        records.add(new ClassRecord("FY MSc IT", "Cloud Computing", "Semester 1"));
        records.add(new ClassRecord("SY MSc IT", "Machine Learning", "Semester 3"));
        return records;
    }

    private List<ClassRecord> buildClassRecords(List<TeacherClassRecord> remoteRecords) {
        if (remoteRecords == null || remoteRecords.isEmpty()) {
            return createSampleClasses();
        }
        List<ClassRecord> records = new ArrayList<>();
        for (TeacherClassRecord remote : remoteRecords) {
            records.add(new ClassRecord(remote.className, remote.subject, remote.semester));
        }
        return records;
    }

    private List<StudentRecord> createSampleStudents() {
        List<StudentRecord> records = new ArrayList<>();
        records.add(new StudentRecord("ST101", "Aditi Kulkarni", "SY BSc CS - A", 93, 18, 42, true));
        records.add(new StudentRecord("ST102", "Rohan Patil", "SY BSc CS - A", 88, 16, 39, true));
        records.add(new StudentRecord("ST103", "Neha Sharma", "SY BSc CS - B", 91, 19, 44, true));
        records.add(new StudentRecord("ST104", "Kunal Verma", "TY BSc CS - A", 84, 15, 36, true));
        records.add(new StudentRecord("ST105", "Meera Desai", "TY BSc CS - B", 96, 20, 47, true));
        records.add(new StudentRecord("ST106", "Arjun Singh", "FY MSc IT", 82, 14, 35, true));
        records.add(new StudentRecord("ST107", "Pooja Nair", "SY MSc IT", 89, 17, 41, true));
        records.add(new StudentRecord("ST108", "Vikram Shah", "SY BSc CS - B", 87, 16, 38, true));
        return records;
    }

    private List<StudentRecord> buildStudentRecords(List<TeacherStudentRecord> remoteRecords) {
        if (remoteRecords == null || remoteRecords.isEmpty() || hasNoUsefulStudentClassData(remoteRecords)) {
            return createSampleStudents();
        }
        List<StudentRecord> records = new ArrayList<>();
        for (TeacherStudentRecord remote : remoteRecords) {
            records.add(new StudentRecord(
                    remote.id,
                    remote.name,
                    remote.className,
                    remote.attendancePercentage,
                    remote.assignmentMarks,
                    remote.examMarks,
                    remote.present
            ));
        }
        return records;
    }

    private boolean hasNoUsefulStudentClassData(List<TeacherStudentRecord> remoteRecords) {
        for (TeacherStudentRecord record : remoteRecords) {
            if (record.className != null && !record.className.isBlank()) {
                return false;
            }
        }
        return true;
    }

    private List<AssignmentRecord> createSampleAssignments() {
        List<AssignmentRecord> records = new ArrayList<>();
        records.add(new AssignmentRecord("Linked List Lab", "Implement insert and delete operations", "2026-05-05"));
        records.add(new AssignmentRecord("SQL Practice", "Write joins and aggregate queries", "2026-05-08"));
        records.add(new AssignmentRecord("Mini Project Proposal", "Submit idea summary and timeline", "2026-05-10"));
        return records;
    }

    private List<AssignmentRecord> buildAssignmentRecords(List<TeacherAssignmentRecord> remoteRecords) {
        if (remoteRecords == null || remoteRecords.isEmpty()) {
            return createSampleAssignments();
        }
        List<AssignmentRecord> records = new ArrayList<>();
        for (TeacherAssignmentRecord remote : remoteRecords) {
            records.add(new AssignmentRecord(remote.title, remote.description, remote.dueDate));
        }
        return records;
    }

    private List<NoticeRecord> createSampleNotices() {
        List<NoticeRecord> records = new ArrayList<>();
        records.add(new NoticeRecord("2026-04-28", "Attendance audit will be reviewed on Friday."));
        records.add(new NoticeRecord("2026-04-27", "Assignment rubrics updated for Semester 5."));
        records.add(new NoticeRecord("2026-04-26", "Lab timings shifted to 2 PM for SY BSc CS - A."));
        return records;
    }

    private List<NoticeRecord> buildNoticeRecords(List<TeacherNoticeRecord> remoteRecords) {
        if (remoteRecords == null || remoteRecords.isEmpty()) {
            return createSampleNotices();
        }
        List<NoticeRecord> records = new ArrayList<>();
        for (TeacherNoticeRecord remote : remoteRecords) {
            records.add(new NoticeRecord(remote.date, remote.text));
        }
        return records;
    }

    private String buildTeacherInitials() {
        if (teacherName == null || teacherName.isBlank()) {
            return "T";
        }
        String[] parts = teacherName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }

    private int averageAttendance() {
        if (studentRecords.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (StudentRecord record : studentRecords) {
            total += record.attendancePercentage;
        }
        return total / studentRecords.size();
    }

    private int parseIntValue(Object value) {
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static class ClassRecord {
        private final String className;
        private final String subject;
        private final String semester;

        private ClassRecord(String className, String subject, String semester) {
            this.className = className;
            this.subject = subject;
            this.semester = semester;
        }
    }

    private static class StudentRecord {
        private final String id;
        private final String name;
        private final String className;
        private final int attendancePercentage;
        private final int assignmentMarks;
        private final int examMarks;
        private final boolean present;

        private StudentRecord(String id, String name, String className, int attendancePercentage, int assignmentMarks, int examMarks, boolean present) {
            this.id = id;
            this.name = name;
            this.className = className;
            this.attendancePercentage = attendancePercentage;
            this.assignmentMarks = assignmentMarks;
            this.examMarks = examMarks;
            this.present = present;
        }
    }

    private static class AssignmentRecord {
        private final String title;
        private final String description;
        private final String dueDate;

        private AssignmentRecord(String title, String description, String dueDate) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
        }
    }

    private static class NoticeRecord {
        private final String date;
        private final String text;

        private NoticeRecord(String date, String text) {
            this.date = date;
            this.text = text;
        }
    }
}
