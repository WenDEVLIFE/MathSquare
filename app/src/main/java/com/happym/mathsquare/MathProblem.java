package com.happym.mathsquare;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class MathProblem implements Parcelable {

    private String question;
    private String operation;
    private String difficulty;
    private int answer;
    private String choices;
    private int[] givenNumbers;
    private String[] choicesArray;

    public MathProblem() {}

    public MathProblem(String question, String operation, String difficulty, int answer, String choices) {
        this.question = question;
        this.operation = operation;
        this.difficulty = difficulty;
        this.answer = answer;
        this.choices = choices;
        setGivenNumbers(question);
        setChoicesArray(choices);
    }

    protected MathProblem(Parcel in) {
        question = in.readString();
        operation = in.readString();
        difficulty = in.readString();
        answer = in.readInt();
        choices = in.readString();
        givenNumbers = in.createIntArray();
        choicesArray = in.createStringArray();
    }

    public static final Creator<MathProblem> CREATOR = new Creator<MathProblem>() {
        @Override
        public MathProblem createFromParcel(Parcel in) {
            return new MathProblem(in);
        }

        @Override
        public MathProblem[] newArray(int size) {
            return new MathProblem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(operation);
        dest.writeString(difficulty);
        dest.writeInt(answer);
        dest.writeString(choices);
        dest.writeIntArray(givenNumbers);
        dest.writeStringArray(choicesArray);
    }

    // Getters and setters
    public String getQuestion() { return question; }

    public void setQuestion(String question) {
        this.question = question;
        setGivenNumbers(question);
    }

    public String getOperation() { return operation; }

    public void setOperation(String operation) { this.operation = operation; }

    public String getDifficulty() { return difficulty; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getAnswer() { return answer; }

    public void setAnswer(int answer) { this.answer = answer; }

    public String getChoices() { return choices; }

    public void setChoices(String choices) {
        this.choices = choices;
        setChoicesArray(choices);
    }

    public int[] getGivenNumbers() { return givenNumbers; }

    public void setGivenNumbers(String questionString) {
        String[] questionGivens = questionString.split("\\|\\|\\|");
        int[] numbers = new int[questionGivens.length];
        for (int i = 0; i < questionGivens.length; i++) {
            numbers[i] = Integer.parseInt(questionGivens[i].trim());
        }
        this.givenNumbers = numbers;
    }

    public String[] getChoicesArray() { return choicesArray; }

    public void setChoicesArray(String choicesString) {
        this.choicesArray = choicesString.split(",");
    }

    @Override
    public String toString() {
        return "MathProblem{" +
                "question='" + question + '\'' +
                ", operation='" + operation + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", answer=" + answer +
                ", choices='" + choices + '\'' +
                ", givenNumbers=" + Arrays.toString(givenNumbers) +
                ", choicesArray=" + Arrays.toString(choicesArray) +
                '}';
    }
}
