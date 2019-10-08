package jp.kusumotolab.kgenprog.ga.mutation.selection;

import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.ga.mutation.Query;

/**
 * ステートメント単位で再利用候補を選択するクラス
 *
 * @see CandidateSelection
 */
public interface StatementSelection extends CandidateSelection {

  /**
   * 再利用するステートメントを取り出す
   *
   * @param query 再利用する候補のクエリ
   * @return 再利用するステートメント
   */
  Statement exec(final Query query);
}
