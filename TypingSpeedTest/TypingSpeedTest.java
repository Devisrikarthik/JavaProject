import java.util.*;
import java.io.*;

public class TypingSpeedTest {
    private static final String sentence = "The quick brown fox jumps over the lazy dog.";
    private static final String FILE_NAME = "typing_scores.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<UserScore> allScores = loadScores();

        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        System.out.println("\nType the following sentence:");
        System.out.println("\"" + sentence + "\"");

        System.out.println("\nPress Enter when you're ready to start...");
        sc.nextLine();

        System.out.println("Start typing:");
        long startTime = System.currentTimeMillis();
        String typed = sc.nextLine();
        long endTime = System.currentTimeMillis();

        double timeTaken = (endTime - startTime) / 1000.0;
        int wordCount = typed.trim().split("\\s+").length;
        double wpm = (wordCount / timeTaken) * 60;

        // Calculate accuracy
        int correctChars = 0;
        for (int i = 0; i < Math.min(typed.length(), sentence.length()); i++) {
            if (typed.charAt(i) == sentence.charAt(i)) {
                correctChars++;
            }
        }
        double accuracy = (correctChars * 100.0) / sentence.length();

        UserScore score = new UserScore(name, wpm, accuracy, timeTaken);
        allScores.add(score);
        saveScore(score);

        System.out.println("\n=== Your Result ===");
        System.out.printf("WPM: %.2f\n", wpm);
        System.out.printf("Accuracy: %.2f%%\n", accuracy);
        System.out.printf("Time: %.2f seconds\n", timeTaken);

        System.out.println("\n=== All Scores ===");
        for (UserScore s : allScores) {
            System.out.println(s);
        }

        sc.close();
    }

    private static void saveScore(UserScore score) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(score.toFileString() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving score.");
        }
    }

    private static ArrayList<UserScore> loadScores() {
        ArrayList<UserScore> scores = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return scores;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                UserScore score = UserScore.fromFileString(line);
                if (score != null) scores.add(score);
            }
        } catch (Exception e) {
            System.out.println("Error reading scores.");
        }

        return scores;
    }
}

class UserScore {
    String name;
    double wpm;
    double accuracy;
    double time;

    UserScore(String name, double wpm, double accuracy, double time) {
        this.name = name;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.time = time;
    }

    public String toString() {
        return name + " - WPM: " + String.format("%.2f", wpm) +
               ", Accuracy: " + String.format("%.2f", accuracy) +
               "%, Time: " + String.format("%.2f", time) + "s";
    }

    public String toFileString() {
        return name + "," + wpm + "," + accuracy + "," + time;
    }

    public static UserScore fromFileString(String line) {
        try {
            String[] parts = line.split(",");
            return new UserScore(parts[0],
                                 Double.parseDouble(parts[1]),
                                 Double.parseDouble(parts[2]),
                                 Double.parseDouble(parts[3]));
        } catch (Exception e) {
            return null;
        }
    }
}
