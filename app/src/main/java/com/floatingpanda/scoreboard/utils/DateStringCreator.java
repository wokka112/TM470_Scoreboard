/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.floatingpanda.scoreboard.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Takes a date and can be used to get different string representations of the date in English.
 *
 * Also provides a static method for some strings.
 *
 * All day and month names are given in English.
 */
public class DateStringCreator {
    private Calendar calendar;

    public DateStringCreator(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
    }

    public void setDate(Date date) {
        calendar.setTime(date);
    }

    public String getDayOfWeekString() {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "ERROR";
        }
    }

    public String getDayOfWeek3LetterString() {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Sun";
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            default:
                return "ERROR";
        }
    }

    public String getDayOfMonthString() {
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return Integer.toString(dayOfMonth);
    }

    public String getEnglishMonthNameString() {
        int monthNo = calendar.get(Calendar.MONTH) + 1;

        return convertMonthNumberToEnglishMonthNameString(monthNo);
    }

    public String getEnglishMonth3LetterString() {
        int monthNo = calendar.get(Calendar.MONTH);

        switch (monthNo) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "ERROR";
        }
    }

    public String getMonthNumberString() {
        //Months are 0-indexed so add 1 to get the correct number
        int monthNo = calendar.get(Calendar.MONTH) + 1;

        if (monthNo < 10) {
            return "0" + monthNo;
        }

        return Integer.toString(monthNo);
    }

    public String getYearString() {
        int year = calendar.get(Calendar.YEAR);
        return Integer.toString(year);
    }

    public String getHourOfDayString() {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay < 10) {
            return "0" + hourOfDay;
        }

        return Integer.toString(hourOfDay);
    }

    public String getMinuteString() {
        int minute = calendar.get(Calendar.MINUTE);

        if (minute < 10){
            return "0" + minute;
        }

        return Integer.toString(minute);
    }

    public static String convertMonthNumberToEnglishMonthNameString(int monthNo) {
        switch (monthNo) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "ERROR";
        }
    }
}
