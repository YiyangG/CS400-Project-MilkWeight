package application;

/**
 * Defines operations on data manipulations and forming the required data for the visualizer
 */

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yiyang Gu ygu75@wisc.edu
 */
public class DataManager {


  public DataManager() {}


  /**
   * calculate the average milk weight of the given farm for each month, the result is sorted by month
   * number 1-12
   *
   * @param farmId given farm
   * @param year given year
   * @return a sorted map which key is the month and value is the average weight
   */
  public Map<Integer, Double> getMonthlyAverageForFarm(String farmId,
      int year) {
    List<Farm> farms = CheeseFactory.getFarms();
    farms = farms.stream()
        .filter(farm -> farm.getId().equals(farmId) && farm.getYear() == year)
        .collect(Collectors.toList());
    Map<Integer, Double> monthAvgWeightMap =
        farms.stream().collect(Collectors.groupingBy(Farm::getMonth,
            Collectors.averagingDouble(Farm::getWeight)));
    return new TreeMap<>(monthAvgWeightMap);
  }

  /**
   * calculate the min milk weight of the given farm among all farms for each month, the result is
   * sorted by month number 1-12
   *
   * @param farmId given farm
   * @param year given year
   * @return a sorted map which key is the month and value is the min weight
   */
  public Map<Integer, Optional<Farm>> getMonthlyMinForFarm(String farmId,
      int year) {
    List<Farm> farms = CheeseFactory.getFarms();
    farms = farms.stream()
        .filter(farm -> farm.getId().equals(farmId) && farm.getYear() == year)
        .collect(Collectors.toList());
    return new TreeMap<>(
        farms.stream().collect(Collectors.groupingBy(Farm::getMonth,
            Collectors.minBy(Comparator.comparing(Farm::getWeight)))));
  }

  /**
   * calculate the max milk weight of the given farm among all farms for each month, the result is
   * sorted by month number 1-12
   *
   * @param farmId given farm
   * @param year given year
   * @return a sorted map which key is the month and value is the max weight
   */
  public Map<Integer, Optional<Farm>> getMonthlyMaxForFarm(String farmId,
      int year) {
    List<Farm> farms = CheeseFactory.getFarms();
    farms = farms.stream()
        .filter(farm -> farm.getId().equals(farmId) && farm.getYear() == year)
        .collect(Collectors.toList());
    return new TreeMap<>(
        farms.stream().collect(Collectors.groupingBy(Farm::getMonth,
            Collectors.maxBy(Comparator.comparing(Farm::getWeight)))));
  }

  /**
   * @param field given sorted field of the farm
   * @param asc indicate ascending or descending
   * @return all farms' information sorted by the given field in given order
   */
  public List<Farm> getDataSortedByField(FiledType field, boolean asc) {
    List<Farm> farms = CheeseFactory.getFarms();
    switch (field) {
      case ID:
        if (asc) {
          farms.sort(Comparator.comparing(Farm::getId));
        } else {
          farms.sort(Comparator.comparing(Farm::getId).reversed());
        }
        break;
      case DATE:
        if (asc) {
          farms.sort(Comparator.comparing(Farm::getDate));
        } else {
          farms.sort(Comparator.comparing(Farm::getDate).reversed());
        }
        break;
      case WEIGHT:
        if (asc) {
          farms.sort(Comparator.comparing(Farm::getWeight));
        } else {
          farms.sort(Comparator.comparing(Farm::getWeight).reversed());
        }
        break;
      default:
        break;
    }
    return farms;
  }

