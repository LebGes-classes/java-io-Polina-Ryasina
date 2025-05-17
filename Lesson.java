import java.sql.Time;

public class Lesson {
    protected Time time;
    protected int sequenceNumber;
    protected String subjectName;
    protected String classroom;

    public Lesson() {}

    public Lesson(Time time, int sequenceNumber, String subjectName, String classroom) {
        this.time = time;
        this.sequenceNumber = sequenceNumber;
        this.subjectName = subjectName;
        this.classroom = classroom;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public Time getTime() {
        return time;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getClassroom() {
        return classroom;
    }


    @Override
    public String toString() {
        return "" + sequenceNumber + ". " + time + " " + subjectName + ", " + classroom;
    }
}
