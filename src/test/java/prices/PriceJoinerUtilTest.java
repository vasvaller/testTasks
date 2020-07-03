package prices;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.*;

public class PriceJoinerUtilTest {
  // тестовые дата-время
  String jan = "01.01.2020 00:00:00";
  String janBeg = "01.01.2020 00:00:01";
  String janEnd = "31.01.2020 23:59:59";
  String feb = "01.02.2020 00:00:00";
  String febBeg = "01.02.2020 00:00:01";
  String febEnd = "29.02.2020 23:59:59";
  String mar = "01.03.2020 00:00:00";
  String marBeg = "01.03.2020 00:00:01";
  String marEnd = "31.03.2020 23:59:59";
  String apr = "01.04.2020 00:00:00";
  String aprBeg = "01.04.2020 00:00:01";
  String aprEnd = "30.04.2020 23:59:59";
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
    LinkedList<Price> expPriceInProcessList = new LinkedList<>();

    // 1.1
    Price oldP11 = new Price("122856", 1, 1, jan, mar, 100);
    Price newP11 = new Price("122856", 1, 1, jan, feb, 200);
    LinkedList<Price> exp11 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, feb, 200)));
    expPriceInProcessList = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, febBeg, mar, 100)));

    Assert.assertArrayEquals("в result 1.1: ", exp11.toArray(), PriceJoinerUtil.resolveConflict(oldP11, newP11, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertEquals("в processList 1.1: ", expPriceInProcessList.get(0), oldPricesForProcessing.get(0));
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 1.2
    Price oldP12 = new Price("122856", 1, 1, jan, feb, 100);
    Price newP12 = new Price("122856", 1, 1, jan, feb, 200);
    LinkedList<Price> exp12 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, feb, 200)));
    expPriceInProcessList = new LinkedList<>();

    Assert.assertArrayEquals("в result 1.2: ", exp12.toArray(), PriceJoinerUtil.resolveConflict(oldP12, newP12, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 1.2: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 1.3
    Price oldP13 = new Price("122856", 1, 1, jan, feb, 100);
    Price newP13 = new Price("122856", 1, 1, jan, mar, 200);
    LinkedList<Price> exp13 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, mar, 200)));
    expPriceInProcessList = new LinkedList<>();

    Assert.assertArrayEquals("в result 1.3: ", exp13.toArray(), PriceJoinerUtil.resolveConflict(oldP13, newP13, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 1.3: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 2.1
    Price oldP21 = new Price("122856", 1, 1, jan, apr, 100);
    Price newP21 = new Price("122856", 1, 1, feb, mar, 200);
    LinkedList<Price> exp21 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, janEnd, 100)));
    exp21.add(new Price("122856", 1, 1, feb, mar, 200));
    expPriceInProcessList = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, marBeg, apr, 100)));

    Assert.assertArrayEquals("в result 2.1: ", exp21.toArray(), PriceJoinerUtil.resolveConflict(oldP21, newP21, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 2.1: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 2.2
    Price oldP22 = new Price("122856", 1, 1, jan, mar, 100);
    Price newP22 = new Price("122856", 1, 1, feb, mar, 200);
    LinkedList<Price> exp22 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, janEnd, 100)));
    exp22.add(new Price("122856", 1, 1, feb, mar, 200));
    expPriceInProcessList = new LinkedList<>();

    Assert.assertArrayEquals("в result 2.2: ", exp22.toArray(), PriceJoinerUtil.resolveConflict(oldP22, newP22, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 2.2: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 2.3
    Price oldP23 = new Price("122856", 1, 1, jan, mar, 100);
    Price newP23 = new Price("122856", 1, 1, feb, apr, 200);
    LinkedList<Price> exp23 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, janEnd, 100)));
    expPriceInProcessList = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, feb, apr, 200)));

    Assert.assertArrayEquals("в result 2.3: ", exp23.toArray(), PriceJoinerUtil.resolveConflict(oldP23, newP23, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 2.3: ", expPriceInProcessList.toArray(), newPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 2.4
    Price oldP24 = new Price("122856", 1, 1, jan, feb, 100);
    Price newP24 = new Price("122856", 1, 1, feb, mar, 200);
    LinkedList<Price> exp24 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, janEnd, 100)));
    expPriceInProcessList = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, feb, mar, 200)));

    Assert.assertArrayEquals("в result 2.4: ", exp24.toArray(), PriceJoinerUtil.resolveConflict(oldP24, newP24, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 2.4: ", expPriceInProcessList.toArray(), newPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 3.1
    Price oldP31 = new Price("122856", 1, 1, feb, mar, 100);
    Price newP31 = new Price("122856", 1, 1, jan, feb, 200);
    LinkedList<Price> exp31 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, feb, 200)));
    expPriceInProcessList = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, febBeg, mar, 100)));

    Assert.assertArrayEquals("в result 3.1: ", exp31.toArray(), PriceJoinerUtil.resolveConflict(oldP31, newP31, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 3.1: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 3.2
    Price oldP32 = new Price("122856", 1, 1, feb, apr, 100);
    Price newP32 = new Price("122856", 1, 1, jan, mar, 200);
    LinkedList<Price> exp32 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, mar, 200)));
    expPriceInProcessList = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, marBeg, apr, 100)));

    Assert.assertArrayEquals("в result 3.2: ", exp32.toArray(), PriceJoinerUtil.resolveConflict(oldP32, newP32, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 3.2: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 3.3
    Price oldP33 = new Price("122856", 1, 1, feb, mar, 100);
    Price newP33 = new Price("122856", 1, 1, jan, mar, 200);
    LinkedList<Price> exp33 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, mar, 200)));
    expPriceInProcessList = new LinkedList<>();

    Assert.assertArrayEquals("в result 3.3: ", exp33.toArray(), PriceJoinerUtil.resolveConflict(oldP33, newP33, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 3.3: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    // 3.4
    Price oldP34 = new Price("122856", 1, 1, feb, mar, 100);
    Price newP34 = new Price("122856", 1, 1, jan, apr, 200);
    LinkedList<Price> exp34 = new LinkedList<>(Arrays.asList(new Price("122856", 1, 1, jan, apr, 200)));
    expPriceInProcessList = new LinkedList<>(Arrays.asList());

    Assert.assertArrayEquals("в result 3.4: ", exp34.toArray(), PriceJoinerUtil.resolveConflict(oldP34, newP34, oldPricesForProcessing, newPricesForProcessing).toArray());
    Assert.assertArrayEquals("в processList 3.4: ", expPriceInProcessList.toArray(), oldPricesForProcessing.toArray());
    oldPricesForProcessing.clear();
    newPricesForProcessing.clear();

    //TODO написать expected



    //TODO написать ассерты

  }
}
