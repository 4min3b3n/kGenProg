package jp.kusumotolab.kgenprog.ga.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.fl.Suspiciousness;
import jp.kusumotolab.kgenprog.ga.Context.MutationContext;
import jp.kusumotolab.kgenprog.ga.Roulette;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.validation.Fitness;
import jp.kusumotolab.kgenprog.ga.variant.Base;
import jp.kusumotolab.kgenprog.ga.variant.Gene;
import jp.kusumotolab.kgenprog.ga.variant.HistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.MutationHistoricalElement;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;
import jp.kusumotolab.kgenprog.project.ASTLocation;
import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.Operation;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;

/**
 * ソースコードの変異を行うクラス
 */
public abstract class Mutation {

  protected final Random random;
  protected final int mutationGeneratingCount;
  protected final CandidateSelection candidateSelection;
  private final boolean needHistoricalElement;

  /**
   * コンストラクタ(Reflection用)
   *
   * @param mutationContext Mutationを生成するまでの過程で生成されたオブジェクトの情報
   */
  public Mutation(final MutationContext mutationContext) {
    this(mutationContext.getConfig(), mutationContext);
  }

  /**
   * コンストラクタ(Reflection用)
   *
   * @param config 設定に関するオブジェクト
   * @param mutationContext Mutationを生成するまでの過程で生成されたオブジェクトの情報
   */
  public Mutation(final Configuration config, final MutationContext mutationContext) {
    this(config.getMutationGeneratingCount(), mutationContext.getRandom(),
        mutationContext.getCandidateSelection(), config.getNeedHistoricalElement());
  }

  /**
   * コンストラクタ
   *
   * @param mutationGeneratingCount 各世代で生成する個体数
   * @param random 乱数生成器
   * @param candidateSelection 再利用する候補を選択するオブジェクト
   * @param needHistoricalElement 個体が生成される過程を記録するか否か
   */
  public Mutation(final int mutationGeneratingCount, final Random random,
      final CandidateSelection candidateSelection, final boolean needHistoricalElement) {
    this.random = random;
    this.mutationGeneratingCount = mutationGeneratingCount;
    this.candidateSelection = candidateSelection;
    this.needHistoricalElement = needHistoricalElement;
  }

  /**
   * @param candidates 再利用するソースコード群
   */
  public void setCandidates(final List<GeneratedAST<ProductSourcePath>> candidates) {
    candidateSelection.setCandidates(candidates);
  }

  /**
   * 変異処理された Variant を mutationGeneratingCount 分だけ返す
   *
   * @param variantStore Variant の情報を格納するオブジェクト
   * @return 変異された Gene を持った Variant のリスト
   */
  public List<Variant> exec(final VariantStore variantStore) {

    final List<Variant> generatedVariants = new ArrayList<>();

    final List<Variant> currentVariants = variantStore.getCurrentVariants();

    final Roulette<Variant> variantRoulette = new Roulette<>(currentVariants, e -> {
      final Fitness fitness = e.getFitness();
      final double value = fitness.getValue();
      return Double.isNaN(value) ? 0 : value;
    }, random);

    for (int i = 0; i < mutationGeneratingCount; i++) {
      final Variant variant = variantRoulette.exec();
      final List<Suspiciousness> suspiciousnesses = variant.getSuspiciousnesses();
      final Function<Suspiciousness, Double> weightFunction = susp -> Math.pow(susp.getValue(), 2);

      if (suspiciousnesses.isEmpty()) {
        continue;
      }
      final Roulette<Suspiciousness> roulette =
          new Roulette<>(suspiciousnesses, weightFunction, random);

      final Suspiciousness suspiciousness = roulette.exec();
      final Base base = makeBase(suspiciousness);
      final Gene gene = makeGene(variant.getGene(), base);
      final HistoricalElement element;
      if (needHistoricalElement) {
        element = new MutationHistoricalElement(variant, base);
      } else {
        element = null;
      }
      generatedVariants.add(variantStore.createVariant(gene, element));
    }

    return generatedVariants;
  }

  protected Base makeBase(final Suspiciousness suspiciousness) {
    final ASTLocation location = suspiciousness.getLocation();
    return new Base(location, makeOperation(location));
  }

  protected abstract Operation makeOperation(final ASTLocation location);

  protected Gene makeGene(final Gene parent, final Base base) {
    final List<Base> bases = new ArrayList<>(parent.getBases());
    bases.add(base);
    return new Gene(bases);
  }

}
