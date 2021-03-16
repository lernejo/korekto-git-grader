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

public class Part1Grader extends AbstractPartGrader {

    static final String BRANCH = "ex/part_1";
    public static final String NAME = "Part 1";

    public Part1Grader(Equalator equalator) {
        super(equalator);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public GradePart grade(GitContext git, Exercise exercise, GitTrainingGraderContext context) {
        List<String> explanations = new ArrayList<>();
        List<RevCommit> masterCommits = git.listOrderedCommits();
        if (masterCommits.size() == 0) {
            explanations.add("No commit on master branch, did you forget to tick the **Initialize this repository with a README** checkbox ?");
            return result(explanations, 0);
        }

        RevCommit firstCommit = masterCommits.get(0);
        if (!"Initial commit".equals(firstCommit.getShortMessage())) {
            explanations.add("The first commit on master branch is not the one that GitHub is supposed to have created");
            return result(explanations, 0);
        }
        context.firstCommitHash = firstCommit.name();

        if (!git.getBranchNames().contains(BRANCH)) {
            explanations.add("Missing branch `" + BRANCH + "`");
            return result(explanations, 0);
        }

        git.checkout(BRANCH);
        List<RevCommit> commits = git.listOrderedCommits();
        context.part1Commits = commits;
        if (commits.size() == 0) {
            explanations.add("The branch `" + BRANCH + "` should have at least 1 commit, found " + commits.size());
            return result(explanations, 0);
        }
        if (!commits.get(0).name().equals(context.firstCommitHash)) {
            explanations.add("The branch `" + BRANCH + "` should have the same first commit as the `master` branch");
            return result(explanations, 0);
        }

        if (commits.size() == 1) {
            explanations.add("The branch `" + BRANCH + "` should have at least 2 commits, found " + commits.size());
            return result(explanations, 0);
        }

        if (!checkCommitMessage(commits, 1, "Add Git documentation", explanations)) {
            return result(explanations, 0);
        }

        MarkdownFile docMd = new MarkdownFile(exercise.getRoot().resolve("Doc.md"));
        if (!docMd.exists()) {
            explanations.add("Missing file **Doc.md**");
            return result(explanations, 0);
        }
        List<Title> firstLevelTitles = docMd.getTitlesOfLevel(1);
        List<Title> expectedTitles = List.of(new Title(1, "Documentation"));
        if (!equalator.equals(expectedTitles, firstLevelTitles)) {
            explanations.add("In **Doc.md**, titles should be " + expectedTitles + ", found " + firstLevelTitles);
            return result(explanations, 0);
        }
        List<Link> links = docMd.getLinks();
        if (links.isEmpty()) {
            explanations.add("In **Doc.md**, at least one link is expected");
            return result(explanations, 0);
        }
        Link expectedLink1 = new Link("Git - Documentation", "https://git-scm.com/doc");
        if (!equalator.equals(expectedLink1, links.get(0))) {
            explanations.add("In **Doc.md**, first link should be " + expectedLink1 + ", found " + links.get(0));
            return result(explanations, 0);
        }

        if (commits.size() == 2) {
            explanations.add("The branch `" + BRANCH + "` should have at least 3 commits, found " + commits.size());
            return result(explanations, 0);
        }
        if (!checkCommitMessage(commits, 2, "Setup exercise file", explanations)) {
            return result(explanations, 0);
        }
        MarkdownFile exMd = new MarkdownFile(exercise.getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            explanations.add("Missing file **Ex.md**");
            return result(explanations, 0);
        }
        List<Title> exTitles = exMd.getTitlesOfLevel(1);
        if (exTitles.size() != 1) {
            explanations.add("In **Ex.md**, expecting 1 title, found " + exTitles.size());
            return result(explanations, 0);
        }
        if (!equalator.equals("Exercice", exTitles.get(0).getText())) {
            explanations.add("In **Ex.md**, title should be `Exercice`, found " + exTitles.get(0).getText());
            return result(explanations, 0);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        if (!equalator.equals(List.of(NAME), bulletPoints)) {
            explanations.add("In **Ex.md**, expecting one bullet point `" + NAME + "`, found " + bulletPoints);
            return result(explanations, 0);
        }
        if (!equalator.equals(exMd.getLineCount(), 3)) {
            explanations.add("**Ex.md** file should be 3 lines long, found " + exMd.getLineCount());
            return result(explanations, 0);
        }

        if (commits.size() == 3) {
            explanations.add("The branch `" + BRANCH + "` should have 4 commits, found " + commits.size());
            return result(explanations, 0);
        }
        if (!checkCommitMessage(commits, 3, "Add Markdown doc", explanations)) {
            return result(explanations, 0);
        }
        if (links.size() < 2) {
            explanations.add("In **Doc.md**, two links are expected");
            return result(explanations, 0);
        }
        Link expectedLink2 = new Link("Markdown - Documentation", "https://guides.github.com/features/mastering-markdown");
        if (!equalator.equals(expectedLink2, links.get(1))) {
            explanations.add("In **Doc.md**, second link should be " + expectedLink2 + ", found " + links.get(1));
            return result(explanations, 0);
        }

        if (!equalator.equals(docMd.getLineCount(), 4)) {
            explanations.add("**Doc.md** file should be 4 lines long, found " + docMd.getLineCount());
            return result(explanations, 0);
        }

        return result(explanations, 1);
    }
}
