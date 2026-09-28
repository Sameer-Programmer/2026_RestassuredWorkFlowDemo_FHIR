package api.payload;

public class PatientData {

    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String birthDate;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String payload;

    public PatientData(String firstName, String lastName, String phone,
                       String gender, String birthDate, String street,
                       String city, String state, String postalCode,
                       String payload) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = birthDate;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.payload = payload;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPayload() {
        return payload;
    }
}