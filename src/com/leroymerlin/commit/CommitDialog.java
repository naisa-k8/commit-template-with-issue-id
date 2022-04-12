package com.leroymerlin.commit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Damien Arrachequesne
 */
public class CommitDialog extends DialogWrapper {

    private final CommitPanel panel;

    CommitDialog(@Nullable Project project, CommitMessage commitMessage) throws IOException, InterruptedException {
        super(project);
        String branchName = getCurrentGitBranch();
        if (!branchName.equals("master")) {
            Matcher match = Pattern.compile("feature/([a-zA-Z]+)(-)([0-9]+)*").matcher(branchName);
            if (match.find())
                commitMessage.setClosedIssues(match.group().substring(8));
        }
        panel = new CommitPanel(project, commitMessage);
        setTitle("Commit");
        setOKButtonText("OK");
        init();
    }

    public static String getCurrentGitBranch() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec( "git rev-parse --abbrev-ref HEAD" );
        process.waitFor();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader( process.getInputStream() ) );

        return reader.readLine();
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
