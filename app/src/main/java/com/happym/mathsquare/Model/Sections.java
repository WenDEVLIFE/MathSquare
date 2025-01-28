package com.happym.mathsquare.Model;

public class Sections {

    private String section;
    private String grade;
    private String docId;

    // Constructor
    public Sections(String section, String grade, String docId) {
        this.section = section;
        this.grade = grade;
        this.docId = docId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDocId() {
        return this.docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
