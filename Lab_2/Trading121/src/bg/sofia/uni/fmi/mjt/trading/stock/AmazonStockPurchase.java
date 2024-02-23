package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public class AmazonStockPurchase extends Stock{
    public AmazonStockPurchase( int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit) {
        super("AMZ", quantity, purchaseTimestamp, purchasePricePerUnit);
    }
}
