public class Grade {
    private String subjectId;
    private int gradeValue;

    public Grade() {}

    public Grade(String subjectId, int gradeValue) {
        this.subjectId = subjectId;
        this.gradeValue = gradeValue;
    }

    public int getGradeValue() {
        return gradeValue;
    }

    public String getSubjectId() {
        return subjectId;
    }

    @Override
    public String toString() {
        return "" + gradeValue;
    }
}
