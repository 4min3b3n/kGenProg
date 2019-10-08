package jp.kusumotolab.kgenprog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.fl.Ample;
import jp.kusumotolab.kgenprog.ga.mutation.HeuristicMutation;

public class CUILauncher {

  public static void main(final String[] args) {
    try {
      final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
      final CUILauncher launcher = new CUILauncher();
      launcher.launch(config);
    } catch (IllegalArgumentException e) {
      System.exit(1);
    }
  }

  public void launch(final Configuration config) {
    setLogLevel(config.getLogLevel());

    final KGenProgMain kGenProgMain = new KGenProgMainBuilder()
        .faultLocalization(context -> new Ample())
        .mutation(context -> {
          final Configuration configuration = context.getConfig();
          return new HeuristicMutation(configuration.getMutationGeneratingCount(),
              context.getRandom(), context.getCandidateSelection(), configuration.getScope(),
              configuration.getNeedHistoricalElement());
        })
        .build(config);

    kGenProgMain.run();
  }

  // region Private Method

  private void setLogLevel(final Level logLevel) {
    final ch.qos.logback.classic.Logger rootLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(logLevel);
  }

  // endregion
}
