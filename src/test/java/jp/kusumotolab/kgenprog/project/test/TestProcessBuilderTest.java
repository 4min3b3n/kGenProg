package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.COVERED;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.EMPTY;
import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.NOT_COVERED;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import jp.kusumotolab.kgenprog.project.TargetProject;
import org.junit.Before;
import org.junit.Test;

public class TestProcessBuilderTest {

  final static FullyQualifiedName test01 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
  final static FullyQualifiedName test02 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
  final static FullyQualifiedName test03 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
  final static FullyQualifiedName test04 =
      new TestFullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");
  final static FullyQualifiedName buggyCalculator =
      new TargetFullyQualifiedName("jp.kusumotolab.BuggyCalculator");

  @Before
  public void before() throws IOException {
    TestResults.getSerFilePath().toFile().delete();
  }

  @Test
  public void testProcessBuilderBuild01() throws ClassNotFoundException, IOException {
    final Path rootDir = Paths.get("example/example01");
    final Path outDir = rootDir.resolve("_bin");
    final TargetProject targetProject = TargetProject.generate(rootDir);

    // main
    final TestProcessBuilder builder = new TestProcessBuilder(targetProject, outDir);
    builder.start();
    final TestResults r = TestResults.deserialize();

    // テストの結果はこうなるはず
    assertThat(r.getExecutedTestFQNs().size(), is(4));
    assertThat(r.getSuccessRate(), is(1.0 * 3 / 4));
    assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04)));
    assertThat(r.getTestResult(test01).failed, is(false));
    assertThat(r.getTestResult(test02).failed, is(false));
    assertThat(r.getTestResult(test03).failed, is(true));
    assertThat(r.getTestResult(test04).failed, is(false));

    // BuggyCalculatorTest.test01 実行によるbuggyCalculatorのカバレッジはこうなるはず
    assertThat(r.getTestResult(test01).getCoverages(buggyCalculator).statuses, is(contains(EMPTY,
        COVERED, EMPTY, COVERED, COVERED, EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED)));

    // BuggyCalculatorTest.test04 実行によるbuggyCalculatorのバレッジはこうなるはず
    assertThat(r.getTestResult(test04).getCoverages(buggyCalculator).statuses, is(contains(EMPTY,
        COVERED, EMPTY, COVERED, NOT_COVERED, EMPTY, EMPTY, COVERED, EMPTY, COVERED)));
  }

}
