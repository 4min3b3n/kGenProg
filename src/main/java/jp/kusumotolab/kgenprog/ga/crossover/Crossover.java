package jp.kusumotolab.kgenprog.ga.crossover;

import java.util.List;
import java.util.Random;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

/**
 * 交叉を表すインターフェース．<br>
 * 交叉アルゴリズムを実装するクラスはこのインターフェースを実装しなければならない．<br>
 *
 * @author higo
 */
public interface Crossover {

  /**
   * 交叉処理を行うメソッド．<br>
   * 交叉対象の個体群を含んだVariantStoreを引数として与える必要あり．<br>
   *
   * @param variantStore 交叉対象の個体群
   * @return 交叉により生成された個体群
   */
  List<Variant> exec(VariantStore variantStore);

  /**
   * 1つ目の親を返す．
   *
   * @return 1つ目の親
   */
  FirstVariantSelectionStrategy getFirstVariantSelectionStrategy();

  /**
   * 2つ目の親を返す．
   *
   * @return 2つ目の親
   */
  SecondVariantSelectionStrategy getSecondVariantSelectionStrategy();
}
