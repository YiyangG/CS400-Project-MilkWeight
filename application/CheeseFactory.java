package application;

/**
 * Defines a factory that gets its supplies from multiple farms each year
 */

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yiyang Gu ygu75@wisc.edu
 */
public class CheeseFactory {

  private static Map<String, Farm> milkData = new HashMap<>();


  /**
   * add a new farm into the data map, add that only if there is no farm for the same farmId and date
   *
   * @param farm given farm
   * @return true when added successfully, otherwise false
   */
  public static boolean insertFarm(Farm farm) {
    String key = generateUniqueId(farm);
    Farm existFarm = milkData.get(key);
    if (farm.equals(existFarm)) {
      return false;
    }
    milkData.put(key, farm);
    return true;
  }

  /**
   * replace the old farm's info with new info, edit that only if there is one farm for the same
   * farmId and date
   *
   * @param newFarm new farm info
   * @return true when edit successfully, otherwise false
   */
  public static boolean editFarm(Farm newFarm) {
    String key = generateUniqueId(newFarm);
    Farm existFarm = milkData.get(key);
    if (null == existFarm) {
      return false;
    }
    existFarm.setWeight(newFarm.getWeight());
    return true;
  }

  /**
   * remove the farm with same farmID and date from the data map
   *
   * @param farm given farm
   * @return removed farm if exists or null for non-exists.
   */
  public static Farm removeFarm(Farm farm) {
    return milkData.remove(generateUniqueId(farm));
  }

  /**
   * @return all farms list
   */
  public static List<Farm> getFarms() {
    return new ArrayList<>(milkData.values());
  }

  /**
   * in the dataset, a farm is identified only by its ID and Date.
   *
   * @param farm given farm
   * @return a string stands fot the farm's unique id
   */
  private static String generateUniqueId(Farm farm) {
    return farm.getId() + "#"
        + farm.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

}
