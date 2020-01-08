package security.packaging

import com.google.common.collect.Sets
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.PyBundle
import com.jetbrains.python.packaging.PyPackageManager
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUpdater
import com.jetbrains.python.sdk.PythonSdkUtil

object PyPackageSecurityScan {
    private val LOG = Logger.getInstance(PyPackageSecurityScan::class.java)
    private val NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Python Package Security Checker")

    fun checkPackages(project: Project){
        for (sdk in getPythonSdks(project)) {
            val packageManager = PyPackageManager.getInstance(sdk)
            packageManager.refreshAndGetPackages(true)
            if (packageManager.packages == null)
            {
                LOG.error("Could not locate package cache to check for security exceptions.")
                return;
            }
            var packageChecker = SafetyDbChecker()

            for (pack in packageManager.packages!!){
                if (packageChecker.hasMatch(pack)){
                    for (issue in packageChecker.getMatches(pack)) {
                        NOTIFICATION_GROUP
                                .createNotification("Found Security Vulnerability in $pack package", null,
                                        issue.advisory,
                                        NotificationType.WARNING)
                                .notify(project)
                    }
                }
            }
        }
    }

    private fun getPythonSdks(project: Project): Set<Sdk> {
        val pythonSdks: MutableSet<Sdk> = Sets.newLinkedHashSet()
        for (module in ModuleManager.getInstance(project).modules) {
            val sdk = PythonSdkUtil.findPythonSdk(module)
            if (sdk != null && sdk.sdkType is PythonSdkType) {
                pythonSdks.add(sdk)
            }
        }
        return pythonSdks
    }
}