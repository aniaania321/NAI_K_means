import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String filePath = "/Users/aniasmuga/IdeaProjects/psm8/NAI_K_means/src/IRIS.csv";
        List<double[]> data = loadData(filePath);

        int k = 3;
        KMeans kMeans = new KMeans(k);
        kMeans.fit(data);

        double wcss = EvaluationMetrics.calculateWCSS(kMeans.clusters, kMeans.centroids);
        System.out.printf("WCSS score: %.4f%n", wcss);

        for (int i = 0; i < kMeans.clusters.size(); i++) {
            System.out.println("Cluster " + i + ": " + kMeans.clusters.get(i).size() + " points");
        }

        exportClusteredData(data, kMeans.assignments, "iris_clustered.csv");
        exportCentroids(kMeans.centroids, "centroids.csv");
    }

    private static List<double[]> loadData(String filePath) {
        List<double[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length < 4) continue;

                double[] vector = new double[4];
                for (int i = 0; i < 4; i++) {
                    vector[i] = Double.parseDouble(parts[i]);
                }
                data.add(vector);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return data;
    }

    private static void exportClusteredData(List<double[]> data, List<Integer> assignments, String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("sepal_length,sepal_width,petal_length,petal_width,cluster");
            for (int i = 0; i < data.size(); i++) {
                double[] row = data.get(i);
                int cluster = assignments.get(i);
                writer.printf(Locale.US, "%.2f,%.2f,%.2f,%.2f,%d%n", row[0], row[1], row[2], row[3], cluster);
            }
        } catch (IOException e) {
            System.err.println("Error writing clustered CSV: " + e.getMessage());
        }
    }

    private static void exportCentroids(List<double[]> centroids, String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("sepal_length,sepal_width,petal_length,petal_width,cluster");
            for (int i = 0; i < centroids.size(); i++) {
                double[] c = centroids.get(i);
                writer.printf(Locale.US, "%.2f,%.2f,%.2f,%.2f,%d%n", c[0], c[1], c[2], c[3], i);
            }
        } catch (IOException e) {
            System.err.println("Error writing centroids CSV: " + e.getMessage());
        }
    }
}
