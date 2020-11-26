package security.packaging

import com.jetbrains.python.packaging.PyPackage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SnykCheckerTest {
    private val testPackage = PyPackage("rsa", "3.4.0", null, listOf())
    private val testUrl = "https://private-anon-fb110a8acb-snyk.apiary-mock.com/api/v1"

    @Test
    fun `test rsa package has match on test API`() {
        val checker = SnykChecker("test", "test")
        // Use the mock API
        checker.baseUrl = testUrl
        assertTrue(checker.hasMatch(testPackage))
    }

    @Test
    suspend fun `test single match`() {
        val checker = SnykChecker("test", "test")
        // Use the mock API
        checker.baseUrl = testUrl
        assertTrue(checker.hasMatch(testPackage))
        val match = checker.getMatches(testPackage)
        assertEquals(match.first().record.id, "SNYK-PYTHON-RSA-40541")
        assertTrue(match.first().getMessage().isNotBlank())
    }
}