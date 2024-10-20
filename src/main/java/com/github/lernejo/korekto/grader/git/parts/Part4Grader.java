package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.grader.git.GitGrader;
import com.github.lernejo.korekto.grader.git.LaunchingContext;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitNature;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.MarkdownFile;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Title;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Part4Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    static final String BRANCH = "ex/part_4";

    @NotNull
    @Override
    public GradePart grade(@NotNull LaunchingContext context) {
        GitContext git = context.getExercise().lookupNature(GitNature.class).get().getContext();

        if (!git.getBranchNames().contains(BRANCH)) {
            return result(List.of("Missing branch `" + BRANCH + "`"), 0);
        }

        git.checkout(BRANCH);
        List<RevCommit> commits = git.listOrderedCommits();
        if (commits.size() < 3) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 3 commits, found " + commits.size()), 0);
        }
        if (context.part3Commits == null || !context.part3Commits.subList(0, 2).equals(commits.subList(0, 2))) {
            return result(List.of("The first two commits of branch `" + BRANCH + "` should be the same as in branch `" + Part3Grader.BRANCH + "`"), 0);
        }

        List<String> errors = context.checkCommitMessage(commits, 2, "Back to the future");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }
        MarkdownFile endMd = new MarkdownFile(context.getExercise().getRoot().resolve("End.md"));
        if (!endMd.exists()) {
            return result(List.of("Missing file **End.md**"), 0);
        }
        List<Title> titles = endMd.getTitlesOfLevel(1);
        List<Title> expectedTitles = List.of(new Title(1, "The end"));
        if (!context.equalator.equals(expectedTitles, titles)) {
            return result(List.of("In **End.md**, title should be " + expectedTitles + ", found " + titles), 0);
        }
        if (!context.equalator.equals(endMd.getLineCount(), 2)) {
            return result(List.of("**End.md** file should be 2 lines long, found " + endMd.getLineCount()), 0);
        }

        if (commits.size() < 4) {
            return result(List.of("The branch `" + BRANCH + "` should have 4 commits, found " + commits.size()), 0);
        }
        errors = context.checkCommitMessage(commits, 3, "Exercise");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }
        MarkdownFile exMd = new MarkdownFile(context.getExercise().getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            return result(List.of("Missing file **Ex.md**"), 0);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        List<String> expectedBulletPoints = List.of(GitGrader.PART_1_NAME, GitGrader.PART_2_NAME, GitGrader.PART_3_NAME);
        if (!expectedBulletPoints.equals(bulletPoints)) {
            return result(List.of("In **Ex.md**, expecting bullet points " + expectedBulletPoints + ", found " + bulletPoints), 0);
        }

        return result(List.of(), maxGrade);
    }
}
