package security

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SimpleTest: BasePlatformTestCase() {
    /// Test that the SDK is setup and testing framework is ok.

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun testNothing() {
        assertTrue(true)
    }
}