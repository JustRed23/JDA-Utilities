package dev.JustRed23.jdautils.data;

import java.util.LinkedHashMap;
import java.util.Map;

class DBCache extends LinkedHashMap<String, String> {
    private final int maxSize;

    DBCache(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        return size() > maxSize;
    }
}
