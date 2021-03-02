package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.misc.Equalator;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Link;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.MarkdownFile;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Title;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class Part3Grader extends AbstractPartGrader {

    static final String BRANCH = "ex/part_3";
    static final String NAME = "Part 3";

    public Part3Grader(Equalator equalator) {
        super(equalator);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public GradePart grade(GitContext git, Exercise exercise, GitTrainingGraderContext context) {
        List<String> explanations = new ArrayList<>();
        double grade = 0;

        if (!git.getBranchNames().contains(BRANCH)) {
            explanations.add("Missing branch `" + BRANCH + "`");
            return result(explanations, grade);
        }

        git.checkout(BRANCH);
        List<RevCommit> commits = git.listOrderedCommits();
        context.part3Commits = commits;
        if (commits.size() < 1) {
            explanations.add("The branch `" + BRANCH + "` should have at least 1 commit, found " + commits.size());
            return result(explanations, grade);
        }
        if (!context.part2Commits.subList(0, 1).equals(commits.subList(0, 1))) {
            explanations.add("The first commit of branch `" + BRANCH + "` should be the same as in branch `" + Part2Grader.BRANCH + "`");
            return result(explanations, grade);
        }

        if (commits.size() < 2) {
            explanations.add("The branch `" + BRANCH + "` should have at least 2 commits, found " + commits.size());
            return result(explanations, grade);
        }
        if (!checkCommitMessage(commits, 1, "Add documentation", explanations)) {
            return result(explanations, grade);
        }
        MarkdownFile docMd = new MarkdownFile(exercise.getRoot().resolve("Doc.md"));
        if (!docMd.exists()) {
            explanations.add("Missing file **Doc.md**");
            return result(explanations, grade);
        }
        List<Title> firstLevelTitles = docMd.getTitlesOfLevel(1);
        List<Title> expectedTitles = List.of(new Title(1, "Documentation"));
        if (!expectedTitles.equals(firstLevelTitles)) {
            explanations.add("In **Doc.md**, title should be " + expectedTitles + ", found " + firstLevelTitles);
            return result(explanations, grade);
        }
        List<Link> links = docMd.getLinks();
        List<Link> expectedLinks = List.of(
            new Link("Git - Documentation", "https://git-scm.com/doc"),
            new Link("Markdown - Documentation", "https://guides.github.com/features/mastering-markdown"),
            new Link("Markdown bestpractices", "https://www.markdownguide.org/basic-syntax/")
        );
        if (!expectedLinks.equals(links)) {
            explanations.add("In **Doc.md**, links should be " + expectedLinks + ", found " + links);
            return result(explanations, grade);
        }

        if (commits.size() < 3) {
            explanations.add("The branch `" + BRANCH + "` should have at least 3 commits, found " + commits.size());
            return result(explanations, grade);
        }
        if (!checkCommitMessage(commits, 2, "Exercise", explanations)) {
            return result(explanations, grade);
        }
        MarkdownFile exMd = new MarkdownFile(exercise.getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            explanations.add("Missing file **Ex.md**");
            return result(explanations, grade);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        List<String> expectedBulletPoints = List.of(Part1Grader.NAME, Part2Grader.NAME, NAME);
        if (!expectedBulletPoints.equals(bulletPoints)) {
            explanations.add("In **Ex.md**, expecting bullet points " + expectedBulletPoints + ", found " + bulletPoints);
            return result(explanations, grade);
        }

        grade = 1;
        return result(explanations, grade);
    }
}
