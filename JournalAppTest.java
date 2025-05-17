import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JournalAppTest {

    private JournalApp app;
    private JournalApp app2 = new JournalApp();
    private JournalApp app3 = new JournalApp();

    @BeforeEach
    public void setup() throws IOException {
        app = new JournalApp();

        Workbook workbook = new XSSFWorkbook();

        Sheet studentSheet = workbook.createSheet("Students");
        Row StHead = studentSheet.createRow(0);
        StHead.createCell(0).setCellValue("ID");
        StHead.createCell(1).setCellValue("Full Name");
        StHead.createCell(2).setCellValue("Group ID");
        StHead.createCell(3).setCellValue("Sub001");
        Row student = studentSheet.createRow(1);
        student.createCell(0).setCellValue("S001");
        student.createCell(1).setCellValue("Иванов Иван Иванович");
        student.createCell(2).setCellValue("G01");
        student.createCell(3).setCellValue("5 4");

        Sheet teacherSheet = workbook.createSheet("Teachers");
        Row thHead = teacherSheet.createRow(0);
        thHead.createCell(0).setCellValue("ID");
        thHead.createCell(1).setCellValue("Full Name");
        thHead.createCell(2).setCellValue("Subjects");
        Row teacher = teacherSheet.createRow(1);
        teacher.createCell(0).setCellValue("T001");
        teacher.createCell(1).setCellValue("Петров Петр Петрович");
        teacher.createCell(2).setCellValue("Sub001");

        Sheet groupSheet = workbook.createSheet("Groups");
        Row grHead = groupSheet.createRow(0);
        grHead.createCell(0).setCellValue("ID");
        grHead.createCell(1).setCellValue("Name");
        grHead.createCell(2).setCellValue("Students");
        Row group = groupSheet.createRow(1);
        group.createCell(0).setCellValue("G01");
        group.createCell(1).setCellValue(101);
        group.createCell(2).setCellValue("S001");

        Sheet subjectSheet = workbook.createSheet("Subjects");
        Row subHead = subjectSheet.createRow(0);
        subHead.createCell(0).setCellValue("ID");
        subHead.createCell(1).setCellValue("Name");
        Row subject = subjectSheet.createRow(1);
        subject.createCell(0).setCellValue("Sub001");
        subject.createCell(1).setCellValue("Математика");

        File tempFile = File.createTempFile("testData", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            workbook.write(fos);
        }
        workbook.close();

        app.loadDataFromXLSX(tempFile.getAbsolutePath());

        tempFile.deleteOnExit();
    }

    @Test
    public void loadStudentsfromXLSX() {
        assertEquals(1, app.getStudents().size());
        Student student = app.getStudents().get(0);
        assertEquals("S001", student.getId());
        assertEquals("Иванов Иван Иванович", student.getFullName());
        assertEquals("G01", student.getGroupId());
        assertTrue(student.getGrades().containsKey("Math"));
    }

    @Test
    public void loadTeachersfromXLSX() {
        assertEquals(1, app.getTeachers().size());
        assertEquals("Петров Петр Петрович", app.getTeachers().get(0).getFullName());
    }

    @Test
    public void loadGroupsfromXLSX() {
        assertEquals(1, app.getGroups().size());
        assertEquals("101", app.getGroups().get(0).getName());
    }

    @Test
    public void loadSubjectsfromXLSX() {
        assertEquals(1, app.getSubjects().size());
        assertEquals("Math", app.getSubjects().get(0).getName());
    }

    @org.junit.jupiter.api.Test
    public void loadDataFromJSON() throws IOException {

        String json = """
                {
                  "students": [
                    { "id": "S1", "fullName": "Иванов Иван Иванович", "groupId": "G1", "grades": {} }
                  ],
                  "teachers": [
                    { "id": "T1", "fullName": "Петров Петр Петрович", "subjectIds": [] }
                  ],
                  "groups": [
                    { "id": "G1", "name": "101", "studentIds": ["S1"] }
                  ],
                  "subjects": [
                    { "id": "Sub1", "name": "Math" }
                  ],
                  "groupSchedules": [],
                  "teacherSchedules": []
                }
                """;

        File file = File.createTempFile("testData", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), json);

        app.loadDataFromJSON(file.getAbsolutePath());

        assertEquals(1, app.getStudents().size());
        assertEquals("Иванов Иван Иванович", app.getStudents().get(0).getFullName());

        assertEquals(1, app.getTeachers().size());
        assertEquals("Петров Петр Петрович", app.getTeachers().get(0).getFullName());

        assertEquals(1, app.getGroups().size());
        assertEquals("101", app.getGroups().get(0).getName());

        assertEquals(1, app.getSubjects().size());
        assertEquals("Math", app.getSubjects().get(0).getName());
    }

    @org.junit.jupiter.api.Test
    public void saveDataToJSON() throws IOException {

        app.getStudents().add(new Student("S1", "Иванов Иван Иванович", "G1"));
        app.getTeachers().add(new Teacher("T1", "Петров Петр Петрович", new ArrayList<>()));
        app.getGroups().add(new Group("G1", "101", new ArrayList<>(List.of("S1"))));
        app.getSubjects().add(new Subject("Sub1", "Math"));

        File file = File.createTempFile("testData", ".json");
        file.deleteOnExit();

        app.saveDataToJSON(file.getAbsolutePath());

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @org.junit.jupiter.api.Test
    public void addGradeToStudent() {

        Student student = new Student("S1", "Иванов Иван Иванович", "G1");
        String subjectId = "Sub1";
        Grade grade = new Grade(subjectId, 5);

        app.addGradeToStudent(student, subjectId, grade);

        assertTrue(student.getGrades().containsKey(subjectId));
        assertTrue(student.getGrades().get(subjectId).contains(grade));
    }

    @org.junit.jupiter.api.Test
    public void deleteGradeToStudent() {

        Student student = new Student("S1", "Иванов Иван Иванович", "G1");
        String subjectId = "Sub1";
        Grade grade = new Grade(subjectId, 4);
        student.addGrade(subjectId, grade);

        app.deleteGradeToStudent(student, subjectId, grade);

        assertFalse(student.getGrades().get(subjectId).contains(grade));
    }

    @org.junit.jupiter.api.Test
    public void getGroupScheduleByDay() {

        GroupLesson lesson = new GroupLesson(Time.valueOf("10:00:00"), 1, "Math", "101", "Иванов Иван Иванович");
        GroupSchedule<GroupLesson> schedule = new GroupSchedule<>("G1");
        schedule.addLesson(DayOfWeek.MONDAY, lesson);
        app.getGroupSchedules().add(schedule);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.getGroupScheduleByDay(DayOfWeek.MONDAY, "G1");

        String output = out.toString();
        assertTrue(output.contains("Math"));
    }

    @org.junit.jupiter.api.Test
    public void getFullGroupSchedule() {

        GroupLesson lesson1 = new GroupLesson(Time.valueOf("10:00:00"), 1, "История", "201", "Петров Петр Петрович");
        GroupLesson lesson2 = new GroupLesson(Time.valueOf("12:00:00"), 2, "Биология", "202", "Сидоров Сидр Сидорович");

        GroupSchedule<GroupLesson> schedule = new GroupSchedule<>("G1");
        schedule.addLesson(DayOfWeek.TUESDAY, lesson1);
        schedule.addLesson(DayOfWeek.WEDNESDAY, lesson2);
        app.getGroupSchedules().add(schedule);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.getFullGroupSchedule("G1");

        String output = out.toString();
        assertTrue(output.contains("История"));
        assertTrue(output.contains("Биология"));
        assertTrue(output.contains("TUESDAY"));
        assertTrue(output.contains("WEDNESDAY"));
    }

    @org.junit.jupiter.api.Test
    public void getTeacherScheduleByDay() {

        TeacherLesson lesson = new TeacherLesson(Time.valueOf("10:00:00"), 2, "Физика", "202", "Группа 1");
        TeacherSchedule<TeacherLesson> schedule = new TeacherSchedule<>("T1");
        schedule.addLesson(DayOfWeek.MONDAY, lesson);
        app.getTeacherSchedules().add(schedule);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.getTeacherScheduleByDay(DayOfWeek.MONDAY, "T1");

        System.setOut(originalOut);

        String output = out.toString();
        assertTrue(output.contains("Физика"));
        assertTrue(output.contains("202"));
        assertTrue(output.contains("Группа 1"));
    }

    @org.junit.jupiter.api.Test
    public void getFullTeacherSchedule() {
        TeacherLesson mondayLesson = new TeacherLesson(Time.valueOf("09:00:00"), 1, "Математика", "101", "Группа A");
        TeacherLesson tuesdayLesson = new TeacherLesson(Time.valueOf("11:00:00"), 2, "Информатика", "102", "Группа B");

        TeacherSchedule<TeacherLesson> schedule = new TeacherSchedule<>("T2");
        schedule.addLesson(DayOfWeek.MONDAY, mondayLesson);
        schedule.addLesson(DayOfWeek.TUESDAY, tuesdayLesson);
        app.getTeacherSchedules().add(schedule);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.getFullTeacherSchedule("T2");

        System.setOut(originalOut);

        String output = out.toString();
        assertTrue(output.contains("Математика"));
        assertTrue(output.contains("Информатика"));
        assertTrue(output.contains("Группа A"));
        assertTrue(output.contains("Группа B"));
    }

    @org.junit.jupiter.api.Test
    void runStudentConsoleInterfaceCase1() {

        String input = "1\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runStudentConsoleInterface("Иванов Иван Иванович");

        String output = out.toString();

        assertTrue(output.contains("Математика"));
        assertTrue(output.contains("5, 4"));
        assertTrue(output.contains("Выход"));
    }

    @org.junit.jupiter.api.Test
    void runStudentConsoleInterfaceCase2Full() {
        GroupLesson lesson = new GroupLesson(Time.valueOf("10:10:00"), 1, "Математика", "1701", "Петров Петр Петрович");
        GroupSchedule<GroupLesson> schedule = new GroupSchedule<>("G01");
        schedule.addLesson(DayOfWeek.MONDAY, lesson);
        app.getGroupSchedules().add(schedule);

        String input = "2\n1\n101\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runStudentConsoleInterface("Иванов Иван Иванович");

        String output = out.toString();
        assertTrue(output.contains("Математика"));
        assertTrue(output.contains("MONDAY"));
    }

    @org.junit.jupiter.api.Test
    void runStudentConsoleInterfaceCase2ByDay() {
        GroupLesson lesson = new GroupLesson(Time.valueOf("12:10:00"), 1, "Физика", "1705", "Сидоров Сидр Сидорович");
        GroupSchedule<GroupLesson> schedule = new GroupSchedule<>("G01");
        schedule.addLesson(DayOfWeek.TUESDAY, lesson);
        app.getGroupSchedules().add(schedule);


        String input = "2\n2\n101\n2\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runStudentConsoleInterface("Иванов Иван Иванович");

        String output = out.toString();
        System.out.println(output);
        assertTrue(output.contains("Физика"));

    }

    @org.junit.jupiter.api.Test
    void runStudentConsoleInterfaceCase3Full() {
        TeacherLesson lesson = new TeacherLesson(Time.valueOf("10:10:00"), 2, "Математика", "1701", "101");
        TeacherSchedule<TeacherLesson> schedule = new TeacherSchedule<>("T001");
        schedule.addLesson(DayOfWeek.TUESDAY, lesson);
        app.getTeacherSchedules().add(schedule);

        String input = "3\n1\nПетров Петр Петрович\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runStudentConsoleInterface("Иванов Иван Иванович");

        String output = out.toString();
        assertTrue(output.contains("Математика"));
        assertTrue(output.contains("TUESDAY"));
    }

    @org.junit.jupiter.api.Test
    void runStudentConsoleInterfaceCase3ByDay() {

        TeacherLesson lesson = new TeacherLesson(Time.valueOf("12:10:00"), 3, "География", "1704", "101");
        TeacherSchedule<TeacherLesson> schedule = new TeacherSchedule<>("T001");
        schedule.addLesson(DayOfWeek.FRIDAY, lesson);
        app.getTeacherSchedules().add(schedule);

        String input = "3\n2\nПетров Петр Петрович\n5\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runStudentConsoleInterface("Иванов Иван Иванович");

        String output = out.toString();
        assertTrue(output.contains("География"));
    }

    @org.junit.jupiter.api.Test
    void runStudentConsoleInterfaceCase4() {
        String input = "7\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runStudentConsoleInterface("Иванов Иван Иванович");

        String output = out.toString();
        assertTrue(output.contains("Неверный ввод"));
        assertTrue(output.contains("Выход"));
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase1() {
        String input = "1\nИванов Иван Иванович\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Петров Петр Петрович");

        String output = out.toString();
        assertTrue(output.contains("Математика"));
        assertTrue(output.contains("5, 4"));
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase2() {

        String input = "2\nИванов Иван Иванович\n1\n5\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Петров Петр Петрович");

        assertFalse(app.getStudents().get(0).getGrades().get("Sub001").isEmpty());
        assertEquals(5, app.getStudents().get(0).getGrades().get("Sub001").getLast().getGradeValue());
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase3() {
        String input = "3\nИванов Иван Иванович\n1\n5\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Петров Петр Петрович");

        assertEquals(1, app.getStudents().get(0).getGrades().get("Sub001").size());
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase4Full() {

        GroupLesson lesson = new GroupLesson(Time.valueOf("08:30:00"), 1, "Математика", "1701", "Петров Петр Петрович");
        GroupSchedule<GroupLesson> schedule = new GroupSchedule<>("G01");
        schedule.addLesson(DayOfWeek.MONDAY, lesson);
        app.getGroupSchedules().add(schedule);

        String input = "4\n1\n101\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Петров Петр Петрович");

        String output = out.toString();
        assertTrue(output.contains("Математика"));
        assertTrue(output.contains("MONDAY"));
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase4ByDay() {

        GroupLesson lesson = new GroupLesson(Time.valueOf("10:00:00"), 1, "Физика", "1705", "Петров Петр Петрович");
        GroupSchedule<GroupLesson> schedule = new GroupSchedule<>("G01");
        schedule.addLesson(DayOfWeek.TUESDAY, lesson);
        app.getGroupSchedules().add(schedule);

        String input = "4\n2\n101\n2\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Сидоров Сидр Сидорович");

        String output = out.toString();
        System.out.println(output);
        assertTrue(output.contains("Физика"));
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase5Full() {
        TeacherLesson lesson = new TeacherLesson(Time.valueOf("12:10:00"), 2, "Математика", "1703", "101");
        TeacherSchedule<TeacherLesson> schedule = new TeacherSchedule<>("T001");
        schedule.addLesson(DayOfWeek.WEDNESDAY, lesson);
        app.getTeacherSchedules().add(schedule);

        String input = "5\n1\nПетров Петр Петрович\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Петров Петр Петрович");

        String output = out.toString();
        assertTrue(output.contains("Математика"));
        assertTrue(output.contains("WEDNESDAY"));
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase5ByDay() {

        TeacherLesson lesson = new TeacherLesson(Time.valueOf("12:10:00"), 3, "Математика", "1704", "101");
        TeacherSchedule<TeacherLesson> schedule = new TeacherSchedule<>("T001");
        schedule.addLesson(DayOfWeek.THURSDAY, lesson);
        app.getTeacherSchedules().add(schedule);

        String input = "5\n2\nПетров Петр Петрович\n4\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Петров Петр Петрович");

        String output = out.toString();
        assertTrue(output.contains("Математика"));
    }

    @org.junit.jupiter.api.Test
    void runTeacherConsoleInterfaceCase6() {
        String input = "7\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        app.runTeacherConsoleInterface("Петров Петр Петрович");

        String output = out.toString();
        assertTrue(output.contains("Неверный ввод"));
        assertTrue(output.contains("Выход"));
    }

    @org.junit.jupiter.api.Test
    void authorizationValidStudent() {

        String input = "1\nИванов Иван Иванович\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] result = app.authorization();
        String output = out.toString();

        assertEquals("Иванов Иван Иванович", result[0]);
        assertEquals("1", result[1]);
        assertTrue(output.contains("Вход студента"));
    }

    @org.junit.jupiter.api.Test
    void authorizationValidTeacher() {
        String input = "2\nПетров Петр Петрович\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] result = app.authorization();
        String output = out.toString();

        assertEquals("Петров Петр Петрович", result[0]);
        assertEquals("2", result[1]);
        assertTrue(output.contains("Вход преподавателя"));
    }

    @org.junit.jupiter.api.Test
    void authorizationInvalidStudent() {
        String input = "1\nНесуществующий Студент\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] result = app.authorization();
        String output = out.toString();

        assertNull(result);
        assertTrue(output.contains("Неверный ввод"));
        assertTrue(output.contains("Выход"));
    }

    @org.junit.jupiter.api.Test
    void authorizationInvalidTeacher() {
        String input = "2\nНесуществующий Преподаватель\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] result = app.authorization();
        String output = out.toString();

        assertNull(result);
        assertTrue(output.contains("Неверный ввод"));
        assertTrue(output.contains("Выход"));
    }

    @org.junit.jupiter.api.Test
    void authorizationCase3() {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] result = app.authorization();
        String output = out.toString();

        assertNull(result);
        assertTrue(output.contains("Выход..."));
    }
}