import java.util.ArrayList;

public class Group {
    private String id;
    private String name;
    private ArrayList<String> studentIds;

    public Group() {}

    public Group(String id, String name, ArrayList<String> studentIds) {
        this.id = id;
        this.name = name;
        this.studentIds = studentIds;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getStudentIds() {
        return studentIds;
    }

    public void addStudent(String studentId) {
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
