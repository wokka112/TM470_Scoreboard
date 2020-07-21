package com.floatingpanda.scoreboard;

import android.util.Log;

import com.floatingpanda.scoreboard.utils.DateStringCreator;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DateStringCreatorTest {
    private final int DEFAULT_YEAR = 2019;
    //Months are zero-indexed in Java dates, so the month is November, not October.
    private final int DEFAULT_MONTH = 10;
    private final int DEFAULT_DAY_OF_MONTH = 14;
    private final int DEFAULT_HOUR_OF_DAY = 18;
    private final int DEFAULT_MINUTE = 42;
    private final int MONTH_BELOW_10 = 6;
    private final int HOUR_OF_DAY_BELOW_10 = 8;
    private final int MINUTE_BELOW_10 = 4;

    private final String DEFAULT_DAY_OF_WEEK_STRING = "Thursday";
    private final String DEFAULT_DAY_OF_WEEK_3_LETTER_STRING = "Thu";

    @Test
    public void getDayOfWeekStringTest() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String dayOfWeekString = dateStringCreator.getDayOfWeekString();
        assertTrue(dayOfWeekString.equals(DEFAULT_DAY_OF_WEEK_STRING));
    }

    @Test
    public void getDayOfWeek3LetterStringTest() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String dayOfWeek3LetterString = dateStringCreator.getDayOfWeek3LetterString();
        assertTrue(dayOfWeek3LetterString.equals(DEFAULT_DAY_OF_WEEK_3_LETTER_STRING));
    }

    @Test
    public void getDayOfMonthStringTest() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String dayOfMonthString = dateStringCreator.getDayOfMonthString();
        assertTrue(dayOfMonthString.equals(Integer.toString(DEFAULT_DAY_OF_MONTH)));
    }

    @Test
    public void getEnglishMonthNameStringTest() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String monthNameString = dateStringCreator.getEnglishMonthNameString();
        String correctMonthName = "November";
        assertTrue(monthNameString.equals(correctMonthName));
    }

    @Test
    public void getEnglishMonth3LetterStringTest() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String month3LetterString = dateStringCreator.getEnglishMonth3LetterString();
        String correctMonth3LetterString = "Nov";
        assertTrue(month3LetterString.equals(correctMonth3LetterString));
    }

    @Test
    public void getMonthNumberStringForMonthAbove10Test() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String monthNumberString = dateStringCreator.getMonthNumberString();
        assertTrue(monthNumberString.equals(Integer.toString(DEFAULT_MONTH + 1)));
    }

    @Test
    public void getMonthNumberStringForMonthBelow10Test() {
        DateStringCreator dateStringCreator = createDateStringCreatorWithMonthBelow10();

        String monthNumberString = dateStringCreator.getMonthNumberString();
        assertTrue(monthNumberString.equals("0" + (MONTH_BELOW_10 + 1)));
    }

    @Test
    public void getYearStringTest() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String yearString = dateStringCreator.getYearString();
        assertTrue(yearString.equals(Integer.toString(DEFAULT_YEAR)));
    }

    @Test
    public void getHourOfDayStringForHourAbove10Test() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String hourOfDayString = dateStringCreator.getHourOfDayString();
        assertTrue(hourOfDayString.equals(Integer.toString(DEFAULT_HOUR_OF_DAY)));
    }

    @Test
    public void getHourOfDayStringForHourBelow10Test() {
        DateStringCreator dateStringCreator = createDateStringCreatorWithHourBelow10();

        String hourOfDayString = dateStringCreator.getHourOfDayString();
        assertFalse(hourOfDayString.equals(Integer.toString(HOUR_OF_DAY_BELOW_10)));
        assertTrue(hourOfDayString.equals("0" + HOUR_OF_DAY_BELOW_10));
    }

    @Test
    public void getMinuteStringForMinuteAbove10Test() {
        DateStringCreator dateStringCreator = createDefaultDateStringCreator();

        String minuteString = dateStringCreator.getMinuteString();
        assertTrue(minuteString.equals(Integer.toString(DEFAULT_MINUTE)));
    }

    @Test
    public void getMinuteStringForMinuteBelow10Test() {
        DateStringCreator dateStringCreator = createDateStringCreatorWithMinuteBelow10();

        String minuteString = dateStringCreator.getMinuteString();
        assertFalse(minuteString.equals(Integer.toString(MINUTE_BELOW_10)));
        assertTrue(minuteString.equals("0" + MINUTE_BELOW_10));
    }

    @Test
    public void createDateCreatorThenSetDateTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY_OF_MONTH, DEFAULT_HOUR_OF_DAY, DEFAULT_MINUTE);
        Date date = calendar.getTime();
        DateStringCreator dateStringCreator = new DateStringCreator(date);

        String monthNumberString = dateStringCreator.getMonthNumberString();
        String yearString = dateStringCreator.getYearString();

        assertTrue(monthNumberString.equals(Integer.toString(DEFAULT_MONTH + 1)));
        assertTrue(yearString.equals(Integer.toString(DEFAULT_YEAR)));

        int newMonth = 2;
        int newYear = 1995;
        calendar.set(Calendar.MONTH, newMonth);
        calendar.set(Calendar.YEAR, newYear);

        Date newDate = calendar.getTime();
        dateStringCreator.setDate(newDate);

        monthNumberString = dateStringCreator.getMonthNumberString();
        yearString = dateStringCreator.getYearString();

        assertTrue(monthNumberString.equals("0" + (newMonth + 1)));
        assertTrue(yearString.equals(Integer.toString(newYear)));
    }

    @Test
    public void convertMonthNumberToEnglishNameStringTest() {
        int monthNo = DEFAULT_MONTH + 1;

        String monthString = DateStringCreator.convertMonthNumberToEnglishMonthNameString(monthNo);
        assertTrue(monthString.equals("November"));
    }

    private DateStringCreator createDefaultDateStringCreator() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY_OF_MONTH, DEFAULT_HOUR_OF_DAY, DEFAULT_MINUTE);
        Date date = calendar.getTime();
        return new DateStringCreator(date);
    }

    private DateStringCreator createDateStringCreatorWithMonthBelow10() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(DEFAULT_YEAR, MONTH_BELOW_10, DEFAULT_DAY_OF_MONTH, DEFAULT_HOUR_OF_DAY, DEFAULT_MINUTE);
        Date date = calendar.getTime();
        return new DateStringCreator(date);
    }

    private DateStringCreator createDateStringCreatorWithHourBelow10() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY_OF_MONTH, HOUR_OF_DAY_BELOW_10, DEFAULT_MINUTE);
        Date date = calendar.getTime();
        return new DateStringCreator(date);
    }

    private DateStringCreator createDateStringCreatorWithMinuteBelow10() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY_OF_MONTH, DEFAULT_HOUR_OF_DAY, MINUTE_BELOW_10);
        Date date = calendar.getTime();
        return new DateStringCreator(date);
    }
}
