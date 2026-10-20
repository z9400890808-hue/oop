package roadaccident.model;

public class Registration {

    private String fullName;
    private String phoneNumber;
    private String password;
    private String role;
    private String zone;

    
    private String drivingLicenceNumber;
    private String ambulanceNumber;

     
    private String hospitalName;
    private String designation;

    
    private String policeStation;
    private String badgeNumber;

    public Registration(
            String fullName,
            String phoneNumber,
            String password,
            String role,
            String zone,
            String drivingLicenceNumber,
            String ambulanceNumber,
            String hospitalName,
            String designation,
            String policeStation,
            String badgeNumber) {

        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.zone = zone;
        this.drivingLicenceNumber = drivingLicenceNumber;
        this.ambulanceNumber = ambulanceNumber;
        this.hospitalName = hospitalName;
        this.designation = designation;
        this.policeStation = policeStation;
        this.badgeNumber = badgeNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getZone() {
        return zone;
    }

    public String getDrivingLicenceNumber() {
        return drivingLicenceNumber;
    }

    public String getAmbulanceNumber() {
        return ambulanceNumber;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDesignation() {
        return designation;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }
}