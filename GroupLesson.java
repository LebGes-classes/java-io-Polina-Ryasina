import java.sql.Time;

public class GroupLesson extends Lesson {
    private String teacherFullName;

    public GroupLesson() {
        super();
    }

    public GroupLesson(Time time, int sequenceNumber, String subjectName, String classroom, String teacherFullName) {
        super(time, sequenceNumber, subjectName, classroom);
        this.teacherFullName = teacherFullName;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }


    @Override
    public String toString() {
        return "" + sequenceNumber + ". " + time + " " + subjectName + ", " + classroom + ", " + teacherFullName;
    }
}
