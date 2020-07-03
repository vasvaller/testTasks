package prices;

import java.util.*;

public class PriceJoinerUtil {
  private long id;            // идентификатор в БД
  private String productCode; // код товара
  private int number;         // номер цены
  private int depart;         // номер отдела
  private Date begin;         // начало действия
  private Date end;           // конец действия
  private long value;         // значение цены в копейках

  /**
   * Main method for this application
   * @param oldPrices list of old prices
   * @param newPrices list of new prices
   * @return Collection with joined prices
   */
  public static Collection<Price> joinPrices(LinkedList<Price> oldPrices, LinkedList<Price> newPrices) {
    LinkedList<Price> result = new LinkedList<>();

    while (oldPrices.size() != 0) {
      LinkedList<Price> oldPricesForProcessing = new LinkedList<>();

      // get current price and same prices
      Price currentPrice = oldPrices.remove(0);
      List<Price> samePrices = getSamePricesByCodeAndNumberAndDeparture(oldPrices, currentPrice);

      // put same prices to list for processing
      oldPricesForProcessing.add(currentPrice);
      oldPricesForProcessing.addAll(samePrices);
      oldPrices.removeAll(samePrices);

      // find same price in newPrices and remove all this prices from processing list
      LinkedList<Price> newPricesForProcessing = new LinkedList<>(getSamePricesByCodeAndNumberAndDeparture(newPrices, currentPrice));
      newPrices.removeAll(newPricesForProcessing);

      // now we have two lists based on currentPrice with intersections
      result.addAll(processIntersections(oldPricesForProcessing, newPricesForProcessing));
    }

    // add all remaining prices from new prices
    result.addAll(newPrices);
    return result;
  }

  /**
   * Combines prices with same end datetime and begin datetime to single
   *
   * @param listForCombining not pured list
   * @return pured list
   */
  private static LinkedList<Price> combineConsecuentivePrices(LinkedList<Price> listForCombining) {
    LinkedList<Price> result = new LinkedList<>();

    while (listForCombining.size() > 1) {
      Price price = listForCombining.remove(0);
      Price priceNext = listForCombining.get(0);

      if (price.getProductCode().equals(priceNext.getProductCode())
          && price.getNumber() == priceNext.getNumber()
          && price.getDepart() == priceNext.getDepart()
          && price.getValue().equals(priceNext.getValue())
          && priceNext.getBegin().getTime() - price.getEnd().getTime() <= 1000) {
        listForCombining.remove(0);
        price.setEnd(priceNext.getEnd());
        result.add(price);
      } else { // otherwise add first price to result and start again
        result.add(price);
        if (listForCombining.size() == 1) {
          result.add(priceNext);
          break;
        }
      }
    }
    return result;
  }

  /**
   * Creates list of
   *
   * @param oldPricesForProcessing list of old selected single price history
   * @param newPricesForProcessing list of new selected single price history
   * @return joined list
   */
  public static List<Price> processIntersections(LinkedList<Price> oldPricesForProcessing, LinkedList<Price> newPricesForProcessing) {
    LinkedList<Price> result = new LinkedList<>();
    Collections.sort(oldPricesForProcessing);
    Collections.sort(newPricesForProcessing);

    while (oldPricesForProcessing.size() > 0 && newPricesForProcessing.size() > 0) {
      Price oldP = oldPricesForProcessing.remove(0);
      Price newP = newPricesForProcessing.remove(0);

      // if there is conflict resolve it
      if (arePricesDatesIntersects(oldP, newP)) {
        result.addAll(resolveConflict(oldP, newP, oldPricesForProcessing, newPricesForProcessing));
      } else { // если не пересекаются - добавляем в резалт ту цену, которая раньше по графику и вторую возвращаем в свою очередь на обработку первым элементом
        if (oldP.getEnd().getTime() < newP.getBegin().getTime()) {
          result.add(oldP);
          newPricesForProcessing.add(0, newP);
        } else if (newP.getEnd().getTime() < oldP.getBegin().getTime()) {
          result.add(newP);
          oldPricesForProcessing.add(0, oldP);
        }
      }

      if (oldPricesForProcessing.size() == 0) result.addAll(newPricesForProcessing);
      else result.addAll(oldPricesForProcessing);
    }
    result = combineConsecuentivePrices(result);
    return result;
  }

