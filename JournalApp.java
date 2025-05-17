import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.*;

public class JournalApp {
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Subject> subjects = new ArrayList<>();
    private ArrayList<GroupSchedule> groupSchedules = new ArrayList<>();
    private ArrayList<TeacherSchedule> teacherSchedules = new ArrayList<>();

    public JournalApp() {
    }

    public void loadDataFromXLSX(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename); // создаем поток для файла
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet studentsSheet = workbook.getSheet("Students"); // считываем лист Students
            if (studentsSheet.getPhysicalNumberOfRows() > 0) {
                Row firstRow = studentsSheet.getRow(0); // считываем первую строку листа
                for (int i = 1; i <= studentsSheet.getLastRowNum(); i++) {
                    Row row = studentsSheet.getRow(i); // считываем новую строку
                    if (row.getPhysicalNumberOfCells() > 0) {

                        // считываем нужные значения и создаем экземпляр Student
                        String id = row.getCell(0).getStringCellValue();
                        String fullName = row.getCell(1).getStringCellValue();
                        String groupId = row.getCell(2).getStringCellValue();
                        Student student = new Student(id, fullName, groupId);

                        // добавляем все имеющиеся оценки по каждому предмету
                        for (int x = 3; x < row.getLastCellNum(); x++) {
                            if (!row.getCell(x).toString().trim().equals("")) {
                                String[] grades = row.getCell(x).toString().trim().split(" "); // считываем строку из оценок и делим ее по пробелам в список
                                for (String grade : grades) {
                                    student.addGrade(firstRow.getCell(x).getStringCellValue(), new Grade(firstRow.getCell(x).getStringCellValue(), (int) Double.parseDouble(grade)));
                                }
                            }
                        }
                        students.add(student);
                    }
                }
            }

            Sheet teachersSheet = workbook.getSheet("Teachers");
            if (teachersSheet.getPhysicalNumberOfRows() > 0) {
                Row firstRow = teachersSheet.getRow(0); // считываем первую строку листа
                for (int i = 1; i <= teachersSheet.getLastRowNum(); i++) {
                    Row row = teachersSheet.getRow(i); // считываем новую строку
                    if (row.getPhysicalNumberOfCells() > 0) {

                        // считываем нужные значения и создаем экземпляр Teacher
                        String id = row.getCell(0).getStringCellValue();
                        String fullName = row.getCell(1).getStringCellValue();
                        ArrayList<String> subjectIds = new ArrayList<>(List.of(row.getCell(2).getStringCellValue().split(" ")));
                        Teacher teacher = new Teacher(id, fullName, subjectIds);

                        teachers.add(teacher);
                    }
                }
            }

            Sheet groupsSheet = workbook.getSheet("Groups");
            if (groupsSheet.getPhysicalNumberOfRows() > 0) {
                Row firstRow = groupsSheet.getRow(0); // считываем первую строку листа
                for (int i = 1; i <= groupsSheet.getLastRowNum(); i++) {
                    Row row = groupsSheet.getRow(i); // считываем новую строку
                    if (row.getPhysicalNumberOfCells() > 0) {

                        // считываем нужные значения и создаем экземпляр Group
                        String id = row.getCell(0).getStringCellValue();
                        String name = "" + (int) row.getCell(1).getNumericCellValue();
                        ArrayList<String> studentsIds = new ArrayList<>(List.of(row.getCell(2).getStringCellValue().split(" ")));
                        Group group = new Group(id, name, studentsIds);
                        groups.add(group);
                    }
                }
            }

            Sheet subjectsSheet = workbook.getSheet("Subjects");
            if (subjectsSheet.getPhysicalNumberOfRows() > 0) {
                Row firstRow = subjectsSheet.getRow(0); // считываем первую строку листа
                for (int i = 1; i <= subjectsSheet.getLastRowNum(); i++) {
                    Row row = subjectsSheet.getRow(i); // считываем новую строку
                    if (row.getPhysicalNumberOfCells() > 0) {

                        // считываем нужные значения и создаем экземпляр Teacher
                        String id = row.getCell(0).getStringCellValue();
                        String name = row.getCell(1).getStringCellValue();
                        Subject subject = new Subject(id, name);

                        subjects.add(subject);
                    }
                }
            }

