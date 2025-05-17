import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JournalApp journalApp = new JournalApp();
        journalApp.loadDataFromJSON("Data.json");
        String[] authorization = journalApp.authorization();
        if (authorization == null) {
        } else if (authorization[1].equals("1")) {
            journalApp.runStudentConsoleInterface(authorization[0]);
        } else if (authorization[1].equals("2")) {
            journalApp.runTeacherConsoleInterface(authorization[0]);
        }

        journalApp.saveDataToJSON("Data.json");
    }
}
