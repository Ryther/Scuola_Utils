package utils.data;

import java.text.ParseException;
import java.time.LocalDate;
import static java.time.LocalDate.parse;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Edoardo Zanoni
 */
public class CalendarUtils {
    
    public static final int FIRST_WEEK_DAY = Calendar.MONDAY;
    
    public static <T extends ChronoLocalDateTime<?>> int getDateDay(T date) {
        
        return date.get(ChronoField.DAY_OF_WEEK);
    }
    
    public static int getDateDay(String date) throws ParseException {
        
        return CalendarUtils.stringToDate(date).getDayOfWeek().getValue();
    }
    
    public static <T extends ChronoLocalDateTime<?>> int getDateWeek(T date) {
        
        TemporalField week = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 
        return date.get(week);
    }
    
    public static int getDateWeek(String date) throws ParseException {
        
        TemporalField week = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 
        return CalendarUtils.stringToDate(date).get(week);
    }
    
    public static <T extends ChronoLocalDateTime<?>> int getDateYear(T date) {

        return date.get(ChronoField.YEAR);
    }
    
    public static int getDateYear(String date) throws ParseException {
        
        return CalendarUtils.stringToDate(date).getYear();
    }
    
    public static LocalDate stringToDate(String s) throws ParseException {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
        return LocalDate.parse(s, formatter);
    }
    
    public static <T extends Temporal> T stringToDate(String s, String format) throws ParseException {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        T formattedDate = null;
        if (format.contains("s") || format.contains("k") || format.contains("m")) {
            
            formattedDate = (T) LocalDateTime.parse(s, formatter);
        } else {
            
            formattedDate = (T) LocalDate.parse(s, formatter);
        }
        return formattedDate;
    }
    
    public static <T extends ChronoLocalDateTime<?>> String dateToString(T data) throws ParseException {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
        return String.valueOf(data.format(formatter));
    }
    
    public static <T extends ChronoLocalDateTime<?>> String dateToString(T data, String format) throws ParseException {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return String.valueOf(data.format(formatter));
    }
    
    public static <T extends ChronoLocalDateTime<?>> boolean equals(T data1, T data2) throws ParseException {
        
        return CalendarUtils.dateToString(data1).equals(CalendarUtils.dateToString(data2));
    }
    
    public static boolean equals(String data1, String data2) throws ParseException {
        
        return data1.equals(data2);
    }
}