package bandit;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name="BanditService")
public class ProjectService implements PersistentStateComponent<ProjectService> {
    private static final Logger LOGGER = Logger.getInstance(ProjectService.class.getPackage().getName());

    public String executable = "";
    public String config = "";

    public ProjectService() {
        LOGGER.debug("Bandit ProjectService instantiated.");
    }

    static ProjectService getInstance(Project project) {
        return ServiceManager.getService(project, ProjectService.class);
    }

    @Nullable
    @Override
    public ProjectService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
