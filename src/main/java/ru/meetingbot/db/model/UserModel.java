package ru.meetingbot.db.model;

public class UserModel {

    private long id;
    private String userName;
    private String fullName;
    private String profileLink;
    private String job;
    private String hobbie;
    private Short age;

    public UserModel(long id, String userName) {
        this.id = id;
        this.userName = userName;
        this.fullName = "";
        this.profileLink = "";
        this.job = "";
        this.hobbie = "";
        this.age = 0;
    }

    public UserModel(long id, String userName, String fullName, String profileLink, String job, String hobbie, int age) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.profileLink = profileLink;
        this.job = job;
        this.hobbie = hobbie;
        this.age = (short) age;
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

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
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
                ", age=" + age +
                '}';
    }
}