  /**
   * Generate the farm report for the given farm and year. The report consists of the total milk
   * weight, percent of the total of all farm, min milk weight, max milk weight and average weight.
   * They are all for each month.
   *
   * @param farmId given farm
   * @param year given year
   * @return report of the given farm for each month sorted by month number 1-12
   */
  public List<FarmReport> getFarmReport(String farmId, int year) {
    // get all correspondent information in the given year
    List<Farm> farms = CheeseFactory.getFarms();
    farms = farms.stream().filter(farm -> farm.getYear() == year)
        .collect(Collectors.toList());
    // calculate total weight for each month for all farms
    Map<Integer, Double> monthTotalMapForAllFarms =
        farms.stream().collect(Collectors.groupingBy(Farm::getMonth,
            Collectors.summingDouble(Farm::getWeight)));

    // get all correspondent information of the farm in the given year
    farms = farms.stream().filter(farm -> farm.getId().equals(farmId))
        .collect(Collectors.toList());
    // calculate total weight for each month for the given farm
    Map<Integer, Integer> monthTotalMapForFarm =
        farms.stream().collect(Collectors.groupingBy(Farm::getMonth,
            Collectors.summingInt(Farm::getWeight)));

    // calculate the min,max and average weight for each month for the given farm
    Map<Integer, Optional<Farm>> monthMinMap =
        getMonthlyMinForFarm(farmId, year);
    Map<Integer, Optional<Farm>> monthMaxMap =
        getMonthlyMaxForFarm(farmId, year);
    Map<Integer, Double> monthlyAvgWeightMap =
        getMonthlyAverageForFarm(farmId, year);

    // generate the report using above information
    List<FarmReport> reports = new ArrayList<>();
    monthTotalMapForFarm.forEach((month, totalForFarm) -> {
      double total = monthTotalMapForAllFarms.get(month);
      double percent = totalForFarm / total * 100;
      int minWeight = monthMinMap.get(month).get().getWeight();
      int maxWeight = monthMaxMap.get(month).get().getWeight();
      double avgWeight = monthlyAvgWeightMap.get(month);
      reports.add(new FarmReport(month, totalForFarm, percent, minWeight,
          maxWeight, avgWeight));
    });
    reports.sort(Comparator.comparing(FarmReport::getMonth));
    return reports;
  }

  /**
   * Generate the annual report for the given year. The report consists of the total milk weight,
   * percent of the total of all farm, min milk weight, max milk weight and average weight of all
   * farms by farm for the year.
   *
   * @param year given year
   * @param sortedBy sorted field
   * @param asc a boolean value indicates ascending or descending
   * @return report of all farms by farm for the year sorted by given field and order.
   */
  public List<DateRangeReport> getAnnualReport(int year, FiledType sortedBy,
      boolean asc) {
    List<Farm> farms = CheeseFactory.getFarms();
    farms = farms.stream().filter(farm -> farm.getYear() == year)
        .collect(Collectors.toList());
    Map<String, Summary> summaryMap = getSummaryForFarm(farms);
    return getDateRangeReports(farms, summaryMap, sortedBy, asc);
  }

  /**
   * Generate the monthly report for the given year and month. The report consists of the total milk
   * weight, percent of the total of all farm, min milk weight, max milk weight and average weight of
   * all farms by farm for the year and month.
   *
   * @param year given year
   * @param month given month
   * @param sortedBy given sorted field
   * @param asc a boolean value indicates ascending or descending
   * @return report of all farms by farm for the year sorted by given field and order.
   */
  public List<DateRangeReport> getMonthlyReport(int year, int month,
      FiledType sortedBy, boolean asc) {
    List<Farm> farms = CheeseFactory.getFarms();
    farms = farms.stream()
        .filter(farm -> farm.getYear() == year && farm.getMonth() == month)
        .collect(Collectors.toList());
    Map<String, Summary> summaryMap = getSummaryForFarm(farms);
    return getDateRangeReports(farms, summaryMap, sortedBy, asc);
  }

  /**
   * Generate the report for the date range. The report consists of the total milk weight, percent of
   * the total of all farm, min milk weight, max milk weight and average weight of all farms by farm
   * over that date range.
   *
   * @param start the inclusive start date
   * @param end the inclusive end date
   * @param sortedBy given sorted field
   * @param asc a boolean value indicates ascending or descending
   * @return report of all farms by farm over that date range sorted by given field and order.
   */
  public List<DateRangeReport> getDateRangeReports(LocalDate start,
      LocalDate end, FiledType sortedBy, boolean asc) {
    List<Farm> farms = CheeseFactory.getFarms();
    farms =
        farms.stream()
            .filter(farm -> farm.getDate().compareTo(start) >= 0
                && farm.getDate().compareTo(end) <= 0)
            .collect(Collectors.toList());
    Map<String, Summary> summaryMap = getSummaryForFarm(farms);
    return getDateRangeReports(farms, summaryMap, sortedBy, asc);
  }

