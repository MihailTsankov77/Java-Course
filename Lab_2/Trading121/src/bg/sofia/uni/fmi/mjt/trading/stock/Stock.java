package bg.sofia.uni.fmi.mjt.trading.stock;

import bg.sofia.uni.fmi.mjt.trading.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

class Stock implements StockPurchase {
    private final int quantity;
    private final LocalDateTime purchaseTimestamp;
    private final double purchasePricePerUnit;
    private final double totalPurchasePrice;
    private final String ticker;

    Stock(String ticker, int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        this.ticker = ticker;
        this.quantity =quantity;
        this.purchaseTimestamp = purchaseTimestamp;

        this.purchasePricePerUnit = Utils.format(purchasePricePerUnit);
        this.totalPurchasePrice = Utils.format(quantity*purchasePricePerUnit);
    }
    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    @Override
    public double getPurchasePricePerUnit() {
        return purchasePricePerUnit;
    }

    @Override
    public double getTotalPurchasePrice() {
        return totalPurchasePrice;
    }

    @Override
    public String getStockTicker() {
        return ticker;
    }
}
