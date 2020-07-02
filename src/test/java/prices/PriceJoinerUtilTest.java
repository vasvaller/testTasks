package prices;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.*;

public class PriceJoinerUtilTest {
  // тестовые дата-время
  String jan = "01.01.2020 00:00:00";
  String feb = "01.02.2020 00:00:00";
  String mar = "01.03.2020 00:00:00";
  String apr = "01.04.2020 00:00:00";
  String may = "01.05.2020 00:00:00";
  String jun = "01.06.2020 00:00:00";
  String jul = "01.07.2020 00:00:00";
  String aug = "01.08.2020 00:00:00";
  String sep = "01.09.2020 00:00:00";
  String oct = "01.10.2020 00:00:00";
  String nov = "01.11.2020 00:00:00";
  String dec = "01.12.2020 00:00:00";

  @Test
  public void priceJoin() throws ParseException {
    LinkedList<Price> oldPrices = new LinkedList<>();
    LinkedList<Price> newPrices = new LinkedList<>();
    LinkedList<Price> expectedPrices = new LinkedList<>();

    oldPrices.add(new Price("122856", 1, 1, "01.01.2013 00:00:00", "31.01.2013 23:59:59", 11000));
    oldPrices.add(new Price("122856", 2, 1, "10.01.2013 00:00:00", "20.01.2013 23:59:59", 99000));
    oldPrices.add(new Price("6654", 1, 2, "01.01.2013 00:00:00", "31.01.2013 00:00:00", 5000));

    newPrices.add(new Price("122856", 1, 1, "20.01.2013 00:00:00", "20.02.2013 23:59:59", 11000));
    newPrices.add(new Price("122856", 2, 1, "15.01.2013 00:00:00", "25.01.2013 23:59:59", 92000));
    newPrices.add(new Price("6654", 1, 2, "12.01.2013 00:00:00", "13.01.2013 00:00:00", 4000));

    expectedPrices.add(new Price("122856", 1, 1, "01.01.2013 00:00:00", "20.02.2013 23:59:59", 11000));
    expectedPrices.add(new Price("122856", 2, 1, "10.01.2013 00:00:00", "15.01.2013 00:00:00", 99000));
    expectedPrices.add(new Price("122856", 2, 1, "15.01.2013 00:00:00", "25.01.2013 23:59:59", 92000));
    expectedPrices.add(new Price("6654", 1, 2, "01.01.2013 00:00:00", "12.01.2013 00:00:00", 5000));
    expectedPrices.add(new Price("6654", 1, 2, "12.01.2013 00:00:00", "13.01.2013 00:00:00", 4000));
    expectedPrices.add(new Price("6654", 1, 2, "13.01.2013 00:00:00", "31.01.2013 00:00:00", 5000));

    Collection<Price> result = PriceJoinerUtil.joinPrices(oldPrices, newPrices);

    Assert.assertArrayEquals(expectedPrices.toArray(), result.toArray());
  }

  @Test
  public void resolveConflict() throws ParseException {
    LinkedList<Price> oldPricesForProcessing = new LinkedList<>();
    LinkedList<Price> newPricesForProcessing = new LinkedList<>();
    LinkedList<Price> expectedPrices = new LinkedList<>();

    // 1.1
    Price oldP11 = new Price("122856", 1, 1, jan, mar, 11000);
    Price newP11 = new Price("122856", 1, 1, jan, feb, 11000);

    // 1.2
    Price oldP12 = new Price("122856", 1, 1, jan, feb, 11000);
    Price newP12 = new Price("122856", 1, 1, jan, feb, 11000);

    // 1.3
    Price oldP13 = new Price("122856", 1, 1, jan, feb, 11000);
    Price newP13 = new Price("122856", 1, 1, jan, mar, 11000);

    // 2.1
    Price oldP21 = new Price("122856", 1, 1, jan, apr, 11000);
    Price newP21 = new Price("122856", 1, 1, feb, mar, 11000);

    // 2.2
    Price oldP22 = new Price("122856", 1, 1, jan, mar, 11000);
    Price newP22 = new Price("122856", 1, 1, feb, mar, 11000);

    // 2.3
    Price oldP23 = new Price("122856", 1, 1, jan, mar, 11000);
    Price newP23 = new Price("122856", 1, 1, feb, apr, 11000);

    // 3.1
    Price oldP31 = new Price("122856", 1, 1, feb, mar, 11000);
    Price newP31 = new Price("122856", 1, 1, jan, feb, 11000);

    // 3.2
    Price oldP32 = new Price("122856", 1, 1, feb, apr, 11000);
    Price newP32 = new Price("122856", 1, 1, jan, mar, 11000);

    // 3.3
    Price oldP33 = new Price("122856", 1, 1, feb, mar, 11000);
    Price newP33 = new Price("122856", 1, 1, jan, mar, 11000);

    // 3.4
    Price oldP34 = new Price("122856", 1, 1, feb, mar, 11000);
    Price newP34 = new Price("122856", 1, 1, jan, apr, 11000);

    // 4
    Price oldP4 = new Price("122856", 1, 1, jan, feb, 11000);
    Price newP4 = new Price("122856", 1, 1, feb, mar, 11000);

    //TODO написать expected
//    LinkedList<Price> exp11 = new LinkedList<>(Arrays.asList(new Price()));

    //TODO написать ассерты
    PriceJoinerUtil.resolveConflict(oldP11, newP11, oldPricesForProcessing, newPricesForProcessing).toArray();
  }
}
