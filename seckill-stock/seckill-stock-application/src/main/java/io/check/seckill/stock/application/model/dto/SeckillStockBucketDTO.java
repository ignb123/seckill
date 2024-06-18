package io.check.seckill.stock.application.model.dto;

import io.check.seckill.common.model.dto.stock.SeckillStockDTO;
import io.check.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.io.Serializable;
import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 库存DTO
 */
public class SeckillStockBucketDTO extends SeckillStockDTO {

    private static final long serialVersionUID = 2704697441525819036L;

    //分桶数量
    private Integer bucketsQuantity;

    //库存分桶信息
    private List<SeckillStockBucket> buckets;

    public SeckillStockBucketDTO() {
    }

    public SeckillStockBucketDTO(Integer totalStock, Integer availableStock, List<SeckillStockBucket> buckets) {
        super(totalStock, availableStock);
        this.buckets = buckets;
        this.bucketsQuantity = buckets.size();
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
