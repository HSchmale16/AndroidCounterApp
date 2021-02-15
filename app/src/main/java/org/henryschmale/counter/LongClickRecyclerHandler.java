package org.henryschmale.counter;

import androidx.lifecycle.LifecycleOwner;

/**
 * Some stupid interface I made for an adapter to share that extends
 * lifecycle owner, and pushed stuff into the activity.
 *
 * @author hschmale
 */
public interface LongClickRecyclerHandler extends LifecycleOwner {
    /**
     *
     * @param id an item id to use to send to an activity on start
     */
    void handleLongClick(int id);
}
