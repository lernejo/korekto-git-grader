package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.grader.git.LaunchingContext;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitNature;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Link;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.MarkdownFile;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Title;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Part1Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {
    static final String BRANCH = "ex/part_1";

    @NotNull
    @Override
    public GradePart grade(@NotNull LaunchingContext context) {
        GitContext git = context.getExercise().lookupNature(GitNature.class).get().getContext();
        List<RevCommit> masterCommits = git.listOrderedCommits();
        if (masterCommits.isEmpty()) {
            return result(List.of("No commit on master branch, did you forget to tick the **Initialize this repository with a README** checkbox ?"), 0);
        }

        RevCommit firstCommit = masterCommits.getFirst();
        if (!"Initial commit".equals(firstCommit.getShortMessage())) {
            return result(List.of("The first commit on master branch is not the one that GitHub is supposed to have created"), 0);
        }
        context.firstCommitHash = firstCommit.name();

        if (!git.getBranchNames().contains(BRANCH)) {
            return result(List.of("Missing branch `" + BRANCH + "`"), 0);
        }

        git.checkout(BRANCH);
        List<RevCommit> commits = git.listOrderedCommits();
        context.part1Commits = commits;
        if (commits.isEmpty()) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 1 commit, found 0"), 0);
        }
        if (!commits.getFirst().name().equals(context.firstCommitHash)) {
            return result(List.of("The branch `" + BRANCH + "` should have the same first commit as the `master` branch"), 0);
        }

        if (commits.size() == 1) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 2 commits, found " + commits.size()), 0);
        }

        List<String> errors = context.checkCommitMessage(commits, 1, "Add Git documentation");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }

        MarkdownFile docMd = new MarkdownFile(context.getExercise().getRoot().resolve("Doc.md"));
        if (!docMd.exists()) {
            return result(List.of("Missing file **Doc.md**"), 0);
        }
        List<Title> firstLevelTitles = docMd.getTitlesOfLevel(1);
        List<Title> expectedTitles = List.of(new Title(1, "Documentation"));
        if (!context.equalator.equals(expectedTitles, firstLevelTitles)) {
            return result(List.of("In **Doc.md**, titles should be " + expectedTitles + ", found " + firstLevelTitles), 0);
        }
        List<Link> links = docMd.getLinks();
        if (links.isEmpty()) {
            return result(List.of("In **Doc.md**, at least one link is expected"), 0);
        }
        Link expectedLink1 = new Link("Git - Documentation", "https://git-scm.com/doc");
        if (!context.equalator.equals(expectedLink1, links.getFirst())) {
            return result(List.of("In **Doc.md**, first link should be " + expectedLink1 + ", found " + links.getFirst()), 0);
        }

        if (commits.size() == 2) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 3 commits, found " + commits.size()), 0);
        }
        errors = context.checkCommitMessage(commits, 2, "Setup exercise file");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }
        MarkdownFile exMd = new MarkdownFile(context.getExercise().getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            return result(List.of("Missing file **Ex.md**"), 0);
        }
        List<Title> exTitles = exMd.getTitlesOfLevel(1);
        if (exTitles.size() != 1) {
            return result(List.of("In **Ex.md**, expecting 1 title, found " + exTitles.size()), 0);
        }
        if (!context.equalator.equals("Exercice", exTitles.getFirst().getText())) {
            return result(List.of("In **Ex.md**, title should be `Exercice`, found " + exTitles.getFirst().getText()), 0);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        if (!context.equalator.equals(List.of(name), bulletPoints)) {
            return result(List.of("In **Ex.md**, expecting one bullet point `" + name + "`, found " + bulletPoints), 0);
        }
        if (!context.equalator.equals(exMd.getLineCount(), 3)) {
            return result(List.of("**Ex.md** file should be 3 lines long, found " + exMd.getLineCount()), 0);
        }

        if (commits.size() == 3) {
            return result(List.of("The branch `" + BRANCH + "` should have 4 commits, found " + commits.size()), 0);
        }
        errors = context.checkCommitMessage(commits, 3, "Add Markdown doc");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }
        if (links.size() < 2) {
            return result(List.of("In **Doc.md**, two links are expected"), 0);
        }
        Link expectedLink2 = new Link("Markdown - Documentation", "https://guides.github.com/features/mastering-markdown");
        if (!context.equalator.equals(expectedLink2, links.get(1))) {
            return result(List.of("In **Doc.md**, second link should be " + expectedLink2 + ", found " + links.get(1)), 0);
        }

        if (!context.equalator.equals(docMd.getLineCount(), 4)) {
            return result(List.of("**Doc.md** file should be 4 lines long, found " + docMd.getLineCount()), 0);
        }

        return result(List.of(), 1);
    }
}
