package jp.kusumotolab.kgenprog.ga;

import java.util.Random;
import jp.kusumotolab.kgenprog.Annotations;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.StrategyType;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;

public class Context {

  private final Random random;
  private final Configuration config;
  private final Annotations annotations;

  public Context(final Random random, final Configuration config, final Annotations annotations) {
    this.random = random;
    this.config = config;
    this.annotations = annotations;
  }

  public Context(final Context context) {
    this(context.getRandom(), context.getConfig(), context.getAnnotations());
  }

  public Random getRandom() {
    return random;
  }

  public Configuration getConfig() {
    return config;
  }

  public Annotations getAnnotations() {
    return annotations;
  }

  public StrategyType getStrategyType() {
    throw new UnsupportedOperationException();
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

    @Override
    public StrategyType getStrategyType() {
      return StrategyType.FaultLocalization;
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

    @Override
    public StrategyType getStrategyType() {
      return StrategyType.CandidateSelection;
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

    @Override
    public StrategyType getStrategyType() {
      return StrategyType.Mutation;
    }
  }
}
