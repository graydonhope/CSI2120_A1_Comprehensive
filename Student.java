import java.util.HashMap;

public class Student {
    private String name;
    private String[] preferences;
    private boolean isMatched;
    private String employer;
    private HashMap<String, Integer> rankings;

    public Student(String name, String[] preferences) {
        this.name = name;
        this.preferences = preferences;
        this.isMatched = false;
        this.configureRankings(preferences);
    }

    protected boolean isMatched() {
        return this.isMatched;
    }

    protected void setIsMatched(boolean isMatched) {
        this.isMatched = isMatched;
    }

    protected void setEmployer(String name) {
        this.employer = name;
    }

    protected String getCurrentEmployer() {
        return this.employer;
    }

    protected String getName() {
        return this.name;
    }

    protected boolean prefers(String oldEmployer, String newEmployer) {
        if (this.rankings.containsKey(oldEmployer) && this.rankings.containsKey(newEmployer) 
                && this.rankings.get(oldEmployer) > this.rankings.get(newEmployer)) {
            return true;
        } 

        return false;
    }

    private void configureRankings(String[] preferences) {
        this.rankings = new HashMap<String, Integer>();
        for (int i = 0; i < preferences.length; i++) {
            this.rankings.put(preferences[i], i);
        }
    }
}