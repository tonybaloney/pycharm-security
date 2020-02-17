package security.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import javax.swing.*

class SecuritySettingsConfigurable : SearchableConfigurable, Configurable.NoScroll {
    private var settingsPanel: SecuritySettingsPane = SecuritySettingsPane()

    @Nls
    override fun getDisplayName(): String? {
        return "Python Security"
    }

    override fun getHelpTopic(): String? {
        return null
    }

    override fun createComponent(): JComponent? {
        settingsPanel.setData(SecuritySettings.instance)
        return settingsPanel.getPanel()
    }

    override fun isModified(): Boolean {
        return settingsPanel.isModified(SecuritySettings.instance)
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        settingsPanel.storeSettings(SecuritySettings.instance)
    }

    override fun reset() {
        // TODO
    }

    override fun disposeUIResources() {
    }

    override fun getId(): String {
        return "org.tonybaloney.security.pycharm-security.settings.SecuritySettingsConfigurable"
    }
}