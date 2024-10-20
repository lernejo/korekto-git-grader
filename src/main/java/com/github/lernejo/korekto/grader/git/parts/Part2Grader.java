package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.grader.git.GitGrader;
import com.github.lernejo.korekto.grader.git.LaunchingContext;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitContext;
import com.github.lernejo.korekto.toolkit.thirdparty.git.GitNature;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.Link;
import com.github.lernejo.korekto.toolkit.thirdparty.markdown.MarkdownFile;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Part2Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    static final String BRANCH = "ex/part_2";

    @NotNull
    @Override
    public GradePart grade(@NotNull LaunchingContext context) {
        GitContext git = context.getExercise().lookupNature(GitNature.class).get().getContext();

        if (!git.getBranchNames().contains(BRANCH)) {
            return result(List.of("Missing branch `" + BRANCH + "`"), 0);
        }

        git.checkout(BRANCH);
        List<RevCommit> commits = git.listOrderedCommits();
        context.part2Commits = commits;
        if (commits.size() < 4) {
            return result(List.of("The branch `" + BRANCH + "` should have at least 4 commits, found " + commits.size()), 0);
        }
        if (context.part1Commits == null || context.part1Commits.size() < 3 || !context.part1Commits.subList(0, 3).equals(commits.subList(0, 3))) {
            return result(List.of("The first three commits of branch `" + BRANCH + "` should be the same as branch `" + Part1Grader.BRANCH + "`"), 0);
        }

        List<String> errors = context.checkCommitMessage(commits, 3, "Add Markdown documentation");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }
        MarkdownFile docMd = new MarkdownFile(context.getExercise().getRoot().resolve("Doc.md"));
        if (!docMd.exists()) {
            return result(List.of("Missing file **Doc.md**"), 0);
        }
        List<Link> links = docMd.getLinks();
        List<Link> expectedLinks = List.of(
            new Link("Git - Documentation", "https://git-scm.com/doc"),
            new Link("Markdown - Documentation", "https://guides.github.com/features/mastering-markdown"),
            new Link("Markdown bestpractices", "https://www.markdownguide.org/basic-syntax/")
        );
        if (!context.equalator.equals(expectedLinks, links)) {
            return result(List.of("In **Doc.md**, links should be \n\t" + expectedLinks + "\n, but found \n\t" + links), 0);
        }

        if (commits.size() != 5) {
            return result(List.of("The branch `" + BRANCH + "` should have 5 commits, found " + commits.size()), 0);
        }
        errors = context.checkCommitMessage(commits, 4, "Ex Part 2");
        if (!errors.isEmpty()) {
            return result(errors, 0);
        }

        MarkdownFile exMd = new MarkdownFile(context.getExercise().getRoot().resolve("Ex.md"));
        if (!exMd.exists()) {
            return result(List.of("Missing file **Ex.md**"), 0);
        }
        List<String> bulletPoints = exMd.getBulletPoints();
        List<String> expectedBulletPoints = List.of(GitGrader.PART_1_NAME, name);
        if (!expectedBulletPoints.equals(bulletPoints)) {
            return result(List.of("In **Ex.md**, expecting bullet points " + expectedBulletPoints + ", found " + bulletPoints), 0);
        }

        return result(List.of(), maxGrade);
    }
}