  /**
   * calculate the min milk weight, max milk weight and average weight and they are called summary of
   * all given farms by farm
   *
   * @param farms given farm list
   * @return a map which key is the Farm ID and the value is the summary of the farm.
   */
  private Map<String, Summary> getSummaryForFarm(List<Farm> farms) {
    return farms.stream().collect(Collectors.groupingBy(Farm::getId,
        Collectors.collectingAndThen(Collectors.toList(), l -> {
          int min = l.stream().mapToInt(Farm::getWeight).min().orElse(0);
          int max = l.stream().mapToInt(Farm::getWeight).max().orElse(0);
          double avg =
              l.stream().mapToDouble(Farm::getWeight).average().orElse(0D);
          return new Summary(min, max, avg);
        })));
  }

  /**
   * Generate the report for the date range(annually, monthly, date range). The report consists of the
   * total milk weight, percent of the total of all farm, min milk weight, max milk weight and average
   * weight of all farms by farm over that date range.
   *
   * @param farms given farms
   * @param summaryMap summary information for all farms
   * @param sortedBy given sorted field
   * @param asc a boolean value indicates ascending or descending
   * @return report of all farms by farm over that date range sorted by given field and order.
   */
  private List<DateRangeReport> getDateRangeReports(List<Farm> farms,
      Map<String, Summary> summaryMap, FiledType sortedBy, boolean asc) {
    double total = farms.stream().mapToDouble(Farm::getWeight).sum();
    Map<String, Integer> farmWeightMap = farms.stream().collect(Collectors
        .groupingBy(Farm::getId, Collectors.summingInt(Farm::getWeight)));

    List<DateRangeReport> reports = new ArrayList<>();
    farmWeightMap.forEach((farmId, weight) -> {
      double percent = weight / total * 100;
      reports.add(
          new DateRangeReport(farmId, weight, percent, summaryMap.get(farmId)));
    });
    if (asc) {
      if (FiledType.ID == sortedBy) {
        reports.sort(Comparator.comparing(DateRangeReport::getFarmId));
      } else {
        reports.sort(Comparator.comparing(DateRangeReport::getTotalWeight));
      }
    } else {
      if (FiledType.ID == sortedBy) {
        reports
            .sort(Comparator.comparing(DateRangeReport::getFarmId).reversed());
      } else {
        reports.sort(
            Comparator.comparing(DateRangeReport::getTotalWeight).reversed());
      }
    }
    return reports;
  }

  /**
   * The object struct is used to store the min,max,average weight for the farm
   */
  protected static class Summary {
    private int min;
    private int max;
    private double avg;

    public Summary(int min, int max, double avg) {
      this.min = min;
      this.max = max;
      this.avg = avg;
    }

    public int getMin() {
      return min;
    }

    public int getMax() {
      return max;
    }

    public double getAvg() {
      return avg;
    }
  }

  protected static class FarmReport extends Summary {
    private int month;
    private int totalWeight;
    private double percent;

    public FarmReport(int month, int totalWeight, double percent, int min,
        int max, double avg) {
      super(min, max, avg);
      this.month = month;
      this.totalWeight = totalWeight;
      this.percent = percent;
    }

    public int getMonth() {
      return month;
    }

    public int getTotalWeight() {
      return totalWeight;
    }

    public double getPercent() {
      return percent;
    }
  }

  protected static class DateRangeReport extends Summary {
    private String farmId;
    private int totalWeight;
    private double percent;

    public DateRangeReport(String farmId, int totalWeight, double percent,
        Summary summary) {
      super(summary.getMin(), summary.getMax(), summary.getAvg());
      this.farmId = farmId;
      this.totalWeight = totalWeight;
      this.percent = percent;
    }

    public String getFarmId() {
      return farmId;
    }

    public int getTotalWeight() {
      return totalWeight;
    }

    public double getPercent() {
      return percent;
    }
  }

}