  /**
   * Resolves conflict of intersected dates
   * There are 11 different cases of relative position of timelines
   * (See "<code>resources/prices_intersecations_cases.png</code>)
   *
   * @param oldP                   old price conflicts with new price
   * @param newP                   new price conflicts with old price
   * @param oldPricesForProcessing all history cases of the same old price
   * @param newPricesForProcessing all history cases of the same new price
   * @return list of resolved dated.
   */
  public static List<Price> resolveConflict(Price oldP, Price newP, LinkedList<Price> oldPricesForProcessing, LinkedList<Price> newPricesForProcessing) {
    LinkedList<Price> result = new LinkedList<>();

    if (oldP.getBegin().equals(newP.getBegin())) { // 1. Begin times equals
      if (oldP.getEnd().after(newP.getEnd())) { // 1.1. 1st end time after 2nd end time
        oldP.setBegin(newP.getEnd());
        oldPricesForProcessing.add(oldP);
        result.add(newP);
      } else if (oldP.getEnd().equals(newP.getEnd())) { // 1.2. 1st end time equal 2nd end time
        result.add(newP);
      } else if (oldP.getEnd().before(newP.getEnd())) { // 1.3. 1st end time before 2nd end time
        result.add(newP);
        newPricesForProcessing.add(0, newP);
      }
    } else if (oldP.getBegin().before(newP.getBegin())) { // 2. 1st begin time before 2nd begin time
      if (oldP.getEnd().after(newP.getEnd())) { // 2.1. 1st end time after 2nd end time
        Price secondPartOfOldP = new Price(oldP);
        secondPartOfOldP.setBegin(newP.getEnd());
        oldP.setEnd(newP.getBegin());
        oldPricesForProcessing.add(0, secondPartOfOldP);
        result.add(oldP);
        result.add(newP);
      } else if (oldP.getEnd().equals(newP.getEnd())) { // 2.2. 1st end time equal 2nd end time
        oldP.setEnd(newP.getBegin());
        result.add(oldP);
        result.add(newP);
      } else if (oldP.getEnd().before(newP.getEnd())) { // 2.3. 1st end time before 2nd end time
        oldP.setEnd(newP.getBegin());
        result.add(oldP);
        newPricesForProcessing.add(0, newP);
      } else if (oldP.getEnd().equals(newP.getBegin())) {// 2.4. 1st END time equal 2nd BEGIN time
        oldP.setEnd(newP.getBegin());
        newPricesForProcessing.add(newP);
        result.add(oldP);
      }
    } else if (oldP.getBegin().after(newP.getBegin())) { // 3. 1st begin time after 2nd begin time
      if (oldP.getBegin().equals(newP.getEnd())) { // 3.1. 1st begin time equal 2nd end time
        oldP.setBegin(newP.getEnd());
        oldPricesForProcessing.add(0, oldP);
        result.add(newP);
      } else if (oldP.getEnd().after(newP.getEnd())) { // 3.2. 1st end time after 2nd end time
        oldP.setBegin(newP.getEnd());
        oldPricesForProcessing.add(0, oldP);
        result.add(newP);
      } else if (oldP.getEnd().equals(newP.getEnd())) { // 3.3. 1st end time equal 2nd end time
        result.add(newP);
      } else if (oldP.getEnd().before(newP.getEnd())) { // 3.4. 1st end time before 2nd end time
        newPricesForProcessing.add(0, newP);
        result.add(newP);
      }
    } else try {
      throw new Exception("FAIL: exception situation in resolveConflict()");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Checks is whether there some intersection of two dates
   *
   * @return true - there is intersection, false - no intersection
   */
  private static boolean arePricesDatesIntersects(Price oldPrice, Price newPrice) {
    long begin1 = oldPrice.getBegin().getTime();
    long begin2 = newPrice.getBegin().getTime();
    long end1 = oldPrice.getEnd().getTime();
    long end2 = newPrice.getEnd().getTime();
    return (end1 > begin2 || begin1 < end2);
  }

  /**
   * @return Returns list of all cases of "searchPrice" from "searchList"
   */
  public static List<Price> getSamePricesByCodeAndNumberAndDeparture(List<Price> searchList, Price searchPrice) {
    List<Price> result = new LinkedList<>();

    String productCodeSearch = searchPrice.getProductCode();
    int numberSearch = searchPrice.getNumber();
    int departSearch = searchPrice.getDepart();

    for (Price priceFromList : searchList) {
      String productCodeFromList = priceFromList.getProductCode();
      int numberFromList = priceFromList.getNumber();
      int departFromList = priceFromList.getDepart();
      if (productCodeSearch.equals(productCodeFromList) && numberSearch == numberFromList && departSearch == departFromList) {
        result.add(priceFromList);
      }
    }
    return result;
  }
}
