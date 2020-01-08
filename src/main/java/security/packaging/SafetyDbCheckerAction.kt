package security.packaging

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task

class SafetyDbCheckerAction: AnAction() {
    private val LOG = Logger.getInstance(PythonPackageVulnerabilityStartupTask::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        val application = ApplicationManager.getApplication()
        if (application.isUnitTestMode) return
        if (e.project?.isDisposed!!) return

        ProgressManager.getInstance().run(object : Task.Backgroundable(e.project, "Checking Python packages for known CVEs", false) {
            override fun run(indicator: ProgressIndicator) {
                LOG.info("Checking Python packages for known security vulnerabilities.")
                PyPackageSecurityScan.checkPackages(project)
            }
        })
    }
}