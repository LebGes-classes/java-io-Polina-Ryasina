import java.util.HashMap;
import java.util.ArrayList;


public class Student {
    private String id;
    private String fullName;
    private String groupId;
    private HashMap<String, ArrayList<Grade>> grades;

    public Student() {}

    public Student(String id, String fullName, String groupId) {
        this.id = id;
        this.fullName = fullName;
        this.groupId = groupId;
        this.grades = new HashMap<String, ArrayList<Grade>>();
    }

    public Student(String id, String fullName, String groupId, HashMap<String, ArrayList<Grade>> grades) {
        this.id = id;
        this.fullName = fullName;
        this.groupId = groupId;
        this.grades = grades;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public HashMap<String, ArrayList<Grade>> getGrades() {
        return grades;
    }

    public void addGrade(String subjectId, Grade grade) {
        ArrayList<Grade> currentGrades;
        if (grades.containsKey(subjectId)) {
            currentGrades = grades.get(subjectId);
        } else {
            currentGrades = new ArrayList<Grade>();
        }
        currentGrades.add(grade);
        grades.put(subjectId, currentGrades);
    }

    public ArrayList<Grade> getGradesBySubject(String subjectId) {
        if (grades.containsKey(subjectId)) {
            return grades.get(subjectId);
        } else {
            System.out.println("Нет оценок по введенному предмету");
        }
        return null;
    }

    public void deleteGrade(String subjectId, Grade grade) {
        if (!grades.containsKey(subjectId)) {
            System.out.println("Предмет не найден");
        } else {
            ArrayList<Grade> curGrades = grades.get(subjectId);
            int index = -1;
            for (int i = 0; i < curGrades.size(); i++) {
                if (curGrades.get(i).getGradeValue() == grade.getGradeValue()) {
                    index = i;
                }
            }
            if (index != -1) {
                curGrades.remove(index);
                grades.put(subjectId, curGrades);
                System.out.println("Оценка удалена!");
            } else {
                System.out.println("Оценка не найдена");
            }
        }
    }

    @Override
    public String toString() {
        return fullName;
    }
}
