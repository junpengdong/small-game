package com.activity.smallgame.controller;

import com.activity.smallgame.dto.ResultDto;
import com.activity.smallgame.enums.ResultEnum;
import com.activity.smallgame.exception.SmallGameException;
import com.activity.smallgame.model.mongodb.MiningArea;
import com.activity.smallgame.service.MiningAreaService;
import com.activity.smallgame.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Mr.dong
 * @create: 2019-08-23 15:56
 **/
@RestController
public class MiningAreaController {

    private Logger logger = LoggerFactory.getLogger(MiningAreaController.class);

    @Autowired
    private MiningAreaService miningAreaService;

    @GetMapping("/api/v1/mining/area/list")
    public ResultDto getMiningAreaList() {
        try {
            List<MiningArea> miningAreaList = miningAreaService.getMiningAreaList();
            return ResponseUtil.success(miningAreaList);
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("MiningAreaController getMiningAreaList error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
    }

    @PutMapping("/api/v1/mining/area")
    public ResultDto changeMiningArea(@RequestParam("userCode")String userCode,
                                      @RequestParam("areaCode")Long areaCode) {
        try {
            miningAreaService.changeMiningArea(userCode, areaCode);
            Map<String, Long> map = new HashMap<>();
            map.put("areaCode", areaCode);
            return ResponseUtil.success(map);
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("MiningAreaController changeMiningArea error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
    }

    @PostMapping("/api/v1/mining/area/init")
    public ResultDto initMiningArea() {
        try {
            miningAreaService.initMiningArea();
        }catch (SmallGameException e) {
            return ResponseUtil.error(e.getCode(), e.getMessage());
        }catch (Exception e) {
            logger.error("MiningAreaController initMiningArea error", e);
            return ResponseUtil.error(ResultEnum.INNER_ERROR.getCode(), ResultEnum.INNER_ERROR.getMsg());
        }
        return ResponseUtil.success();
    }
}
