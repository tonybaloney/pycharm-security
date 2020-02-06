package security.packaging

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task

class SafetyDbCheckerAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        if (e.project?.isDisposed!!) return
        ProgressManager.getInstance().run(object : Task.Backgroundable(e.project, "Checking Python packages for known CVEs", false) {
            override fun run(indicator: ProgressIndicator) {
                PyPackageSecurityScan.checkPackages(project)
            }
        })
    }
}