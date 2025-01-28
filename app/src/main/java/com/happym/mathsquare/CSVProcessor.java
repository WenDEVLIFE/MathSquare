package com.happym.mathsquare;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessor extends AppCompatActivity {

    public List<MathProblem> readCSVFile(BufferedReader bufferedReader) {
        List<MathProblem> mathProblemList = new ArrayList<>();
        try {

            CSVParser csvParser = new CSVParser(bufferedReader,
                    CSVFormat.Builder.create()
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .setRecordSeparator(',').build());
            for (CSVRecord csvRecord : csvParser) {
                // Extracting values from the CSVRecord
                String question = csvRecord.get(0);
                String operation = csvRecord.get(1);
                String difficulty = csvRecord.get(2);
                int answer = Integer.parseInt(csvRecord.get(3));
                String choices = csvRecord.get(4);
                MathProblem mathProblem = new MathProblem();
                mathProblem.setQuestion(question);
                mathProblem.setOperation(operation);
                mathProblem.setDifficulty(difficulty);
                mathProblem.setAnswer(answer);
                mathProblem.setChoices(choices);

                mathProblemList.add(mathProblem);
            }
            // Close the CSVParser
            csvParser.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("done processing csv");
        return mathProblemList;
    }

    public List<MathProblem> getProblemsByOperation(List<MathProblem> mathProblemList, String difficultyText) {
        List<MathProblem> mathProblems = new ArrayList<>();
        for (MathProblem problem : mathProblemList) {

            if (problem.getDifficulty().equalsIgnoreCase(difficultyText)) {
                mathProblems.add(problem);
            }
        }
        return mathProblems;
    }
}
