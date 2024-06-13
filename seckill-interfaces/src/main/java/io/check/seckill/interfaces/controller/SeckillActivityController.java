package io.check.seckill.interfaces.controller;

import io.check.seckill.application.command.SeckillActivityCommand;
import io.check.seckill.application.service.SeckillActivityService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.model.dto.SeckillActivityDTO;
import io.check.seckill.domain.model.entity.SeckillActivity;
import io.check.seckill.domain.response.ResponseMessage;
import io.check.seckill.domain.response.ResponseMessageBuilder;
import io.check.seckill.infrastructure.utils.date.JodaDateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/activity")
public class SeckillActivityController {
    @Autowired
    private SeckillActivityService seckillActivityService;
    /**
     * 保存秒杀活动
     */
    @RequestMapping(value = "/saveSeckillActivity", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> saveSeckillActivityDTO(SeckillActivityCommand seckillActivityCommand){
        seckillActivityService.saveSeckillActivity(seckillActivityCommand);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode());
    }

    /**
     * 根据状态获取秒杀活动列表
     */
    @RequestMapping(value = "/getSeckillActivityList", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillActivity>> getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityList(status));
    }

    /**
     * 获取秒杀活动列表
     */
    @RequestMapping(value = "/seckillActivityList", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillActivityDTO>> getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status,
                                                                            @RequestParam(value = "version", required = false) Long version){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityList(status, version));
    }

    /**
     * 根据时间和状态获取活动列表
     */
    @RequestMapping(value = "/getSeckillActivityListBetweenStartTimeAndEndTime", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillActivity>> getSeckillActivityListBetweenStartTimeAndEndTime(@RequestParam(value = "currentTime", required = false) String currentTime,
                                                                                                   @RequestParam(value = "status", required = false)Integer status){
        List<SeckillActivity> seckillActivityList = seckillActivityService.getSeckillActivityListBetweenStartTimeAndEndTime(JodaDateTimeUtils.parseStringToDate(currentTime, JodaDateTimeUtils.DATE_TIME_FORMAT), status);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillActivityList);
    }

    /**
     * 获取id获取秒杀活动详情
     */
    @RequestMapping(value = "/getSeckillActivityById", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillActivity> getSeckillActivityById(@RequestParam(value = "id", required = false) Long id){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityById(id));
    }

    /**
     * 根据id和版本号获取秒杀活动详情
     * @param id
     * @param version
     * @return
     */
    @RequestMapping(value = "/seckillActivity", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillActivityDTO> getSeckillActivityById(@RequestParam(value = "id", required = false) Long id,
                                                                      @RequestParam(value = "version", required = false) Long version){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivity(id, version));
    }

    /**
     * 更新活动的状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateStatus(@RequestParam(value = "status", required = false) Integer status,
                                                @RequestParam(value = "id", required = false) Long id){
        seckillActivityService.updateStatus(status, id);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode());
    }

}
