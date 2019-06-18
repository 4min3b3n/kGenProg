package jp.kusumotolab.kgenprog.project.test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

/**
 * 単一のテスト結果を表すオブジェクト．<br>
 * テストの成否とカバレッジ情報を持つ．<br>
 * 
 * @author shinsuke
 *
 */
public class TestResult {

  final private FullyQualifiedName executedTestFQN;
  final private boolean failed;
  final private Map<FullyQualifiedName, Coverage> coverages;

  /**
   * constructor
   * 
   * @param executedTestFQN 実行したテストメソッドの名前
   * @param failed テストの結果
   * @param coverages テスト対象それぞれの行ごとのCoverage計測結果
   */
  public TestResult(final FullyQualifiedName executedTestFQN, final boolean failed,
      final Map<FullyQualifiedName, Coverage> coverages) {
    this.executedTestFQN = executedTestFQN;
    this.failed = failed;
    this.coverages = coverages;
  }

  /**
   * 当該テストで実行されたクラスのFQN一覧を取得
   * @return 実行されたクラスのFQN一覧
   */
  public List<FullyQualifiedName> getExecutedTargetFQNs() {
    return this.coverages.keySet()
        .stream()
        .collect(Collectors.toList());
  }

  /**
   * 指定テストFQNに対するカバレッジの結果を取得
   * @param testFQN カバレッジの結果
   * @return
   */
  public Coverage getCoverages(final FullyQualifiedName testFQN) {
    return this.coverages.get(testFQN);
  }

  public FullyQualifiedName getExecutedTestFQN() {
    return executedTestFQN;
  }

  public boolean wasFailed() {
    return failed;
  }

  @Override
  public String toString() {
    return toString(0);
  }

  /**
   * jsonシリアライザ
   * @param indentDepth インデント幅
   * @return
   */
  public String toString(final int indentDepth) {
    final StringBuilder sb = new StringBuilder();
    final String indent = StringUtils.repeat(" ", indentDepth);
    sb.append(indent + "{\n");
    sb.append(indent + "  \"executedTestFQN\": \"" + executedTestFQN + "\",\n");
    sb.append(indent + "  \"wasFailed\": " + failed + ",\n");
    sb.append(indent + "  \"coverages\": [\n");
    sb.append(String.join(",\n", coverages.values()
        .stream()
        .map(c -> c.toString(indentDepth + 2))
        .collect(Collectors.toList())));
    sb.append("\n");
    sb.append(indent + "  ]\n");
    sb.append(indent + "}");
    return sb.toString();
  }
}
