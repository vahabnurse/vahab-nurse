package com.example.ui.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MedicationSchedule
import com.example.ui.theme.StatusDueNow
import com.example.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdministerInjectionDialog(
    schedule: MedicationSchedule,
    patientName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (administeredBy: String, injectionSite: String, status: String, notes: String) -> Unit
) {
    var administeredBy by remember { mutableStateOf("") }
    var injectionSite by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("انجام شد") }
    var notes by remember { mutableStateOf("") }

    val commonSites = when {
        schedule.route.contains("عضلانی") || schedule.route.contains("IM") -> listOf(
            "عضله گلوتئال راست (باسن)",
            "عضله گلوتئال چپ (باسن)",
            "بازوی راست (دلتوئید)",
            "بازوی چپ (دلتوئید)",
            "عضله ران راست (واستوس لترالیس)",
            "عضله ران چپ"
        )
        schedule.route.contains("زیرجلدی") || schedule.route.contains("SC") -> listOf(
            "دور ناف (شکم)",
            "پشت بازوی راست",
            "پشت بازوی چپ",
            "جلوی ران راست",
            "جلوی ران چپ"
        )
        schedule.route.contains("وریدی") || schedule.route.contains("IV") -> listOf(
            "ورید پشت دست راست",
            "ورید پشت دست چپ",
            "ورید ساعد راست",
            "ورید ساعد چپ",
            "آنژیوکت / کاتتر مرکزی"
        )
        else -> listOf(
            "بازوی راست",
            "بازوی چپ",
            "باسن راست",
            "باسن چپ",
            "شکم دور ناف",
            "ورید دست"
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("administer_injection_dialog"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusDueNow,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ثبت تزریق انجام‌شده",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Drug info summary banner
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (patientName.isNotBlank()) {
                            Text(
                                text = "بیمار: $patientName",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${schedule.medicineName} (${schedule.dosage})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "•",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = schedule.route,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "فاصله تزریق: ${if (schedule.intervalDescription.isNotBlank()) schedule.intervalDescription else DateUtils.formatIntervalText(schedule.intervalHours)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Injection Site selection
                Text(
                    text = "محل تزریق (Injection Site):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    commonSites.forEach { site ->
                        FilterChip(
                            selected = injectionSite == site,
                            onClick = { injectionSite = site },
                            label = { Text(site, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("site_chip_$site")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = injectionSite,
                    onValueChange = { injectionSite = it },
                    label = { Text("محل تزریق دستی یا تأیید شده") },
                    placeholder = { Text("مثال: بازوی راست یا ورید دست") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("injection_site_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Administered By (Nurse name)
                OutlinedTextField(
                    value = administeredBy,
                    onValueChange = { administeredBy = it },
                    label = { Text("نام تزریق‌کننده / پرستار") },
                    placeholder = { Text("نام مسئول تزریق") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("administered_by_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status selection
                Text(
                    text = "وضعیت ثبت:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("انجام شد", "به تعویق افتاد", "لغو شد").forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st) },
                            modifier = Modifier.testTag("status_chip_$st")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes / Patient Reactions
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("توضیحات و وضعیت بیمار") },
                    placeholder = { Text("واکنش بیمار، علائم حیاتی یا توضیحات پرستاری...") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("injection_notes_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_administer_button")
                    ) {
                        Text("انصراف")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                administeredBy.trim(),
                                injectionSite.trim(),
                                status,
                                notes.trim()
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusDueNow),
                        modifier = Modifier.testTag("confirm_administer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ثبت و به‌روزرسانی موعد بعدی")
                    }
                }
            }
        }
    }
}
