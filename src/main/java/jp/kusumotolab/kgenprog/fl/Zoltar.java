package jp.kusumotolab.kgenprog.fl;

import java.util.ArrayList;
import java.util.List;
import jp.kusumotolab.kgenprog.Kgp;
import jp.kusumotolab.kgenprog.StrategyType;
import jp.kusumotolab.kgenprog.ga.Context.FaultLocalizationContext;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.ASTLocations;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.test.TestResults;

/**
 *  FL戦略の一つ(Zoltar).
 *  <br>
 *  {@code value = ef / (ef + nf + ep + 10000 * nf * ep / ef)}<br>
 *  {@code ef}:該当する文を実行し，通過しなかったテストの個数<br>
 *  {@code nf}:該当する文を実行せずに，通過しなかったテストの個数<br>
 *  {@code ep}:該当する文を実行し，通過したテストの個数
 */
@Kgp(type = StrategyType.FaultLocalization, name = "Zolter")
public class Zoltar extends FaultLocalization {

  /**
   * コンストラクタ
   * @param context FLを生成するまでの過程で生成されたオブジェクトの情報
   */
  public Zoltar(final FaultLocalizationContext context) {
    super(context);
  }

  /**
   * 疑惑値を計算する.
   * @param generatedSourceCode 自動バグ限局の対象ソースコード
   * @param testResults テストの実行結果
   * @return suspiciousnesses 疑惑値
   */
  @Override
  public List<Suspiciousness> exec(final GeneratedSourceCode generatedSourceCode,
      final TestResults testResults) {

    final List<Suspiciousness> suspiciousnesses = new ArrayList<>();

    for (final GeneratedAST<ProductSourcePath> ast : generatedSourceCode.getProductAsts()) {
      final ProductSourcePath path = ast.getSourcePath();
      final int lastLineNumber = ast.getNumberOfLines();
      final ASTLocations astLocations = ast.createLocations();

      for (int line = 1; line <= lastLineNumber; line++) {
        final List<ASTLocation> locations = astLocations.infer(line);
        if (!locations.isEmpty()) {
          final ASTLocation l = locations.get(locations.size() - 1);
          final long ef = testResults.getNumberOfFailedTestsExecutingTheStatement(path, l);
          final long nf = testResults.getNumberOfFailedTestsNotExecutingTheStatement(path, l);
          final long ep = testResults.getNumberOfPassedTestsExecutingTheStatement(path, l);
          final double value = ef / (ef + nf + ep + 10000d * nf * ep / ef);
          if (0d < value) {
            final Suspiciousness s = new Suspiciousness(l, value);
            suspiciousnesses.add(s);
          }
        }
      }
    }

    return suspiciousnesses;
  }
}
