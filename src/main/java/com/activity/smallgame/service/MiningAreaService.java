package com.activity.smallgame.service;

import com.activity.smallgame.constant.MiningConstant;
import com.activity.smallgame.model.mongodb.MiningArea;
import com.activity.smallgame.socket.NettyWebSocket;
import com.activity.smallgame.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: Mr.dong
 * @create: 2019-08-23 15:38
 **/
@Service
public class MiningAreaService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NettyWebSocket nettyWebSocket;

    public void initMiningArea() {
        List<MiningArea> miningAreas = mongoTemplate.find(new Query(), MiningArea.class);
        if (!CollectionUtils.isEmpty(miningAreas)) {
            return;
        }
        MiningArea miningArea = new MiningArea("GigaWatt矿场", IdGenerator.getInstance().nextId(), 200D, 200D, 1);
        mongoTemplate.save(miningArea);
        miningArea = new MiningArea("Genesis矿场", IdGenerator.getInstance().nextId(), 200D, 200D, 7);
        mongoTemplate.save(miningArea);
        miningArea = new MiningArea("Guido矿场", IdGenerator.getInstance().nextId(), 200D, 200D, 14);
        mongoTemplate.save(miningArea);
        miningArea = new MiningArea("Rudolphi矿场", IdGenerator.getInstance().nextId(), 200D, 200D, 21);
        mongoTemplate.save(miningArea);
        miningArea = new MiningArea("Linthal矿场", IdGenerator.getInstance().nextId(), 200D, 200D, 30);
        mongoTemplate.save(miningArea);
    }

    public List<MiningArea> getMiningAreaList() {
        return mongoTemplate.find(new Query(), MiningArea.class);
    }

    public void changeMiningArea(String userCode, Long areaCode) {
        MiningArea miningArea = mongoTemplate.findOne(new Query(Criteria.where("areaCode").is(areaCode)), MiningArea.class);
        if (miningArea != null) {
            MiningConstant.miningAreaMap.put(userCode, miningArea);
            nettyWebSocket.changeArea(userCode);
        }
    }

    public Boolean checkMiningAreaIsInit() {
        List<MiningArea> miningAreas = mongoTemplate.find(new Query(), MiningArea.class);
        if (!CollectionUtils.isEmpty(miningAreas)) {
            return true;
        }
        return false;
    }

    public MiningArea getMiningAreaByAreaCode(Long areaCode) {
        return mongoTemplate.findOne(new Query(Criteria.where("areaCode").is(areaCode)), MiningArea.class);
    }

    public MiningArea getFirstMiningArea() {
        List<MiningArea> miningAreas = mongoTemplate.find(new Query(), MiningArea.class);
        if (CollectionUtils.isEmpty(miningAreas)) {
            return null;
        }
        return miningAreas.get(0);
    }

    public void updateMiningArea(MiningArea miningArea) {
        mongoTemplate.updateFirst(new Query(Criteria.where("areaCode").is(miningArea.getAreaCode())), new Update().set("surplus", miningArea.getSurplus()), MiningArea.class);
    }
}
