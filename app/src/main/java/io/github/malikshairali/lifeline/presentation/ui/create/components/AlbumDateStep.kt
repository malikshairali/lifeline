package io.github.malikshairali.lifeline.presentation.ui.create.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.malikshairali.lifeline.presentation.ui.create.components.QuickSelectOption.Companion.determineActiveQuickSelect
import io.github.malikshairali.lifeline.presentation.util.DatePickerSelectableDates
import io.github.malikshairali.lifeline.presentation.util.toDateString
import io.github.malikshairali.lifeline.presentation.util.toEpochMillisEndOfDay
import io.github.malikshairali.lifeline.presentation.util.toEpochMillisStartOfDay
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDateStep(
    toDate: Long?,
    fromDate: Long?,
    modifier: Modifier = Modifier,
    onToDateChange: (Long) -> Unit,
    onFromDateChange: (Long) -> Unit,
    onNext: () -> Unit
) {
    var error by remember { mutableStateOf(false) }

    val activeQuickSelect = remember(fromDate, toDate) {
        determineActiveQuickSelect(fromDate, toDate)
    }

    // --- Helper function to handle quick select chip clicks ---
    fun handleQuickSelectClicked(option: QuickSelectOption) {
        var newFromDate: Long? = null
        var newToDate: Long? = null

        when (option) {
            QuickSelectOption.TODAY -> {
                val now = LocalDate.now()
                newFromDate = now.toEpochMillisStartOfDay()
                newToDate = now.toEpochMillisEndOfDay()
            }

            QuickSelectOption.YESTERDAY -> {
                val yesterday = LocalDate.now().minusDays(1)
                newFromDate = yesterday.toEpochMillisStartOfDay()
                newToDate = yesterday.toEpochMillisEndOfDay()
            }

            QuickSelectOption.THIS_WEEK -> {
                val thisWeek = LocalDate.now()
                newFromDate = thisWeek.with(DayOfWeek.MONDAY).toEpochMillisStartOfDay()
                newToDate = thisWeek.with(DayOfWeek.SUNDAY).toEpochMillisEndOfDay()
            }

            QuickSelectOption.LAST_WEEK -> {
                val lastWeek = LocalDate.now().minusWeeks(1)
                newFromDate = lastWeek.with(DayOfWeek.MONDAY).toEpochMillisStartOfDay()
                newToDate = lastWeek.with(DayOfWeek.SUNDAY).toEpochMillisEndOfDay()
            }

            QuickSelectOption.THIS_MONTH -> {
                val thisMonth = LocalDate.now()
                newFromDate = thisMonth.withDayOfMonth(1).toEpochMillisStartOfDay()
                newToDate =
                    thisMonth.withDayOfMonth(thisMonth.lengthOfMonth()).toEpochMillisEndOfDay()
            }

            QuickSelectOption.LAST_MONTH -> {
                val lastMonth = LocalDate.now().minusMonths(1)
                newFromDate = lastMonth.withDayOfMonth(1).toEpochMillisStartOfDay()
                newToDate =
                    lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).toEpochMillisEndOfDay()
            }

            QuickSelectOption.THIS_YEAR -> {
                val thisYear = LocalDate.now()
                newFromDate = thisYear.withDayOfYear(1).toEpochMillisStartOfDay()
                newToDate = thisYear.withDayOfYear(thisYear.lengthOfYear()).toEpochMillisEndOfDay()
            }

            QuickSelectOption.LAST_YEAR -> {
                val lastYear = LocalDate.now().minusYears(1)
                newFromDate = lastYear.withDayOfYear(1).toEpochMillisStartOfDay()
                newToDate = lastYear.withDayOfYear(lastYear.lengthOfYear()).toEpochMillisEndOfDay()
            }

            QuickSelectOption.NONE -> { /* May not be clickable or do nothing */ }
        }

        newFromDate?.let { onFromDateChange(it) }
        newToDate?.let { onToDateChange(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Date Range",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Choose the date range to fetch photos from your gallery for the album.",
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "From Date", color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        DatePickerFieldToModal(
            date = fromDate, onDateChange = onFromDateChange, error = error
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "To Date", color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        DatePickerFieldToModal(
            date = toDate, onDateChange = onToDateChange, error = error
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "Quick Select", color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        QuickSelect(
            activeQuickSelect = activeQuickSelect, onQuickSelectClicked = ::handleQuickSelectClicked
        )

        Spacer(modifier = Modifier.weight(2f))

        Button(
            onClick = {
                if (toDate == null || fromDate == null) {
                    error = true
                } else {
                    onNext()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "Next", color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickSelect(
    modifier: Modifier = Modifier,
    activeQuickSelect: QuickSelectOption?,
    onQuickSelectClicked: (QuickSelectOption) -> Unit
) {
    val optionsRow1 = listOf(QuickSelectOption.TODAY, QuickSelectOption.YESTERDAY)
    val optionsRow3 = listOf(QuickSelectOption.THIS_WEEK, QuickSelectOption.LAST_WEEK)
    val optionsRow2 = listOf(QuickSelectOption.THIS_MONTH, QuickSelectOption.LAST_MONTH)
    val optionsRow4 = listOf(QuickSelectOption.THIS_YEAR, QuickSelectOption.LAST_YEAR)

    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickSelectRow(
            options = optionsRow1,
            activeQuickSelect = activeQuickSelect,
            onQuickSelectClicked = onQuickSelectClicked
        )
        QuickSelectRow(
            options = optionsRow2,
            activeQuickSelect = activeQuickSelect,
            onQuickSelectClicked = onQuickSelectClicked
        )
        QuickSelectRow(
            options = optionsRow3,
            activeQuickSelect = activeQuickSelect,
            onQuickSelectClicked = onQuickSelectClicked
        )
        QuickSelectRow(
            options = optionsRow4,
            activeQuickSelect = activeQuickSelect,
            onQuickSelectClicked = onQuickSelectClicked
        )
    }
}

@Composable
private fun QuickSelectRow(
    options: List<QuickSelectOption>,
    activeQuickSelect: QuickSelectOption?,
    onQuickSelectClicked: (QuickSelectOption) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterHorizontally
        ), verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            // Use Modifier.weight(1f) if you want them to take equal space
            // Or wrap each chip in a Box with a specific size/weight if needed
            QuickSelectChip(
                modifier = Modifier.weight(1f), // Make chips in a row take equal width
                text = option.displayText, // Assuming QuickSelectOption has a display text property
                isSelected = activeQuickSelect == option,
                onClick = { onQuickSelectClicked(option) })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSelectChip(
    modifier: Modifier = Modifier, // Added modifier
    text: String, isSelected: Boolean, onClick: () -> Unit
) {
    FilterChip(
        modifier = modifier.defaultMinSize(minHeight = 48.dp), // Apply the modifier
        selected = isSelected, onClick = onClick, label = {
            Text(
                modifier = Modifier.fillMaxSize(), text = text, textAlign = TextAlign.Center
            )
        }, shape = RoundedCornerShape(8.dp), colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = Color.Black,
            selectedContainerColor = MaterialTheme.colorScheme.tertiary,
            selectedLabelColor = Color.White
        )
    )
}


@Composable
fun DatePickerFieldToModal(
    modifier: Modifier = Modifier,
    date: Long? = null,
    error: Boolean = false,
    onDateChange: (Long) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        readOnly = true,
        value = date?.toDateString() ?: "",
        onValueChange = { /* Read-only, so this won't be called by user input */ },
        placeholder = { Text("MM/DD/YYYY") },
        isError = error == date?.toString().isNullOrEmpty(),
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) { // Keyed on Unit or selectedDateMillis if needed
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            },
        shape = RoundedCornerShape(16.dp), // Matched shape
        colors = OutlinedTextFieldDefaults.colors( // Matched colors
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
            disabledContainerColor = Color.White.copy(alpha = 0.9f), // Added for readOnly state
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent, // Added for readOnly state

            // Error colors (use these if isError is true)
            errorContainerColor = Color.White.copy(alpha = 0.9f),
            errorBorderColor = Color.Cyan, // As per your example, consider if this should be MaterialTheme.colorScheme.error
            errorLabelColor = MaterialTheme.colorScheme.error, // Optional: for the label in error state
            errorTrailingIconColor = MaterialTheme.colorScheme.error, // Optional
            errorPlaceholderColor = Color.Red.copy(alpha = 0.8f), // As per your example

            // Colors for the text itself (cursor and selection are less relevant for readOnly)
            focusedTextColor = MaterialTheme.colorScheme.onSurface, // Or your specific text color
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface, // Or your specific text color
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // For readOnly

            // Placeholder and Label colors
            focusedLabelColor = MaterialTheme.colorScheme.primary, // Or your specific color
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, // Or your specific color
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),

            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
    )

    if (showModal) {
        DatePickerModal(
            date = date,
            onDateSelected = { onDateChange(it) },
            onDismiss = { showModal = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    date: Long?, onDateSelected: (Long) -> Unit, onDismiss: () -> Unit
) {
    val currentDateTime = LocalDate.now()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date ?: System.currentTimeMillis(),
        yearRange = (currentDateTime.year - 20)..(currentDateTime.year),
        selectableDates = DatePickerSelectableDates
    )

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis ?: System.currentTimeMillis())
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(
            state = datePickerState, showModeToggle = false
        )
    }
}