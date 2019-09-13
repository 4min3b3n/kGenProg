package jp.kusumotolab.kgenprog;

public class KgpAnnotatedClass {

  private final Kgp annotation;
  private final Class<?> aClass;

  public KgpAnnotatedClass(final Kgp annotation, final Class<?> aClass) {
    this.annotation = annotation;
    this.aClass = aClass;
  }

  public Kgp getAnnotation() {
    return annotation;
  }

  public Class<?> getAnnotatedClass() {
    return aClass;
  }
}
