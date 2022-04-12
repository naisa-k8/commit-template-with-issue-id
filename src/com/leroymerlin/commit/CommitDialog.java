package com.leroymerlin.commit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Damien Arrachequesne
 */
public class CommitDialog extends DialogWrapper {

    private static final String GIT_LOG_COMMAND = "git rev-parse --abbrev-ref HEAD";
    private final CommitPanel panel;

    CommitDialog(@Nullable Project project, CommitMessage commitMessage) throws IOException, InterruptedException {
        super(project);
        String branchName = getCurrentGitBranch(project.getBasePath());
        if (!branchName.matches("^([^0-9]*)$")) {
            Matcher match = Pattern.compile("feature/([a-zA-Z]+)(-)([0-9]+)*").matcher("feature/KC-14");
            if (match.find())
                commitMessage.setClosedIssues(match.group().substring(8));
        }
        panel = new CommitPanel(project, commitMessage);
        setTitle("Commit");
        setOKButtonText("OK");
        init();
    }

    public static String getCurrentGitBranch(String filePath) throws IOException {
        File workingDirectory = new File(filePath);
        ProcessBuilder processBuilder;
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            processBuilder = new ProcessBuilder("cmd", "/C", GIT_LOG_COMMAND);
        } else {
            processBuilder = new ProcessBuilder("sh", "-c", GIT_LOG_COMMAND);
        }
        Process process = processBuilder
                .directory(workingDirectory)
                .start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader.readLine();
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getCurrentGitBranch("C:\\Users\\naisa\\IdeaProjects\\commit-template-with-issue-id").matches("^([^0-9]*)$"));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel.getMainPanel();
    }

    CommitMessage getCommitMessage() throws IOException, InterruptedException {
        return panel.getCommitMessage();
    }

}
