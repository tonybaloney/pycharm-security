package security.packaging

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PyPackageSecurityScanTest: SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `test no python sdk raises info message`(){
        var mockNotification = mock<Notification> {
            on { notify(any<Project>()) } doAnswer {}
        }
        var mockNotificationGroup = mock<NotificationGroup> {
            on { createNotification( any<String>(), anyOrNull<String>(), any<String>(), any<NotificationType>())} doReturn(mockNotification)
        }
        PyPackageSecurityScan.NOTIFICATION_GROUP = mockNotificationGroup
        PyPackageSecurityScan.checkPackages(project)
        verify(mockNotificationGroup, times(1)).createNotification( eq("Could not check Python packages"), anyOrNull<String>(), any<String>(), eq(NotificationType.INFORMATION))
        verify(mockNotification, times(1)).notify(project)
    }
}