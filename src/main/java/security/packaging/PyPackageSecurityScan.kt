package security.packaging

import com.google.common.collect.Sets
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.PyPackage
import com.jetbrains.python.packaging.PyPackageManager
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil
import security.settings.SecuritySettings

object PyPackageSecurityScan {
    var NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Python Package Security Checker")

    fun checkPackages(project: Project){
        val pythonSdks = getPythonSdks(project)
        if (pythonSdks.isEmpty()){
            returnError(project)
            return
        }
        try {
            if (SecuritySettings.instance.safetyDbMode == SecuritySettings.SafetyDbType.Disabled)
                return
            else if (SecuritySettings.instance.safetyDbMode == SecuritySettings.SafetyDbType.Bundled)
                checkPackagesInSdks(pythonSdks, project, SafetyDbChecker())
            else if (SecuritySettings.instance.safetyDbMode == SecuritySettings.SafetyDbType.Api)
                checkPackagesInSdks(pythonSdks, project, SafetyDbChecker(SecuritySettings.instance.pyupApiKey, SecuritySettings.instance.pyupApiUrl))
            else if (SecuritySettings.instance.safetyDbMode == SecuritySettings.SafetyDbType.Custom)
                checkPackagesInSdks(pythonSdks, project, SafetyDbChecker("", SecuritySettings.instance.pyupCustomUrl))

        } catch (ex: SafetyDbLoadException){
            backendError(project, ex.message)
        }
    }

    fun checkPackagesInSdks(pythonSdks: Set<Sdk>, project: Project, packageChecker: SafetyDbChecker) {
        for (sdk in pythonSdks) {
            val packageManager = PyPackageManager.getInstance(sdk)
            packageManager.refreshAndGetPackages(true)
            inspectLocalPackages(packageManager, project, packageChecker)
        }
    }

    fun inspectLocalPackages(packageManager: PyPackageManager, project: Project, packageChecker: SafetyDbChecker) {
        var matches = 0
        if (packageManager.packages == null) {
            returnError(project)
            return
        }
        packageManager.packages!!.filter { packageChecker.hasMatch(it) }.forEach { pack ->
            packageChecker.getMatches(pack).forEach { issue ->
                matches++
                showFoundIssueWarning(pack, issue, project)
            }
        }
        if (matches == 0)
            showNoMatchesInformation(project)
        else
            showTotalIssuesWarning(matches, project)
    }

    private fun backendError(project: Project, message: String?){
        NOTIFICATION_GROUP
                .createNotification("Could not check Python packages", null,
                        "Could not fetch SafetyDB to validate records. Check your API details.\n$message",
                        NotificationType.ERROR)
                .notify(project)
    }

    private fun returnError(project: Project){
        NOTIFICATION_GROUP
                .createNotification("Could not check Python packages", null,
                        "Could not verify security of Python packages, unable to locate configured Python Interpreter. Please configure your interpreter.",
                        NotificationType.INFORMATION)
                .notify(project)
    }

    private fun showTotalIssuesWarning(matches: Int, project: Project) {
        NOTIFICATION_GROUP
                .createNotification("Completed checking packages", null,
                        "Found $matches potential security issues with your installed packages.",
                        NotificationType.WARNING)
                .notify(project)
    }

    private fun showNoMatchesInformation(project: Project) {
        NOTIFICATION_GROUP
                .createNotification("Completed checking packages", null,
                        "Found no known security issues with your installed packages.",
                        NotificationType.INFORMATION)
                .notify(project)
    }

    private fun showFoundIssueWarning(pack: PyPackage?, issue: SafetyDbChecker.SafetyDbRecord, project: Project) {
        NOTIFICATION_GROUP
                .createNotification("Found Security Vulnerability in $pack package", null,
                        renderMessage(issue),
                        NotificationType.WARNING,
                        NotificationListener.URL_OPENING_LISTENER
                ).notify(project)
    }

    fun renderMessage(issue: SafetyDbChecker.SafetyDbRecord) : String {
        return if (issue.cve.isNullOrEmpty()){
            issue.advisory
        } else {
            "${issue.advisory}<br>See <a href='https://cve.mitre.org/cgi-bin/cvename.cgi?name=${issue.cve}'>${issue.cve}</a>"
        }
    }

    fun getPythonSdks(project: Project): Set<Sdk> {
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