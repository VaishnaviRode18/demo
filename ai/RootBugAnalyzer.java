package ai;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RootBugAnalyzer {

  public static void main(String[] args) {
    String inputFile = "D:\\Downloads\\ai\\ai\\bugs.tsv"; // Specify the full path to bugs.tsv
    try {
      Map<Integer, Integer> bugOccurrences = readInputFile(inputFile);
      int mostAbundantBug = findMostAbundantBug(bugOccurrences);
      int occurrences = bugOccurrences.getOrDefault(mostAbundantBug, 0);
      System.out.println("Most abundant bug is " + mostAbundantBug + " with " + occurrences + " occurrences");
    } catch (IOException e) {
      System.err.println("Error reading input file: " + e.getMessage());
    }
  }

  private static Map<Integer, Integer> readInputFile(String inputFile) throws IOException {
    Map<Integer, Integer> bugOccurrences = new HashMap<>();
    Map<Integer, Double> contributionMap = new HashMap<>(); // To store percentage contribution for each bug

    try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
      String line;
      boolean headerSkipped = false;
      while ((line = br.readLine()) != null) {
        if (!headerSkipped) {
          headerSkipped = true;
          continue; // Skip header row
        }
        String[] parts = line.split("\t");

        int bugId = Integer.parseInt(parts[1]);
        int parentBugId = parts[2].equals("NULL") ? -1 : Integer.parseInt(parts[2]);
        int occurrences = parts[3].equals("NULL") ? 0 : Integer.parseInt(parts[3]);
        double contribution = Double.parseDouble(parts[4]) / 100.0;

        contributionMap.put(bugId, contribution);

        // Adjust occurrences for all bugs (including root bugs)
        occurrences = (int) Math.round(occurrences / contribution);
        bugOccurrences.put(bugId, occurrences);
      }
    }

    return bugOccurrences;
  }

  private static int findMostAbundantBug(Map<Integer, Integer> bugOccurrences) {
    int maxOccurrences = 0;
    int mostAbundantBug = -1;
    for (Map.Entry<Integer, Integer> entry : bugOccurrences.entrySet()) {
      if (entry.getValue() > maxOccurrences) {
        maxOccurrences = entry.getValue();
        mostAbundantBug = entry.getKey();
      }
    }
    return mostAbundantBug;
  }
}
