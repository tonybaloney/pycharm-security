package security.packaging

import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SafetyDbCheckerActionTest : BasePlatformTestCase() {

    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `test action loaded`() {
        var action = SafetyDbCheckerAction()
        action.actionPerformed(TestActionEvent())
        TestCase.assertTrue(true)
    }

    @Test
    fun `test startup task loaded`() {
        var action = PythonPackageVulnerabilityStartupTask()
        action.runActivity(this.project)
        TestCase.assertTrue(true)
    }
}