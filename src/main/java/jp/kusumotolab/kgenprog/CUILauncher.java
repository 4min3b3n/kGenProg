package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.Context;
import jp.kusumotolab.kgenprog.ga.Context.CandidateSelectionContext;
import jp.kusumotolab.kgenprog.ga.Context.CrossoverContext;
import jp.kusumotolab.kgenprog.ga.Context.FaultLocalizationContext;
import jp.kusumotolab.kgenprog.ga.Context.FirstVariantSelectionStrategyContext;
import jp.kusumotolab.kgenprog.ga.Context.MutationContext;
import jp.kusumotolab.kgenprog.ga.Context.SecondVariantSelectionStrategyContext;
import jp.kusumotolab.kgenprog.ga.Contexts;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.output.PatchGenerator;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class CUILauncher {

  public static void main(final String[] args) {
    try {
      final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
      final CUILauncher launcher = new CUILauncher();
      launcher.launch(config);
    } catch (final IllegalArgumentException | IOException e) {
      System.exit(1);
    }
  }

  public void launch(final Configuration config) throws IOException {
    setLogLevel(config.getLogLevel());

    final Annotations annotations = new Annotations();
    annotations.initialize(config);

    final Random random = new Random(config.getRandomSeed());
    final Context context = new Context(random, config, annotations);

    // FLの生成
    final FaultLocalizationContext flContext = context.faultLocalization();
    final FaultLocalization faultLocalization = Contexts.resolve(flContext);

    // CandidateSelectionの生成
    final CandidateSelectionContext candidateSelectionContext = flContext.candidateSelection(
        faultLocalization);
    final CandidateSelection candidateSelection = Contexts.resolve(candidateSelectionContext);

    // Mutationの生成
    final MutationContext mutationContext = candidateSelectionContext.mutation(candidateSelection);
    final Mutation mutation = Contexts.resolve(mutationContext);

    // FirstVariantSelectionStrategyの作成
    final FirstVariantSelectionStrategyContext firstVariantSelectionStrategyContext = mutationContext.firstVariantSelectionStrategy(
        mutation);
    final FirstVariantSelectionStrategy firstVariantSelectionStrategy = Contexts.resolve(
        firstVariantSelectionStrategyContext);

    // SecondVariantSelectionStrategyの作成
    final SecondVariantSelectionStrategyContext secondVariantSelectionStrategyContext = firstVariantSelectionStrategyContext.secondVariantSelectionStrategy(
        firstVariantSelectionStrategy);
    final SecondVariantSelectionStrategy secondVariantSelectionStrategy = Contexts.resolve(secondVariantSelectionStrategyContext);

    // Crossoverの作成
    final CrossoverContext crossoverContext = secondVariantSelectionStrategyContext.crossover(
        secondVariantSelectionStrategy);
    final Crossover crossover = Contexts.resolve(crossoverContext);

    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection(config.getHeadcount(),
        random);
    final TestExecutor testExecutor = new LocalTestExecutor(config);
    final PatchGenerator patchGenerator = new PatchGenerator();

    final KGenProgMain kGenProgMain =
        new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, testExecutor, patchGenerator);

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
