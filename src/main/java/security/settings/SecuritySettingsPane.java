package security.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.components.ActionLink;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SecuritySettingsPane implements ItemListener{
    private JPanel settingsPanel;
    private JPanel apiKeyPanel;
    private JTextField apiKeyField;
    private JRadioButton disabledRadioButton;
    private JRadioButton safetyDbRadioButton;
    private JRadioButton pyupApiRadioButton;
    private JCheckBox ignoreDocstringCheckbox;
    private JRadioButton snykApiRadioButton;
    private JTextField snykApiTextField;
    private JTextField snykOrgIdTextField;
    private JRadioButton pyPiVulnerabilityAPIRadioButton;
    private ActionLink snykActionLink;
    private ActionLink pyupActionLink;

    private ButtonGroup safetyButtonGroup;

    public SecuritySettingsPane() {
        pyupActionLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrowserUtil.browse("https://pyup.io/create-account/?utm_source=pycharm_security");
            }
        });
        snykActionLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrowserUtil.browse("https://app.snyk.io/login?utm_source=pycharm_security");
            }
        });
    }

    public JComponent getPanel() {
        return settingsPanel;
    }

    public void storeSettings(SecuritySettings settings) {
        settings.setPyupApiKey(apiKeyField.getText());
        settings.setSafetyDbMode(getSelectedSafetyDbMode());
        settings.setIgnoreDocstrings(ignoreDocstringCheckbox.isSelected());
        settings.setSnykApiKey(snykApiTextField.getText());
        settings.setSnykOrgId(snykOrgIdTextField.getText());
    }

    public boolean isModified(SecuritySettings settings) {
        return !settings.getPyupApiKey().equals(apiKeyField.getText())
                || settings.getSafetyDbMode() != getSelectedSafetyDbMode()
                || !settings.getSnykApiKey().equals(snykApiTextField.getText())
                || !settings.getSnykOrgId().equals(snykOrgIdTextField.getText())
                || settings.getIgnoreDocstrings() != ignoreDocstringCheckbox.isSelected();
    }


    public void setData(SecuritySettings settings) {
        apiKeyField.setText(settings.getPyupApiKey());
        snykApiTextField.setText(settings.getSnykApiKey());
        snykOrgIdTextField.setText(settings.getSnykOrgId());
        ignoreDocstringCheckbox.setSelected(settings.getIgnoreDocstrings());
        safetyButtonGroup.clearSelection();
        if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Disabled)
            disabledRadioButton.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Bundled)
            safetyDbRadioButton.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Api)
            pyupApiRadioButton.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Custom)
            safetyDbRadioButton.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Snyk)
            snykApiRadioButton.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Pypi)
            pyPiVulnerabilityAPIRadioButton.setSelected(true);
        else
            disabledRadioButton.setSelected(true);
    }

    private SecuritySettings.SafetyDbType getSelectedSafetyDbMode() {
        if (disabledRadioButton.isSelected())
            return SecuritySettings.SafetyDbType.Disabled;
        else if (safetyDbRadioButton.isSelected())
            return SecuritySettings.SafetyDbType.Bundled;
        else if (pyupApiRadioButton.isSelected())
            return SecuritySettings.SafetyDbType.Api;
        else if (snykApiRadioButton.isSelected())
            return SecuritySettings.SafetyDbType.Snyk;
        else if (pyPiVulnerabilityAPIRadioButton.isSelected())
            return SecuritySettings.SafetyDbType.Pypi;
        else
            return SecuritySettings.SafetyDbType.Bundled;
    }


    public void itemStateChanged(ItemEvent itemEvent) {
        if (apiKeyField == null)
            return;
        if (itemEvent.getSource() == pyupApiRadioButton) { // PyUp API
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                apiKeyField.setEnabled(true);
                snykOrgIdTextField.setEnabled(false);
                snykApiTextField.setEnabled(false);
            }
        } else if (itemEvent.getSource() == disabledRadioButton || itemEvent.getSource() == safetyDbRadioButton || itemEvent.getSource() == pyPiVulnerabilityAPIRadioButton) { // Disabled or Bundled
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                apiKeyField.setEnabled(false);
                snykOrgIdTextField.setEnabled(false);
                snykApiTextField.setEnabled(false);
            }
        } else if (itemEvent.getSource() == snykApiRadioButton) { // Snyk
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                apiKeyField.setEnabled(false);
                snykOrgIdTextField.setEnabled(true);
                snykApiTextField.setEnabled(true);
            }
        }
    }

    private void createUIComponents() {
        disabledRadioButton = new JRadioButton();
        disabledRadioButton.addItemListener(this);
        safetyDbRadioButton = new JRadioButton();
        safetyDbRadioButton.addItemListener(this);
        pyupApiRadioButton = new JRadioButton();
        pyupApiRadioButton.addItemListener(this);
        snykApiRadioButton = new JRadioButton();
        snykApiRadioButton.addItemListener(this);
        pyPiVulnerabilityAPIRadioButton = new JRadioButton();
        pyPiVulnerabilityAPIRadioButton.addItemListener(this);
    }
}