            Sheet schedulesSheet = workbook.getSheet("Schedules");
            if (schedulesSheet.getPhysicalNumberOfRows() > 0) {
                Row firstRow = schedulesSheet.getRow(0);
                for (int i = 1; i <= schedulesSheet.getLastRowNum(); i++) {
                    Row row = schedulesSheet.getRow(i);
                    if (row.getPhysicalNumberOfCells() > 0) {
                        DayOfWeek dayOfWeek = DayOfWeek.of((int) row.getCell(0).getNumericCellValue());
                        Time time = Time.valueOf(row.getCell(1).toString().trim());
                        int studentSequenceNumber = (int) row.getCell(2).getNumericCellValue();
                        int teacherSequenceNumber = (int) row.getCell(3).getNumericCellValue();
                        String subjectId = row.getCell(4).getStringCellValue();
                        String classroom = "" + (int) row.getCell(5).getNumericCellValue();
                        String teacherId = row.getCell(6).getStringCellValue();
                        String groupId = row.getCell(7).getStringCellValue();

                        String subjectName = "";
                        String teacherFullName = "";
                        String groupName = "";

                        for (Subject subject : subjects) {
                            if (subject.getId().equals(subjectId)) {
                                subjectName = subject.getName();
                            }
                        }

                        for (Teacher teacher : teachers) {
                            if (teacher.getId().equals(teacherId)) {
                                teacherFullName = teacher.getFullName();
                            }
                        }

                        for (Group group : groups) {
                            if (group.getId().equals(groupId)) {
                                groupName = group.getName();
                            }
                        }

                        GroupLesson groupLesson = new GroupLesson(time, studentSequenceNumber, subjectName, classroom, teacherFullName);
                        TeacherLesson teacherLesson = new TeacherLesson(time, teacherSequenceNumber, subjectName, classroom, groupName);
                        boolean isExist = true;
                        for (GroupSchedule groupSchedule : groupSchedules) {
                            if (groupSchedule.getGroupId().equals(groupId)) {
                                groupSchedule.addLesson(dayOfWeek, groupLesson);
                                isExist = false;
                            }
                        }
                        if (isExist) {
                            GroupSchedule<GroupLesson> groupSchedule = new GroupSchedule<>(groupId);
                            groupSchedule.addLesson(dayOfWeek, groupLesson);
                            groupSchedules.add(groupSchedule);
                        }

                        isExist = true;
                        for (TeacherSchedule teacherSchedule : teacherSchedules) {
                            if (teacherSchedule.getTeacherId().equals(teacherId)) {
                                teacherSchedule.addLesson(dayOfWeek, teacherLesson);
                                isExist = false;
                            }
                        }
                        if (isExist) {
                            TeacherSchedule<TeacherLesson> teacherSchedule = new TeacherSchedule<>(teacherId);
                            teacherSchedule.addLesson(dayOfWeek, teacherLesson);
                            teacherSchedules.add(teacherSchedule);
                        }

                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось загрузить данные");;
        }
    }

    public void loadDataFromJSON(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            ObjectMapper mapper = new ObjectMapper();
            JournalApp data = mapper.readValue(fis, JournalApp.class);

            // копируем значения в текущий JournalApp
            this.students = data.students;
            this.teachers = data.teachers;
            this.groups = data.groups;
            this.subjects = data.subjects;
            this.groupSchedules = data.groupSchedules;
            this.teacherSchedules = data.teacherSchedules;

        } catch (FileNotFoundException e) {
            loadDataFromXLSX("Data.xlsx");
        }
    }

