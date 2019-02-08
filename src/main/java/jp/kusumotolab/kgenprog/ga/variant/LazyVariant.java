package jp.kusumotolab.kgenprog.ga.variant;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jp.kusumotolab.kgenprog.KGenProgMain;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.EmptyTestResults;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class LazyVariant extends Variant {

  private Single<TestResults> testResultsSingle;
  private Single<Fitness> fitnessSingle;
  private Single<List<Suspiciousness>> suspiciousnessListSingle;

  public LazyVariant(final long id, final int generationNumber, final Gene gene,
      final GeneratedSourceCode generatedSourceCode, final HistoricalElement historicalElement) {
    super(id, generationNumber, gene, generatedSourceCode, null, null, null,
        historicalElement);
  }

  void subscribe() {
    if (testResultsSingle == null) {
      return;
    }
    testResultsSingle.observeOn(Schedulers.io()).subscribe(e -> {
      System.out.print("X");
    });
  }

  void setTestResultsSingle(
      final Single<TestResults> testResultsSingle) {
    this.testResultsSingle = testResultsSingle;
  }

  void setFitnessSingle(
      final Single<Fitness> fitnessSingle) {
    this.fitnessSingle = fitnessSingle;
  }

  void setSuspiciousnessListSingle(
      final Single<List<Suspiciousness>> suspiciousnessListSingle) {
    this.suspiciousnessListSingle = suspiciousnessListSingle;
  }

  @Override
  public boolean isCompleted() {
    return fitnessSingle.blockingGet()
        .isMaximum();
  }

  @Override
  public boolean isBuildSucceeded() {
    return EmptyTestResults.class != testResultsSingle.blockingGet()
        .getClass();
  }

  @Override
  public TestResults getTestResults() {
    return this.testResultsSingle.blockingGet();
  }

  @Override
  public Fitness getFitness() {
    return this.fitnessSingle.blockingGet();
  }

  @Override
  public List<Suspiciousness> getSuspiciousnesses() {
    return this.suspiciousnessListSingle.blockingGet();
  }
}
