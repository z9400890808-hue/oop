package roadaccident.model;

public class AccidentAlert {

    private final int accidentId;
    private final String location;
    private final String reporterPhone;
    private final String zone;
    private final String severity;
    private final int victimCount;
    private final String description;
    private final String status;
    private final String pickupTime;

    public AccidentAlert(
            int accidentId,
            String location,
            String reporterPhone,
            String zone,
            String severity,
            int victimCount,
            String description,
            String status,
            String pickupTime) {

        this.accidentId = accidentId;
        this.location = location;
        this.reporterPhone = reporterPhone;
        this.zone = zone;
        this.severity = severity;
        this.victimCount = victimCount;
        this.description = description;
        this.status = status;
        this.pickupTime = pickupTime;
    }

    public int getAccidentId() {
        return accidentId;
    }

    public String getLocation() {
        return location;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }

    public String getZone() {
        return zone;
    }

    public String getSeverity() {
        return severity;
    }

    public int getVictimCount() {
        return victimCount;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPickupTime() {
        return pickupTime;
    }
}