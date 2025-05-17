import java.util.ArrayList;

public class Teacher {
    private String id;
    private String fullName;
    private ArrayList<String> subjectIds;

    public Teacher() {}

    public Teacher(String id, String fullName, ArrayList<String> subjectIds) {
        this.id = id;
        this.fullName = fullName;
        this.subjectIds = subjectIds;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public ArrayList<String> getSubjectIds() {
        return subjectIds;
    }

    public void addSubject(String subjectId) {
        if (!subjectIds.contains(subjectId)) {
            subjectIds.add(subjectId);
        }
    }

    @Override
    public String toString() {
        return fullName;
    }

}
