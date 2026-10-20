package roadaccident.model;

public class Accident {

    private int id;
    private String name;
    private String phone;
    private String date;
    private String time;
    private String location;
    private String accidentType;
    private String severity;
    private int numberOfVictims;
    private String description;
    private String status;

    // Constructor used when creating a new accident
    public Accident(
            String name,
            String phone,
            String date,
            String time,
            String location,
            String accidentType,
            String severity,
            int numberOfVictims,
            String description) {

        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.location = location;
        this.accidentType = accidentType;
        this.severity = severity;
        this.numberOfVictims = numberOfVictims;
        this.description = description;
    }

    // Constructor used when reading data from database
    public Accident(
            int id,
            String name,
            String phone,
            String date,
            String time,
            String location,
            String accidentType,
            String severity,
            int numberOfVictims,
            String description,
            String status) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.location = location;
        this.accidentType = accidentType;
        this.severity = severity;
        this.numberOfVictims = numberOfVictims;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getAccidentType() {
        return accidentType;
    }

    public String getSeverity() {
        return severity;
    }

    public int getNumberOfVictims() {
        return numberOfVictims;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
}