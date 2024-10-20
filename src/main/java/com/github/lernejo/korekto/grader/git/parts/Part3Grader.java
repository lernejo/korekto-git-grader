package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.grader.git.GitGrader;
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

import java.util.ArrayList;
import java.util.List;

public record Part3Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    static final String BRANCH = "ex/part_3";

    @NotNull
    @Override
    public GradePart grade(@NotNull LaunchingContext context) {
        GitContext git = context.getExercise().lookupNature(GitNature.class).get().getContext();

        if (!git.getBranchNames().contains(BRANCH)) {
            return result(List.of("Missing branch `" + BRANCH + "`"), 0);
        }

        git.checkout(BRANCH);
        List<RevCommit> commits = git.listOrderedCommits();
        context.part3Commits = commits;
        if (commits.isEmpty()) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 1 commit, found 0"), 0);
        }
        if (!context.part2Commits.subList(0, 1).equals(commits.subList(0, 1))) {
            return result(List.of("The first commit of branch `" + BRANCH + "` should be the same as in branch `" + Part2Grader.BRANCH + "`"), 0);
        }

        if (commits.size() < 2) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 2 commits, found " + commits.size()), 0);
        }
        List<String> errors = context.checkCommitMessage(commits, 1, "Add documentation");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }
        MarkdownFile docMd = new MarkdownFile(context.getExercise().getRoot().resolve("Doc.md"));
        if (!docMd.exists()) {
            return result(List.of("Missing file **Doc.md**"), 0);
        }
        List<Title> firstLevelTitles = docMd.getTitlesOfLevel(1);
        List<Title> expectedTitles = List.of(new Title(1, "Documentation"));
        if (!expectedTitles.equals(firstLevelTitles)) {
            return result(List.of("In **Doc.md**, title should be " + expectedTitles + ", found " + firstLevelTitles), 0);
        }
        List<Link> links = docMd.getLinks();
        List<Link> expectedLinks = List.of(
            new Link("Git - Documentation", "https://git-scm.com/doc"),
            new Link("Markdown - Documentation", "https://guides.github.com/features/mastering-markdown"),
            new Link("Markdown bestpractices", "https://www.markdownguide.org/basic-syntax/")
        );
        if (!expectedLinks.equals(links)) {
            return result(List.of("In **Doc.md**, links should be " + expectedLinks + ", found " + links), 0);
        }

        if (commits.size() < 3) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 3 commits, found " + commits.size()), 0);
        }
        errors = context.checkCommitMessage(commits, 2, "Exercise");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }
        MarkdownFile exMd = new MarkdownFile(context.getExercise().getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            return result(List.of("Missing file **Ex.md**"), 0);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        List<String> expectedBulletPoints = List.of(GitGrader.PART_1_NAME, GitGrader.PART_2_NAME, name);
        if (!expectedBulletPoints.equals(bulletPoints)) {
            return result(List.of("In **Ex.md**, expecting bullet points " + expectedBulletPoints + ", found " + bulletPoints), 0);
        }

        return result(List.of(), maxGrade);
    }
}
