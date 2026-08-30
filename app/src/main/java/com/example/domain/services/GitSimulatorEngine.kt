package com.example.domain.services

import com.example.data.models.GitCommit
import java.util.UUID

data class GitRepoState(
  val currentBranch: String = "main",
  val branches: List<String> = listOf("main"),
  val workingDirectory: List<String> = listOf("main.py", "README.md"),
  val stagingArea: List<String> = emptyList(),
  val commitHistory: List<GitCommit> = listOf(
    GitCommit(
      hash = "c1a4e89",
      message = "Initial repository setup",
      author = "Alex Dev <alex@codequest.dev>",
      timestamp = "Yesterday",
      changedFiles = listOf("README.md", "main.py")
    )
  ),
  val isConflictPresent: Boolean = false,
  val conflictFile: String? = null,
  val terminalOutput: List<String> = listOf("Initialized empty Git repository in /workspace/project/.git/")
)

object GitSimulatorEngine {

  fun initScenario(
    branch: String = "main",
    branches: List<String> = listOf("main"),
    workingFiles: List<String> = emptyList(),
    stagedFiles: List<String> = emptyList(),
    conflictFile: String? = null
  ): GitRepoState {
    return GitRepoState(
      currentBranch = branch,
      branches = branches,
      workingDirectory = workingFiles,
      stagingArea = stagedFiles,
      isConflictPresent = conflictFile != null,
      conflictFile = conflictFile,
      terminalOutput = listOf("Git workspace ready on branch '$branch'")
    )
  }

  fun executeGitStatus(state: GitRepoState): Pair<GitRepoState, String> {
    val sb = StringBuilder()
    sb.append("On branch ${state.currentBranch}\n")
    if (state.stagingArea.isNotEmpty()) {
      sb.append("Changes to be committed:\n")
      for (file in state.stagingArea) {
        sb.append("  (use \"git restore --staged <file>...\" to unstage)\n")
        sb.append("    \u001B[32mmodified:   $file\u001B[0m\n")
      }
    }
    if (state.workingDirectory.isNotEmpty()) {
      sb.append("Changes not staged for commit:\n")
      for (file in state.workingDirectory) {
        sb.append("  (use \"git add <file>...\" to update what will be committed)\n")
        sb.append("    \u001B[31mmodified:   $file\u001B[0m\n")
      }
    }
    if (state.workingDirectory.isEmpty() && state.stagingArea.isEmpty()) {
      sb.append("nothing to commit, working tree clean\n")
    }

    val output = sb.toString()
    val newOut = state.terminalOutput + listOf("$ git status", output)
    return state.copy(terminalOutput = newOut) to output
  }

  fun executeGitAdd(state: GitRepoState, file: String = "."): Pair<GitRepoState, String> {
    val toStage = if (file == "." || file == "-A") {
      state.workingDirectory
    } else {
      state.workingDirectory.filter { it.startsWith(file.substringBefore(" ")) }
    }

    val newWorking = state.workingDirectory.filterNot { toStage.contains(it) }
    val newStaged = (state.stagingArea + toStage.map { it.replace(" (modified)", "").replace(" (untracked)", "") }).distinct()

    val logMsg = "$ git add $file\nStaged ${toStage.size} file(s) for commit."
    val newOut = state.terminalOutput + listOf(logMsg)

    return state.copy(
      workingDirectory = newWorking,
      stagingArea = newStaged,
      terminalOutput = newOut
    ) to "Staged successfully."
  }

  fun executeGitCommit(state: GitRepoState, message: String): Pair<GitRepoState, String> {
    if (state.stagingArea.isEmpty()) {
      val msg = "$ git commit -m \"$message\"\nOn branch ${state.currentBranch}\nNothing to commit, working tree clean."
      return state.copy(terminalOutput = state.terminalOutput + listOf(msg)) to "Nothing to commit."
    }

    val shortHash = UUID.randomUUID().toString().take(7)
    val commit = GitCommit(
      hash = shortHash,
      message = message,
      author = "Alex Dev <alex@codequest.dev>",
      timestamp = "Just now",
      changedFiles = state.stagingArea,
      parentHash = state.commitHistory.firstOrNull()?.hash
    )

    val logMsg = "$ git commit -m \"$message\"\n[${state.currentBranch} $shortHash] $message\n ${state.stagingArea.size} file(s) changed."
    val newHistory = listOf(commit) + state.commitHistory

    return state.copy(
      stagingArea = emptyList(),
      commitHistory = newHistory,
      terminalOutput = state.terminalOutput + listOf(logMsg)
    ) to "Commit $shortHash created."
  }

  fun executeGitBranch(state: GitRepoState, newBranch: String): Pair<GitRepoState, String> {
    val cleanName = newBranch.trim().replace(" ", "-")
    val branches = (state.branches + cleanName).distinct()
    val logMsg = "$ git checkout -b $cleanName\nSwitched to a new branch '$cleanName'"
    return state.copy(
      currentBranch = cleanName,
      branches = branches,
      terminalOutput = state.terminalOutput + listOf(logMsg)
    ) to "Switched to branch $cleanName"
  }

  fun executeGitCheckout(state: GitRepoState, targetBranch: String): Pair<GitRepoState, String> {
    if (!state.branches.contains(targetBranch)) {
      val logMsg = "$ git checkout $targetBranch\nerror: pathspec '$targetBranch' did not match any file(s) known to git"
      return state.copy(terminalOutput = state.terminalOutput + listOf(logMsg)) to "Branch not found"
    }
    val logMsg = "$ git checkout $targetBranch\nSwitched to branch '$targetBranch'"
    return state.copy(
      currentBranch = targetBranch,
      terminalOutput = state.terminalOutput + listOf(logMsg)
    ) to "Switched to branch $targetBranch"
  }

  fun executeResolveConflict(state: GitRepoState, resolvedFileName: String): Pair<GitRepoState, String> {
    val logMsg = "Resolved merge conflict in $resolvedFileName\nAuto-staged resolution."
    return state.copy(
      isConflictPresent = false,
      conflictFile = null,
      workingDirectory = state.workingDirectory.filterNot { it.contains("conflict") },
      stagingArea = (state.stagingArea + resolvedFileName).distinct(),
      terminalOutput = state.terminalOutput + listOf(logMsg)
    ) to "Conflict resolved."
  }
}
