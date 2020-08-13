package application;

/**
 * Defines required operations to read/write input/output files
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yiyang Gu ygu75@wisc.edu
 */
public class FileManager {

  private List<String> contents;

  public FileManager() {
    contents = new ArrayList<>();
  }


  /**
   * read the data file line by line and ignore header
   *
   * @param inputFile data file
   * @return true when read successfully, otherwise false
   */
  public boolean readFile(File inputFile) {
    List<String> lines = new ArrayList<>();
    try (
        BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      String line;
      while (null != (line = reader.readLine())) {
        if (line.toLowerCase().startsWith("date")) {
          continue;
        }
        lines.add(line);
      }
      contents.clear();
      contents.addAll(lines);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Read file error, please check file format.");
      return false;
    }
    return true;
  }

  /**
   * store all the farms into the given file in csv format with header "date,farm_id,weight"
   *
   * @param outputFile output file
   * @return true when save successfully, otherwise false
   */
  public boolean writeToFile(File outputFile) {
    try (PrintWriter writer = new PrintWriter(outputFile)) {
      writer.println("date,farm_id,weight");
      List<Farm> farms = CheeseFactory.getFarms();
      for (Farm farm : farms) {
        writer.println(String.format("%s,%s,%d", farm.getDate().toString(),
            farm.getId(), farm.getWeight()));
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Write file error, please check file.");
      return false;
    }
  }

  /**
   * @return all the contents of the current file
   */
  public List<String> getFileContents() {
    return new ArrayList<>(contents);
  }

}
