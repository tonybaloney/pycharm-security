package security.settings;

import javax.swing.*;
import com.intellij.ui.components.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SecuritySettingsPane implements ItemListener{
    private JPanel settingsPanel;
    private JPanel apiKeyPanel;
    private JBTextField apiKeyField;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;

    private ButtonGroup safetyButtonGroup;

    public JComponent getPanel() {
        return settingsPanel;
    }

    public void storeSettings(SecuritySettings settings) {
        settings.setPyupApiKey(apiKeyField.getText());
        settings.setSafetyDbMode(getSelectedSafetyDbMode());
    }

    public boolean isModified(SecuritySettings settings) {
        return ! settings.getPyupApiKey().equals(apiKeyField.getText()) || settings.getSafetyDbMode() != getSelectedSafetyDbMode();
    }


    public void setData(SecuritySettings settings) {
        apiKeyField.setText(settings.getPyupApiKey());
        safetyButtonGroup.clearSelection();
        if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Disabled)
            radioButton1.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Bundled)
            radioButton2.setSelected(true);
        else if (settings.getSafetyDbMode() == SecuritySettings.SafetyDbType.Api)
            radioButton3.setSelected(true);
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
        else
            return SecuritySettings.SafetyDbType.Bundled;
    }


    public void itemStateChanged(ItemEvent itemEvent) {
        if (apiKeyField == null)
            return;
        if (itemEvent.getSource() == radioButton3) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                apiKeyField.setEnabled(true);
        } else if (itemEvent.getSource() == radioButton1 || itemEvent.getSource() == radioButton2) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                apiKeyField.setEnabled(false);
        }
    }

    private void createUIComponents() {
        radioButton1 = new JRadioButton();
        radioButton1.addItemListener(this);
        radioButton2 = new JRadioButton();
        radioButton2.addItemListener(this);
        radioButton3 = new JRadioButton();
        radioButton3.addItemListener(this);
    }
}
