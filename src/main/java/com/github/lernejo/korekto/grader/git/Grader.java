package com.github.lernejo.korekto.grader.git;

import java.util.List;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.GradingContext;

class Grader {

    void grade(GradingConfiguration configuration, GradingContext context) {
        context.getGradeDetails().getParts().addAll(
            List.of(
                new GradePart("part_1", 0.5D, List.of("demo")),
                new GradePart("part_2", 0D, List.of("demo2", "some error"))
            )
        );
    }
}
