import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;

public class Schedule<T> {
    protected HashMap<DayOfWeek, ArrayList<T>> weekSchedule;

    public Schedule() {
        this.weekSchedule = new HashMap<DayOfWeek, ArrayList<T>>();
    }

    public HashMap<DayOfWeek, ArrayList<T>> getWeekSchedule() {
        return weekSchedule;
    }

    public void addLesson(DayOfWeek dayOfWeek, T lesson) {
        ArrayList<T> currentLessons;
        if (weekSchedule.containsKey(dayOfWeek)) {
            currentLessons = weekSchedule.get(dayOfWeek);
        } else {
            currentLessons = new ArrayList<T>();
        }
        currentLessons.add(lesson);
        weekSchedule.put(dayOfWeek, currentLessons);
    }

    public ArrayList<T> getLessonByDay(DayOfWeek dayOfWeek) {
        if (weekSchedule.containsKey(dayOfWeek)) {
            return weekSchedule.get(dayOfWeek);
        } else {
            System.out.println("Нет занятий в указанный день");
            return null;
        }
    }

}