    public void saveDataToJSON(String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(fos, this); // сериализация текущего JournalApp
        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные в JSON");
        }
    }

    public void addGradeToStudent(Student student, String subjectId, Grade grade) {
        student.addGrade(subjectId, grade);
        System.out.println("Оценка добавлена!");
    }

    public void deleteGradeToStudent(Student student, String subjectId, Grade grade) {
        student.deleteGrade(subjectId, grade);
    }

    public void getGroupScheduleByDay(DayOfWeek day, String groupId) {
        Comparator<GroupLesson> bySequenceNumber = Comparator.comparingInt(lesson -> lesson.getSequenceNumber());
        boolean flag = false;
        for (int i = 0; i < groupSchedules.size() & !flag; i++) {
            if (groupSchedules.get(i).getGroupId().equals(groupId)) {
                ArrayList<GroupLesson> ws = groupSchedules.get(i).getLessonByDay(day);
                flag = true;
                if (ws != null && !ws.isEmpty()) {
                    ws.sort(bySequenceNumber);
                    for (int j = 0; j < ws.size(); j++) {
                        System.out.println(ws.get(j));
                    }
                } else {
                    System.out.println("Нет занятий в указанный день");
                }
            }
        }
        if (!flag) {
            System.out.println("Расписание группы не найдено");
        }
    }

    public void getFullGroupSchedule(String groupId) {
        boolean flag = false;
        Comparator<GroupLesson> bySequenceNumber = Comparator.comparingInt(lesson -> lesson.getSequenceNumber());
        for (int i = 0; i < groupSchedules.size() & !flag; i++) {
            if (groupSchedules.get(i).getGroupId().equals(groupId)) {
                HashMap<DayOfWeek, ArrayList<GroupLesson>> ws = groupSchedules.get(i).getWeekSchedule();
                flag = true;

                for (int day = 1; day < 7; day++) {
                    ArrayList<GroupLesson> wsd = ws.get(DayOfWeek.of(day));
                    if (wsd != null && !wsd.isEmpty()) {
                        wsd.sort(bySequenceNumber);
                        System.out.println(DayOfWeek.of(day));
                        for (int j = 0; j < wsd.size(); j++) {
                            System.out.println(wsd.get(j));
                        }
                    } else {
                        System.out.println("Нет занятий в указанный день");
                    }
                }
            }
        }
        if (!flag) {
            System.out.println("Расписание группы не найдено");
        }
    }

    public void getTeacherScheduleByDay(DayOfWeek day, String teacherId) {
        Comparator<TeacherLesson> bySequenceNumber = Comparator.comparingInt(lesson -> lesson.getSequenceNumber());
        boolean flag = false;
        for (int i = 0; i < teacherSchedules.size() & !flag; i++) {
            if (teacherSchedules.get(i).getTeacherId().equals(teacherId)) {
                ArrayList<TeacherLesson> ws = teacherSchedules.get(i).getLessonByDay(day);
                flag = true;
                if (ws != null && !ws.isEmpty()) {
                    ws.sort(bySequenceNumber);
                    for (int j = 0; j < ws.size(); j++) {
                        System.out.println(ws.get(j));
                    }
                } else {
                    System.out.println("Нет занятий в указанный день");
                }
            }
        }
        if (!flag) {
            System.out.println("Расписание преподавателя не найдено");
        }
    }

    public void getFullTeacherSchedule(String teacherId) {
        Comparator<TeacherLesson> bySequenceNumber = Comparator.comparingInt(lesson -> lesson.getSequenceNumber());
        boolean flag = false;
        for (int i = 0; i < teacherSchedules.size() & !flag; i++) {
            if (teacherSchedules.get(i).getTeacherId().equals(teacherId)) {
                HashMap<DayOfWeek, ArrayList<TeacherLesson>> ws = teacherSchedules.get(i).getWeekSchedule();
                flag = true;
                for (int day = 1; day < 7; day++) {
                    ArrayList<TeacherLesson> wsd = ws.get(DayOfWeek.of(day));
                    if (wsd != null && !wsd.isEmpty()) {
                        wsd.sort(bySequenceNumber);
                        System.out.println(DayOfWeek.of(day));
                        for (int j = 0; j < wsd.size(); j++) {
                            System.out.println(wsd.get(j));
                        }
                    } else {
                        System.out.println("Нет занятий в указанный день");
                    }
                }
            }
        }
        if (!flag) {
            System.out.println("Расписание преподавателя не найдено");
        }
    }


    public void runStudentConsoleInterface(String studentFullName) {
        Scanner scanner = new Scanner(System.in);
        boolean work = true;
        while (work) {
            System.out.println("\n<Меню>");
            System.out.println("1. Посмотреть оценки");
            System.out.println("2. Посмотреть расписание группы");
            System.out.println("3. Посмотреть расписание преподавателя");
            System.out.println("4. Выход");
            System.out.print("Выберите пункт: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    boolean flag = false;
                    for (int i = 0; i < students.size() & !flag; i++) {
                        if (students.get(i).getFullName().equals(studentFullName)) {
                            HashMap<String, ArrayList<Grade>> gr = students.get(i).getGrades();
                            flag = true;
                            System.out.println();
                            for (Map.Entry<String, ArrayList<Grade>> entry : gr.entrySet()) {
                                String subjectId = entry.getKey();
                                ArrayList<Grade> grades = entry.getValue();
                                String subjectName = "";
                                for (Subject subject : subjects) {
                                    if (subject.getId().equals(subjectId)) {
                                        subjectName = subject.getName();
                                    }
                                }
                                System.out.println(subjectName + ": " + grades);
                            }
                        }
                    }
                    if (!flag) {
                        System.out.println("Оценки не найдены");
                    }
                    break;

                case "2":
                    System.out.println("\n1. Посмотреть все расписание");
                    System.out.println("2. Посмотреть расписание на день");
                    System.out.print("Выберите пункт (для возврата в меню введите «Назад»): ");
                    String typeOfSchedule = scanner.nextLine().trim();

                    if (!typeOfSchedule.equalsIgnoreCase("назад")) {
                        if (typeOfSchedule.equals("1")) {
                            System.out.print("Введите группу: ");
                            String groupName = scanner.nextLine().trim();
                            flag = false;
                            for (int i = 0; i < groups.size() & !flag; i++) {
                                if (groups.get(i).getName().equals(groupName)) {
                                    System.out.println();
                                    getFullGroupSchedule(groups.get(i).getId());
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                System.out.println("Группа не найдена");
                            }
                        } else if (typeOfSchedule.equals("2")) {
                            System.out.print("Введите группу: ");
                            String groupName = scanner.nextLine().trim();

                            flag = false;
                            for (int i = 0; i < groups.size() & !flag; i++) {
                                if (groups.get(i).getName().equals(groupName)) {
                                    flag = true;
                                    System.out.print("Введите номер дня недели: ");
                                    ArrayList<String> avaliableNumbers = new ArrayList<>(List.of(new String[]{"1", "2", "3", "4", "5", "6"}));
                                    String dayOfWeek = scanner.nextLine().trim();
                                    if (!avaliableNumbers.contains(dayOfWeek)) {
                                        System.out.println("Введен неверный номер дня недели");
                                    } else {
                                        System.out.println();
                                        getGroupScheduleByDay(DayOfWeek.of(Integer.parseInt(dayOfWeek)), groups.get(i).getId());
                                    }
                                }
                            }
                            if (!flag) {
                                System.out.println("Группа не найдена");
                            }
                        } else {
                            System.out.println("Введено некорректное значение");
                        }
                    }
                    break;


                case "3":
                    System.out.println("\n1. Посмотреть все расписание");
                    System.out.println("2. Посмотреть расписание на день");
                    System.out.print("Выберите пункт (для возврата в меню введите «Назад»): ");
                    typeOfSchedule = scanner.nextLine().trim();

                    if (!typeOfSchedule.equalsIgnoreCase("назад")) {
                        if (typeOfSchedule.equals("1")) {
                            System.out.print("Введите ФИО преподавателя: ");
                            String teacherName = scanner.nextLine().trim();
                            flag = false;
                            for (int i = 0; i < teachers.size() & !flag; i++) {
                                if (teachers.get(i).getFullName().equals(teacherName)) {
                                    System.out.println();
                                    getFullTeacherSchedule(teachers.get(i).getId());
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                System.out.println("Преподаватель не найден");
                            }
                        } else if (typeOfSchedule.equals("2")) {
                            System.out.print("Введите ФИО преподавателя: ");
                            String teacherName = scanner.nextLine().trim();

                            flag = false;
                            for (int i = 0; i < teachers.size() & !flag; i++) {
                                if (teachers.get(i).getFullName().equals(teacherName)) {
                                    flag = true;
                                    System.out.print("Введите номер дня недели: ");
                                    ArrayList<String> avaliableNumbers = new ArrayList<>(List.of(new String[]{"1", "2", "3", "4", "5", "6"}));
                                    String dayOfWeek = scanner.nextLine().trim();
                                    if (!avaliableNumbers.contains(dayOfWeek)) {
                                        System.out.println("Введен неверный номер дня недели");
                                    } else {
                                        System.out.println();
                                        getTeacherScheduleByDay(DayOfWeek.of(Integer.parseInt(dayOfWeek)), teachers.get(i).getId());
                                    }
                                }
                            }
                            if (!flag) {
                                System.out.println("Преподаватель не найден");
                            }
                        } else {
                            System.out.println("Введено некорректное значение");
                        }
                    }
                    break;

                case "4":
                    System.out.println("Выход...");
                    work = false;
                    break;

                default:
                    System.out.println("Неверный ввод");
                    break;
            }

        }
    }

    public void runTeacherConsoleInterface(String teacherFullName) {
        Scanner scanner = new Scanner(System.in);

        Teacher teacher = new Teacher("a", "a", new ArrayList<>());
        for (Teacher t : teachers) {
            if (t.getFullName().equals(teacherFullName)) {
                teacher = t;
            }
        }
        boolean work = true;
        while (work) {
            System.out.println("\n<Меню>");
            System.out.println("1. Посмотреть оценки студента");
            System.out.println("2. Добавить оценку");
            System.out.println("3. Удалить оценку");
            System.out.println("4. Посмотреть расписание группы");
            System.out.println("5. Посмотреть расписание преподавателя");
            System.out.println("6. Выход");
            System.out.print("Выберите пункт: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Введите ФИО студента (для возврата в меню введите «Назад»): ");
                    String studentFullName = scanner.nextLine().trim();
                    if (!studentFullName.equalsIgnoreCase("назад")) {
                        boolean flag = false;
                        System.out.println();
                        for (int i = 0; i < students.size() & !flag; i++) {
                            if (students.get(i).getFullName().equals(studentFullName)) {
                                HashMap<String, ArrayList<Grade>> gr = students.get(i).getGrades();
                                flag = true;

                                for (Map.Entry<String, ArrayList<Grade>> entry : gr.entrySet()) {
                                    String subjectId = entry.getKey();
                                    ArrayList<Grade> grades = entry.getValue();
                                    String subjectName = "";
                                    for (Subject subject : subjects) {
                                        if (subject.getId().equals(subjectId)) {
                                            subjectName = subject.getName();
                                        }
                                    }
                                    System.out.println(subjectName + ": " + grades);
                                }
                            }
                        }
                        if (!flag) {
                            System.out.println("Студент не найден");
                        }
                    }
                    break;

                case "2":
                    System.out.print("Введите ФИО студента (для возврата в меню введите «Назад»): ");
                    studentFullName = scanner.nextLine().trim();
                    if (!studentFullName.equalsIgnoreCase("назад")) {
                        boolean flag = false;
                        for (int i = 0; i < students.size() & !flag; i++) {
                            if (students.get(i).getFullName().equals(studentFullName)) {
                                flag = true;
                                int cnt = 0;
                                for (int j = 0; j < subjects.size(); j++) {
                                    if (teacher.getSubjectIds().contains(subjects.get(j).getId())) {
                                        System.out.println("" + (cnt + 1) + ". " + subjects.get(j).getName());
                                        cnt++;
                                    }
                                }

                                System.out.print("Выберите номер предмета: ");
                                boolean isRight = true;
                                int subjectNum = 0;
                                try {
                                    subjectNum = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Номер предмета должен быть числом");
                                    isRight = false;
                                }
                                scanner.nextLine();

                                if (isRight & (subjectNum < 1 | subjectNum > teacher.getSubjectIds().size())) {
                                    System.out.println("Введен неверный номер предмета");
                                } else if (isRight) {
                                    System.out.print("Введите числовое значение оценки: ");
                                    int gradeValue = 0;
                                    try {
                                        gradeValue = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                    }
                                    scanner.nextLine();

                                    if (gradeValue > 0 && gradeValue < 6) {
                                        addGradeToStudent(students.get(i), teacher.getSubjectIds().get(subjectNum - 1), new Grade(teacher.getSubjectIds().get(subjectNum - 1), gradeValue));
                                    } else {
                                        System.out.println("Введена несуществующая оценка");
                                    }
                                }
                            }
                        }
                        if (!flag) {
                            System.out.println("Студент не найден");
                        }
                    }
                    break;

                case "3":
                    System.out.print("Введите ФИО студента (для возврата в меню введите «Назад»): ");
                    studentFullName = scanner.nextLine().trim();
                    if (!studentFullName.equalsIgnoreCase("назад")) {
                        boolean flag = false;
                        for (int i = 0; i < students.size() & !flag; i++) {
                            if (students.get(i).getFullName().equals(studentFullName)) {
                                flag = true;
                                int cnt = 0;
                                for (int j = 0; j < subjects.size(); j++) {
                                    if (teacher.getSubjectIds().contains(subjects.get(j).getId())) {
                                        System.out.println("" + (cnt + 1) + ". " + subjects.get(j).getName());
                                        cnt++;
                                    }
                                }

                                System.out.print("Выберите номер предмета: ");
                                boolean isRight = true;
                                int subjectNum = 0;
                                try {
                                    subjectNum = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Номер предмета должен быть числом");
                                    isRight = false;
                                }
                                scanner.nextLine();

                                if (isRight & (subjectNum < 1 | subjectNum > teacher.getSubjectIds().size())) {
                                    System.out.println("Введен неверный номер предмета");
                                } else if (isRight) {
                                    System.out.print("Введите числовое значение оценки: ");
                                    int gradeValue = 0;
                                    try {
                                        gradeValue = scanner.nextInt();
                                    } catch (InputMismatchException e) {
                                    }
                                    scanner.nextLine();

                                    if (gradeValue > 0 && gradeValue < 6) {
                                        deleteGradeToStudent(students.get(i), teacher.getSubjectIds().get(subjectNum - 1), new Grade(teacher.getSubjectIds().get(subjectNum - 1), gradeValue));
                                    } else {
                                        System.out.println("Введена несуществующая оценка");
                                    }
                                }
                            }
                        }
                        if (!flag) {
                            System.out.println("Студент не найден");
                        }
                    }
                    break;

                case "4":
                    System.out.println("\n1. Посмотреть все расписание");
                    System.out.println("2. Посмотреть расписание на день");
                    System.out.print("Выберите пункт (для возврата в меню введите «Назад»): ");
                    String typeOfSchedule = scanner.nextLine().trim();

                    if (!typeOfSchedule.equalsIgnoreCase("назад")) {
                        if (typeOfSchedule.equals("1")) {
                            System.out.print("Введите группу: ");
                            String groupName = scanner.nextLine().trim();
                            boolean flag = false;
                            for (int i = 0; i < groups.size() & !flag; i++) {
                                if (groups.get(i).getName().equals(groupName)) {
                                    System.out.println();
                                    getFullGroupSchedule(groups.get(i).getId());
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                System.out.println("Группа не найдена");
                            }
                        } else if (typeOfSchedule.equals("2")) {
                            System.out.print("Введите группу: ");
                            String groupName = scanner.nextLine().trim();

                            boolean flag = false;
                            for (int i = 0; i < groups.size() & !flag; i++) {
                                if (groups.get(i).getName().equals(groupName)) {
                                    flag = true;
                                    System.out.print("Введите номер дня недели: ");
                                    ArrayList<String> avaliableNumbers = new ArrayList<>(List.of(new String[]{"1", "2", "3", "4", "5", "6"}));
                                    String dayOfWeek = scanner.nextLine().trim();
                                    if (!avaliableNumbers.contains(dayOfWeek)) {
                                        System.out.println("Введен неверный номер дня недели");
                                    } else {
                                        System.out.println();
                                        getGroupScheduleByDay(DayOfWeek.of(Integer.parseInt(dayOfWeek)), groups.get(i).getId());
                                    }
                                }
                            }
                            if (!flag) {
                                System.out.println("Группа не найдена");
                            }
                        } else {
                            System.out.println("Введено некорректное значение");
                        }
                    }
                    break;

                case "5":
                    System.out.println("\n1. Посмотреть все расписание");
                    System.out.println("2. Посмотреть расписание на день");
                    System.out.print("Выберите пункт (для возврата в меню введите «Назад»): ");
                    typeOfSchedule = scanner.nextLine().trim();

                    if (!typeOfSchedule.equalsIgnoreCase("назад")) {
                        if (typeOfSchedule.equals("1")) {
                            System.out.print("Введите ФИО преподавателя: ");
                            String teacherName = scanner.nextLine().trim();
                            boolean flag = false;
                            for (int i = 0; i < teachers.size() & !flag; i++) {
                                if (teachers.get(i).getFullName().equals(teacherName)) {
                                    System.out.println();
                                    getFullTeacherSchedule(teachers.get(i).getId());
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                System.out.println("Преподаватель не найден");
                            }
                        } else if (typeOfSchedule.equals("2")) {
                            System.out.print("Введите ФИО преподавателя: ");
                            String teacherName = scanner.nextLine().trim();

                            boolean flag = false;
                            for (int i = 0; i < teachers.size() & !flag; i++) {
                                if (teachers.get(i).getFullName().equals(teacherName)) {
                                    flag = true;
                                    System.out.print("Введите номер дня недели: ");
                                    ArrayList<String> avaliableNumbers = new ArrayList<>(List.of(new String[]{"1", "2", "3", "4", "5", "6"}));
                                    String dayOfWeek = scanner.nextLine().trim();
                                    if (!avaliableNumbers.contains(dayOfWeek)) {
                                        System.out.println("Введен неверный номер дня недели");
                                    } else {
                                        System.out.println();
                                        getTeacherScheduleByDay(DayOfWeek.of(Integer.parseInt(dayOfWeek)), teachers.get(i).getId());
                                    }
                                }
                            }
                            if (!flag) {
                                System.out.println("Преподаватель не найден");
                            }
                        } else {
                            System.out.println("Введено некорректное значение");
                        }
                    }
                    break;

                case "6":
                    System.out.println("Выход...");
                    work = false;
                    break;

                default:
                    System.out.println("Неверный ввод");
                    break;
            }
        }
    }

    public String[] authorization() {
        Scanner scanner = new Scanner(System.in);
        boolean work = true;
        while (work) {
            System.out.println("\n<Меню>");
            System.out.println("1. Вход студента");
            System.out.println("2. Вход преподавателя");
            System.out.println("3. Выход");
            System.out.print("Выберите пункт: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Введите свое ФИО (в формате Иванов Иван Иванович): ");
                    String fullName = scanner.nextLine();
                    for (int i = 0; i < students.size(); i++) {
                        if (students.get(i).getFullName().equals(fullName)) {
                            return new String[]{fullName, "1"};
                        }
                    }
                    System.out.println("Неверный ввод");
                    break;
                case "2":
                    System.out.print("Введите свое ФИО (в формате Иванов Иван Иванович): ");
                    fullName = scanner.nextLine();
                    for (int i = 0; i < teachers.size(); i++) {
                        if (teachers.get(i).getFullName().equals(fullName)) {
                            return new String[]{fullName, "2"};
                        }
                    }
                    System.out.println("Неверный ввод");
                    break;
                case "3":
                    System.out.println("Выход...");
                    work = false;
                    break;
                default:
                    System.out.println("Неверный ввод");
                    break;
            }
        }
        return null;
    }

    public ArrayList<TeacherSchedule> getTeacherSchedules() {
        return teacherSchedules;
    }

    public ArrayList<GroupSchedule> getGroupSchedules() {
        return groupSchedules;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }


}
