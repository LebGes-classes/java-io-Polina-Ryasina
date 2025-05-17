import java.sql.Time;

public class TeacherLesson extends Lesson {
    private String groupName;

    public TeacherLesson() {
        super();
    }

    public TeacherLesson(Time time, int sequenceNumber, String subjectName, String classroom, String groupName) {
        super(time, sequenceNumber, subjectName, classroom);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }


    @Override
    public String toString() {
        return "" + sequenceNumber + ". " + time + " " + subjectName + ", " + classroom + ", " + groupName;
    }
}
