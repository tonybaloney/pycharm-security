package security.cli

import com.intellij.analysis.AnalysisScope
import com.intellij.codeInspection.CommandLineInspectionProgressReporter
import com.intellij.codeInspection.CommandLineInspectionProjectConfigurator
import com.intellij.facet.FacetManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.facet.PythonFacetType
import com.jetbrains.python.sdk.PythonSdkUpdater
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.findAllPythonSdks
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path


class SecurityPluginCommandLineInspectionProjectConfigurator : CommandLineInspectionProjectConfigurator {
    override fun isApplicable(projectPath: Path, logger: CommandLineInspectionProgressReporter): Boolean {
        val sdks = PythonSdkUtil.getAllSdks()
        logger.reportMessage(3, "Checking if Python inspections are worth doing..")
        for (sdk in sdks){
            logger.reportMessage(3, "Found SDK : ${sdk.name}")
        }
        return if (sdks.isEmpty()) false else try {
            val hasAnyPythonFiles = Files.walk(projectPath).anyMatch { f: Path -> f.toString().endsWith(".py") }
            if (!hasAnyPythonFiles) {
                logger.reportMessage(3, "Skipping Python interpreter autodetection because the project doesn't contain any Python files")
            }
            hasAnyPythonFiles
        } catch (e: IOException) {
            false
        }
    }

    override fun configureEnvironment(projectPath: Path, logger: CommandLineInspectionProgressReporter) {
        logger.reportMessage(3, "Python environment configuration...")
        val sdks = PythonSdkUtil.getAllSdks()
        logger.reportMessage(3, "Python interpreters detected:")
        for (sdk in sdks) {
            logger.reportMessage(3, "SDK for environment $sdk.homePath")
        }
        if (sdks.isNotEmpty()) {
            val detectedSdks = findAllPythonSdks(projectPath)
            if (detectedSdks.size > 0) {
                logger.reportMessage(3, "Python SDKs detected:")
                for (sdk in detectedSdks) {
                    logger.reportMessage(3, sdk.homePath)
                }
                val sdk = detectedSdks[0]
                ApplicationManager.getApplication().runWriteAction {
                    logger.reportMessage(1, "Setting up interpreter : $sdk.name")
                    ProjectJdkTable.getInstance().addJdk(sdk)
                }
                PythonSdkUpdater.update(sdk, null, null, null)
            } else {
                logger.reportMessage(1, "ERROR: Can't find Python interpreter")
            }
        }
    }

    override fun configureProject(project: Project, scope: AnalysisScope, logger: CommandLineInspectionProgressReporter) {
        val sdks = PythonSdkUtil.getAllSdks()
        logger.reportMessage(3, "Looking for Python SDKs in Project..")
        for (sdk in sdks){
            logger.reportMessage(3, "Found SDK for project : ${sdk.name}")
        }
        if (!sdks.isEmpty()) {
            val facetType = PythonFacetType.getInstance()
            for (f in scope.files) {
                if (FileTypeRegistry.getInstance().isFileOfType(f!!, PythonFileType.INSTANCE)) {
                    val m = ModuleUtilCore.findModuleForFile(f, project)
                    if (m != null && FacetManager.getInstance(m).getFacetByType(facetType.id) == null) {
                        ApplicationManager.getApplication().runWriteAction { FacetManager.getInstance(m).addFacet(facetType, facetType.presentableName, null) }
                    } else {
                        logger.reportMessage(3, "No module detected..")
                    }
                }
            }
        }
    }
}