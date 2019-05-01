package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 1バリアントの変更内容．
 * @author k-naitou
 *
 */
public class Patch {

  private static Logger log = LoggerFactory.getLogger(Patch.class);

  private final List<FileDiff> diffs = new ArrayList<>();

  /**
   * 差分を追加する
   *
   * @param diff 追加する差分
   */
  public void add(final FileDiff diff) {
    this.diffs.add(diff);
  }

  /**
   * 指定した位置にある差分を取得する
   *
   * @param index 取得したい差分のリスト上の位置
   */
  public FileDiff get(final int index) {
    return diffs.get(index);
  }

  /**
   * 保持している全ての差分を取得する
   */
  List<FileDiff> getAll() {
    return diffs;
  }

  /**
   * 保持している全ての差分をファイルに出力する
   *
   * @param outDir 出力先のディレクトリ
   */
  public void writeToFile(final Path outDir) {
    try {
      if (Files.notExists(outDir)) {
        Files.createDirectories(outDir);
      }
    } catch (final IOException e) {
      log.error(e.getMessage());
    }

    for (final FileDiff fileDiff : diffs) {
      fileDiff.write(outDir);
    }
  }

  /**
   * 保持している全ての差分をログに出力する
   */
  public void writeToLogger() {
    for (final FileDiff fileDiff : diffs) {
      log.info(System.lineSeparator() + fileDiff.getDiff());
    }
  }
}
