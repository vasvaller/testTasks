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

      // find same price in newPrices
      LinkedList<Price> newPricesForProcessing = new LinkedList<>(getSamePricesByCodeAndNumberAndDeparture(newPrices, currentPrice));
      newPrices.removeAll(newPricesForProcessing);

      // now we have two lists based on currentPrice with intersections probably
      result.addAll(processIntersections(oldPricesForProcessing, newPricesForProcessing));
    }

    // add all remaining prices from new prices
    result.addAll(newPrices);
    return result;
  }

  private static List<Price> processIntersections(LinkedList<Price> oldPricesForProcessing, LinkedList<Price> newPricesForProcessing) {
    return new LinkedList<>(); //TODO write joining method here
  }

  private static boolean arePricesDatesIntersect(Price newPrice, Price oldPrice) {
    long begin1 = oldPrice.getBegin().getTime();
    long begin2 = newPrice.getBegin().getTime();
    long end1 = oldPrice.getEnd().getTime();
    long end2 = newPrice.getEnd().getTime();
    return !(end1 < begin2 || begin1 > end2);
  }

  /**
   * @return Вернет лист из аналогичных цен
   */
  public static List<Price> getSamePricesByCodeAndNumberAndDeparture(List<Price> fromList, Price searchPrice) {
    List<Price> result = new LinkedList<>();

    String productCodeSearch = searchPrice.getProductCode();
    int numberSearch = searchPrice.getNumber();
    int departSearch = searchPrice.getDepart();

    for (Price priceFromList : fromList) {
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
