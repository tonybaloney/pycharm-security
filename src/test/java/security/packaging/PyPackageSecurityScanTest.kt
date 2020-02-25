package security.packaging

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.PyPackage
import com.jetbrains.python.packaging.PyPackageManager
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask
import java.io.StringReader

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PyPackageSecurityScanTest: SecurityTestTask() {
    lateinit var instance: SafetyDbChecker

    @BeforeAll
    override fun setUp() {
        val testLookupData = """
            {
             "apples": [
                "<0.6.0"
                ],
             "bananas": [
                "<1.0.0,>=0.5.0"
             ]
            }
        """.trimIndent()
        val testData = """
            {
             "apples": [
                 {
                    "advisory": "apple pips taste nasty.",
                    "cve": null,
                    "id": "pyup.io-25612",
                    "specs": [
                        "<0.6.0"
                    ],
                    "v": "<0.6.0"
                }
                ],
              "bananas": [
                 {
                    "advisory": "green bananas give you stomach ache.",
                    "cve": "CVE-1234",
                    "id": "pyup.io-253",
                    "specs": [
                        "<1.0.0,>=0.5.0"
                    ],
                    "v": "<1.0.0,>=0.5.0"
                }
                ]
            }
        """.trimIndent()
        val lookupReader = StringReader(testLookupData)
        val databaseReader = StringReader(testData)
        this.instance = SafetyDbChecker(lookupReader = lookupReader, databaseReader = databaseReader)
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `test no python sdk raises info message`(){
        val mockNotification = mock<Notification> {
            on { notify(any()) } doAnswer {}
        }
        val mockNotificationGroup = mock<NotificationGroup> {
            on { createNotification( any(), anyOrNull(), any(), any<NotificationType>(), anyOrNull())} doReturn(mockNotification)
        }
        PyPackageSecurityScan.NOTIFICATION_GROUP = mockNotificationGroup
        PyPackageSecurityScan.checkPackages(project)
        verify(mockNotificationGroup, times(1)).createNotification( eq("Could not check Python packages"), anyOrNull(), any(), eq(NotificationType.INFORMATION), anyOrNull())
        verify(mockNotification, times(1)).notify(project)
    }

    @Test
    fun `test check packages`(){
        val mockNotification = mock<Notification> {
            on { notify(any()) } doAnswer {}
        }
        val mockNotificationGroup = mock<NotificationGroup> {
            on { createNotification( any(), anyOrNull(), any(), any<NotificationType>(), anyOrNull())} doReturn(mockNotification)
        }
        val mockSdk = mock<Sdk> {
            on { name } doReturn ("test")
            on { homePath } doReturn (".")
        }
        PyPackageSecurityScan.NOTIFICATION_GROUP = mockNotificationGroup
        PyPackageSecurityScan.checkPackagesInSdks(setOf(mockSdk), project, instance)
        verify(mockNotificationGroup, times(1)).createNotification( eq("Completed checking packages"), anyOrNull(), any(), eq(NotificationType.INFORMATION), anyOrNull())
        verify(mockNotification, times(1)).notify(project)
    }

    @Test
    fun `test null packages`(){
        val mockNotification = mock<Notification> {
            on { notify(any()) } doAnswer {}
        }
        val mockNotificationGroup = mock<NotificationGroup> {
            on { createNotification( any(), anyOrNull(), any(), any<NotificationType>(), anyOrNull())} doReturn(mockNotification)
        }
        val mockPackageManager = mock<PyPackageManager> {
            on { packages } doReturn(null)
        }
        PyPackageSecurityScan.NOTIFICATION_GROUP = mockNotificationGroup
        PyPackageSecurityScan.inspectLocalPackages(mockPackageManager, project, instance)
        verify(mockNotificationGroup, times(1)).createNotification( eq("Could not check Python packages"), anyOrNull(), any(), eq(NotificationType.INFORMATION), anyOrNull())
        verify(mockNotification, times(1)).notify(project)
    }

    @Test
    fun `test no packages`(){
        val mockNotification = mock<Notification> {
            on { notify(any()) } doAnswer {}
        }
        val mockNotificationGroup = mock<NotificationGroup> {
            on { createNotification( any(), anyOrNull(), any(), any<NotificationType>(), anyOrNull())} doReturn(mockNotification)
        }
        val mockPackageManager = mock<PyPackageManager> {
            on { packages } doReturn(listOf())
        }
        PyPackageSecurityScan.NOTIFICATION_GROUP = mockNotificationGroup
        PyPackageSecurityScan.inspectLocalPackages(mockPackageManager, project, instance)
        verify(mockNotificationGroup, times(1)).createNotification( eq("Completed checking packages"), anyOrNull(), any(), eq(NotificationType.INFORMATION), anyOrNull())
        verify(mockNotification, times(1)).notify(project)
    }

    @Test
    fun `test ok packages`(){
        val mockNotification = mock<Notification> {
            on { notify(any()) } doAnswer {}
        }
        val mockNotificationGroup = mock<NotificationGroup> {
            on { createNotification( any(), anyOrNull(), any(), any<NotificationType>(), anyOrNull())} doReturn(mockNotification)
        }
        val testPackage1 = mock<PyPackage> {
            on { name } doReturn "good"
            on { version } doReturn "0.4.0"
        }
        val mockPackageManager = mock<PyPackageManager> {
            on { packages } doReturn(listOf(testPackage1))
        }
        PyPackageSecurityScan.NOTIFICATION_GROUP = mockNotificationGroup
        PyPackageSecurityScan.inspectLocalPackages(mockPackageManager, project, instance)
        verify(mockNotificationGroup, times(1)).createNotification( eq("Completed checking packages"), anyOrNull(), any(), eq(NotificationType.INFORMATION), anyOrNull())
        verify(mockNotification, times(1)).notify(project)
        verify(testPackage1, times(1)).name
        verify(mockPackageManager, times(2)).packages
    }

    @Test
    fun `test bad packages`(){
        val mockNotification = mock<Notification> {
            on { notify(any()) } doAnswer {}
        }
        val mockNotificationGroup = mock<NotificationGroup> {
            on { createNotification( any(), anyOrNull(), any(), any<NotificationType>(), anyOrNull())} doReturn(mockNotification)
            on { createNotification( any(), anyOrNull(), any(), any(), any())} doReturn(mockNotification)
        }
        val testPackage1 = mock<PyPackage> {
            on { name } doReturn "apples"
            on { toString() } doReturn "apples"
            on { version } doReturn "0.4.0"
        }
        val testPackage2 = mock<PyPackage> {
            on { name } doReturn "bananas"
            on { toString() } doReturn "bananas"
            on { version } doReturn "0.6.0"
        }
        val mockPackageManager = mock<PyPackageManager> {
            on { packages } doReturn(listOf(testPackage1, testPackage2))
        }
        PyPackageSecurityScan.NOTIFICATION_GROUP = mockNotificationGroup
        PyPackageSecurityScan.inspectLocalPackages(mockPackageManager, project, instance)
        verify(mockNotificationGroup, times(1)).createNotification( eq("Completed checking packages"), anyOrNull(), any(), eq(NotificationType.WARNING), anyOrNull())
        verify(mockNotificationGroup, times(1)).createNotification( eq("Found Security Vulnerability in apples package"), anyOrNull(), any(), eq(NotificationType.WARNING), any())
        verify(mockNotificationGroup, times(1)).createNotification( eq("Found Security Vulnerability in bananas package"), anyOrNull(), any(), eq(NotificationType.WARNING), any())
        verify(mockNotification, times(3)).notify(project)
        verify(testPackage1, times(2)).name
        verify(mockPackageManager, times(2)).packages
    }

    @Test
    fun `test render renderMessage with null cve record`(){
        val record = SafetyDbChecker.SafetyDbRecord("Test is bad", null, "xyz", listOf("<= 1.0.0"), "<= 1.0.0")
        val message = PyPackageSecurityScan.renderMessage(record)
        assertFalse(message.isEmpty())
    }

    @Test
    fun `test render renderMessage with valid cve record`(){
        val record = SafetyDbChecker.SafetyDbRecord("Test is bad", "CVE-2020-123.3", "xyz", listOf("<= 1.0.0"), "<= 1.0.0")
        val message = PyPackageSecurityScan.renderMessage(record)
        assertFalse(message.isEmpty())
    }
}