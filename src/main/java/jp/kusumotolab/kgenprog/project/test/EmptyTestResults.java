package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * テスト失敗時のテスト結果．<br>
 * いわゆるNullオブジェクト．<br>
 *
 * @author shinsuke
 */
public class EmptyTestResults extends TestResults {

  final private String cause;

  @Deprecated
  public EmptyTestResults() {
    this("");
  }

  public EmptyTestResults(final String cause) {
    this.cause = cause;
  }

  /**
   * {@inheritDoc}<br>
   * Double.NaNを返す
   */
  @Override
  public double getSuccessRate() {
    return Double.NaN;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfPassedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return 0;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfFailedTestsExecutingTheStatement(final ProductSourcePath productSourcePath,
      final ASTLocation location) {
    return 0;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfPassedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return 0;
  }

  /**
   * {@inheritDoc}<br>
   * 0を返す
   */
  @Override
  public long getNumberOfFailedTestsNotExecutingTheStatement(
      final ProductSourcePath productSourcePath, final ASTLocation location) {
    return 0;
  }

  /**
   * テスト結果が得られなかった理由を返す．
   *
   * @return テスト結果が得られなかった理由
   */
  public String getCause() {
    return cause;
  }
}
