package jp.kusumotolab.kgenprog.ga;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import jp.kusumotolab.kgenprog.Annotations;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.Kgp;
import jp.kusumotolab.kgenprog.KgpAnnotatedClass;
import jp.kusumotolab.kgenprog.StrategyType;

public class Contexts {

  @SuppressWarnings("unchecked")
  public static <T> T resolve(final Context context) {
    final Annotations annotations = context.getAnnotations();
    final StrategyType strategyType = context.getStrategyType();
    final Configuration config = context.getConfig();

    final List<KgpAnnotatedClass> annotatedClasses = annotations.getAnnotations(strategyType);
    final KgpAnnotatedClass annotatedClass = extractAnnotatedClass(annotatedClasses,
        strategyType.extractNameFromConfiguration(config));

    final Kgp annotation = annotatedClass.getAnnotation();
    final Class<?> aClass = annotatedClass.getAnnotatedClass();

    try {
      final T object = (T) aClass.getDeclaredConstructor(context.getClass())
          .newInstance(context);
      return object;
    } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to construct " + annotation.name() + ".");
    }
  }

  private static KgpAnnotatedClass extractAnnotatedClass(
      final List<KgpAnnotatedClass> annotatedClasses, final String name) {
    final Optional<KgpAnnotatedClass> optionalKgpAnnotatedClass = annotatedClasses.stream()
        .filter(e -> equalName(e, name))
        .findFirst();

    if (!optionalKgpAnnotatedClass.isPresent()) {
      throw new RuntimeException(name + " doesn't exist in Class Loader.");
    }

    return optionalKgpAnnotatedClass.get();
  }

  private static boolean equalName(final KgpAnnotatedClass annotatedClass, final String name) {
    return annotatedClass.getAnnotation()
        .name()
        .equals(name);
  }
}
