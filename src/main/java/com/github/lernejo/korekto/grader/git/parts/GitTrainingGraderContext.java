package com.github.lernejo.korekto.grader.git.parts;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class GitTrainingGraderContext {

    String firstCommitHash;
    List<RevCommit> part1Commits;
    List<RevCommit> part2Commits;
    List<RevCommit> part3Commits;
}
