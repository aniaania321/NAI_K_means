import java.util.List;

public class EvaluationMetrics {
    public static double calculateWCSS(List<List<double[]>> clusters, List<double[]> centroids) {
        double wcss = 0.0;
        for (int i = 0; i < clusters.size(); i++) {
            for (double[] point : clusters.get(i)) {
                double dist = KMeans.euclideanDistance(point, centroids.get(i));
                wcss += dist * dist;
            }
        }
        return wcss;
    }
}
