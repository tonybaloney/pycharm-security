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
             "$\\meta": {
                "advisory": "PyUp.io metadata",
                "timestamp": 1619848801
             },
             "test_package": [],
             "aiocouchdb": [
                "<0.6.0"
                ],
             "bananas": [
                "<1.0.0,>=0.5.0"
             ],
             "eee": [
                 "===0.5.0"
              ],
              "ee": [
                  "==0.5.0"
               ],
               "lte": [
                   "<=0.5.0"
              ],
              "ne": [
                   "!=0.5.0"
              ],
              "gt": [
                     ">0.5.0"
                ],
              "co": [
                   "~=0.5.0"
              ],
              "invalid": [
                "!!22"
              ],
              "django": [
                "==0.1.0"
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
    fun `test vulnerable package with complex range does not have match 1`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "bananas"
            on { version } doReturn "0.4.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(2)).version
    }

    @Test
    fun `test vulnerable package with complex range does not have match 2`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "eee"
            on { version } doReturn "0.4.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(1)).version
    }

    @Test
    fun `test vulnerable package with complex range does not have match 3`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "ee"
            on { version } doReturn "0.4.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(1)).version
    }

    @Test
    fun `test vulnerable package with complex range does not have match 4`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "lte"
            on { version } doReturn "0.6.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(1)).version
    }

    @Test
    fun `test vulnerable package with complex range does not have match 5`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "ne"
            on { version } doReturn "0.5.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(1)).version
    }

    @Test
    fun `test vulnerable package with complex range does not have match 6`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "gt"
            on { version } doReturn "0.5.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(1)).version
    }

    @Test
    fun `test vulnerable package with complex range does not have match 7`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "co"
            on { version } doReturn "0.5.1"
        }
        assertTrue(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
        verify(testPackage, times(1)).version
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
    fun `test invalid specifier`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "invalid"
            on { version } doReturn "0.1.0"
        }
        assertFalse(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
    }

    @Test
    fun `test capitalized`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "Django"
            on { version } doReturn "0.1.0"
        }
        assertTrue(instance.hasMatch(testPackage))
        verify(testPackage, times(1)).name
    }

    @Test
    suspend fun getMatches() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "aiocouchdb"
            on { version } doReturn "0.1.0"
        }
        assertTrue(instance.hasMatch(testPackage))

        val matches: List<SafetyDbChecker.SafetyDbIssue> = instance.getMatches(testPackage)
        verify(testPackage, times(2)).name
        verify(testPackage, times(2)).version

        assertEquals(matches.size, 1)
        assertEquals(matches[0].record.v, "<0.6.0")
        assertEquals(matches[0].record.advisory, "aiocouchdb 0.6.0 now correctly set members for database security.")
        assertNull(matches[0].record.cve)
        assertEquals(matches[0].record.id, "pyup.io-25612")
        assertEquals(matches[0].record.specs.size, 1)
        assertEquals(matches[0].record.specs[0], "<0.6.0")
        assertTrue(matches[0].getMessage().isNotBlank())
    }

    @Test
    fun `test whitespace locator`() {
        assertEquals(instance.findFirstNotWhiteSpaceAfter("   x ", 0), 3)
    }

    @Test
    fun `test whitespace locator 2`() {
        assertEquals(instance.findFirstNotWhiteSpaceAfter("   ", 0), 3)
    }
}