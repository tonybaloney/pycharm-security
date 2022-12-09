package security.packaging

import com.jetbrains.python.packaging.PyPackage
import org.junit.After
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.validateMockitoUsage
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.io.StringReader
import kotlinx.coroutines.*


internal class SafetyDbCheckerTest {
    lateinit var instance: SafetyDbChecker

    @BeforeEach
    fun setUp() {
        val testLookupData = """
            {
            "meta": {
                "advisory": "PyUp.io metadata",
                "timestamp": 1666399806,
                "last_updated": "2022-10-22 00:50:06",
                "base_domain": "https://pyup.io",
                "attribution": "Licensed under CC-BY-4.0 by pyup.io."
            },
            "vulnerable_packages": 
                {
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
                  ],
                  "gunicorn": [
                    "<19.10.0",
                    "<19.4.0",
                    "<19.5.0",
                    ">=20.0.0,<20.0.1"
                  ]
                }
            }
        """.trimIndent()
        val testData = """
            {
            "meta": {
                "advisory": "PyUp.io metadata",
                "timestamp": 1666399806,
                "last_updated": "2022-10-22 00:50:06",
                "base_domain": "https://pyup.io",
                "attribution": "Licensed under CC-BY-4.0 by pyup.io."
            },
            "vulnerable_packages": 
                {
                 "aiocouchdb": [
                  {
                    "specs": [
                      "<0.6.0"
                    ],
                    "type": "pve",
                    "cve": "PVE-2021-25612",
                    "advisory": "aiocouchdb 0.6.0 now correctly set members for data...",
                    "id": "pyup.io-25612",
                    "transitive": false,
                    "more_info_path": "/v/25612/fe5/"
                  }
                ],
                "gunicorn": [
                  {
                    "specs": [
                      "<19.10.0",
                      ">=20.0.0,<20.0.1"
                    ],
                    "type": "pve",
                    "cve": "PVE-2021-40104",
                    "advisory": "Gunicorn 20.0.1 fixes chunked encoding support to p...",
                    "id": "pyup.io-40104",
                    "transitive": false,
                    "more_info_path": "/v/40104/fe5/"
                  },
                  {
                    "specs": [
                      "<19.4.0"
                    ],
                    "type": "pve",
                    "cve": "PVE-2021-40103",
                    "advisory": "Gunicorn 19.4.0 includes a security fix to raise 'I...",
                    "id": "pyup.io-40103",
                    "transitive": false,
                    "more_info_path": "/v/40103/fe5/"
                  },
                  {
                    "specs": [
                      "<19.5.0"
                    ],
                    "type": "cve",
                    "cve": "CVE-2018-1000164",
                    "advisory": "Gunicorn 19.5.0 includes a fix for CVE-2018-1000164...",
                    "id": "pyup.io-40105",
                    "transitive": false,
                    "more_info_path": "/v/40105/fe5/"
                  }
                ]
                }
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
    fun `test get matches with aiocouch known vulnerability`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "aiocouchdb"
            on { version } doReturn "0.1.0"
        }
        assertTrue(instance.hasMatch(testPackage))

        val matches: List<SafetyDbChecker.SafetyDbIssue> = runBlocking { instance.getMatches(testPackage) }
        verify(testPackage, times(2)).name
        verify(testPackage, times(2)).version

        assertEquals(matches.size, 1)
        assertEquals(matches[0].record.specs.size, 1)
        assertEquals(matches[0].record.specs[0], "<0.6.0")
        assertEquals(matches[0].record.advisory, "aiocouchdb 0.6.0 now correctly set members for data...")
        assertEquals(matches[0].record.cve, "PVE-2021-25612")
        assertEquals(matches[0].record.id, "pyup.io-25612")
        assertTrue(matches[0].getMessage().isNotBlank())
    }

    @Test
    fun `test get matches with gunicorn known vulnerability`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "gunicorn"
            on { version } doReturn "20.0.0"
        }
        assertTrue(instance.hasMatch(testPackage))

        val matches: List<SafetyDbChecker.SafetyDbIssue> = runBlocking { instance.getMatches(testPackage) }

        assertEquals(matches.size, 1)
        assertEquals(matches[0].record.specs.size, 2)
        assertEquals(matches[0].record.specs[0], "<19.10.0")
        assertEquals(matches[0].record.specs[1], ">=20.0.0,<20.0.1")
        assertEquals(matches[0].record.advisory, "Gunicorn 20.0.1 fixes chunked encoding support to p...")
        assertEquals(matches[0].record.cve, "PVE-2021-40104")
        assertEquals(matches[0].record.id, "pyup.io-40104")
        assertTrue(matches[0].getMessage().isNotBlank())
    }

    @Test
    fun `test get multiple matches with gunicorn known vulnerabilities`() {
        val testPackage = mock<PyPackage> {
            on { name } doReturn "gunicorn"
            on { version } doReturn "19.0.0"
        }
        assertTrue(instance.hasMatch(testPackage))

        val matches: List<SafetyDbChecker.SafetyDbIssue> = runBlocking { instance.getMatches(testPackage) }

        assertEquals(matches.size, 3)
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