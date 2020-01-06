package bandit;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BanditConfigurationPanel implements SearchableConfigurable {
    private final ProjectService state;
    private final Project project;

    private TextFieldWithHistoryWithBrowseButton banditExecutableField;
    private TextFieldWithHistoryWithBrowseButton banditConfigField;
    private JRadioButton searchForConfigRadioButton;
    private JRadioButton useSpecificConfigRadioButton;
    private JPanel rootPanel;
    private JBList ruleList;

    public BanditConfigurationPanel(@NotNull Project project) {
        this.state = ProjectService.getInstance(project).getState();
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return "bandit";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Bandit";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return rootPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
