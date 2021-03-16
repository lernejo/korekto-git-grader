package com.github.lernejo.korekto.grader.git;

import com.github.lernejo.korekto.grader.git.parts.*;
import com.github.lernejo.korekto.toolkit.*;
import com.github.lernejo.korekto.toolkit.misc.Equalator;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitNature;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

class Grader implements GradingStep {

    private final Equalator equalator = new Equalator(1);

    @Override
    public void run(GradingConfiguration configuration, GradingContext context) {
        Optional<GitNature> optionalGitNature = context.getExercise().lookupNature(GitNature.class);
        if (optionalGitNature.isEmpty()) {
            context.getGradeDetails().getParts().add(new GradePart(Part1Grader.NAME, 0D, 4D, List.of("Not a Git project")));
        } else {
            GitNature gitNature = optionalGitNature.get();
            context.getGradeDetails().getParts().addAll(gitNature.withContext(c -> grade(c, context.getExercise())));
        }
    }

    private List<GradePart> grade(GitContext git, Exercise exercise) {
        GitTrainingGraderContext context = new GitTrainingGraderContext();

        return List.of(
            new Part1Grader(equalator).grade(git, exercise, context),
            new Part2Grader(equalator).grade(git, exercise, context),
            new Part3Grader(equalator).grade(git, exercise, context),
            new Part4Grader(equalator).grade(git, exercise, context)
        );
    }
}
