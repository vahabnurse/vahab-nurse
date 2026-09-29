package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.MedicationSchedule
import com.example.ui.components.MedicationScheduleCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleSchedule = MedicationSchedule(
      id = 1L,
      patientId = 1L,
      medicineName = "سفتریاکسون",
      dosage = "1g",
      route = "عضلانی (IM)",
      intervalHours = 12,
      intervalDescription = "هر ۱۲ ساعت",
      startDateTime = System.currentTimeMillis(),
      nextDueDateTime = System.currentTimeMillis() + 3600_000L,
      totalDoses = 6,
      completedDosesCount = 2,
      instructions = "تزریق عمیق عضلانی"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        MedicationScheduleCard(
          schedule = sampleSchedule,
          onAdminister = {},
          onEdit = {},
          onDelete = {},
          onToggleActive = {},
          onViewLogs = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

