package ru.vorobyov;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Main {
    public static void main(String[] args) {
        double minTempDiff = Double.MAX_VALUE;
        long maxTimeDiff = Long.MIN_VALUE;
        JSONObject dayWithMinTempDif = null;
        JSONObject dayWithMaxTimeDif = null;
        Date date = getDate();
        long unixTime = date.getTime() / 1000;
        for (int i = 0; i < 5; i++){
            JSONObject jsonObject = getObjectByUnixTime(unixTime);
            assert jsonObject != null;
            double differenceTemp = countDifference(jsonObject.getDouble("feels_like"), jsonObject.getDouble("temp"));
            if (differenceTemp < minTempDiff){
                dayWithMinTempDif = jsonObject;
                minTempDiff = differenceTemp;
            }
            long differenceTime = countDifference(jsonObject.getLong("sunrise"), jsonObject.getLong("sunset"));
            if (differenceTime > maxTimeDiff){
                dayWithMaxTimeDif = jsonObject;
                maxTimeDiff = differenceTime;
            }
            unixTime -= 3600 * 24;
        }
        assert dayWithMinTempDif != null;
        printDayWithMinTempDif(dayWithMinTempDif, minTempDiff);
        assert dayWithMaxTimeDif != null;
        printDayWithMaxTimeDif(dayWithMaxTimeDif, maxTimeDiff);
    }

    private static double countDifference(double x, double y){
        return Math.abs(x - y);
    }

    private static long countDifference(long x, long y){
        return Math.abs(x - y);
    }

    private static Date getDate(){
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    private static JSONObject getObjectByUnixTime(long unixTime){
        try {
            return new JsonReader()
                    .readJsonFromUrl("https://api.openweathermap.org/data/2.5/onecall/timemachine?lat="
                            + "59.917857350000006"
                            + "&lon="
                            + "30.380619357025516"
                            + "&dt="
                            + unixTime
                            + "&appid="
                            + "c4a479a2594da4725b8930b3704ef1ed")
                    .getJSONObject("current");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void printDayWithMinTempDif(JSONObject dayWithMinTempDif, double minTempDiff){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println("День с минимальной разницей ощущаемой и фактической температурой за ближайшие 5 дней: \n"
                + "Дата: " + formatter.format(new Date(dayWithMinTempDif.getLong("dt") * 1000)) + "\n"
                + "Разница: " + minTempDiff);
    }

    private static void printDayWithMaxTimeDif(JSONObject dayWithMaxTimeDif, long maxTimeDiff){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println("День с максимальной продолжительностью светового дня за ближайшие 5 дней: \n"
                + "Дата: " + formatter.format(new Date(dayWithMaxTimeDif.getLong("dt") * 1000)) + "\n"
                + "Разница: " + maxTimeDiff);
    }
}
