package com.leroymerlin.commit;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.Refreshable;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author Damien Arrachequesne
 */
public class CreateCommitAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        CommitMessageI commitPanel = getCommitPanel(actionEvent);
        if (commitPanel == null) return;

        CommitMessage commitMessage = parseExistingCommitMessage(commitPanel);
        CommitDialog dialog = null;
        try {
            dialog = new CommitDialog(actionEvent.getProject(), commitMessage);
        } catch (IOException | InterruptedException ignored) {
        }
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            try {
                commitPanel.setCommitMessage(dialog.getCommitMessage().toString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private CommitMessage parseExistingCommitMessage(CommitMessageI commitPanel) {
        if (commitPanel instanceof CheckinProjectPanel) {
            String commitMessageString = ((CheckinProjectPanel) commitPanel).getCommitMessage();
            return CommitMessage.parse(commitMessageString);
        }
        return null;
    }

    @Nullable
    private static CommitMessageI getCommitPanel(@Nullable AnActionEvent e) {
        if (e == null) {
            return null;
        }
        Refreshable data = Refreshable.PANEL_KEY.getData(e.getDataContext());
        if (data instanceof CommitMessageI) {
            return (CommitMessageI) data;
        }
        return VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext());
    }
}