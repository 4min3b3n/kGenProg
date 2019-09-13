package jp.kusumotolab.kgenprog;

import java.util.List;
import org.reflections.Reflections;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class Annotations {

  private final Multimap<StrategyType, KgpAnnotatedClass> annotatedClassMap = HashMultimap.create();

  public void initialize() {
    annotatedClassMap.clear();

    new Reflections("").getTypesAnnotatedWith(Kgp.class)
        .stream()
        .map(e -> new KgpAnnotatedClass(e.getAnnotation(Kgp.class), e))
        .forEach(e -> annotatedClassMap.put(e.getAnnotation().type(), e));
  }

  public List<KgpAnnotatedClass> getAnnotations(final StrategyType strategyType) {
    return Lists.newArrayList(annotatedClassMap.get(strategyType));
  }
}
