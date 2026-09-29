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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Patient

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditPatientDialog(
    patient: Patient? = null,
    onDismiss: () -> Unit,
    onSave: (Patient) -> Unit
) {
    var firstName by remember { mutableStateOf(patient?.firstName ?: "") }
    var lastName by remember { mutableStateOf(patient?.lastName ?: "") }
    var nationalId by remember { mutableStateOf(patient?.nationalId ?: "") }
    var phoneNumber by remember { mutableStateOf(patient?.phoneNumber ?: "") }
    var age by remember { mutableStateOf(patient?.age ?: "") }
    var gender by remember { mutableStateOf(patient?.gender ?: "آقا") }
    var roomOrBedNumber by remember { mutableStateOf(patient?.roomOrBedNumber ?: "") }
    var allergies by remember { mutableStateOf(patient?.allergies ?: "") }
    var diagnosis by remember { mutableStateOf(patient?.diagnosis ?: "") }
    var notes by remember { mutableStateOf(patient?.notes ?: "") }

    var isError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("add_edit_patient_dialog"),
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
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (patient == null) "تعریف بیمار جدید" else "ویرایش مشخصات بیمار",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // First Name and Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            isError = false
                        },
                        label = { Text("نام بیمار *") },
                        placeholder = { Text("مثال: علی") },
                        singleLine = true,
                        isError = isError && firstName.isBlank(),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("patient_first_name_input")
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            isError = false
                        },
                        label = { Text("نام خانوادگی *") },
                        placeholder = { Text("مثال: رضایی") },
                        singleLine = true,
                        isError = isError && lastName.isBlank(),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("patient_last_name_input")
                    )
                }

                if (isError && (firstName.isBlank() || lastName.isBlank())) {
                    Text(
                        text = "وارد کردن نام و نام خانوادگی بیمار الزامی است",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Age & Gender
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("سن") },
                        placeholder = { Text("مثلاً: ۴۵") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("patient_age_input")
                    )

                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            text = "جنسیت:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("آقا", "خانم", "کودک").forEach { g ->
                                FilterChip(
                                    selected = gender == g,
                                    onClick = { gender = g },
                                    label = { Text(g) },
                                    modifier = Modifier.testTag("gender_chip_$g")
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bed / Room Number
                OutlinedTextField(
                    value = roomOrBedNumber,
                    onValueChange = { roomOrBedNumber = it },
                    label = { Text("شماره اتاق / تخت / بخش") },
                    placeholder = { Text("مثال: تخت ۲۰۴ - بخش جراحی") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.MeetingRoom, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_room_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // National ID & Phone Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nationalId,
                        onValueChange = { nationalId = it },
                        label = { Text("کد ملی / شماره پرونده") },
                        placeholder = { Text("0012345678") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("patient_national_id_input")
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("شماره تماس") },
                        placeholder = { Text("0912...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("patient_phone_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Allergies Warning Input
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("حساسیت‌های دارویی (Allergies)") },
                    placeholder = { Text("مثال: حساسیت به پنی‌سیلین یا آسپرین") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_allergies_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Diagnosis & Clinical Notes
                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("تشخیص بالینی / علت بستری") },
                    placeholder = { Text("مثال: آنتی‌بیوتیک تراپی پس از جراحی") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_diagnosis_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("توضیحات و ملاحظات پرستاری") },
                    placeholder = { Text("ملاحظات خاص برای تزریقات این بیمار...") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_notes_input")
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
                        modifier = Modifier.testTag("cancel_patient_button")
                    ) {
                        Text("انصراف")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (firstName.isBlank() || lastName.isBlank()) {
                                isError = true
                            } else {
                                val updatedPatient = patient?.copy(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    nationalId = nationalId.trim(),
                                    phoneNumber = phoneNumber.trim(),
                                    age = age.trim(),
                                    gender = gender,
                                    roomOrBedNumber = roomOrBedNumber.trim(),
                                    allergies = allergies.trim(),
                                    diagnosis = diagnosis.trim(),
                                    notes = notes.trim()
                                ) ?: Patient(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    nationalId = nationalId.trim(),
                                    phoneNumber = phoneNumber.trim(),
                                    age = age.trim(),
                                    gender = gender,
                                    roomOrBedNumber = roomOrBedNumber.trim(),
                                    allergies = allergies.trim(),
                                    diagnosis = diagnosis.trim(),
                                    notes = notes.trim()
                                )
                                onSave(updatedPatient)
                            }
                        },
                        modifier = Modifier.testTag("save_patient_button")
                    ) {
                        Text(if (patient == null) "ثبت بیمار" else "ذخیره تغییرات")
                    }
                }
            }
        }
    }
}
