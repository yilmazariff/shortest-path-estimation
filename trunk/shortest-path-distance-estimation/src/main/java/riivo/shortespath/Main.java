package riivo.shortespath;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Main {

  private String file;
  private int LANDMARKS = 11;
  private int TEST_SET_SIZE = 31;

  /**
   * @param args
   */
  public static void main(String[] args) {
    Main m = new Main();
    m.init(args);
    m.run();

  }

  private void run() {

    GraphDistanceEstimation gde = new GraphDistanceEstimation(file, LANDMARKS, TEST_SET_SIZE);
    gde.runExperiments();

  }

  public void init(String[] args) {
    CommandLineParser parser = new PosixParser();

    Options options = new Options();
    options.addOption("file", true, "graph file");
    options.addOption("landmarks", true, "landmarks size, integer");
    options.addOption("test_set", true, "test_set size, integer");

    try {
      CommandLine line = parser.parse(options, args);

      if (line.hasOption("file")) {
        this.file = line.getOptionValue("file").trim();
      } else {
        (new HelpFormatter()).printHelp("main", options, true);
        System.exit(0);
      }
      if (line.hasOption("landmarks")) {
        this.LANDMARKS = Integer.parseInt(line.getOptionValue("landmarks"));
      }
      if (line.hasOption("test_set")) {
        this.TEST_SET_SIZE = Integer.parseInt(line.getOptionValue("test_set"));
      }

    } catch (ParseException exp) {
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
    }

  }

}
