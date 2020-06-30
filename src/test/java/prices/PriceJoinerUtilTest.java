package prices;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PriceJoinerUtilTest {

  @Test
  public void priceJoin() throws ParseException {
    LinkedList<Price> oldPrices = new LinkedList<>();
    LinkedList newPrices = new LinkedList();
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

    Collection result = PriceJoinerUtil.joinPrices(oldPrices, newPrices);

    Assert.assertArrayEquals(expectedPrices.toArray(), result.toArray());
  }
}
