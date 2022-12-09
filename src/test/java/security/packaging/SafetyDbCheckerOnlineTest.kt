package security.packaging

import com.jetbrains.python.packaging.PyPackage
import com.jetbrains.python.packaging.PyRequirement
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SafetyDbCheckerOnlineTest {
    @Test
    fun `test online resource`(){
        val checker = SafetyDbChecker()
        assertTrue(checker.hasMatch(PyPackage("requests", "1.0.0", "", listOf<PyRequirement>())))
    }
}