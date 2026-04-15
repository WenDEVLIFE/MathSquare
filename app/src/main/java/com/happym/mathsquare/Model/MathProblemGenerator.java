package com.happym.mathsquare.Model;

import com.happym.mathsquare.MathProblem;

import java.util.*;

public final class MathProblemGenerator {

    private static final Random rand = new Random();

    private MathProblemGenerator() {
    }

    public static List<MathProblem> generate(
            String operation,
            int gradeLevel,
            String levelId,
            int limit
    ) {
        List<MathProblem> problems = new ArrayList<>();
        Set<String> used = new HashSet<>();

        int levelIndex = parseLevel(levelId);
        DifficultyConfig cfg =
                DifficultyConfig.from(gradeLevel, operation, levelIndex);

        while (problems.size() < limit) {
            int[] nums = generateNumbers(cfg, operation);
            int answer = compute(nums, operation);
            if (answer < 0) continue;

            String q = join(nums, "|||");
            if (!used.add(q)) continue;

            problems.add(new MathProblem(
                    q,
                    operation.toLowerCase(),
                    "grade_" + gradeLevel,
                    answer,
                    generateChoices(answer)
            ));
        }
        return problems;
    }

    private static int parseLevel(String levelId) {
        if (levelId == null) return 0;
        try {
            return Integer.parseInt(levelId.replace("level_", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private static int[] generateNumbers(DifficultyConfig cfg, String operation) {
        int count = cfg.operands;
        int[] nums = new int[count];

        for (int i = 0; i < count; i++) {
            nums[i] = rand.nextInt(cfg.max - cfg.min + 1) + cfg.min;
        }

        // Ensure valid division
        if ("division".equalsIgnoreCase(operation)) {
            nums[1] = Math.max(1, nums[1]);
            nums[0] = nums[1] * (rand.nextInt(cfg.max / nums[1] + 1));
        }

        return nums;
    }

    private static int compute(int[] n, String op) {
        int r = n[0];
        for (int i = 1; i < n.length; i++) {
            switch (op.toLowerCase()) {
                case "addition":
                    r += n[i];
                    break;
                case "subtraction":
                    r -= n[i];
                    break;
                case "multiplication":
                    r *= n[i];
                    break;
                case "division":
                    if (n[i] == 0 || r % n[i] != 0) return -1;
                    r /= n[i];
                    break;
            }
        }
        return r;
    }

    private static String generateChoices(int answer) {
        Set<Integer> set = new LinkedHashSet<>();
        set.add(answer);
        while (set.size() < 4) {
            set.add(answer + rand.nextInt(11) - 5);
        }
        return set.toString().replaceAll("[\\[\\] ]", "");
    }

    private static String join(int[] arr, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(sep);
        }
        return sb.toString();
    }

    /* ---------------- DIFFICULTY CONFIG ---------------- */

    private static class DifficultyConfig {
        int min;
        int max;
        int operands;

        static DifficultyConfig from(int grade, String operation, int levelIndex) {
            DifficultyConfig d = new DifficultyConfig();

            switch (grade) {
                case 1:
                    d.min = 1;
                    d.max = 10;
                    d.operands = 2;
                    break;
                case 2:
                    d.min = 1;
                    d.max = 20;
                    d.operands = 2;
                    break;
                case 3:
                    d.min = 1;
                    d.max = 100;
                    d.operands = 2;
                    break;
                case 4:
                    d.min = 1;
                    d.max = 500;
                    d.operands = 2;
                    break;
                case 5:
                    d.min = 1;
                    d.max = 1000;
                    d.operands = 2;
                    break;
                case 6:
                    d.min = 1;
                    d.max = 10000;
                    d.operands = 2;
                    break;
                default:
                    d.min = 1;
                    d.max = 10;
                    d.operands = 2;
            }

            // Multiplication: kids learn times tables, cap per grade
            if ("multiplication".equalsIgnoreCase(operation)) {
                switch (grade) {
                    case 1:
                        d.max = 5;
                        break; // 1×1 to 5×5
                    case 2:
                        d.max = 10;
                        break; // times tables up to 10
                    case 3:
                        d.max = 12;
                        break; // times tables up to 12
                    case 4:
                        d.max = 20;
                        break;
                    case 5:
                        d.max = 50;
                        break;
                    case 6:
                        d.max = 100;
                        break;
                }
            }

            // Division: keep divisor and quotient manageable
            if ("division".equalsIgnoreCase(operation)) {
                switch (grade) {
                    case 1:
                        d.max = 10;
                        break; // 10 ÷ 2 = 5 style
                    case 2:
                        d.max = 20;
                        break;
                    case 3:
                        d.max = 50;
                        break;
                    case 4:
                        d.max = 100;
                        break;
                    case 5:
                        d.max = 200;
                        break;
                    case 6:
                        d.max = 500;
                        break;
                }
            }

            if ("subtraction".equalsIgnoreCase(operation) && grade <= 2) {
                d.min = 1;
            }

            // Level scaling within a grade (for practice mode levels)
            if (levelIndex > 0) {
                float scale = 1f + (levelIndex * 0.15f);
                d.max = Math.round(d.max * scale);
                if (levelIndex >= 3) {
                    d.operands = Math.min(3, d.operands + 1);
                }
            }

            return d;
        }
    }

}