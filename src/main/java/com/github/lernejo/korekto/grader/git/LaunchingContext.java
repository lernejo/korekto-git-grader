package com.github.lernejo.korekto.grader.git;

import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.GradingContext;
import com.github.lernejo.korekto.toolkit.misc.Equalator;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class LaunchingContext extends GradingContext {
    public final Equalator equalator = new Equalator(Integer.parseInt(System.getProperty("equalator.threshold", "3")));

    public String firstCommitHash;
    public List<RevCommit> part1Commits;
    public List<RevCommit> part2Commits;
    public List<RevCommit> part3Commits;

    public LaunchingContext(GradingConfiguration configuration) {
        super(configuration);
    }

    public List<String> checkCommitMessage(List<RevCommit> commits, int commitIndex, String expectedMessage) {
        RevCommit commit = commits.get(commitIndex);
        if (!equalator.equals(expectedMessage, commit.getShortMessage())) {
            return List.of("The " + toHrIndex(commitIndex) + " should be named **" + expectedMessage + "**, but found **" + commit.getShortMessage() + "**");
        }
        return List.of();
    }

    private String toHrIndex(int commitIndex) {
        return switch (commitIndex) {
            case 0 -> "first commit";
            case 1 -> "second commit";
            case 2 -> "third commit";
            case 3 -> "fourth commit";
            case 4 -> "fifth commit";
            default -> "commit " + (commitIndex + 1);
        };
    }
}
