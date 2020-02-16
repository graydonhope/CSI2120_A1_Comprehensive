import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.nio.file.Path; 
import java.nio.file.Files; 
import java.nio.file.FileSystems;  
import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class StableMatching {

    private HashMap<String, Student> studentIndices;
    private HashMap<String, Employer> employerIndices;
    private String n;

    public static void main(String[] args) {
        if (args != null && args.length == 2) {
            StableMatching stableMatching = new StableMatching();
            stableMatching.readInput(args[0], args[1]);
        }
        else {
            throw new IllegalArgumentException("CSI2120 Invalid Input! Please enter valid filenames as command-line arguments.");
        }
    }
    
    private void readInput(String employeeFile, String studentFile) {
        Path employeePath = FileSystems.getDefault().getPath(employeeFile);
        Path studentPath = FileSystems.getDefault().getPath(studentFile);   

        try {
            List<String> employerInput = Files.readAllLines(employeePath);
            List<String> studentInput = Files.readAllLines(studentPath);
            Employer[] employers = configureEmployers(employerInput);
            Student[] students = configureStudents(studentInput);
            this.n = String.valueOf(students.length);
            this.match(employers, students);
        }
        catch (IOException e) {
            System.out.println("CSI 2120! Unable to find file - Please enter filename including filetype (i.e employer.csv)");
            e.printStackTrace();
        }
    }

    private void match(Employer[] employers, Student[] students) {
        if (employers == null || students == null || employers.length == 0 || students.length == 0) {
            System.out.println("Invalid Input to the matching algorithm");
            throw new IllegalArgumentException("Input cannot be null or of length 0.");
        }

        HashMap<String, String> matches = new HashMap<String, String>();
        LinkedList<Employer> unmatchedEmployers = new LinkedList(Arrays.asList(employers));

        while (!unmatchedEmployers.isEmpty()) {
            Employer employer = unmatchedEmployers.remove();

            for (int i = 0; i < employer.getNumPreferences(); i++) {
                if (!employer.isMatched()) {
                    Student mostPreferred = this.studentIndices.get(employer.getMostPreferred());

                    if (!mostPreferred.isMatched()) {
                        this.addPair(matches, employer.getName(), mostPreferred.getName());
                        employer.setIsMatched(true, employer.getName());
                        mostPreferred.setIsMatched(true);
                        mostPreferred.setEmployer(employer.getName());
                    }
                    else if (mostPreferred.prefers(mostPreferred.getCurrentEmployer(), employer.getName())) {
                        // replace match. Add old employer back into queue
                        Employer oldEmployer = this.replaceMatch(matches, employer.getName(), mostPreferred.getName(), mostPreferred);
                        employer.setIsMatched(true, employer.getName()); 
                        mostPreferred.setIsMatched(true);
                        mostPreferred.setEmployer(employer.getName());
                        oldEmployer.setIsMatched(false, oldEmployer.getName());
                        unmatchedEmployers.add(oldEmployer);
                    }
                    else {
                        // student rejects match
                        employer.setOfferedJob(mostPreferred.getName(), true);
                    }
                }
            }
        }

        this.writeResults(matches);
    }

    private void addPair(HashMap<String, String> pairs, String employer, String student) {
        pairs.put(employer, student);
    }

    private Employer replaceMatch(HashMap<String, String> pairs, String employer, String student, Student preferred) {
        String oldEmployer = preferred.getCurrentEmployer();

        if (oldEmployer != null) {
            pairs.remove(oldEmployer);
        }

        pairs.put(employer, student);

        return this.employerIndices.get(oldEmployer);
    }

    private Employer[] configureEmployers(List<String> employerInput) {
        Employer[] employers = new Employer[employerInput.size()];
        this.employerIndices = new HashMap<String, Employer>();

        for (int i = 0; i < employerInput.size(); i++) {
            String[] input = employerInput.get(i).split(",");
            String[] employerPreferences = new String[input.length - 1];
            String name = input[0];

            for (int j = 1; j < input.length; j++) {
                employerPreferences[j - 1] = input[j];
            }

            Employer employer = new Employer(name, employerPreferences);
            employers[i] = employer;
            this.employerIndices.put(name, employer);
        }

        return employers;
    }

    private Student[] configureStudents(List<String> studentInput) {
        this.studentIndices = new HashMap<String, Student>();
        Student[] students = new Student[studentInput.size()];

        for (int i = 0; i < studentInput.size(); i++) {
            String[] input = studentInput.get(i).split(",");
            String[] studentPreferences = new String[input.length - 1];
            String name = input[0];

            for (int j = 1; j < input.length; j++) {
                studentPreferences[j - 1] = input[j];
            }

            Student student = new Student(name, studentPreferences);
            students[i] = student;
            this.studentIndices.put(name, student);
        }

        return students;
    }

    private void writeResults(HashMap<String, String> matches) {
        Iterator it = matches.entrySet().iterator();
        try (PrintWriter writer = new PrintWriter(new File("matches_java_" + this.n + "x" + this.n + ".csv"))) {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder colNames = new StringBuilder("Employer");
            colNames.append(','); colNames.append("Student"); colNames.append('\n');
            writer.write(colNames.toString());
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String employer = (String) pair.getKey();
                stringBuilder.append(employer);
                stringBuilder.append(',');
                String student = (String) pair.getValue();
                stringBuilder.append(student);
                stringBuilder.append('\n');
                it.remove(); 
            }
            writer.write(stringBuilder.toString());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}