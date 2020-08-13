package application;

import java.time.LocalDate;

/**
 * @author Yiyang Gu ygu75@wisc.edu
 */
public class Util {

  /**
   * parse the date string in "yyyy-MM-dd" format
   *
   * @param str given date
   * @return a local date
   * @throws IllegalArgumentException if given date is in incorrect format.
   */
  public static LocalDate parseAsDate(String str) {
    try {
      String[] split = str.split("-");
      int year = Integer.parseInt(split[0]);
      int month = Integer.parseInt(split[1]);
      int day = Integer.parseInt(split[2]);
      return LocalDate.of(year, month, day);
    } catch (Exception e) {
      throw new IllegalArgumentException("Illegal date value:" + str);
    }
  }

  /**
   * parse give number string
   *
   * @param str an int str
   * @return an int value
   * @throws IllegalArgumentException if given str is not an int value.
   */
  public static int parseAsInt(String str) {
    try {
      return Integer.parseInt(str);
    } catch (Exception e) {
      throw new IllegalArgumentException("Illegal int value:" + str);
    }
  }

  /**
   * parse the farm information from the given content
   *
   * @param content given content, a str split by ',' and the value order is "Date,FarmID,Weight".
   * @return an farm object
   * @throws IllegalArgumentException if given str is not in expected format and order.
   */
  public static Farm parseFarm(String content) {
    try {
      String[] split = content.split(",");
      if (3 != split.length) {
        throw new IllegalArgumentException(
            "Missing farm information:" + content);
      }
      LocalDate date = parseAsDate(split[0]);
      String farmId = split[1];
      int weight = parseAsInt(split[2]);
      return new Farm(farmId, date, weight);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * format the double number into str with given decimal
   *
   * @param v given double value
   * @param decimal the number of decimals
   * @return a double str
   */
  public static String formatDouble(double v, int decimal) {
    String format = "%,." + decimal + "f";
    return String.format(format, v);
  }

}
