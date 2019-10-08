package jp.kusumotolab.kgenprog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureClassLoader;
import java.util.List;
import org.reflections.Reflections;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class Annotations {

  private final Multimap<StrategyType, KgpAnnotatedClass> annotatedClassMap = HashMultimap.create();

  public void initialize(final Configuration configuration) throws IOException {
    annotatedClassMap.clear();

    importPluginToClassLoader(configuration.getPluginDir());

    new Reflections("").getTypesAnnotatedWith(Kgp.class)
        .stream()
        .map(e -> new KgpAnnotatedClass(e.getAnnotation(Kgp.class), e))
        .forEach(e -> annotatedClassMap.put(e.getAnnotation()
            .type(), e));
  }

  private void importPluginToClassLoader(final String pluginDir) throws IOException {
    final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    Files.walk(Paths.get(pluginDir).toAbsolutePath(), Integer.MAX_VALUE)
        .filter(path -> {
          final String pathString = path.getFileName()
              .toString();
          return pathString.endsWith(".class") || pathString.endsWith(".jar");
        })
        .forEach(path -> {
          try {
            final URI uri = path.toUri();
            final URL url = uri.toURL();
            if (classLoader instanceof URLClassLoader) {
              final URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
              final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
              method.setAccessible(true);
              method.invoke(urlClassLoader, url);
            } else {
              //appendToClassPathForInstrumentation
              final Class<?> aClass = classLoader.getClass();
              final Method method = aClass.getDeclaredMethod(
                  "appendToClassPathForInstrumentation", String.class);
//              method.setAccessible(true);
//              method.invoke(classLoader, path.toString());
            }
          } catch (final MalformedURLException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
          }
        });
  }


  public List<KgpAnnotatedClass> getAnnotations(final StrategyType strategyType) {
    return Lists.newArrayList(annotatedClassMap.get(strategyType));
  }
}
