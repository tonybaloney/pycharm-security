package security.settings;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SecuritySettingsPane implements ItemListener{
    private JPanel settingsPanel;
    private JPanel apiKeyPanel;
    private JTextField apiKeyField;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;
    private JRadioButton radioButton4;
    private JTextField apiUrlField;
    private JTextField customUrlField;
    private JCheckBox ignoreDocstringCheckbox;
    private JRadioButton radioButton5;
    private JTextField snykApiTextField;
    private JTextField snykOrgIdTextField;

    private ButtonGroup safetyButtonGroup;

    public JComponent getPanel() {
        return settingsPanel;
    }

    public void storeSettings(SecuritySettings settings) {
        settings.setPyupApiKey(apiKeyField.getText());
        settings.setPyupApiUrl(apiUrlField.getText());
        settings.setPyupCustomUrl(customUrlField.getText());
        settings.setSafetyDbMode(getSelectedSafetyDbMode());
        settings.setIgnoreDocstrings(ignoreDocstringCheckbox.isSelected());
        settings.setSnykApiKey(snykApiTextField.getText());
        settings.setSnykOrgId(snykOrgIdTextField.getText());
    }

    public boolean isModified(SecuritySettings settings) {
        return !settings.getPyupApiKey().equals(apiKeyField.getText())
                || settings.getSafetyDbMode() != getSelectedSafetyDbMode()
                || !settings.getPyupApiUrl().equals(apiUrlField.getText())
                || !settings.getPyupCustomUrl().equals(customUrlField.getText())
                || !settings.getSnykApiKey().equals(snykApiTextField.getText())
                || !settings.getSnykOrgId().equals(snykOrgIdTextField.getText())
                || settings.getIgnoreDocstrings() != ignoreDocstringCheckbox.isSelected();
    }


    public void setData(SecuritySettings settings) {
        apiKeyField.setText(settings.getPyupApiKey());
        customUrlField.setText(settings.getPyupCustomUrl());
        apiUrlField.setText(settings.getPyupApiUrl());
        snykApiTextField.setText(settings.getSnykApiKey());
        snykOrgIdTextField.setText(settings.getSnykOrgId());
        ignoreDocstringCheckbox.setSelected(settings.getIgnoreDocstrings());
        safetyButtonGroup.clearSelection();
        if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Disabled)
            radioButton1.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Bundled)
            radioButton2.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Api)
            radioButton3.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Custom)
            radioButton4.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Snyk)
            radioButton5.setSelected(true);
        else
            radioButton1.setSelected(true);
    }

    private SecuritySettings.SafetyDbType getSelectedSafetyDbMode() {
        if (radioButton1.isSelected())
            return SecuritySettings.SafetyDbType.Disabled;
        else if (radioButton2.isSelected())
            return SecuritySettings.SafetyDbType.Bundled;
        else if (radioButton3.isSelected())
            return SecuritySettings.SafetyDbType.Api;
        else if (radioButton4.isSelected())
            return SecuritySettings.SafetyDbType.Custom;
        else if (radioButton5.isSelected())
            return SecuritySettings.SafetyDbType.Snyk;
        else
            return SecuritySettings.SafetyDbType.Bundled;
    }


    public void itemStateChanged(ItemEvent itemEvent) {
        if (apiKeyField == null)
            return;
        if (itemEvent.getSource() == radioButton3) { // PyUp API
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                apiKeyField.setEnabled(true);
                apiUrlField.setEnabled(true);
                customUrlField.setEnabled(false);
                snykOrgIdTextField.setEnabled(false);
                snykApiTextField.setEnabled(false);
            }
        } else if (itemEvent.getSource() == radioButton1 || itemEvent.getSource() == radioButton2) { // Disabled or Bundled
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                apiKeyField.setEnabled(false);
                apiUrlField.setEnabled(false);
                customUrlField.setEnabled(false);
                snykOrgIdTextField.setEnabled(false);
                snykApiTextField.setEnabled(false);
            }
        } else if (itemEvent.getSource() == radioButton4) { // Custom URL
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                apiKeyField.setEnabled(false);
                apiUrlField.setEnabled(false);
                customUrlField.setEnabled(true);
                snykOrgIdTextField.setEnabled(false);
                snykApiTextField.setEnabled(false);
            }
        } else if (itemEvent.getSource() == radioButton5) { // Snyk
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                apiKeyField.setEnabled(false);
                apiUrlField.setEnabled(false);
                customUrlField.setEnabled(false);
                snykOrgIdTextField.setEnabled(true);
                snykApiTextField.setEnabled(true);
            }
        }
    }

    private void createUIComponents() {
        radioButton1 = new JRadioButton();
        radioButton1.addItemListener(this);
        radioButton2 = new JRadioButton();
        radioButton2.addItemListener(this);
        radioButton3 = new JRadioButton();
        radioButton3.addItemListener(this);
        radioButton4 = new JRadioButton();
        radioButton4.addItemListener(this);
        radioButton5 = new JRadioButton();
        radioButton5.addItemListener(this);
    }
}
