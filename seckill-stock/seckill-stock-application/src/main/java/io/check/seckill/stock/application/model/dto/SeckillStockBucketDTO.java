package io.check.seckill.stock.application.model.dto;

import io.check.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.io.Serializable;
import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 库存DTO
 */
public class SeckillStockBucketDTO implements Serializable {

    private static final long serialVersionUID = 6707252274621460974L;

    //库存总量
    private Integer totalStock;

    //可用库存量
    private Integer availableStock;

    //分桶数量
    private Integer bucketsQuantity;

    //库存分桶信息
    private List<SeckillStockBucket> buckets;

    public SeckillStockBucketDTO() {
    }

    public SeckillStockBucketDTO(Integer totalStock, Integer availableStock, List<SeckillStockBucket> buckets) {
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.buckets = buckets;
        this.bucketsQuantity = buckets.size();
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getBucketsQuantity() {
        return bucketsQuantity;
    }

    public void setBucketsQuantity(Integer bucketsQuantity) {
        this.bucketsQuantity = bucketsQuantity;
    }

    public List<SeckillStockBucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<SeckillStockBucket> buckets) {
        this.buckets = buckets;
    }
}
