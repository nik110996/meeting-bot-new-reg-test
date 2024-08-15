package ru.meetingbot.db.model;

public class UserModel {

    private long id;
    private String userName;
    private String fullName;
    private String profileLink;
    private String job;
    private String hobbie;
    private Short yearsOfExperience;
    private String location;

    public UserModel(long id, String userName) {
        this.id = id;
        this.userName = userName;
        this.fullName = "";
        this.profileLink = "";
        this.job = "";
        this.hobbie = "";
        this.yearsOfExperience = 0;
    }

    public UserModel(long id, String userName, String fullName, String profileLink, String job, String hobbie, int yearsOfExperience, String location) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.profileLink = profileLink;
        this.job = job;
        this.hobbie = hobbie;
        this.yearsOfExperience = (short) yearsOfExperience;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHobbie() {
        return hobbie;
    }

    public void setHobbie(String hobbie) {
        this.hobbie = hobbie;
    }

    public Short getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Short yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", profileLink='" + profileLink + '\'' +
                ", job='" + job + '\'' +
                ", hobbie='" + hobbie + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", location='" + location + '\'' +
                '}';
    }
}
