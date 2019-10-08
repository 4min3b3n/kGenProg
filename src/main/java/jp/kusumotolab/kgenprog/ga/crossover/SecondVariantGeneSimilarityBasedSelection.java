package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Random;
import jp.kusumotolab.kgenprog.Kgp;
import jp.kusumotolab.kgenprog.StrategyType;
import jp.kusumotolab.kgenprog.ga.Context.SecondVariantSelectionStrategyContext;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親を1つ目の親との遺伝子の違いに基づいて選択するアルゴリズムを実装したクラス．
 *
 * @author higo
 *
 */
@Kgp(type = StrategyType.SecondVariantSelectionStrategy, name = "GeneSimilarity")
public class SecondVariantGeneSimilarityBasedSelection
    extends SecondVariantSimilarityBasedSelection {

  /**
   * コンストラクタ
   * Reflectionで呼び出されるので引数を変えないこと
   * @param context こののコンストラクタが呼ばれる過程で生成されたオブジェクト
   */
  public SecondVariantGeneSimilarityBasedSelection(final SecondVariantSelectionStrategyContext context) {
    this(context.getRandom());
  }

  /**
   * コンストラクタ．選択においてランダム処理を行うためのシードを引数として渡す必要あり．
   *
   * @param random ランダム処理を行うためのシード
   */
  public SecondVariantGeneSimilarityBasedSelection(final Random random) {
    super(random);
  }

  @Override
  protected double calculateSimilarity(final Variant variant1, final Variant variant2) {
    return SimilarityCalculator.exec(variant1, variant2, v -> v.getGene()
        .getBases());
  }
}
