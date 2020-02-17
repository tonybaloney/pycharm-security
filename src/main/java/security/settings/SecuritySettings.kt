package security.settings

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "PythonSecuritySettings", storages = [Storage("python-security.xml")])
class SecuritySettings : PersistentStateComponent<SecuritySettings.State> {
    private val state: State = State()

    var pyupApiKey: String
        get() = state.PYUP_API_KEY
        set(value) {
            state.PYUP_API_KEY = value
        }

    var safetyDbMode: SafetyDbType
        get() = state.SAFETY_DB_MODE
        set(value) {
            state.SAFETY_DB_MODE = value
        }

    override fun getState(): State = state

    override fun loadState(state: SecuritySettings.State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    @Suppress("PropertyName")
    class State {
        @JvmField
        var PYUP_API_KEY: String = ""
        var SAFETY_DB_MODE: SafetyDbType = SafetyDbType.Bundled
    }

    enum class SafetyDbType {
        Disabled,
        Bundled,
        Api
    }

    companion object {
        @JvmStatic
        val instance: SecuritySettings
            get() = ServiceManager.getService(SecuritySettings::class.java)
    }
}