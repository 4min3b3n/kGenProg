package jp.kusumotolab.kgenprog.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.jdt.InsertBlockOperation;

public class PatchGenerator {

  private static final Logger log = LoggerFactory.getLogger(PatchGenerator.class);
  private final boolean normalizeSourceCode;

  public PatchGenerator(final boolean normalizeSourceCode) {
    this.normalizeSourceCode = normalizeSourceCode;
  }

  public Patch exec(final Variant modifiedVariant) {

    final Patch patch = new Patch();
    final GeneratedSourceCode modifiedSourceCode;
    if (normalizeSourceCode) {
      modifiedSourceCode = normalizeGeneratedSourceCode(modifiedVariant.getGene(),
          modifiedVariant.getGeneratedSourceCode());
    } else {
      modifiedSourceCode = modifiedVariant.getGeneratedSourceCode();
    }

    final List<GeneratedAST<ProductSourcePath>> modifiedAsts = modifiedSourceCode.getProductAsts();

    for (final GeneratedAST<ProductSourcePath> ast : modifiedAsts) {
      try {
        final FileDiff fileDiff = makeFileDiff(ast);
        final String diff = fileDiff.getDiff();
        if (diff.isEmpty()) {
          continue;
        }
        patch.add(fileDiff);
      } catch (final IOException | DiffException e) {
        log.error(e.getMessage());
        return new Patch();
      }
    }
    return patch;
  }

  // テストのためにpackage privateとする
  GeneratedSourceCode normalizeGeneratedSourceCode(final Gene gene,
      final GeneratedSourceCode origin) {
    // 変更されたファイルのみ、正規化を行う
    final List<ASTLocation> locations = gene.getBases()
        .stream()
        .map(Base::getTargetLocation)
        .collect(
            Collectors.groupingBy(ASTLocation::getSourcePath, Collectors.reducing((a, b) -> a)))
        .values()
        .stream()
        .map(Optional::get)
        .collect(Collectors.toList());

    GeneratedSourceCode current = origin;
    final InsertBlockOperation operation = new InsertBlockOperation();

    for (final ASTLocation location : locations) {
      current = operation.apply(current, location);
    }

    return current;
  }

  /***
   * FileDiff オブジェクトの生成を行う
   *
   * @param ast
   * @return
   * @throws IOException
   * @throws DiffException
   */
  private FileDiff makeFileDiff(final GeneratedAST<?> ast) throws IOException, DiffException {
    final Path originPath = ast.getSourcePath()
        .getResolvedPath();

    final String modifiedSourceCodeText = format(ast.getSourceCode());
    final Document document = new Document(modifiedSourceCodeText);

    final String fileName = ast.getPrimaryClassName().value;
    final String delimiter = document.getDefaultLineDelimiter();
    final List<String> modifiedSourceCodeLines =
        Arrays.asList(modifiedSourceCodeText.split(delimiter));

    final String xx =
        String.join(document.getDefaultLineDelimiter(), Files.readAllLines(originPath));
    final String x = format(xx);
    final List<String> originalSourceCodeLines = Arrays.asList(x.split(delimiter));
    final List<String> noBlankLineOriginalSourceCodeLines =
        removeEndDelimiter(originalSourceCodeLines);


    final List<String> diffLines =
        makeDiff(fileName, noBlankLineOriginalSourceCodeLines, modifiedSourceCodeLines);

    return new FileDiff(diffLines, fileName, originalSourceCodeLines, modifiedSourceCodeLines);
  }

  /***
   * UnifiedDiff 形式の diff を返す．
   *
   * @param fileName
   * @param originalSourceCodeLines
   * @param modifiedSourceCodeLines
   * @return
   */
  private List<String> makeDiff(final String fileName, final List<String> originalSourceCodeLines,
      final List<String> modifiedSourceCodeLines) throws DiffException {
    final com.github.difflib.patch.Patch<String> diff =
        DiffUtils.diff(originalSourceCodeLines, modifiedSourceCodeLines);
    return UnifiedDiffUtils.generateUnifiedDiff(fileName, fileName, originalSourceCodeLines, diff,
        3);
  }

  private List<String> removeEndDelimiter(final List<String> sourceCodeLines) {
    for (int index = sourceCodeLines.size() - 1; index >= 0; index--) {
      final String sourceCodeLine = sourceCodeLines.get(index);
      if (!sourceCodeLine.equals("")) {
        return sourceCodeLines.subList(0, index + 1);
      }
    }

    return Collections.emptyList();
  }


  private static String format(String source) {
    Map<String, String> options = DefaultCodeFormatterOptions.getEclipseDefaultSettings()
        .getMap();
    // initialize the compiler settings to be able to format 1.8 code
    options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
    options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
    options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
    options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "2");

    // instantiate the default code formatter with the given options
    final CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

    // retrieve the source to format
    String separator = System.getProperty("line.separator");
    final TextEdit edit;
    try {
      edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, // format a compilation unit
          source, // source to format
          0, // starting position
          source.length(), // length
          0, // initial indentation
          separator // line separator
      );
    } catch (RuntimeException e) {
      log.error("--------");
      log.error("formatting error", e);
      log.error(source);
      throw e;
    }

    IDocument document = new Document(source);
    try {
      edit.apply(document);
    } catch (MalformedTreeException | BadLocationException e) {
      e.printStackTrace();
    }

    return document.get();
  }
}
