package jp.kusumotolab.kgenprog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * kGenProgの各ストラテジーに付与するアノテーション
 * このアノテーションが付与されたクラスをkGenProgはクラスローダーから読み込み，Configの値によって切り替える
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Kgp {

  /**
   * @return どのストラテジーについての実装か
   */
  StrategyType type();

  /**
   * @return このアノテーションが付与されたクラスの識別子．
   * 実行時の引数などで指定する．
   */
  String name();

  /**
   * (Option)
   * @return このアノテーションが付与されたクラスについての説明
   */
  String description() default "No Description";
}