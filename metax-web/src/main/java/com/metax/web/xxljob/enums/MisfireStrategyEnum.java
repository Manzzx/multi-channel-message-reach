package com.metax.web.xxljob.enums;

/**
 * 调度过期策略
 *
 * @author hanabi
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING,

    /**
     * fire once now
     */
    FIRE_ONCE_NOW;

    MisfireStrategyEnum() {
    }
}
