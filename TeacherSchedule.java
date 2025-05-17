public class TeacherSchedule<T> extends Schedule<T> {
    private String teacherId;

    public TeacherSchedule() {}

    public TeacherSchedule(String teacherId) {
        super();
        this.teacherId = teacherId;
    }

    public String getTeacherId() {
        return teacherId;
    }

}
