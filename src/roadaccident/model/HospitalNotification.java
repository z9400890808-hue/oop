package roadaccident.model;

public class HospitalNotification {

    private final int notificationId;
    private final int accidentId;
    private final String location;
    private final String severity;
    private final int victimCount;
    private final String pickupTime;
    private final String driverName;
    private final String message;
    private final String notificationStatus;

    public HospitalNotification(
            int notificationId,
            int accidentId,
            String location,
            String severity,
            int victimCount,
            String pickupTime,
            String driverName,
            String message,
            String notificationStatus) {

        this.notificationId = notificationId;
        this.accidentId = accidentId;
        this.location = location;
        this.severity = severity;
        this.victimCount = victimCount;
        this.pickupTime = pickupTime;
        this.driverName = driverName;
        this.message = message;
        this.notificationStatus = notificationStatus;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getAccidentId() {
        return accidentId;
    }

    public String getLocation() {
        return location;
    }

    public String getSeverity() {
        return severity;
    }

    public int getVictimCount() {
        return victimCount;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getMessage() {
        return message;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }
}