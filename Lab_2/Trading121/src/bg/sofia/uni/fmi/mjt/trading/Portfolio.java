package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class Portfolio implements PortfolioAPI {
    private final String owner;
    private final PriceChartAPI priceChart;
    private double budget;
    private final int maxSize;
    private int currentStockIndex = 0;
    private final StockPurchase[] stockPurchases;


    Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
        this.maxSize = maxSize;
        this.stockPurchases = new StockPurchase[maxSize];
    }

    Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget, int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
        this.maxSize = maxSize;

        this.stockPurchases = new StockPurchase[maxSize];

        currentStockIndex =Math.min(stockPurchases.length, maxSize);

        System.arraycopy(stockPurchases, 0, this.stockPurchases, 0,  Math.min(stockPurchases.length,maxSize));
    }

    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if(quantity<0 || maxSize<=currentStockIndex || stockTicker==null) {
            return null;
        }

        double price= priceChart.getCurrentPrice(stockTicker);
        double neededMoney = price*quantity;

        if(neededMoney>budget){
            return null;
        }

        budget-=neededMoney;

        StockPurchase newStockPurchase =  switch (stockTicker){
            case "GOOG" -> new GoogleStockPurchase(quantity, LocalDateTime.now(), price);
            case "MSFT"-> new MicrosoftStockPurchase(quantity, LocalDateTime.now(), price);
            case "AMZ"-> new AmazonStockPurchase(quantity, LocalDateTime.now(), price);
            default -> null;
        };

        if(newStockPurchase==null){
            return null;
        }

        stockPurchases[currentStockIndex++] = newStockPurchase;
        priceChart.changeStockPrice(stockTicker,5);

        return newStockPurchase;
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        return stockPurchases;
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {

        StockPurchase[]  filteredStockPurchases = new StockPurchase[maxSize];
        int index=0;
        for (int i = 0; i<currentStockIndex;++i){
            if(stockPurchases[i].getPurchaseTimestamp().isAfter(startTimestamp)
                    && stockPurchases[i].getPurchaseTimestamp().isBefore(endTimestamp)){
                filteredStockPurchases[index++] = stockPurchases[i];
            }
        }

        StockPurchase[] smallArr = new StockPurchase[index];

        System.arraycopy(filteredStockPurchases, 0, smallArr, 0, index);

        return smallArr;
    }

    @Override
    public double getNetWorth() {
        double netWorth = 0;

        for (int i = 0; i<currentStockIndex; ++i) {
            netWorth+= stockPurchases[i].getQuantity()* priceChart.getCurrentPrice(stockPurchases[i].getStockTicker());
        }

        return  Utils.format(netWorth);
    }

    @Override
    public double getRemainingBudget() {
        return  Utils.format(budget);
    }

    @Override
    public String getOwner() {
        return owner;
    }
}
