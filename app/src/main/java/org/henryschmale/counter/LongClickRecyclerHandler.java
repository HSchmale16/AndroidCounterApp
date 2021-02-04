package org.henryschmale.counter;

import androidx.lifecycle.LifecycleOwner;

public interface LongClickRecyclerHandler extends LifecycleOwner {
    /**
     *
     * @param id an item id to use to send to an activity on start
     */
    void handleLongClick(int id);
}
