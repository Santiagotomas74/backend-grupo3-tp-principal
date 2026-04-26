import smile.classification.RandomForest;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.io.Read;
import smile.validation.metric.Accuracy;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

public class TrainModel {

  public static void main(String[] args) throws IOException, URISyntaxException {

    DataFrame df = Read.csv(
        Paths.get("data/dataset_envios.csv").toString(),
        CSVFormat.DEFAULT.withFirstRecordAsHeader());

    System.out.println("Dataset loaded: " + df.size() + " rows × " + df.ncol() + " columns");

    System.out.println("\n=== Raw Schema ===");
    System.out.println(df.schema());

    System.out.println("\n=== After Factorize ===");
    System.out.println(df.schema());

    String target = "prioridad";

    Formula formula = Formula.lhs(target);

    System.out.println("\nTraining Random Forest...");

    RandomForest model = RandomForest.fit(formula, df);

    System.out.println("\n=== Model Metrics ===");
    System.out.println(model.metrics());

    String[] featureNames = df.drop(target).names();
    double[] importance = model.importance();

    System.out.println("\n=== Feature Importance ===");
    for (int i = 0; i < importance.length; i++) {
      if (i < featureNames.length) {
        System.out.printf("%-18s : %.4f%n", featureNames[i], importance[i]);
      }
    }

    int[] predictions = model.predict(df);
    int[] actual = df.column(target).toIntArray();

    double accuracy = Accuracy.of(actual, predictions);

    System.out.println("\nTraining Accuracy: " +
        String.format("%.4f", accuracy) +
        " (" + String.format("%.2f", accuracy * 100) + "%)");

    // GUARDAR EL MODELO EN DISCO
    try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("priorityModel.ser"))) {
      oos.writeObject(model);
    }
  }
}