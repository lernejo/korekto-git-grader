package com.github.lernejo.korekto.grader.git.parts;

import com.github.lernejo.korekto.toolkit.misc.Equalator;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public abstract class AbstractPartGrader implements PartGrader {

    protected final Equalator equalator;

    AbstractPartGrader(Equalator equalator) {
        this.equalator = equalator;
    }

    boolean checkCommitMessage(List<RevCommit> commits, int commitIndex, String expectedMessage, List<String> explanations) {
        RevCommit commit = commits.get(commitIndex);
        if (!equalator.equals(expectedMessage, commit.getShortMessage())) {
            explanations.add("The " + toHrIndex(commitIndex) + " should be named **" + expectedMessage + "**, but found **" + commit.getShortMessage() + "**");
            return false;
        }
        return true;
    }

    private String toHrIndex(int commitIndex) {
        switch (commitIndex) {
            case 0:
                return "first commit";
            case 1:
                return "second commit";
            case 2:
                return "third commit";
            case 3:
                return "fourth commit";
            case 4:
                return "fifth commit";

        }
        return "commit " + (commitIndex + 1);
    }
}
