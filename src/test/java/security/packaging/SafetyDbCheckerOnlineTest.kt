package security.packaging

import com.jetbrains.python.packaging.PyPackage
import com.jetbrains.python.packaging.PyRequirement
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SafetyDbCheckerOnlineTest {
    @Test
    fun `test online resource`(){
        val checker = SafetyDbChecker("key", "https://github.com/pyupio/safety-db/raw/2020.4.14/data/")
        assertTrue(checker.hasMatch(PyPackage("requests", "1.0.0", "", listOf<PyRequirement>())))
    }

    @Test
    fun `test online resource bad URL`(){
        assertThrows(PackageCheckerLoadException::class.java) { SafetyDbChecker("key", "https://bad.bad/bad/") }
    }
}