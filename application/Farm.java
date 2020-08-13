package application;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Yiyang Gu ygu75@wisc.edu
 */
public class Farm {

  private String id;
  private LocalDate date;
  private int weight;

  public Farm(String id, LocalDate date, int weight) {
    this.id = id;
    this.date = date;
    this.weight = weight;
  }

  public String getId() {
    return id;
  }

  public LocalDate getDate() {
    return date;
  }

  public int getWeight() {
    return weight;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getYear() {
    return date.getYear();
  }

  /**
   * @return the month-of-year, from 1 to 12
   */
  public int getMonth() {
    return date.getMonthValue();
  }

  /**
   * @return the day-of-month, from 1 to 31
   */
  public int getDay() {
    return date.getDayOfMonth();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Farm farm = (Farm) o;
    return Objects.equals(id, farm.id) && Objects.equals(date, farm.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, date);
  }
}
