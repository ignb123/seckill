package io.check.seckill.stock.application.model.command;

/**
 * @author check
 * @version 1.0.0
 * @description 库存分桶
 */
public class SeckillStockBucketWrapperCommand extends SeckillStockBucketGoodsCommand {
    private static final long serialVersionUID = 2920951547657301665L;

    //库存分桶信息
    private SeckillStockBucketCommand stockBucketCommand;

    public SeckillStockBucketWrapperCommand() {
    }

    public SeckillStockBucketWrapperCommand(Long userId, Long goodsId, SeckillStockBucketCommand stockBucketCommand) {
        super(userId, goodsId);
        this.stockBucketCommand = stockBucketCommand;
    }

    public SeckillStockBucketCommand getStockBucketCommand() {
        return stockBucketCommand;
    }

    public void setStockBucketCommand(SeckillStockBucketCommand stockBucketCommand) {
        this.stockBucketCommand = stockBucketCommand;
    }

    public boolean isEmpty(){
        return this.stockBucketCommand == null
                || super.isEmpty()
                || stockBucketCommand.isEmpty();
    }


}
