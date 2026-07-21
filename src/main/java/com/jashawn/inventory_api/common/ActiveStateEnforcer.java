package com.jashawn.inventory_api.common;

import com.jashawn.inventory_api.Exceptions.InvalidStateException;

public interface ActiveStateEnforcer {
    boolean isActive();

    default void enforceActiveState(String resourceName) {
        if (!isActive()) {
            throw new InvalidStateException(resourceName, "", "inactive");
        }
    }
}
