package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MedicationSchedule
import com.example.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditMedicationDialog(
    patientId: Long,
    patientName: String = "",
    schedule: MedicationSchedule? = null,
    onDismiss: () -> Unit,
    onSave: (MedicationSchedule) -> Unit
) {
    var medicineName by remember { mutableStateOf(schedule?.medicineName ?: "") }
    var dosage by remember { mutableStateOf(schedule?.dosage ?: "") }
    var route by remember { mutableStateOf(schedule?.route ?: "عضلانی (IM)") }
    var intervalHours by remember { mutableIntStateOf(schedule?.intervalHours ?: 8) }
    var intervalDescription by remember { mutableStateOf(schedule?.intervalDescription ?: "هر ۸ ساعت") }
    var customHoursText by remember { mutableStateOf(if (schedule != null && schedule.intervalHours !in listOf(4, 6, 8, 12, 24, 48, 168)) schedule.intervalHours.toString() else "") }
    var totalDosesText by remember { mutableStateOf(if (schedule != null && schedule.totalDoses > 0) schedule.totalDoses.toString() else "") }
    var instructions by remember { mutableStateOf(schedule?.instructions ?: "") }
    var colorTagHex by remember { mutableLongStateOf(schedule?.colorTagHex ?: 0xFF0288D1) }

    var isError by remember { mutableStateOf(false) }

    val commonMedications = listOf(
        "سفتریاکسون (Ceftriaxone)",
        "سفازولین (Cefazolin)",
        "هپارین (Heparin)",
        "انوکساپارین (Clexane)",
        "انسولین رگولار (Regular)",
        "انسولین لانتوس (Lantus)",
        "دگزامتازون (Dexamethasone)",
        "هیدروکورتیزون (Hydrocortisone)",
        "اندانسترون (Ondansetron)",
        "متوکلوپرامید (Plasil)",
        "پنتوپرازول وریدی (Pantoprazole)",
        "ویتامین B12 (B12)",
        "پنی‌سیلین 6.3.3 (Penicillin)",
        "آمپی‌سیلین (Ampicillin)",
        "آمپول نوروبیون (Neurobion)",
        "ترامادول (Tramadol)"
    )

    val commonRoutes = listOf(
        "عضلانی (IM)",
        "وریدی (IV)",
        "زیرجلدی (SC)",
        "انفوزیون (IV Drip)",
        "داخل جلدی (ID)"
    )

    val commonIntervals = listOf(
        4 to "هر ۴ ساعت (Q4H)",
        6 to "هر ۶ ساعت (Q6H)",
        8 to "هر ۸ ساعت (Q8H)",
        12 to "هر ۱۲ ساعت (BD)",
        24 to "روزی یک بار (Daily)",
        48 to "هر ۴۸ ساعت (Q48H)",
        168 to "هفتگی (Weekly)"
    )

    val colorOptions = listOf(
        0xFF0288D1 to "آبی",
        0xFF00796B to "سبزآبی",
        0xFF7B1FA2 to "بنفش",
        0xFFD32F2F to "قرمز",
        0xFFF57C00 to "نارنجی",
        0xFF388E3C to "سبز"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("add_edit_medication_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (schedule == null) "تعریف رژیم دارویی و تزریق" else "ویرایش رژیم دارویی",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (patientName.isNotBlank()) {
                            Text(
                                text = "برای بیمار: $patientName",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Medicine Name Input
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = {
                        medicineName = it
                        isError = false
                    },
                    label = { Text("نام دارو و آمپول *") },
                    placeholder = { Text("مثال: سفتریاکسون یا انتخاب از گزینه‌ها") },
                    singleLine = true,
                    isError = isError && medicineName.isBlank(),
                    leadingIcon = {
                        Icon(Icons.Default.Medication, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medication_name_input")
                )

                if (isError && medicineName.isBlank()) {
                    Text(
                        text = "وارد کردن نام دارو الزامی است",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                // Quick common medicine chips
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "داروهای پرتکرار و پیشنهادی:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonMedications.forEach { med ->
                        FilterChip(
                            selected = medicineName == med,
                            onClick = { medicineName = med },
                            label = { Text(med, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dosage & Route Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = {
                            dosage = it
                            isError = false
                        },
                        label = { Text("دوز / مقدار مصرف *") },
                        placeholder = { Text("1g, 500mg, 10IU...") },
                        singleLine = true,
                        isError = isError && dosage.isBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("medication_dosage_input")
                    )

                    OutlinedTextField(
                        value = totalDosesText,
                        onValueChange = { totalDosesText = it },
                        label = { Text("تعداد کل دوزها") },
                        placeholder = { Text("اختیاری (مثلاً ۱۰)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("medication_total_doses_input")
                    )
                }

                if (isError && dosage.isBlank()) {
                    Text(
                        text = "وارد کردن مقدار و دوز دارو الزامی است",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Route Selection (IM, IV, SC, etc.)
                Text(
                    text = "روش تزریق (Route):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    commonRoutes.forEach { r ->
                        FilterChip(
                            selected = route == r,
                            onClick = { route = r },
                            label = { Text(r) },
                            modifier = Modifier.testTag("route_chip_$r")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Injection Interval Selection (فاصله زمانی تزریق)
                Text(
                    text = "فاصله ی تزریق (Injection Interval) *:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    commonIntervals.forEach { (hours, desc) ->
                        FilterChip(
                            selected = intervalHours == hours && customHoursText.isBlank(),
                            onClick = {
                                intervalHours = hours
                                intervalDescription = desc
                                customHoursText = ""
                            },
                            label = { Text(desc) },
                            leadingIcon = if (intervalHours == hours && customHoursText.isBlank()) {
                                { Icon(Icons.Default.Timelapse, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.testTag("interval_chip_$hours")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom hours option
                OutlinedTextField(
                    value = customHoursText,
                    onValueChange = {
                        customHoursText = it
                        val parsed = it.toIntOrNull()
                        if (parsed != null && parsed > 0) {
                            intervalHours = parsed
                            intervalDescription = DateUtils.formatIntervalText(parsed)
                        }
                    },
                    label = { Text("یا فاصله سفارشی (تعداد ساعت دلخواه)") },
                    placeholder = { Text("مثلاً: ۱۸ ساعت") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.AccessTime, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_interval_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Special clinical notes / instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("دستورات و ملاحظات بالینی تزریق") },
                    placeholder = { Text("مثال: تست پنی‌سیلین انجام شود / تزریق عضلانی عمیق / رقیق‌سازی با نرمال سالین...") },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = null)
                    },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medication_instructions_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_medication_button")
                    ) {
                        Text("انصراف")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (medicineName.isBlank() || dosage.isBlank()) {
                                isError = true
                            } else {
                                val total = totalDosesText.toIntOrNull() ?: 0
                                val now = System.currentTimeMillis()

                                val updatedSchedule = schedule?.copy(
                                    medicineName = medicineName.trim(),
                                    dosage = dosage.trim(),
                                    route = route,
                                    intervalHours = intervalHours,
                                    intervalDescription = intervalDescription,
                                    totalDoses = total,
                                    instructions = instructions.trim(),
                                    colorTagHex = colorTagHex
                                ) ?: MedicationSchedule(
                                    patientId = patientId,
                                    medicineName = medicineName.trim(),
                                    dosage = dosage.trim(),
                                    route = route,
                                    intervalHours = intervalHours,
                                    intervalDescription = intervalDescription,
                                    startDateTime = now,
                                    nextDueDateTime = now + (intervalHours * 3600_000L),
                                    totalDoses = total,
                                    completedDosesCount = 0,
                                    instructions = instructions.trim(),
                                    isActive = true,
                                    colorTagHex = colorTagHex
                                )

                                onSave(updatedSchedule)
                            }
                        },
                        modifier = Modifier.testTag("save_medication_button")
                    ) {
                        Text(if (schedule == null) "ثبت رژیم دارویی" else "ذخیره تغییرات")
                    }
                }
            }
        }
    }
}
