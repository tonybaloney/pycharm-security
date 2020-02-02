package security.packaging

import com.google.common.collect.Sets
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.PyPackageManager
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil

object PyPackageSecurityScan {
    var NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Python Package Security Checker")

    private fun returnError(project: Project){
        NOTIFICATION_GROUP
                .createNotification("Could not check Python packages", null,
                        "Could not verify security of Python packages, unable to locate configured Python Interpreter. Please configure your interpreter.",
                        NotificationType.INFORMATION)
                .notify(project)
    }

    fun checkPackages(project: Project){
        var pythonSdks = getPythonSdks(project)
        if (pythonSdks.isEmpty()){
            returnError(project)
            return
        }
        for (sdk in pythonSdks) {
            val packageManager = PyPackageManager.getInstance(sdk)
            packageManager.refreshAndGetPackages(true)
            if (packageManager.packages == null)
            {
                NOTIFICATION_GROUP
                        .createNotification("Cannot check package security", null,
                                "Could not locate the package manager.",
                                NotificationType.INFORMATION)
                        .notify(project)
                return;
            }
            var packageChecker = SafetyDbChecker()
            var matches = 0;
            if (packageManager.packages == null){
                returnError(project)
                return
            }
            for (pack in packageManager.packages!!){
                if (packageChecker.hasMatch(pack)) {
                    for (issue in packageChecker.getMatches(pack)) {
                        matches++;
                        NOTIFICATION_GROUP
                                .createNotification("Found Security Vulnerability in $pack package", null,
                                        renderMessage(issue),
                                        NotificationType.WARNING,
                                        NotificationListener.URL_OPENING_LISTENER
                                        ).notify(project)
                    }
                }
            }
            if (matches == 0){
                NOTIFICATION_GROUP
                        .createNotification("Completed checking packages", null,
                                "Found no known security issues with your installed packages.",
                                NotificationType.INFORMATION)
                        .notify(project)
            } else {
                NOTIFICATION_GROUP
                        .createNotification("Completed checking packages", null,
                                "Found $matches potential security issues with your installed packages.",
                                NotificationType.WARNING)
                        .notify(project)
            }

        }
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