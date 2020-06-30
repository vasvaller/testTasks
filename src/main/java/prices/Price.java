package prices;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Price {
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
}
