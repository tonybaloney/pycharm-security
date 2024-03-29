package security.packaging

import com.google.common.collect.Sets
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.PyPackage
import com.jetbrains.python.packaging.PyPackageManager
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import security.settings.SecuritySettings

object PyPackageSecurityScan {
    private var NOTIFICATION_GROUP: NotificationGroup? = NotificationGroupManager.getInstance().getNotificationGroup("pythonsecurity.checker")

    fun checkPackages(project: Project): Boolean?{
        val pythonSdks = getPythonSdks(project)
        if (pythonSdks.isEmpty()){
            returnError(project)
            return null
        }
        try {
            when (SecuritySettings.instance.safetyDbMode) {
                SecuritySettings.SafetyDbType.Disabled -> return false
                SecuritySettings.SafetyDbType.Bundled -> { checkPackagesInSdks(pythonSdks, project, SafetyDbChecker()); notifyTerms(project) }
                SecuritySettings.SafetyDbType.Api -> checkPackagesInSdks(pythonSdks, project, SafetyDbChecker(SecuritySettings.instance.pyupApiKey))
                SecuritySettings.SafetyDbType.Custom -> checkPackagesInSdks(pythonSdks, project, SafetyDbChecker())
                SecuritySettings.SafetyDbType.Snyk -> checkPackagesInSdks(pythonSdks, project, SnykChecker(SecuritySettings.instance.snykApiKey, SecuritySettings.instance.snykOrgId))
                SecuritySettings.SafetyDbType.Pypi -> checkPackagesInSdks(pythonSdks, project, PypiChecker())
            }
            return true
        } catch (ex: PackageCheckerLoadException){
            backendError(project, ex.message)
            return null
        }
    }

    fun checkPackagesInSdks(pythonSdks: Set<Sdk>, project: Project, packageChecker: PackageChecker): Int {
        var total = 0
        pythonSdks.forEach { sdk ->
            val packageManager = PyPackageManager.getInstance(sdk)
            packageManager.refreshAndGetPackages(true)
            total += inspectLocalPackages(packageManager, project, packageChecker) ?: 0
        }
        return total
    }

    private suspend fun collectPackages (packageChecker: PackageChecker, packages: List<PyPackage?>) : Collection<List<PackageIssue>>
    {
        val matches = packages.filter { packageChecker.hasMatch(it) }
        val tasks= runBlocking {
            matches.map {
                this.async {
                        packageChecker.getMatches(it)
                }
            }
        }
        return tasks.map{ it.await() }
    }

    fun inspectLocalPackages(packageManager: PyPackageManager, project: Project, packageChecker: PackageChecker): Int? {
        var matches = 0
        if (packageManager.packages == null){
            returnError(project)
            return null
        }
        val packages: List<PyPackage> = packageManager.packages!!

        runBlocking {
            collectPackages(packageChecker, packages).forEach { issues ->
                matches += issues.size
                issues.forEach { issue -> showFoundIssueWarning(issue.pyPackage, issue, project) }
            }
        }
        if (matches == 0) {
            showNoMatchesInformation(project)
            return 0
        }
        showTotalIssuesWarning(matches, project)
        return matches
    }

    private fun backendError(project: Project, message: String?){
        NOTIFICATION_GROUP
                ?.createNotification("Could not check Python packages",
                        "Could not fetch API to validate records. Check your API details.\n$message",
                        NotificationType.ERROR)
                ?.notify(project)
    }

    private fun returnError(project: Project){
        NOTIFICATION_GROUP
                ?.createNotification("Could not check Python packages of ${project.name}",
                        "Could not verify security of Python packages, unable to locate configured Python Interpreter. Please configure your interpreter.",
                        NotificationType.INFORMATION)
                ?.notify(project)
    }

    private fun showTotalIssuesWarning(matches: Int, project: Project) {
        NOTIFICATION_GROUP
                ?.createNotification("Completed checking packages of ${project.name}",
                        "Found $matches potential security issues with your installed packages.",
                        NotificationType.WARNING)
                ?.notify(project)
    }

    private fun showNoMatchesInformation(project: Project) {
        NOTIFICATION_GROUP
                ?.createNotification("Completed checking packages of ${project.name}",
                        "Found no known security issues with your installed packages.",
                        NotificationType.INFORMATION)
                ?.notify(project)
    }

    private fun showFoundIssueWarning(pack: PyPackage?, issue: PackageIssue, project: Project) {
        val not = NOTIFICATION_GROUP
                ?.createNotification("Found Vulnerability in $pack package in ${project.name}",
                        issue.getMessage(),
                        NotificationType.WARNING

                )
        not?.setListener(NotificationListener.URL_OPENING_LISTENER)
        not?.notify(project)
    }

    private fun notifyTerms(project: Project) {
        NOTIFICATION_GROUP
            ?.createNotification("This check uses a free version of the PyUp.io database",
                "This check uses a free version of the PyUp.io database, vulnerabilities found in the last 30 days may not be reported. If you want to check against the real-time vulnerability database, please sign up for a PyUp.io subscription.",
                NotificationType.INFORMATION)
            ?.notify(project)
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