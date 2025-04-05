package application.utils;

/**
 * Event indicating that data has changed in the application
 */
public class DataChangedEvent {
    private long timestamp;
    
    public DataChangedEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}