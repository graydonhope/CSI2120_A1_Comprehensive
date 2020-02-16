import java.util.HashMap;

public class Employer {
    private String name;
    private String[] preferences;
    private boolean isMatched;
    private HashMap<String, Boolean> offeredJob;

    public Employer(String name, String[] preferences) {
        this.name = name;
        this.preferences = preferences;
        this.isMatched = false;
        this.configureOfferedJobs(preferences);
    }

    private void configureOfferedJobs(String[] preferences) {
        this.offeredJob = new HashMap<String, Boolean>();
        for (int i = 0; i < preferences.length; i++) {
            this.offeredJob.put(preferences[i], false);
        }
    }

    protected String getName() {
        return this.name;
    }

    protected String[] getPreferences() {
        return this.preferences;
    }

    protected int getNumPreferences() {
        return this.preferences.length;
    }

    protected boolean isMatched() {
        return this.isMatched;
    }

    protected void setIsMatched(boolean isMatched, String name) {
        this.isMatched = isMatched;
        this.offeredJob.put(name, isMatched);
    }

    protected void setOfferedJob(String name, boolean offered) {
        this.offeredJob.put(name, offered);
    }

    protected String getMostPreferred() {
        for (int i = 0; i < this.preferences.length; i++) {
            if (!this.offeredJob.get(preferences[i])) {
                return preferences[i];
            }
        }

        System.out.println("Employer offered job to everyone retunr null NOT GOOD");
        return null;
    }
}