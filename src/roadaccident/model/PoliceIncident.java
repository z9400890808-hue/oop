package roadaccident.model;

public class PoliceIncident {

    private final int accidentId;
    private final String reporterName;
    private final String reporterPhone;
    private final String accidentDate;
    private final String accidentTime;
    private final String location;
    private final String accidentType;
    private final String severity;
    private final int victimCount;
    private final String description;
    private final String status;
    private final String reportedAt;
    private final String pickupTime;
    private final String driverName;
    private final String ambulanceNumber;
    private final String hospitalName;
    private final String hospitalPhone;

    public PoliceIncident(
            int accidentId,
            String reporterName,
            String reporterPhone,
            String accidentDate,
            String accidentTime,
            String location,
            String accidentType,
            String severity,
            int victimCount,
            String description,
            String status,
            String reportedAt,
            String pickupTime,
            String driverName,
            String ambulanceNumber,
            String hospitalName,
            String hospitalPhone) {
        this.accidentId = accidentId;
        this.reporterName = reporterName;
        this.reporterPhone = reporterPhone;
        this.accidentDate = accidentDate;
        this.accidentTime = accidentTime;
        this.location = location;
        this.accidentType = accidentType;
        this.severity = severity;
        this.victimCount = victimCount;
        this.description = description;
        this.status = status;
        this.reportedAt = reportedAt;
        this.pickupTime = pickupTime;
        this.driverName = driverName;
        this.ambulanceNumber = ambulanceNumber;
        this.hospitalName = hospitalName;
        this.hospitalPhone = hospitalPhone;
    }

    public int getAccidentId() { return accidentId; }
    public String getReporterName() { return reporterName; }
    public String getReporterPhone() { return reporterPhone; }
    public String getAccidentDate() { return accidentDate; }
    public String getAccidentTime() { return accidentTime; }
    public String getLocation() { return location; }
    public String getAccidentType() { return accidentType; }
    public String getSeverity() { return severity; }
    public int getVictimCount() { return victimCount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getReportedAt() { return reportedAt; }
    public String getPickupTime() { return pickupTime; }
    public String getDriverName() { return driverName; }
    public String getAmbulanceNumber() { return ambulanceNumber; }
    public String getHospitalName() { return hospitalName; }
    public String getHospitalPhone() { return hospitalPhone; }
}
