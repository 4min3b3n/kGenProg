package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;

/**
 * 交叉において，2つ目の親の選択アルゴリズムを表すインターフェース．<br>
 * 1つ目の親として選択された個体とは異なる個体を選択する必要がある．<br>
 * 
 * @author higo
 *
 */
public interface SecondVariantSelectionStrategy {

  /**
   * 選択を行うメソッド．<br>
   * 選択対象の個体群および1つ目の親として選択された個体をを引数として与える必要あり．<br>
   * 
   * @param variants 選択対象の個体群
   * @param firstVariant 1つ目の親として選択された個体
   * @return 選択された個体
   */
  Variant exec(List<Variant> variants, Variant firstVariant) throws CrossoverInfeasibleException;
}
