package jp.kusumotolab.kgenprog.ga.mutation.selection;

import org.eclipse.jdt.core.dom.Statement;
import jp.kusumotolab.kgenprog.ga.Context.CandidateSelectionContext;
import jp.kusumotolab.kgenprog.ga.mutation.Query;

/**
 * ステートメント単位で再利用候補を選択するクラス
 *
 * @see CandidateSelection
 */
public abstract class StatementSelection extends CandidateSelection {

  /**
   * コンストラクタ
   * @param context CandidateSelectionを生成するまでの過程で生成されたオブジェクトの情報
   */
  public StatementSelection(final CandidateSelectionContext context) {
    super(context);
  }

  /**
   * 再利用するステートメントを取り出す
   *
   * @param query 再利用する候補のクエリ
   * @return 再利用するステートメント
   */
  public abstract Statement exec(final Query query);
}
