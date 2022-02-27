package com.kashigin.stanislav;

import com.digdes.school.DatesToCronConverter;
import com.digdes.school.DatesToCronConvertException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

public class Converter implements DatesToCronConverter {

    SimpleDateFormat formatter;

    public Converter() {
        formatter = new SimpleDateFormat(DatesToCronConverter.DATE_FORMAT);
    }

    @Override
    public String convert(List<String> dates) throws DatesToCronConvertException {
        TreeSet<Integer> sec = new TreeSet<>();
        TreeSet<Integer> min = new TreeSet<>();
        TreeSet<Integer> hours = new TreeSet<>();
        TreeSet<Integer> day = new TreeSet<>();
        TreeSet<Integer> month = new TreeSet<>();
        TreeSet<Integer> weekday = new TreeSet<>();
        for (String date : dates) {
            try {
                Date formatedDate = formatter.parse(date);
                sec.add(formatedDate.getSeconds());
                min.add(formatedDate.getMinutes());
                hours.add(formatedDate.getHours());
                day.add(formatedDate.getDate());
                month.add(formatedDate.getMonth());
                weekday.add(formatedDate.getDay());
            }
            catch(java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        return generateCronExpr(sec, min, hours, day, month, weekday);
    }

    private String generateCronExpr(TreeSet<Integer> sec, TreeSet<Integer> min, TreeSet<Integer> hours,
                                    TreeSet<Integer> day, TreeSet<Integer> month, TreeSet<Integer> weekday) {
        return String.join(" ", generateTimeCronValue(sec), generateTimeCronValue(min), generateTimeCronValue(hours),
                generateTimeCronValue(day), generateTimeCronValue(month), generateDayOfWeekCronValue(weekday));
    }

    private String generateTimeCronValue(TreeSet<Integer> set) {
        boolean period = true;
        boolean sequence = true;
        int min = set.first();
        int max = set.last();
        if (1 == set.size()) {
            return set.first().toString();
        }
        Integer[] arr = new Integer[set.size()];
        arr = set.toArray(arr);
        int delta = arr[1]-arr[0];
        for (int i=0; i<arr.length-1; ++i ) {
            if (arr[i+1]-arr[i] != delta)
                period = false;
            if (arr[i+1]-arr[i] != 1)
                sequence = false;
            if (!period && !sequence)
                break;
        }
        if (sequence) {
            return String.join("-", Integer.toString(min), Integer.toString(max));
        }
        if (period) {
            return String.join("/", "0", Integer.toString(delta));
        }
        return "*";
    }

    private String generateDayOfWeekCronValue(TreeSet<Integer> set) {
        boolean sequence = true;
        int min = set.first();
        int max = set.last();
        if (1 == set.size()) {
            return DayOfWeek.of(set.first()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        }
        Integer[] arr = new Integer[set.size()];
        arr = set.toArray(arr);
        for (int i=0; i<arr.length-1; ++i ) {
            if (arr[i+1]-arr[i] != 1) {
                sequence = false;
                break;
            }
        }
        if (sequence) {
            return String.join("-", DayOfWeek.of(min).getDisplayName(TextStyle.SHORT, Locale.ENGLISH), DayOfWeek.of(max).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }
        return "*";
    }

    @Override
    public String getImplementationInfo() {
        return String.join(" ", "Кашигин Станислав Сергеевич", getClass().getSimpleName(),
                getClass().getPackage().toString(), "https://github.com/Eviljellyfish/DatesConverterDigitalDesignTask");
    }
}
