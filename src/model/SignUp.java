package model;

public class SignUp {
    private String name;
    private String username;
    private String email;
    private String course;
    private String year;
    private String password;

    public SignUp(String name, String username, String email, String course, String year, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.course = course;
        this.year = year;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public String getYear() {
        return year;
    }

    public String getPassword() {
        return password;
    }
}
