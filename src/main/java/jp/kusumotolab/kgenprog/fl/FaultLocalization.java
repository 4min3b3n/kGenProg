package jp.kusumotolab.kgenprog.fl;

import java.util.List;
import jp.kusumotolab.kgenprog.ga.Context.FaultLocalizationContext;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.test.TestResults;

public abstract class FaultLocalization {

  /**
   * コンストラクタ
   * @param context FLを生成するまでの過程で生成されたオブジェクトの情報
   */
  public FaultLocalization(final FaultLocalizationContext context) {
  }

  public abstract List<Suspiciousness> exec(GeneratedSourceCode generatedSourceCode, TestResults testResults);
}
