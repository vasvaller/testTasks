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
    LinkedList<Price> joinedListWithIntersections = new LinkedList<>();
    for (int i = 0; i < oldPrices.size(); i++) {
      Price firstFromOldList = oldPrices.remove(0);
      joinedListWithIntersections.add(firstFromOldList);
      joinedListWithIntersections.addAll(getSamePricesByCodeAndNumberAndDeparture(Collections.unmodifiableList(oldPrices), firstFromOldList));

    }


    return result;
  }

  private static boolean arePricesDatesIntersect(Price newPrice, Price oldPrice) {
    Long begin1 = oldPrice.getBegin().getTime();
    Long begin2 = newPrice.getBegin().getTime();
    Long end1 = oldPrice.getEnd().getTime();
    Long end2 = newPrice.getEnd().getTime();
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

  public static
}
