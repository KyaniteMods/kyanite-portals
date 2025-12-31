package dev.kyanitemods.kyaniteportals.content.portalactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionExecutionData {
    private final Map<String, List<Object>> map;

    public ActionExecutionData() {
        this.map = new HashMap<>();
    }

    public <V> V put(String key, V value) {
        V previousValue = this.get(key);
        if (previousValue != null) {
            map.get(key).remove(previousValue);
        }

        if (map.containsKey(key)) {
            map.get(key).add(value);
            return value;
        }
        List<Object> list = new ArrayList<>();
        list.add(value);
        map.put(key, list);
        return previousValue;
    }

    public <V> V get(String key) {
        if (!map.containsKey(key)) return null;
        for (Object object : map.get(key)) {
            try {
                return (V) object;
            } catch (Exception ignored) {}
        }
        return null;
    }
}
