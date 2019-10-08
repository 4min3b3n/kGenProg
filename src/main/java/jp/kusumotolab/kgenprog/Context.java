package jp.kusumotolab.kgenprog;

import java.util.Random;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;

public class Context {

  private final Random random;
  private final Configuration config;

  public Context(final Random random, final Configuration config) {
    this.random = random;
    this.config = config;
  }

  public Context(final Context context) {
    this(context.getRandom(), context.getConfig());
  }

  public Random getRandom() {
    return random;
  }

  public Configuration getConfig() {
    return config;
  }

  public FaultLocalizationContext faultLocalization() {
    return new FaultLocalizationContext(this);
  }

  // ==================================== Subtype of Context ====================================

  // For FaultLocalization
  public static class FaultLocalizationContext extends Context {

    public FaultLocalizationContext(final Context context) {
      super(context);
    }

    public CandidateSelectionContext candidateSelection(final FaultLocalization fl) {
      return new CandidateSelectionContext(this, fl);
    }
  }

  // For CandidateSelection
  public static class CandidateSelectionContext extends FaultLocalizationContext {

    private final FaultLocalization faultLocalization;

    public CandidateSelectionContext(final FaultLocalizationContext context,
        final FaultLocalization faultLocalization) {
      super(context);
      this.faultLocalization = faultLocalization;
    }

    public FaultLocalization getFaultLocalization() {
      return faultLocalization;
    }

    public MutationContext mutation(final CandidateSelection candidateSelection) {
      return new MutationContext(this, candidateSelection);
    }
  }

  // For Mutation
  public static class MutationContext extends CandidateSelectionContext {

    private final CandidateSelection candidateSelection;

    public MutationContext(final CandidateSelectionContext context,
        final CandidateSelection candidateSelection) {
      super(context, context.getFaultLocalization());
      this.candidateSelection = candidateSelection;
    }

    public CandidateSelection getCandidateSelection() {
      return candidateSelection;
    }

    public FirstVariantSelectionStrategyContext firstVariantSelectionStrategy(
        final Mutation mutation) {
      return new FirstVariantSelectionStrategyContext(this, mutation);
    }
  }

  // For FirstVariantSelectionStrategy
  public static class FirstVariantSelectionStrategyContext extends MutationContext {

    private final Mutation mutation;

    public FirstVariantSelectionStrategyContext(final MutationContext context,
        final Mutation mutation) {
      super(context, context.getCandidateSelection());
      this.mutation = mutation;
    }

    public Mutation getMutation() {
      return mutation;
    }

    public SecondVariantSelectionStrategyContext secondVariantSelectionStrategy(
        final FirstVariantSelectionStrategy firstVariantSelectionStrategy) {
      return new SecondVariantSelectionStrategyContext(this, firstVariantSelectionStrategy);
    }
  }

  // For SecondVariantSelectionStrategy
  public static class SecondVariantSelectionStrategyContext extends
      FirstVariantSelectionStrategyContext {

    private final FirstVariantSelectionStrategy firstVariantSelectionStrategy;

    public SecondVariantSelectionStrategyContext(final FirstVariantSelectionStrategyContext context,
        final FirstVariantSelectionStrategy firstVariantSelectionStrategy) {
      super(context, context.getMutation());
      this.firstVariantSelectionStrategy = firstVariantSelectionStrategy;
    }

    public FirstVariantSelectionStrategy getFirstVariantSelectionStrategy() {
      return firstVariantSelectionStrategy;
    }

    public CrossoverContext crossover(
        final SecondVariantSelectionStrategy secondVariantSelectionStrategy) {
      return new CrossoverContext(this, secondVariantSelectionStrategy);
    }
  }

  // For Crossover
  public static class CrossoverContext extends SecondVariantSelectionStrategyContext {

    private final SecondVariantSelectionStrategy secondVariantSelectionStrategy;

    public CrossoverContext(final SecondVariantSelectionStrategyContext context, final
    SecondVariantSelectionStrategy secondVariantSelectionStrategy) {
      super(context, context.getFirstVariantSelectionStrategy());
      this.secondVariantSelectionStrategy = secondVariantSelectionStrategy;
    }

    public SecondVariantSelectionStrategy getSecondVariantSelectionStrategy() {
      return secondVariantSelectionStrategy;
    }
  }
}