package bg.sofia.uni.fmi.mjt.trading.price;

import bg.sofia.uni.fmi.mjt.trading.Utils;

import java.text.DecimalFormat;

public class PriceChart implements PriceChartAPI{
    private double microsoftStockPrice;
    private double googleStockPrice;
    private double amazonStockPrice;

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice){
        this.microsoftStockPrice = microsoftStockPrice;
        this.googleStockPrice = googleStockPrice;
        this.amazonStockPrice = amazonStockPrice;
    }


    @Override
    public double getCurrentPrice(String stockTicker) {
        if(stockTicker==null){
            return 0.00;
        }

        switch (stockTicker){
            case "GOOG": {
                return  Utils.format(googleStockPrice);
            }
            case "MSFT":{
                return  Utils.format(microsoftStockPrice);
            }
            case "AMZ":{
                return  Utils.format(amazonStockPrice);
            }
            default:{
                return 0.00;
            }
        }
    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {
        if(percentChange<0 || stockTicker==null){
            return false;
        }

        switch (stockTicker){
            case "GOOG": {
                googleStockPrice += (googleStockPrice* percentChange)/100;

                return true;
            }
            case "MSFT":{
                microsoftStockPrice += (microsoftStockPrice* percentChange)/100;

                return true;
            }
            case "AMZ":{
                amazonStockPrice += (amazonStockPrice* percentChange)/100;

                return true;
            }
            default:{
                return false;
            }
        }
    }
}
