import java.util.*;

public class KMeans {
    public int k;
    public List<double[]> centroids;
    public List<List<double[]>> clusters;
    public List<Integer> assignments = new ArrayList<>();
    private final Random random = new Random();

    public KMeans(int k) {
        this.k = k;
        this.centroids = new ArrayList<>();
        this.clusters = new ArrayList<>();
    }

    public void fit(List<double[]> data) {
        initializeClusters(data);

        boolean changed;
        do {
            calculateCentroids();
            changed = reassignPoints(data);
        } while (changed);
    }

    private void initializeClusters(List<double[]> data) {
        clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            clusters.add(new ArrayList<>());
        }

        List<double[]> shuffled = new ArrayList<>(data);
        Collections.shuffle(shuffled, new Random());

        // First, assign one point to each cluster to ensure none are empty
        for (int i = 0; i < k; i++) {
            clusters.get(i).add(shuffled.get(i));
        }

        // Assign the rest randomly
        Random rand = new Random();
        for (int i = k; i < shuffled.size(); i++) {
            int randomCluster = rand.nextInt(k);
            clusters.get(randomCluster).add(shuffled.get(i));
        }

        calculateCentroids(); // Recalculate centroids based on the initialized clusters
    }


    private void calculateCentroids() {
        centroids.clear();
        for (List<double[]> cluster : clusters) {
            int dim = cluster.get(0).length;
            double[] centroid = new double[dim];

            for (double[] point : cluster) {
                for (int i = 0; i < dim; i++) {
                    centroid[i] += point[i];
                }
            }
            for (int i = 0; i < dim; i++) {
                centroid[i] /= cluster.size();
            }

            centroids.add(centroid);
        }
    }

    private boolean reassignPoints(List<double[]> data) {
        List<List<double[]>> newClusters = new ArrayList<>();
        List<Integer> newAssignments = new ArrayList<>();

        for (int i = 0; i < k; i++) newClusters.add(new ArrayList<>());

        for (double[] point : data) {
            int index = findClosestCluster(point, centroids);
            newClusters.get(index).add(point);
            newAssignments.add(index);
        }

        boolean changed = !clustersEqual(newClusters, clusters);
        clusters = newClusters;
        assignments = newAssignments;
        return changed;
    }


    private boolean clustersEqual(List<List<double[]>> a, List<List<double[]>> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).size() != b.get(i).size()) return false;
        }
        return true; // Simplified check
    }

    public static int findClosestCluster(double[] vector, List<double[]> centroids) {
        List<Integer> closestIndices = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.size(); i++) {
            double distance = euclideanDistance(vector, centroids.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestIndices.clear();
                closestIndices.add(i);
            } else if (distance == minDistance) {
                closestIndices.add(i);
            }
        }
        return closestIndices.get(new Random().nextInt(closestIndices.size()));
    }

    public static double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += Math.pow(a[i] - b[i], 2);
        return Math.sqrt(sum);
    }
}
