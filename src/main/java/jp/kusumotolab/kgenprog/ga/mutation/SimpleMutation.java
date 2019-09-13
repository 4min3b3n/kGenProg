package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.Random;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.Kgp;
import jp.kusumotolab.kgenprog.StrategyType;
import jp.kusumotolab.kgenprog.ga.Context.MutationContext;
import jp.kusumotolab.kgenprog.ga.mutation.Scope.Type;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;
import jp.kusumotolab.kgenprog.project.NoneOperation;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.jdt.DeleteOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertAfterOperation;
import jp.kusumotolab.kgenprog.project.jdt.InsertBeforeOperation;
import jp.kusumotolab.kgenprog.project.jdt.ReplaceOperation;

/**
 * 乱数に基づいて変異処理をするクラス
 *
 * @see Mutation
 */
@Kgp(type = StrategyType.Mutation, name = "Simple", description = "The mutation is completely based on random numbers.")
public class SimpleMutation extends Mutation {

  protected final Type type;

  /**
   * コンストラクタ(Reflection用)
   *
   * @param mutationContext Mutationを生成するまでの過程で生成されたオブジェクトの情報
   */
  public SimpleMutation(final MutationContext mutationContext) {
    super(mutationContext);
    this.type = mutationContext.getConfig()
        .getScope();
  }

  /**
   * コンストラクタ (テスト用)
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param type 選択する候補のスコープ
   * @param needHistoricalElement 個体が生成される過程を記録するか否か
   */
  public SimpleMutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final Type type,
      final boolean needHistoricalElement) {
    super(mutationGeneratingCount, random, candidateSelection, needHistoricalElement);
    this.type = type;
  }

  protected Operation makeOperation(final ASTLocation location) {
    final int randomNumber = random.nextInt(3);
    switch (randomNumber) {
      case 0:
        return new DeleteOperation();
      case 1:
        return random.nextBoolean() ?
            new InsertAfterOperation(chooseNodeForReuse(location, InsertAfterOperation.class))
            : new InsertBeforeOperation(chooseNodeForReuse(location, InsertBeforeOperation.class));
      case 2:
        final ASTNode node = chooseNodeForReuse(location, ReplaceOperation.class);
        return new ReplaceOperation(node);
    }
    return new NoneOperation();
  }

  protected ASTNode chooseNodeForReuse(final ASTLocation location,
      final Class<? extends Operation> operationClass) {
    final FullyQualifiedName fqn = location.getGeneratedAST()
        .getPrimaryClassName();
    final Scope scope = new Scope(type, fqn);
    final Query query = new Query(scope);
    return candidateSelection.exec(query);
  }
}
