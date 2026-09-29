package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusDueNow
import com.example.ui.theme.StatusDueNowBg
import com.example.ui.theme.StatusOverdue
import com.example.ui.theme.StatusOverdueBg
import com.example.ui.theme.StatusUpcoming
import com.example.ui.theme.StatusUpcomingBg
import com.example.ui.viewmodel.UpcomingInjectionItem
import com.example.util.DateUtils
import com.example.util.DueStatus

@Composable
fun UpcomingInjectionsView(
    items: List<UpcomingInjectionItem>,
    onItemClick: (UpcomingInjectionItem) -> Unit,
    onAdministerClick: (UpcomingInjectionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "صف تزریقات سررسید شده و پیش‌رو",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .testTag("upcoming_injections_row")
        ) {
            items(items, key = { "${it.patient.id}_${it.schedule.id}" }) { item ->
                UpcomingInjectionCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    onAdminister = { onAdministerClick(item) }
                )
            }
        }
    }
}

@Composable
fun UpcomingInjectionCard(
    item: UpcomingInjectionItem,
    onClick: () -> Unit,
    onAdminister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = item.dueStatus
    val (cardBorder, badgeBg, badgeColor, statusTitle) = when (status) {
        DueStatus.OVERDUE -> Quad(StatusOverdue, StatusOverdueBg, StatusOverdue, "تأخیر در تزریق!")
        DueStatus.DUE_NOW -> Quad(StatusDueNow, StatusDueNowBg, StatusDueNow, "موعد تزریق فرا رسیده")
        DueStatus.UPCOMING_SOON -> Quad(StatusUpcoming, StatusUpcomingBg, StatusUpcoming, "به‌زودی")
        DueStatus.SCHEDULED_LATER -> Quad(Color.Transparent, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "برنامه‌ریزی‌شده")
    }

    Card(
        modifier = modifier
            .width(260.dp)
            .clickable { onClick() }
            .testTag("upcoming_card_${item.schedule.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (status == DueStatus.OVERDUE) Icons.Default.Warning else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusTitle,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = badgeColor
                        )
                    }
                }

                Text(
                    text = item.relativeTimeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Patient Name & Room
            Text(
                text = item.patient.fullName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            if (item.patient.roomOrBedNumber.isNotBlank()) {
                Text(
                    text = item.patient.roomOrBedNumber,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Drug & Interval
            Text(
                text = "${item.schedule.medicineName} (${item.schedule.dosage})",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.schedule.route,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = DateUtils.formatIntervalText(item.schedule.intervalHours),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Administer button
            Button(
                onClick = onAdminister,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (status == DueStatus.OVERDUE || status == DueStatus.DUE_NOW) StatusDueNow else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ثبت تزریق",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
