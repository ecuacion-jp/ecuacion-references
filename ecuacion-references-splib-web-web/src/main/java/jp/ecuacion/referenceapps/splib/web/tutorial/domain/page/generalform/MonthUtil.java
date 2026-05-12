package jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform;

import java.util.Arrays;
import java.util.List;
import jp.ecuacion.referenceapps.splib.web.tutorial.domain.page.generalform.record.MonthRecord;

public class MonthUtil {
  public static List<MonthRecord> getInitialData() {

    return Arrays.asList(new MonthRecord[] {
        new MonthRecord("1", "1月", "睦月", "January", "Januar", "janvier", SeasonEnum.SPRING, 31),
        new MonthRecord("2", "2月", "如月", "February", "Februar", "février", SeasonEnum.SPRING, 28),
        new MonthRecord("3", "3月", "弥生", "March", "März", "mars", SeasonEnum.SPRING, 31),
        new MonthRecord("4", "4月", "卯月", "April", "April", "avril", SeasonEnum.SUMMER, 30),
        new MonthRecord("5", "5月", "皐月", "May", "Mai", "mai", SeasonEnum.SUMMER, 31),
        new MonthRecord("6", "6月", "水無月", "June", "Juni", "juin", SeasonEnum.SUMMER, 30),
        new MonthRecord("7", "7月", "文月", "July", "Juli", "juillet", SeasonEnum.FALL, 31),
        new MonthRecord("8", "8月", "葉月", "August", "August", "août", SeasonEnum.FALL, 31),
        new MonthRecord("9", "9月", "長月", "September", "September", "septembre", SeasonEnum.FALL,
            30),
        new MonthRecord("10", "10月", "神無月", "October", "Oktober", "octobre", SeasonEnum.WINTER, 31),
        new MonthRecord("11", "11月", "霜月", "November", "November", "novembre", SeasonEnum.WINTER,
            30),
        new MonthRecord("12", "12月", "師走", "December", "Dezember", "décembre", SeasonEnum.WINTER,
            31)});
  }
}
