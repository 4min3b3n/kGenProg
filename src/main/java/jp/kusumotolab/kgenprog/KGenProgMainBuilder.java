package jp.kusumotolab.kgenprog;

import java.util.Random;
import java.util.function.Function;
import jp.kusumotolab.kgenprog.Context.CandidateSelectionContext;
import jp.kusumotolab.kgenprog.Context.CrossoverContext;
import jp.kusumotolab.kgenprog.Context.FaultLocalizationContext;
import jp.kusumotolab.kgenprog.Context.FirstVariantSelectionStrategyContext;
import jp.kusumotolab.kgenprog.Context.MutationContext;
import jp.kusumotolab.kgenprog.Context.SecondVariantSelectionStrategyContext;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.mutation.SimpleMutation;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementSelection;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.output.PatchGenerator;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class KGenProgMainBuilder {

  private Function<FaultLocalizationContext, FaultLocalization> faultLocalizationCreator = (context) -> context.getConfig()
      .getFaultLocalization()
      .initialize();

  private Function<CandidateSelectionContext, CandidateSelection> candidateSelectionCreator = (context) -> new RouletteStatementSelection(
      context.getRandom());

  private Function<MutationContext, Mutation> mutationCreator = (context) -> {
    final Configuration config = context.getConfig();
    return new SimpleMutation(config.getMutationGeneratingCount(), context.getRandom(),
        context.getCandidateSelection(), config.getScope(), config.getNeedHistoricalElement());
  };

  private Function<FirstVariantSelectionStrategyContext, FirstVariantSelectionStrategy> firstVariantSelectionStrategyCreator = (context) -> context.getConfig()
      .getFirstVariantSelectionStrategy()
      .initialize(context.getRandom());

  private Function<SecondVariantSelectionStrategyContext, SecondVariantSelectionStrategy> secondVariantSelectionStrategyCreator = (context) -> context.getConfig()
      .getSecondVariantSelectionStrategy()
      .initialize(context.getRandom());

  private Function<CrossoverContext, Crossover> crossoverCreator = (context) -> {
    final Configuration config = context.getConfig();
    return config.getCrossoverType()
        .initialize(context.getRandom(), context.getFirstVariantSelectionStrategy(),
            context.getSecondVariantSelectionStrategy(), config.getCrossoverGeneratingCount(),
            config.getNeedHistoricalElement());
  };

  public KGenProgMain build(final Configuration config) {
    final Random random = new Random(config.getRandomSeed());
    final Context context = new Context(random, config);

    final FaultLocalizationContext faultLocalizationContext = context.faultLocalization();
    final FaultLocalization faultLocalization = faultLocalizationCreator.apply(
        faultLocalizationContext);

    final CandidateSelectionContext candidateSelectionContext = faultLocalizationContext.candidateSelection(
        faultLocalization);
    final CandidateSelection candidateSelection = candidateSelectionCreator.apply(
        candidateSelectionContext);

    final MutationContext mutationContext = candidateSelectionContext.mutation(candidateSelection);
    final Mutation mutation = mutationCreator.apply(mutationContext);

    final FirstVariantSelectionStrategyContext firstVariantSelectionStrategyContext = mutationContext.firstVariantSelectionStrategy(
        mutation);
    final FirstVariantSelectionStrategy firstVariantSelectionStrategy = firstVariantSelectionStrategyCreator.apply(
        firstVariantSelectionStrategyContext);

    final SecondVariantSelectionStrategyContext secondVariantSelectionStrategyContext = firstVariantSelectionStrategyContext.secondVariantSelectionStrategy(
        firstVariantSelectionStrategy);
    final SecondVariantSelectionStrategy secondVariantSelectionStrategy = secondVariantSelectionStrategyCreator.apply(
        secondVariantSelectionStrategyContext);

    final CrossoverContext crossoverContext = secondVariantSelectionStrategyContext.crossover(
        secondVariantSelectionStrategy);
    final Crossover crossover = crossoverCreator.apply(crossoverContext);

    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection(config.getHeadcount(),
        random);
    final TestExecutor testExecutor = new LocalTestExecutor(config);
    final PatchGenerator patchGenerator = new PatchGenerator();

    final KGenProgMain kGenProgMain =
        new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, testExecutor, patchGenerator);

    return kGenProgMain;
  }

  public KGenProgMainBuilder faultLocalization(
      final Function<FaultLocalizationContext, FaultLocalization> faultLocalizationCreator) {
    this.faultLocalizationCreator = faultLocalizationCreator;
    return this;
  }

  public KGenProgMainBuilder candidateSelection(
      final Function<CandidateSelectionContext, CandidateSelection> candidateSelectionCreator) {
    this.candidateSelectionCreator = candidateSelectionCreator;
    return this;
  }

  public KGenProgMainBuilder mutation(final Function<MutationContext, Mutation> mutationCreator) {
    this.mutationCreator = mutationCreator;
    return this;
  }

  public KGenProgMainBuilder firstVariantSelectionStrategy(
      final Function<FirstVariantSelectionStrategyContext, FirstVariantSelectionStrategy> firstVariantSelectionStrategyCreator) {
    this.firstVariantSelectionStrategyCreator = firstVariantSelectionStrategyCreator;
    return this;
  }

  public KGenProgMainBuilder secondVariantSelectionStrategy(
      final Function<SecondVariantSelectionStrategyContext, SecondVariantSelectionStrategy> secondVariantSelectionStrategyCreator) {
    this.secondVariantSelectionStrategyCreator = secondVariantSelectionStrategyCreator;
    return this;
  }

  public KGenProgMainBuilder crossover(
      final Function<CrossoverContext, Crossover> crossoverCreator) {
    this.crossoverCreator = crossoverCreator;
    return this;
  }
}
