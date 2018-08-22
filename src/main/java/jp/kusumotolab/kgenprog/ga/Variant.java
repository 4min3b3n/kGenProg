package jp.kusumotolab.kgenprog.ga;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public class Variant {

  private static Logger log = LoggerFactory.getLogger(Variant.class);

  private final Gene gene;
  private Fitness fitness;
  private GeneratedSourceCode generatedSourceCode;
  private List<Suspiciousness> suspiciousnesses;
  private TestResults testResults;

  public Variant(final Gene gene) {
    this.gene = gene;
  }

  public boolean isCompleted() {
    return fitness.isMaximum();
  }

  public Gene getGene() {
    log.debug("enter getGene()");
    return gene;
  }

  public Fitness getFitness() {
    log.debug("enter getFitness()");
    return fitness;
  }
  
  public List<Suspiciousness> getSuspiciousnesses(){
    log.debug("enter getSuspiciousnesses()");
    return suspiciousnesses;
  }
  
  public GeneratedSourceCode getGeneratedSourceCode() {
    log.debug("enter getGeneratedSourceCode()");
    return generatedSourceCode;
  }
  
  public TestResults getTestResults() {
    log.debug("enter getTestResults()");
    return testResults;
  }

  public void setFitness(final Fitness fitness) {
    log.debug("enter setFitness(Fitness)");
    this.fitness = fitness;
  }

  public void setGeneratedSourceCode(final GeneratedSourceCode generatedSourceCode) {
    log.debug("enter setGeneratedSourceCode(GeneratedSourceCode)");
    this.generatedSourceCode = generatedSourceCode;
  }
  
  public void setSuspiciousnesses(final List<Suspiciousness> suspiciousnesses) {
    log.debug("enter setSuspiciousnesses(List<Suspiciousness>)");
    this.suspiciousnesses = suspiciousnesses;
  }

  public void setTestResults(TestResults testResults) {
    log.debug("enter setTestResults(TestResults)");
    this.testResults = testResults;
  }
}
