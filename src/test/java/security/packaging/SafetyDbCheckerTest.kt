package security.packaging

import com.jetbrains.python.packaging.PyPackage
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.validateMockitoUsage
import java.io.StringReader


internal class SafetyDbCheckerTest {
    lateinit var instance: SafetyDbChecker

    @BeforeEach
    fun setUp() {
        val testLookupData = """
            {
             "test_package": [],
             "aiocouchdb": [
                "<0.6.0"
                ],
             "bananas": [
                "<1.0.0,>=0.5.0"
             ]
            }
        """.trimIndent()
        val testData = """
            {
             "aiocouchdb": [
                 {
                    "advisory": "aiocouchdb 0.6.0 now correctly set members for database security.",
                    "cve": null,
                    "id": "pyup.io-25612",
                    "specs": [
                        "<0.6.0"
                    ],
                    "v": "<0.6.0"
                }
                ]
            }
        """.trimIndent()
        val lookupReader = StringReader(testLookupData)
        val databaseReader = StringReader(testData)
        this.instance = SafetyDbChecker(lookupReader = lookupReader, databaseReader = databaseReader)
    }

    @After
    fun validate() {
        validateMockitoUsage()
    }

    @Test
    fun testVulnPackageHasMatch() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "aiocouchdb"
            on { version } doReturn "0.1.0"
        }
        assertTrue(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(1)).version
    }

    @Test
    fun testVulnPackageWithComplexRangeHasMatch() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "bananas"
            on { version } doReturn "0.6.0"
        }
        assertTrue(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(2)).version
    }

    @Test
    fun testVulnPackageWithComplexRangeDoesNotHaveMatch() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "bananas"
            on { version } doReturn "0.4.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(2)).version
    }

    @Test
    fun testMissingPackageDoesNotHaveMatch() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "madeup"
            on { version } doReturn "0.1.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
    }

    @Test
    fun getMatches() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "aiocouchdb"
            on { version } doReturn "0.1.0"
        }
        assertTrue(instance.hasMatch(testPackage))

        val matches = instance.getMatches(testPackage)
        verify(testPackage, times(2)).name
        verify(testPackage, times(2)).version

        assertEquals(matches.size, 1)
        assertEquals(matches[0].v, "<0.6.0")
        assertEquals(matches[0].advisory, "aiocouchdb 0.6.0 now correctly set members for database security.")
        assertNull(matches[0].cve)
        assertEquals(matches[0].id, "pyup.io-25612")
        assertEquals(matches[0].specs.size, 1)
        assertEquals(matches[0].specs[0], "<0.6.0")
    }
}