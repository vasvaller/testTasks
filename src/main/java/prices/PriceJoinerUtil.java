package prices;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PriceJoinerUtil {
  private long id;            // идентификатор в БД
  private String productCode; // код товара
  private int number;         // номер цены
  private int depart;         // номер отдела
  private Date begin;         // начало действия
  private Date end;           // конец действия
  private long value;         // значение цены в копейках

  /**
   * Главный тестируемый высокоуровневый метод (сама суть программы)
   * @param oldPrices данные со старыми ценами
   * @param newPrices данные с новыми ценами
   * @return возвращает коллекцию с объединенными ценами
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

      // now we have two lists based on currentPrice with intersections probably
      result.addAll(processIntersections(oldPricesForProcessing, newPricesForProcessing));
    }

    // add all remaining prices from new prices
    result.addAll(newPrices);
    return result;
  }

  /**
   * в рамках одной цены
   * @param oldPricesForProcessing list of old selected single price history
   * @param newPricesForProcessing list of new selected single price history
   * @return joined list
   */
  private static List<Price> processIntersections(LinkedList<Price> oldPricesForProcessing, LinkedList<Price> newPricesForProcessing) {
    LinkedList<Price> result = new LinkedList<>();
    while (newPricesForProcessing.size() != 0) {
      Price currentNewPrice = newPricesForProcessing.remove(0);

      // пока очереди не пустые проверяем на пересечение элементы в порядке упоминания дат
      while (oldPricesForProcessing.size() != 0 && newPricesForProcessing.size() != 0) {
        Price oldP = oldPricesForProcessing.remove(0);
        Price newP = oldPricesForProcessing.remove(0);

        // если пересекаются решаем конфликт
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
      }

      // остаток одной из очередей просто добавим в результат
      if (oldPricesForProcessing.size() == 0) result.addAll(newPricesForProcessing);
      else result.addAll(oldPricesForProcessing);
    }
    return result;
  }

  /**
   * Решит конфликт, вернет список того что нужно положить в резалт.
   * Есть 11 возможных ситуаций относительного расположения временных отрезков.
   * Для наглядности см. графическое представление Variants.jpg где нибудь в каталоге рядом с исходниками проекта
   *
   * @param oldP
   * @param newP
   * @param oldPricesForProcessing
   * @param newPricesForProcessing
   */
  public static List<Price> resolveConflict(Price oldP, Price newP, LinkedList<Price> oldPricesForProcessing, LinkedList<Price> newPricesForProcessing) {
    LinkedList<Price> result = new LinkedList<>();


    if (oldP.getBegin().equals(newP.getBegin())) { // 1. Begin times equals
      if (oldP.getEnd().after(newP.getEnd())) { // 1.1. 1st end time after 2nd end time
        oldP.setBegin(new Date(newP.getEnd().getTime() + 1));
        oldPricesForProcessing.add(oldP);
        result.add(newP);
        result.add(oldP);
      } else if (oldP.getEnd().equals(newP.getEnd())) { // 1.2. 1st end time equal 2nd end time
        result.add(newP);
      } else if (oldP.getEnd().before(newP.getEnd())) { // 1.3. 1st end time before 2nd end time
        newPricesForProcessing.add(0, newP);
      } else if (oldP.getBegin().before(newP.getBegin())) { // 2. 1st begin time before 2nd begin time
        if (oldP.getEnd().after(newP.getEnd())) { // 2.1. 1st end time after 2nd end time
          Price secondPartOfOldP = new Price(oldP);
          secondPartOfOldP.setBegin(new Date(newP.getEnd().getTime() + 1));
          oldP.setEnd(new Date(newP.getBegin().getTime() - 1));
          result.add(oldP);
          result.add(newP);
          oldPricesForProcessing.add(0, secondPartOfOldP);
        } else if (oldP.getEnd().equals(newP.getEnd())) { // 2.2. 1st end time equal 2nd end time
          oldP.setEnd(new Date(newP.getBegin().getTime() - 1));
          result.add(oldP);
          result.add(newP);
        } else if (oldP.getEnd().before(newP.getEnd())) { // 2.3. 1st end time before 2nd end time
          oldP.setEnd(new Date(newP.getBegin().getTime() - 1));
          result.add(oldP);
          newPricesForProcessing.add(0, newP);
        }
      } else if (oldP.getBegin().after(newP.getBegin())) { // 3. 1st begin time after 2nd begin time
        if (oldP.getBegin().equals(newP.getEnd())) { // 3.1. 1st begin time equal 2nd end time
          oldP.setBegin(new Date(newP.getEnd().getTime() + 1));
          oldPricesForProcessing.add(0, oldP);
          result.add(newP);
        } else if (oldP.getEnd().after(newP.getEnd())) { // 3.2. 1st end time after 2nd end time
          oldP.setBegin(new Date(newP.getEnd().getTime() + 1));
          oldPricesForProcessing.add(0, oldP);
          result.add(newP);
        } else if (oldP.getEnd().equals(newP.getEnd())) { // 3.3. 1st end time equal 2nd end time
          result.add(newP);
        } else if (oldP.getEnd().before(newP.getEnd())) { // 3.4. 1st end time before 2nd end time
          newPricesForProcessing.add(0, newP);
        }
      } else if (oldP.getEnd().equals(newP.getBegin())) {// 4. 1st END time equal 2nd BEGIN time
          oldP.setEnd(new Date(newP.getBegin().getTime() - 1));
      } else try {
        throw new Exception("Решение конфликта пошло не по сценарию");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * Проверяет есть ли пересечение у двух цен по датам
   * @return true - есть пересечение, false - нет пересечения
   */
  private static boolean arePricesDatesIntersects(Price oldPrice, Price newPrice) {
    long begin1 = oldPrice.getBegin().getTime();
    long begin2 = newPrice.getBegin().getTime();
    long end1 = oldPrice.getEnd().getTime();
    long end2 = newPrice.getEnd().getTime();
    return (end1 > begin2 || begin1 < end2);
  }

  /**
   * @return Вернет лист историю (все варианты) переданной цены из переданного списка
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
