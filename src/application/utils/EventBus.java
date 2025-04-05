package application.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple event bus implementation to propagate events across the application
 */
public class EventBus {
    private static EventBus instance;
    private Map<Class<?>, List<EventListener<?>>> listeners = new HashMap<>();
    
    private EventBus() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance
     * 
     * @return The EventBus instance
     */
    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
    
    /**
     * Register a listener for an event type
     * 
     * @param <T> The event type
     * @param eventType The class of the event
     * @param listener The listener to register
     */
    public <T> void register(Class<T> eventType, EventListener<T> listener) {
        List<EventListener<?>> eventListeners = listeners.computeIfAbsent(eventType, k -> new ArrayList<>());
        eventListeners.add(listener);
    }
    
    /**
     * Unregister a listener for an event type
     * 
     * @param <T> The event type
     * @param eventType The class of the event
     * @param listener The listener to unregister
     */
    public <T> void unregister(Class<T> eventType, EventListener<T> listener) {
        List<EventListener<?>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }
    
    /**
     * Post an event to all registered listeners
     * 
     * @param <T> The event type
     * @param event The event to post
     */
    @SuppressWarnings("unchecked")
    public <T> void post(T event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<?> listener : eventListeners) {
                ((EventListener<T>) listener).onEvent(event);
            }
        }
    }
    
    /**
     * Interface for event listeners
     * 
     * @param <T> The event type
     */
    public interface EventListener<T> {
        void onEvent(T event);
    }
}