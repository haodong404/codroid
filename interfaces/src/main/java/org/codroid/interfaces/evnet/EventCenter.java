/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.interfaces.evnet;

import androidx.annotation.NonNull;

import org.codroid.interfaces.evnet.editor.DirTreeItemLoadEvent;
import org.codroid.interfaces.evnet.editor.SelectionChangedEvent;
import org.codroid.interfaces.evnet.editor.TextChangedEvent;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * This file is Event Center, all of the events in Codroid are managed by it.
 * An Event should be registered at the property file (addon-des.toml) first.
 * And then, it will be loaded after it's host addon loaded.
 * <p>
 * Every registered event is stored in an enum map, which is a static instance.
 * And the events will be triggered when needed.
 */
public final class EventCenter {
 
    /**
     * This enum class contains all the events in Codroid.
     * It must be registered in this class before adding a new event.
     */
    public enum EventsEnum {
        ADDON_IMPORT(AddonImportEvent.class),

        EDITOR_SELECTION_CHANGED(SelectionChangedEvent.class),
        EDITOR_TEXT_CHANGED(TextChangedEvent.class),

        PROJECT_STRUCT_ITEM_LOAD(DirTreeItemLoadEvent.class);

        private final Class<?> clazz;

        EventsEnum(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public static EventsEnum getEnumByClass(Class<?> clazz) {
            for (var i : values()) {
                if (i.getClazz() == clazz) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Class: " + clazz.getName() + " is not an event enum.");
        }

        @Override
        public String toString() {
            return "EventsEnum{" +
                    "clazz=" + clazz +
                    '}';
        }

    }

    // It stores the events registered.
    private static EnumMap<EventsEnum, LinkedList<Event>> registeredEvents;

    public EventCenter() {
        if (registeredEvents == null) {
            registeredEvents = new EnumMap<>(EventsEnum.class);
        }
    }

    /**
     * Register a new Event.
     *
     * @param type  What the event type is
     * @param event new event
     */
    public void register(EventsEnum type, Event event) {
        if (!registeredEvents.containsKey(type)) {
            LinkedList<Event> linkedList = new LinkedList<>();
            linkedList.add(event);
            registeredEvents.put(type, linkedList);
        } else {
            var list = registeredEvents.get(type);
            list.add(event);
        }
    }


    /**
     * Execute the events
     *
     * @param eventsEnum What the event type you want to trigger.
     * @param <T>        Class type
     * @return a list contains addons of the same type.
     */
    @NonNull
    public <T extends Event> LinkedList<T> execute(EventsEnum eventsEnum) {
        var temp = registeredEvents.get(eventsEnum);
        if (temp == null) {
            temp = new LinkedList<>();
        }
        return (LinkedList<T>) temp;
    }

    /**
     * Execute the events by parallel stream.
     *
     * @param eventsEnum What the event type you want to trigger.
     * @param <T>        Class type
     * @return a stream contains addons of the same type.
     */
    @NonNull
    public <T extends Event> Stream<T> executeStream(EventsEnum eventsEnum) {
        return execute(eventsEnum).stream().parallel().map(event -> (T) event);
    }

    /**
     * Whether a giving class is an addon event.
     *
     * @param clazz what class you want to check.
     * @return true if it is.
     */
    public boolean isAnAddonEvent(Class<?> clazz) {
        for (var i : EventsEnum.values()) {
            if (i.getClazz() == clazz) {
                return true;
            }
        }
        return false;
    }

    public int eventCount() {
        return registeredEvents.size();
    }
}