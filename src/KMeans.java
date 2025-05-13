import java.util.*;

public class KMeans {
    public int k;
    public List<double[]> centroids;
    public List<List<double[]>> clusters;//double [] to punkt, lissta to cluster zewnetrzna lista to kilka clusterow
    public List<Integer> assignments = new ArrayList<>();//store which cluster the point is assigned to

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
        } while (changed);// stop when the assignment didn't change
    }

    //initialize
    private void initializeClusters(List<double[]> data) {
        clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {//we create k clusters
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

        calculateCentroids();//calculate centroids based on initial clusters
    }

    //calculate centroid by computing the mean of each cluster, assignemnt deosnt matter because we calculate a new one anyway
    //we always clear the centorids list and then add by iterating though clusters so the indexes will always be the same
    private void calculateCentroids() {
        centroids.clear();
        for (List<double[]> cluster : clusters) {
            if (cluster.isEmpty()) {
                // Shouldn't happen because k should be <= 3 as we have 3 species but just in case
                System.out.println("Skipping empty cluster.");
                continue;
            }
            int dim = cluster.get(0).length;
            double[] centroid = new double[dim];

            for (double[] point : cluster) {//przechodzimy przez punkty
                for (int i = 0; i < dim; i++) {
                    centroid[i] += point[i];//dodajemy wszytskie dimensions z każdego punktu do centorid która ma taki sam wymiar
                }
            }
            for (int i = 0; i < dim; i++) {
                centroid[i] /= cluster.size();//dzielimy
            }

            centroids.add(centroid);
        }
    }

    //Use find closest cluster to reassign points to the nearest centroid, update assignements
    private boolean reassignPoints(List<double[]> data) {
        List<List<double[]>> newClusters = new ArrayList<>();
        List<Integer> newAssignments = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            newClusters.add(new ArrayList<>());
        }

        for (double[] point : data) {// add point to the nearest centroid taken from centroid list
            int index = findClosestCluster(point, centroids);
            newClusters.get(index).add(point);
            newAssignments.add(index);
        }

        boolean changed = !clustersEqual(newClusters, clusters);//check if assignment changed
        clusters = newClusters;//update clusters
        assignments = newAssignments;//update assignments
        return changed;
    }


    private boolean clustersEqual(List<List<double[]>> a, List<List<double[]>> b) {
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).size() != b.get(i).size())//compare sizes of clusters to see if they are equal
                return false;
        }
        return true;
    }

    public static int findClosestCluster(double[] vector, List<double[]> centroids) {
        List<Integer> closestIndices = new ArrayList<>();//list for the case there are a few equal
        double minDistance = 100000000.0;

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
