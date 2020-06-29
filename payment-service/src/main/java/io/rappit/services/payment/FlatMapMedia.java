package io.rappit.services.payment;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FlatMapMedia implements Media {
    private final Map<String, Object> map;
    private final String prefix;

    private FlatMapMedia(Map<String, Object> map, String prefix) {
        this.map = map;
        this.prefix = prefix;
    }

    public FlatMapMedia() {
        this(new HashMap<>(), "");
    }

    @Override
    public Media with(String name, Printable value) {
        final Media media = new FlatMapMedia(map, prefix + name + "_");
        if (value != null) {
            value.print(media);
        }
        return this;
    }

    @Override
    public Media with(String name, Number value) {
        map.put(prefix + name, value);
        return this;
    }

    @Override
    public Media with(String name, Boolean value) {
        map.put(prefix + name, value);
        return this;
    }

    @Override
    public Media with(String name, String value) {
        map.put(prefix + name, value);
        return this;
    }

    @Override
    public Media with(String name, Collection collection) {
        if (collection != null && !collection.isEmpty()) {
            Collection items = new ArrayList(collection.size());
            collection.forEach(item -> {
                if (item instanceof Printable) {
                    final FlatMapMedia flatMapMedia = new FlatMapMedia();
                    ((Printable) item).print(flatMapMedia);
                    items.add(flatMapMedia.toPrintable());
                } else {
                    items.add(item);
                }
            });
            map.put(prefix + name, items);
        }
        return this;
    }

    public Map<String, Object> toMap() {
        return map;
    }

    public Printable toPrintable() {
        return media -> map.forEach(
                (key, value) -> {
                    if (value instanceof Collection) {
                        media.with(key, (Collection) value);
                    } else {
                        media.with(key, value.toString());
                    }
                }
        );
    }
}
