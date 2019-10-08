package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.Comparator;
import java.util.List;
import jp.kusumotolab.kgenprog.Kgp;
import jp.kusumotolab.kgenprog.StrategyType;
import jp.kusumotolab.kgenprog.ga.Context.SecondVariantSelectionStrategyContext;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親を評価関数に基づいて選択するアルゴリズムを実装したクラス．
 * 
 * @author higo
 *
 */
@Kgp(type = StrategyType.SecondVariantSelectionStrategy, name = "Elite")
public class SecondVariantEliteSelection implements SecondVariantSelectionStrategy {

  /**
   * コンストラクタ
   * Reflectionで呼び出されるので引数を変えないこと
   * @param context こののコンストラクタが呼ばれる過程で生成されたオブジェクト
   */
  public SecondVariantEliteSelection(final SecondVariantSelectionStrategyContext context) {
    this();
  }

  /**
   * コンストラクタ
   */
  public SecondVariantEliteSelection() {
  }

  /**
   * 選択を行うメソッド．<br>
   * 選択対象の個体群および1つ目の親として選択された個体をを引数として与える必要あり．<br>
   *
   * @see jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy#exec(List, Variant)
   * 
   * @param variants 選択対象の個体群
   * @param firstVariant 1つ目の親として選択された個体
   * @return 選択された個体
   */
  @Override
  public Variant exec(final List<Variant> variants, final Variant firstVariant)
      throws CrossoverInfeasibleException {
    return variants.stream()
        .sorted(Comparator.comparing(Variant::getFitness)
            .reversed())
        .filter(v -> v != firstVariant) // TODO 本来は Variantにequalsメソッドを定義すべき？
        .findFirst()
        .orElseThrow(() -> new CrossoverInfeasibleException("no variant for second parent"));
  }
}
