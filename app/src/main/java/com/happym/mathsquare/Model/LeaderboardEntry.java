package com.happym.mathsquare.Model;
public class LeaderboardEntry {
    public String firstName;
    public String lastName;
    public String section;
    public String grade;
    private int points;

    public LeaderboardEntry() {} // empty constructor for Firestore

    public LeaderboardEntry(String firstName, String lastName, String section, String grade, int points) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.section = section;
        this.grade = grade;
        this.points = points;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSection() { return section; }
    public String getGrade() { return grade; }
    public int getPoints() { return points; }
}