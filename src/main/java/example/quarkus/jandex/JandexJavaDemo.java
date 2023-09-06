package example.quarkus.jandex;

import java.util.List;
import java.util.Map;

public class JandexJavaDemo {
    Map<Integer, List<@Label("Name") String>> names;
}
