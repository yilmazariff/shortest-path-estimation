package riivo.shortespath;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Main {

  private String file;
  private int landmarks = 11;
  private int testSetSize = 15;
  private int iterations = 2;

  /**
   * @param args
   */
  public static void main(String[] args) {
    Main m = new Main();
    m.init(args);
    m.run();

  }

  private void run() {

    GraphDistanceEstimation gde = new GraphDistanceEstimation(file, landmarks, testSetSize, iterations);
    gde.runExperiments();

  }

  public void init(String[] args) {
    CommandLineParser parser = new PosixParser();

    Options options = new Options();
    options.addOption("file", true, "graph file");
    options.addOption("landmarks", true, "landmarks size, integer");
    options.addOption("test_set", true, "test_set size, integer");
    options.addOption("iterations", true, "number of iterations for validation, integer");

    try {
      CommandLine line = parser.parse(options, args);

      if (line.hasOption("file")) {
        this.file = line.getOptionValue("file").trim();
      } else {
        (new HelpFormatter()).printHelp("main", options, true);
        System.exit(0);
      }
      if (line.hasOption("landmarks")) {
        this.landmarks = Integer.parseInt(line.getOptionValue("landmarks"));
      }
      if (line.hasOption("test_set")) {
        this.testSetSize = Integer.parseInt(line.getOptionValue("test_set"));
      }
      if (line.hasOption("iterations")) {
        this.iterations = Integer.parseInt(line.getOptionValue("iterations"));
      }

    } catch (ParseException exp) {
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
    }

  }

}
