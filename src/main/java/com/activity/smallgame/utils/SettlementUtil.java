package com.activity.smallgame.utils;

import java.math.BigDecimal;

/**
 * SMO币计算工具
 */
public class SettlementUtil {

    /** 结算标准 */
    private static final Long SETTLEMENT_STANDARD = 42949672960L;

    /**
     * 根据工作证明结算SMO币
     * @param proof
     * @return
     */
    public static Double settleByProof(Long proof, double multiple) {
        BigDecimal var1 = new BigDecimal(proof);
        BigDecimal var2 = new BigDecimal(SETTLEMENT_STANDARD);
        BigDecimal var3 = var1.divide(var2);
        var3 = var3.setScale(8, BigDecimal.ROUND_HALF_UP);
        if (multiple > 0) {
            return var3.doubleValue() * multiple;
        }
        return var3.doubleValue();
    }
}
