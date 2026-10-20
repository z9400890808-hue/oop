package roadaccident.model;

public class User {

    private final int userId;
    private final String fullName;
    private final String phoneNumber;
    private final String role;
    private final String zone;
    private final String verificationStatus;
    private final String hospitalName;

    public User(
            int userId,
            String fullName,
            String phoneNumber,
            String role,
            String zone,
            String verificationStatus,
            String hospitalName) {

        this.userId = userId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.zone = zone;
        this.verificationStatus = verificationStatus;
        this.hospitalName = hospitalName;

    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public String getZone() {
        return zone;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getHospitalName() {
        return hospitalName;
    }
}