package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;

import java.util.List;

public interface PartGrader {

    String name();

    GradePart grade(GitContext c, Exercise exercise, GitTrainingGraderContext context);

    default GradePart result(List<String> explanations, double grade) {
        return new GradePart(name(), grade, 1L, explanations);
    }
}
