package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.misc.Equalator;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.MarkdownFile;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Title;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class Part4Grader extends AbstractPartGrader {

    static final String BRANCH = "ex/part_4";
    static final String NAME = "Part 4";

    public Part4Grader(Equalator equalator) {
        super(equalator);
    }

    @Override
    public String name() {
        return NAME;
    }

    public GradePart grade(GitContext git, Exercise exercise, GitTrainingGraderContext context) {
        List<String> explanations = new ArrayList<>();
        double grade = 0;

        if (!git.getBranchNames().contains(BRANCH)) {
            explanations.add("Missing branch `" + BRANCH + "`");
            return result(explanations, grade);
        }

        git.checkout(BRANCH);
        List<RevCommit> commits = git.listOrderedCommits();
        if (commits.size() < 3) {
            explanations.add("The branch `" + BRANCH + "` should have at least 3 commits, found " + commits.size());
            return result(explanations, grade);
        }
        if (!context.part3Commits.subList(0, 2).equals(commits.subList(0, 2))) {
            explanations.add("The first two commits of branch `" + BRANCH + "` should be the same as in branch `" + Part3Grader.BRANCH + "`");
            return result(explanations, grade);
        }

        if (!checkCommitMessage(commits, 2, "Back to the future", explanations)) {
            return result(explanations, grade);
        }
        MarkdownFile endMd = new MarkdownFile(exercise.getRoot().resolve("End.md"));
        if (!endMd.exists()) {
            explanations.add("Missing file **End.md**");
            return result(explanations, grade);
        }
        List<Title> titles = endMd.getTitlesOfLevel(1);
        List<Title> expectedTitles = List.of(new Title(1, "The end"));
        if (!expectedTitles.equals(titles)) {
            explanations.add("In **End.md**, title should be " + expectedTitles + ", found " + titles);
            return result(explanations, grade);
        }
        if (endMd.getLineCount() != 2) {
            explanations.add("**End.md** file should be 2 lines long, found " + endMd.getLineCount());
            return result(explanations, grade);
        }

        if (commits.size() < 4) {
            explanations.add("The branch `" + BRANCH + "` should have 4 commits, found " + commits.size());
            return result(explanations, grade);
        }
        if (!checkCommitMessage(commits, 3, "Exercise", explanations)) {
            return result(explanations, grade);
        }
        MarkdownFile exMd = new MarkdownFile(exercise.getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            explanations.add("Missing file **Ex.md**");
            return result(explanations, grade);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        List<String> expectedBulletPoints = List.of(Part1Grader.NAME, Part2Grader.NAME, Part3Grader.NAME);
        if (!expectedBulletPoints.equals(bulletPoints)) {
            explanations.add("In **Ex.md**, expecting bullet points " + expectedBulletPoints + ", found " + bulletPoints);
            return result(explanations, grade);
        }

        grade = 1;
        return result(explanations, grade);
    }
}
