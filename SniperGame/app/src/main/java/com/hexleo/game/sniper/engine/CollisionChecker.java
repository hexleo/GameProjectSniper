package com.hexleo.game.sniper.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hexleo on 2017/8/20.
 */

public class CollisionChecker {

    private Map<Long, Long> mChecked = new HashMap<>();


    public CollisionChecker() {
    }

    public void check(List<Spirit> spiritList) {
        int size = spiritList.size();
        for (int i = 0; i< size-1; i++) {
            for (int j=i+1; j<size; j++) {
                check(spiritList.get(i), spiritList.get(j));
            }
        }
        mChecked.clear();
    }

    private void check(Spirit a, Spirit b) {
        if (isChecked(a.getId(), b.getId()) || isChecked(a.getId(), b.getId())) {
            return;
        }
        if (a.checkCollisionWith(b) || b.checkCollisionWith(a)) {
            a.addCollision(b);
            b.addCollision(a);
            mChecked.put(a.getId(), b.getId());
        }
    }

    private boolean isChecked(long a, long b) {
        if (mChecked.containsKey(a) && mChecked.get(a) == b) {
            return true;
        }
        return false;
    }

}
