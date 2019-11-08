package com.activity.smallgame.model.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 矿区对象
 * @author: Mr.dong
 * @create: 2019-08-20 10:25
 **/
@Data
@Document(collection = "mining_area")
@CompoundIndexes({
        @CompoundIndex(name = "idx_areaCode", def = "{'areaCode': 1}")
})
public class MiningArea {

    /**
     * 矿区名称
     */
    private String areaName;

    /**
     * 矿区编码
     */
    private Long areaCode;

    /**
     * 矿总量
     */
    private Double total;

    /**
     * 矿余量
     */
    private Double surplus;

    /**
     * 刷新矿量日期（每月几号：比如30，就是每月30号刷新）
     */
    private int refreshDate;

    public MiningArea() {
    }

    public MiningArea(String areaName, Long areaCode, Double total, Double surplus, int refreshDate) {
        this.areaName = areaName;
        this.areaCode = areaCode;
        this.total = total;
        this.surplus = surplus;
        this.refreshDate = refreshDate;
    }
}
