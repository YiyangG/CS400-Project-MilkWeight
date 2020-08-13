package application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yiyang Gu ygu75@wisc.edu
 */
public enum FiledType {
  ID, DATE, WEIGHT;


  public static List<String> getNames() {
    return Arrays.stream(FiledType.values()).map(Enum::name)
        .collect(Collectors.toList());
  }
}
