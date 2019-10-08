package jp.kusumotolab.kgenprog;

/**
 * kGenProgで使用するストラテジーの列挙
 */
public enum StrategyType {
  FaultLocalization {
    @Override
    public String extractNameFromConfiguration(final Configuration configuration) {
      return configuration.getFaultLocalizationName();
    }
  },
  Mutation {
    @Override
    public String extractNameFromConfiguration(final Configuration configuration) {
      return configuration.getMutationName();
    }
  },
  CandidateSelection {
    @Override
    public String extractNameFromConfiguration(final Configuration configuration) {
      return configuration.getCandidateSelectionName();
    }
  },
  FirstVariantSelectionStrategy {
    @Override
    public String extractNameFromConfiguration(final Configuration configuration) {
      return configuration.getFirstVariantSelectionStrategyName();
    }
  },
  SecondVariantSelectionStrategy {
    @Override
    public String extractNameFromConfiguration(final Configuration configuration) {
      return configuration.getSecondVariantSelectionStrategyName();
    }
  },
  Crossover {
    @Override
    public String extractNameFromConfiguration(final Configuration configuration) {
      return configuration.getCrossoverName();
    }
  };

  public abstract String extractNameFromConfiguration(final Configuration configuration);
}
