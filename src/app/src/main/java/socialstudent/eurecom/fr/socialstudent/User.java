package socialstudent.eurecom.fr.socialstudent;

public class User {
    private String key ;
    private String name;
    private String email;
    private String gender;
    private String birthdate;
    private String desc;
    private int residence;
    private String country ;
    private String phone ;
    private String interrests ;
    private String langages;
    private String studies;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public User(){

    }

    public User(String key,String name, String email, String gender, String birthdate, String desc, int residence, String country, String phone, String interrests, String langages, String studies) {
        this.key=key;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birthdate = birthdate;
        this.desc = desc;
        this.residence = residence;
        this.country = country;
        this.phone = phone;
        this.interrests = interrests;
        this.langages = langages;
        this.studies = studies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getResidence() {
        return residence;
    }

    public void setResidence(int residence) {
        this.residence = residence;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getInterrests() {
        return interrests;
    }

    public void setInterrests(String interrests) {
        this.interrests = interrests;
    }

    public String getLangages() {
        return langages;
    }

    public void setLangages(String langages) {
        this.langages = langages;
    }

    public String getStudies() {
        return studies;
    }

    public void setStudies(String studies) {
        this.studies = studies;
    }
}
