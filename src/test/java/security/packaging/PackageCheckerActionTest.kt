package security.packaging

import com.intellij.testFramework.TestActionEvent.createTestEvent
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PackageCheckerActionTest : BasePlatformTestCase() {

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
        val action = PackageCheckerAction()
        action.actionPerformed(createTestEvent())
        TestCase.assertTrue(true)
    }

    @Test
    suspend fun `test startup task loaded`() {
        val action = PythonPackageVulnerabilityStartupTask()
        action.execute(this.project)
        TestCase.assertTrue(true)
    }
}