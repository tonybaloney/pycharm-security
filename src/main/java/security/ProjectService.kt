package security

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "BanditService")
class ProjectService : PersistentStateComponent<ProjectService?> {
    override fun getState(): ProjectService? {
        return this
    }

    override fun loadState(state: ProjectService) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        private val LOGGER = Logger.getInstance(ProjectService::class.java.getPackage().name)
        fun getInstance(project: Project?): ProjectService {
            return ServiceManager.getService(project!!, ProjectService::class.java)
        }
    }

    init {
        LOGGER.debug("Python Security ProjectService instantiated.")
    }
}