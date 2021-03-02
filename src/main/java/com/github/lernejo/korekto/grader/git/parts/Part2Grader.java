package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.misc.Equalator;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Link;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.MarkdownFile;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class Part2Grader extends AbstractPartGrader {

    static final String BRANCH = "ex/part_2";
    static final String NAME = "Part 2";

    public Part2Grader(Equalator equalator) {
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
        context.part2Commits = commits;
        if (commits.size() < 4) {
            explanations.add("The branch `" + BRANCH + "` should have at least 4 commits, found " + commits.size());
            return result(explanations, grade);
        }
        if (!context.part1Commits.subList(0, 3).equals(commits.subList(0, 3))) {
            explanations.add("The first three commits of branch `" + BRANCH + "` should be the same as branch `" + Part1Grader.BRANCH + "`");
            return result(explanations, grade);
        }

        if (!checkCommitMessage(commits, 3, "Add Markdown documentation", explanations)) {
            return result(explanations, grade);
        }
        MarkdownFile docMd = new MarkdownFile(exercise.getRoot().resolve("Doc.md"));
        if (!docMd.exists()) {
            explanations.add("Missing file **Doc.md**");
            return result(explanations, grade);
        }
        List<Link> links = docMd.getLinks();
        List<Link> expectedLinks = List.of(
            new Link("Git - Documentation", "https://git-scm.com/doc"),
            new Link("Markdown - Documentation", "https://guides.github.com/features/mastering-markdown"),
            new Link("Markdown bestpractices", "https://www.markdownguide.org/basic-syntax/")
        );
        if (!equalator.equals(expectedLinks, links)) {
            explanations.add("In **Doc.md**, links should be \n\t" + expectedLinks + "\n, but found \n\t" + links);
            return result(explanations, grade);
        }

        if (commits.size() != 5) {
            explanations.add("The branch `" + BRANCH + "` should have 5 commits, found " + commits.size());
            return result(explanations, grade);
        }
        if (!checkCommitMessage(commits, 4, "Ex Part 2", explanations)) {
            return result(explanations, grade);
        }

        MarkdownFile exMd = new MarkdownFile(exercise.getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            explanations.add("Missing file **Ex.md**");
            return result(explanations, grade);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        List<String> expectedBulletPoints = List.of(Part1Grader.NAME, NAME);
        if (!expectedBulletPoints.equals(bulletPoints)) {
            explanations.add("In **Ex.md**, expecting bullet points " + expectedBulletPoints + ", found " + bulletPoints);
            return result(explanations, grade);
        }

        grade = 1;
        return result(explanations, grade);
    }
}
