package com.github.lernejo.korekto.grader.git;

import com.github.lernejo.korekto.grader.git.parts.Part1Grader;
import com.github.lernejo.korekto.grader.git.parts.Part2Grader;
import com.github.lernejo.korekto.grader.git.parts.Part3Grader;
import com.github.lernejo.korekto.grader.git.parts.Part4Grader;
import com.github.lernejo.korekto.toolkit.Grader;
import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.PartGrader;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GitGrader implements Grader<LaunchingContext> {

    public static String PART_1_NAME = "Part 1";
    public static String PART_2_NAME = "Part 2";
    public static String PART_3_NAME = "Part 3";

    @NotNull
    @Override
    public String name() {
        return "Git training";
    }

    @NotNull
    @Override
    public LaunchingContext gradingContext(@NotNull GradingConfiguration configuration) {
        return new LaunchingContext(configuration);
    }

    @NotNull
    @Override
    public Collection<PartGrader<LaunchingContext>> graders() {
        return List.of(
            new Part1Grader(PART_1_NAME, 1.0D),
            new Part2Grader(PART_2_NAME, 2.0D),
            new Part3Grader(PART_3_NAME, 4.0D),
            new Part4Grader("Part 4", 8.0D)
        );
    }

    @Override
    public boolean needsWorkspaceReset() {
        return true;
    }

    @NotNull
    @Override
    public String slugToRepoUrl(@NotNull String slug) {
        return "https://github.com/" + slug + "/git_training";
    }
}
