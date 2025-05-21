package com.mycompany.prolab2_1;

import java.util.Calendar;
import java.util.Date;


public class HolidayManager 
{
    private static Date testDate = null;
    
    
    public static void simulateDate(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day); 
        testDate = cal.getTime();
        

        String ayAdi = "";
        switch(month) {
            case 1: ayAdi = "Ocak"; break;
            case 2: ayAdi = "Şubat"; break;
            case 3: ayAdi = "Mart"; break;
            case 4: ayAdi = "Nisan"; break;
            case 5: ayAdi = "Mayıs"; break;
            case 6: ayAdi = "Haziran"; break;
            case 7: ayAdi = "Temmuz"; break;
            case 8: ayAdi = "Ağustos"; break;
            case 9: ayAdi = "Eylül"; break;
            case 10: ayAdi = "Ekim"; break;
            case 11: ayAdi = "Kasım"; break;
            case 12: ayAdi = "Aralık"; break;
        }
        
        System.out.println("Simüle edilen tarih: " + day + " " + ayAdi + " " + year);
    }
    
    public static void resetDateSimulation() {
        testDate = null;
    }
    
    public static Date getCurrentDate() {
        return testDate != null ? testDate : new Date();
    }
    
    public static boolean isFreeTransportationDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1; 
        
        return (day == 1 && month == 1) ||    // 1 Ocak - Yılbaşı
               (day == 23 && month == 4) ||   // 23 Nisan - Ulusal Egemenlik ve Çocuk Bayramı
               (day == 1 && month == 5) ||    // 1 Mayıs - Emek ve Dayanışma Günü
               (day == 19 && month == 5) ||   // 19 Mayıs - Atatürk'ü Anma, Gençlik ve Spor Bayramı
               (day == 29 && month == 5) ||   // 29 Mayıs - İstanbul'un Fethi
               (day == 15 && month == 7) ||   // 15 Temmuz - Demokrasi ve Milli Birlik Günü
               (day == 30 && month == 8) ||   // 30 Ağustos - Zafer Bayramı
               (day == 6 && month == 10) ||   // 6 Ekim - İstanbul'un Kurtuluşu
               (day == 29 && month == 10);    // 29 Ekim - Cumhuriyet Bayramı
    }
    
    public static boolean isTodayFreeTransportationDay() {
        return isFreeTransportationDay(getCurrentDate());
    }
    
    public static double applyHolidayDiscount(double originalFare) {
        if (isTodayFreeTransportationDay()) {
            return 0.0;
        }
        return originalFare;
    }
    
    public static String getHolidayDiscountMessage() {
        if (isTodayFreeTransportationDay()) {
            return "Bayram günü - Ücretsiz toplu taşıma";
        }
        return "";
    }
    
    public static Date getTestDate() {
        return testDate;
    }
} 