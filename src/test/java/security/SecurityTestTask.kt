package security

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.python.packaging.PyPackageManager
import com.jetbrains.python.sdk.PythonSdkType

open class SecurityTestTask: BasePlatformTestCase() {
    lateinit var myPythonSdk: PythonSdkType
    lateinit var mySdk: Sdk

    fun loadEnvironment() {
        myPythonSdk = PythonSdkType.getInstance()
        mySdk = SdkConfigurationUtil.findOrCreateSdk(myPythonSdk.comparator)!!
        PythonSdkType.activateVirtualEnv(myPythonSdk.name)
        PyPackageManager.getInstance(mySdk).install("httpx")
    }

}