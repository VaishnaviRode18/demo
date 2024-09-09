package ai;
import java.util.*;
import java.io.*;

public class BugAnalyzer {

    static class BugNode {
        int bugId;
        int totalOccurrences = 0; // Total occurrences for this bug, initialized to 0
        List<BugNode> children = new ArrayList<>(); // List of children bugs

        public BugNode(int id) {
            this.bugId = id; // Constructor to set bug ID
        }
    }

    public static void main(String[] args) {
        try {
            // Default file name for input
            String fileName = "D:\\Downloads\\ai\\ai\\bugs.tsv";
            File inputFile = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            Map<Integer, BugNode> nodeMap = new HashMap<>();
            String line = reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length < 5) {
                    System.out.println("Skipping incomplete line: " + Arrays.toString(parts));
                    continue; // Skip incomplete lines
                }

                try {
                    int bugId = Integer.parseInt(parts[1].trim());
                    int parentBugId = Integer.parseInt(parts[2].trim());
                    int occurrences = "NULL".equals(parts[3]) ? 0 : Integer.parseInt(parts[3].trim());

                    BugNode currentNode = nodeMap.getOrDefault(bugId, new BugNode(bugId));
                    currentNode.totalOccurrences += occurrences;
                    nodeMap.put(bugId, currentNode);

                    if (parentBugId != -1) { // Check if there is a parent bug
                        BugNode parentNode = nodeMap.getOrDefault(parentBugId, new BugNode(parentBugId));
                        parentNode.children.add(currentNode);
                        nodeMap.put(parentBugId, parentNode);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line due to invalid number format: " + line);
                    continue;
                }
            }

            // Finding the root bugs and calculating the most abundant bug
            int maxOccurrences = 0;
            int bugIdWithMaxOccurrences = -1;

            Set<Integer> potentialRoots = new HashSet<>(nodeMap.keySet());
            for (BugNode node : nodeMap.values()) {
                for (BugNode child : node.children) {
                    potentialRoots.remove(child.bugId);
                }
            }

            for (int rootId : potentialRoots) {
                BugNode rootNode = nodeMap.get(rootId);
                calculateTotalOccurrences(rootNode);
                if (rootNode.totalOccurrences > maxOccurrences) {
                    maxOccurrences = rootNode.totalOccurrences;
                    bugIdWithMaxOccurrences = rootId;
                }
            }

            System.out.println("Most abundant bug is " + bugIdWithMaxOccurrences + " with " + maxOccurrences + " occurrences");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Recursive method to calculate total occurrences for a bug and its descendants
    private static void calculateTotalOccurrences(BugNode node) {
        for (BugNode child : node.children) {
            calculateTotalOccurrences(child);
            node.totalOccurrences += child.totalOccurrences;
        }
    }
}
