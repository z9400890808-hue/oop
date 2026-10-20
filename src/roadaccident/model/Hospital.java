package roadaccident.model;

public class Hospital {

    private final int hospitalId;
    private final String hospitalName;
    private final String zone;
    private final int availableBeds;
    private final String phoneNumber;

    public Hospital(
            int hospitalId,
            String hospitalName,
            String zone,
            int availableBeds,
            String phoneNumber) {

        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.zone = zone;
        this.availableBeds = availableBeds;
        this.phoneNumber = phoneNumber;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getZone() {
        return zone;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}