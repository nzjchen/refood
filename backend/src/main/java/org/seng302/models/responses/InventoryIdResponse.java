package org.seng302.models.responses;

import lombok.Data;
import org.seng302.models.Product;
import java.util.Date;
/**
 * A simple DTO which holds the inventory id to transfer to frontend.
 */
@Data
public class InventoryIdResponse {

    private long inventoryId;
    private Product product;
    private int quantity;
    private double pricePerItem;
    private double totalPrice;
    private Date manufactured;
    private Date sellBy;
    private Date bestBefore;
    private Date expires;

    public InventoryIdResponse(long inventoryId, Product product, int quantity, double pricePerItem, double totalPrice, Date manufactured, Date sellBy, Date bestBefore, Date expires) {
        this.inventoryId = inventoryId;
        this.product = product;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalPrice = totalPrice;
        this.manufactured = manufactured;
        this.sellBy = sellBy;
        this.bestBefore = bestBefore;
        this.expires = expires;
    }

    public InventoryIdResponse(Long inventoryId){
        this.inventoryId = inventoryId;
    }

}
