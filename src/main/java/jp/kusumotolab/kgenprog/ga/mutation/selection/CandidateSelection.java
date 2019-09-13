package jp.kusumotolab.kgenprog.ga.mutation.selection;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import jp.kusumotolab.kgenprog.ga.Context.CandidateSelectionContext;
import jp.kusumotolab.kgenprog.ga.mutation.Query;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * 再利用候補の選択をするクラス
 */
public abstract class CandidateSelection {

  public CandidateSelection(final CandidateSelectionContext context) {
  }

  /**
   * 再利用するソースコードをセットする
   *
   * @param candidates 再利用するソースコードのリスト
   */
  public abstract void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates);

  /**
   * 再利用する AST ノードを取り出す
   *
   * @param query 再利用する候補のクエリ
   * @return 再利用する AST ノード
   */
  public abstract ASTNode exec(final Query query);
}
