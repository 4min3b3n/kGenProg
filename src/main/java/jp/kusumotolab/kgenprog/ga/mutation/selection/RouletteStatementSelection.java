package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jp.kusumotolab.kgenprog.Kgp;
import jp.kusumotolab.kgenprog.StrategyType;
import jp.kusumotolab.kgenprog.ga.Context.CandidateSelectionContext;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.Query;
import jp.kusumotolab.kgenprog.ga.mutation.Scope;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.GeneratedJDTAST;

/**
 * 再利用するステートメントを重みをつけたルーレットで選択するクラス
 *
 * @see StatementSelection
 */
@Kgp(type = StrategyType.CandidateSelection, name = "Roulette")
public class RouletteStatementSelection extends StatementSelection {

  private final Random random;
  private Roulette<ReuseCandidate<Statement>> projectRoulette;
  private final Multimap<String, ReuseCandidate<Statement>> packageNameStatementMultimap = ArrayListMultimap.create();
  private final Multimap<FullyQualifiedName, ReuseCandidate<Statement>> fqnStatementMultiMap = ArrayListMultimap.create();
  private final Map<String, Roulette<ReuseCandidate<Statement>>> packageNameRouletteMap = new HashMap<>();
  private final Map<FullyQualifiedName, Roulette<ReuseCandidate<Statement>>> fqnRouletteMap = new HashMap<>();

  /**
   * コンストラクタ
   *
   * @param context CandidateSelectionを生成するまでの過程で生成されたオブジェクトの情報
   */
  public RouletteStatementSelection(final CandidateSelectionContext context) {
    super(context);
    this.random = context.getRandom();
  }

  /**
   * ソースコードに含まれるステートメントを探索し，見つけたステートメントを保持する
   *
   * @param candidates 再利用するソースコードのリスト
   */
  @Override
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    final List<ReuseCandidate<Statement>> reuseCandidates = candidates.stream()
        .flatMap(e -> {
          final FullyQualifiedName fqn = e.getPrimaryClassName();
          final CompilationUnit unit = ((GeneratedJDTAST<ProductSourcePath>) e).getRoot();
          final List<Statement> statements = new StatementVisitor(unit).getStatements();
          return statements.stream()
              .map(s -> new ReuseCandidate<>(s, fqn.getPackageName(), fqn));
        })
        .collect(Collectors.toList());

    putMaps(reuseCandidates);

    projectRoulette = createRoulette(reuseCandidates);
  }

  /**
   * 各ステートメントの重みを計算するメソッド
   *
   * @param reuseCandidate 重みを計算したいステートメント
   * @return 重み
   */
  public double getStatementWeight(final ReuseCandidate<Statement> reuseCandidate) {
    final Statement statement = reuseCandidate.getValue();
    final FullyQualifiedName fqn = reuseCandidate.getFqn();
    final StatementVisitor statementVisitor = new StatementVisitor(statement);
    final List<ReuseCandidate<Statement>> statements = statementVisitor.getStatements()
        .stream()
        .map(e -> new ReuseCandidate<>(e, fqn.getPackageName(), fqn))
        .collect(Collectors.toList());
    final int size = statements.size();
    return 1 / ((double) size);
  }

  /**
   * 再利用するステートメントを重みに基づいて選択し，返すメソッド
   *
   * @param query クエリ
   * @return 再利用するステートメント
   */
  @Override
  public Statement exec(final Query query) {
    final Scope scope = query.getScope();
    final Roulette<ReuseCandidate<Statement>> roulette = getRoulette(scope);
    final ReuseCandidate<Statement> candidate = roulette.exec();
    return candidate.getValue();
  }

  private void putMaps(final List<ReuseCandidate<Statement>> reuseCandidates) {
    for (final ReuseCandidate<Statement> reuseCandidate : reuseCandidates) {
      packageNameStatementMultimap.put(reuseCandidate.getPackageName(), reuseCandidate);
      fqnStatementMultiMap.put(reuseCandidate.getFqn(), reuseCandidate);
    }
  }

  private Roulette<ReuseCandidate<Statement>> getRouletteInProjectScope() {
    return projectRoulette;
  }

  private Roulette<ReuseCandidate<Statement>> getRouletteInPackage(final String packageName) {
    return getRoulette(packageName, packageNameRouletteMap, packageNameStatementMultimap);
  }

  private Roulette<ReuseCandidate<Statement>> getRouletteInFile(final FullyQualifiedName fqn) {
    return getRoulette(fqn, fqnRouletteMap, fqnStatementMultiMap);
  }

  private <T> Roulette<ReuseCandidate<Statement>> getRoulette(final T key,
      final Map<T, Roulette<ReuseCandidate<Statement>>> rouletteMap,
      final Multimap<T, ReuseCandidate<Statement>> candidateMap) {
    Roulette<ReuseCandidate<Statement>> roulette = rouletteMap.get(key);
    if (roulette != null) {
      return roulette;
    }
    final Collection<ReuseCandidate<Statement>> candidates = candidateMap.get(key);
    roulette = createRoulette(new ArrayList<>(candidates));
    rouletteMap.put(key, roulette);
    return roulette;
  }

  Roulette<ReuseCandidate<Statement>> getRoulette(final Scope scope) {
    final FullyQualifiedName fqn = scope.getFqn();
    switch (scope.getType()) {
      case PROJECT:
        return getRouletteInProjectScope();
      case PACKAGE:
        return getRouletteInPackage(fqn.getPackageName());
      case FILE:
        return getRouletteInFile(fqn);
    }
    throw new IllegalArgumentException("This scope is not implemented.");
  }

  private Roulette<ReuseCandidate<Statement>> createRoulette(
      final List<ReuseCandidate<Statement>> candidates) {
    final Function<ReuseCandidate<Statement>, Double> weightFunction = this::getStatementWeight;
    return new Roulette<>(candidates, weightFunction, random);
  }
}
