package prices;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Price implements Comparable{
  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  private long id;            // идентификатор в БД
  private String productCode; // код товара
  private int number;         // номер цены
  private int depart;         // номер отдела
  private Date begin;         // начало действия
  private Date end;           // конец действия
  private Long value;         // значение цены в копейках

  public Price(String productCode, int number, int depart, String begin, String end, long value) throws ParseException {
    this.id = id;
    this.productCode = productCode;
    this.number = number;
    this.depart = depart;
    this.begin = simpleDateFormat.parse(begin);
    this.end = simpleDateFormat.parse(end);
    this.value = value;
  }

  public Price(Price priceReference) {
    this.productCode = priceReference.getProductCode();
    this.number = priceReference.getNumber();
    this.depart = priceReference.getDepart();
    this.begin = priceReference.getBegin();
    this.end = priceReference.getEnd();
    this.value = priceReference.getValue();
  }

  @Override
  public int compareTo(Object o) {
    if (this.getBegin().before(((Price) o).begin)) return -1;
    if (this.getBegin().after(((Price) o).begin)) return 1;
    else return 0;
  }
}
