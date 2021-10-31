package com.holarin.monefy;

import java.util.Calendar;
import java.util.Date;


public class Flow {
    public double money;
    public Calendar calendar;
    public String description;

    public Flow(double money, Calendar calendar, String description) {
        this.money = money;
        this.calendar = calendar;
        this.description = description;
    }
}
