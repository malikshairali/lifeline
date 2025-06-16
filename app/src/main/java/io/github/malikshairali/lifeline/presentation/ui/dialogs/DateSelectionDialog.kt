package io.github.malikshairali.lifeline.presentation.ui.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

// Enum to represent the type of selection
enum class DateSelectionType {
    SINGLE_DAY, MONTH, YEAR, DATE_RANGE
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class) // For SelectableDates
object PastOrPresentSelectableDates : SelectableDates {

    // Calculate midnight UTC of today. The picker deals with UTC dates at their start of day.
    private val todayEpochMillisInUtcZoneAtStartOfDay: Long by lazy {
        val systemDefaultToday = LocalDate.now(ZoneId.systemDefault())
        systemDefaultToday.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        // Allow selection if the given date (at midnight UTC) is less than or equal to today (at midnight UTC).
        return utcTimeMillis <= todayEpochMillisInUtcZoneAtStartOfDay
    }

    override fun isSelectableYear(year: Int): Boolean {
        // Also ensure that selectable years do not go into the future.
        return year <= LocalDate.now(ZoneId.systemDefault()).year
    }
}

@RequiresApi(Build.VERSION_CODES.O) // For modern Java Time API
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionDialog(
    onDismiss: () -> Unit,
    onConfirmSelection: (selectionType: DateSelectionType, startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    var selectedType by remember { mutableStateOf(DateSelectionType.SINGLE_DAY) }
    val currentDateTime = LocalDate.now()

    // States for DatePicker and DateRangePicker
    val singleDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        yearRange = (currentDateTime.year - 20)..(currentDateTime.year),
        selectableDates = PastOrPresentSelectableDates
    )
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = System.currentTimeMillis() - (72 * 60 * 60 * 1000),
        initialSelectedEndDateMillis = System.currentTimeMillis(), // Default to 1 day range
        yearRange = (currentDateTime.year - 20)..(currentDateTime.year),
        selectableDates = PastOrPresentSelectableDates
    )

    // States for custom Month/Year Pickers
    var selectedMonthForMonthYearPicker by remember { mutableStateOf(currentDateTime.month) }
    var selectedYearForMonthYearPicker by remember { mutableIntStateOf(currentDateTime.year) }
    var selectedYearForYearPicker by remember { mutableIntStateOf(currentDateTime.year) }

    val yearRangeList = remember {
        ((currentDateTime.year - 20)..(currentDateTime.year)).toList().sortedDescending()
    } // More user friendly
    val monthList = remember { Month.entries }

    val convertMillisToLocalDate = { millis: Long? ->
        millis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        // Full screen content with a background
        Scaffold(
            modifier = Modifier.fillMaxSize(),
//                .background(MaterialTheme.colorScheme.surface),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onDismiss) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                        }
                    },
                    title = { Text("Create New Album") }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Button(
                        modifier = Modifier.fillMaxSize(),
                        shape = RectangleShape,
                        onClick = {
                            val today = LocalDate.now(ZoneId.systemDefault()) // Fallback
                            when (selectedType) {
                                DateSelectionType.SINGLE_DAY -> {
                                    val selectedDate =
                                        convertMillisToLocalDate(singleDatePickerState.selectedDateMillis)
                                            ?: today
                                    onConfirmSelection(
                                        DateSelectionType.SINGLE_DAY,
                                        selectedDate,
                                        selectedDate
                                    )
                                }

                                DateSelectionType.DATE_RANGE -> {
                                    val startDate =
                                        convertMillisToLocalDate(dateRangePickerState.selectedStartDateMillis)
                                            ?: today
                                    val endDate =
                                        convertMillisToLocalDate(dateRangePickerState.selectedEndDateMillis)
                                            ?: startDate // Ensure endDate isn't before startDate
                                    onConfirmSelection(
                                        DateSelectionType.DATE_RANGE,
                                        startDate,
                                        endDate
                                    )
                                }

                                DateSelectionType.MONTH -> {
                                    val yearMonth = YearMonth.of(
                                        selectedYearForMonthYearPicker,
                                        selectedMonthForMonthYearPicker
                                    )
                                    val startDate = yearMonth.atDay(1)
                                    val endDate = yearMonth.atEndOfMonth()
                                    onConfirmSelection(DateSelectionType.MONTH, startDate, endDate)
                                }

                                DateSelectionType.YEAR -> {
                                    val startDate =
                                        LocalDate.of(selectedYearForYearPicker, Month.JANUARY, 1)
                                    val endDate =
                                        LocalDate.of(selectedYearForYearPicker, Month.DECEMBER, 31)
                                    onConfirmSelection(DateSelectionType.YEAR, startDate, endDate)
                                }
                            }
                            onDismiss()
                        }
                    ) {
                        Text("Import")
                    }
                }
            }
        ) { innerPadding ->
            // Content of your dialog
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Import photos based on a:",
                    style = MaterialTheme.typography.titleMedium
                )

                DateSelectionType.entries.forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                        Text(
                            text = when (type) {
                                DateSelectionType.SINGLE_DAY -> "Single day"
                                DateSelectionType.MONTH -> "Specific month of the year"
                                DateSelectionType.YEAR -> "Specific year"
                                DateSelectionType.DATE_RANGE -> "Custom date range"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))

                when (selectedType) {
                    DateSelectionType.SINGLE_DAY -> {
                        DatePicker(
                            state = singleDatePickerState,
                            modifier = Modifier.fillMaxWidth(),
                            showModeToggle = false
                        )
                    }

                    DateSelectionType.DATE_RANGE -> {
                        DateRangePicker(
                            state = dateRangePickerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 450.dp),
                            showModeToggle = false
                        )
                    }

                    DateSelectionType.MONTH -> {
                        MonthYearPicker(
                            months = monthList,
                            years = yearRangeList,
                            selectedMonth = selectedMonthForMonthYearPicker,
                            selectedYear = selectedYearForMonthYearPicker,
                            onMonthSelected = { selectedMonthForMonthYearPicker = it },
                            onYearSelected = { selectedYearForMonthYearPicker = it }
                        )
                    }

                    DateSelectionType.YEAR -> {
                        YearPicker(
                            years = yearRangeList,
                            selectedYear = selectedYearForYearPicker,
                            onYearSelected = { selectedYearForYearPicker = it }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class) // For ExposedDropdownMenuBox
@Composable
fun MonthYearPicker(
    months: List<Month>,
    years: List<Int>,
    selectedMonth: Month,
    selectedYear: Int,
    onMonthSelected: (Month) -> Unit,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally
        ), // Spacing between dropdowns
        verticalAlignment = Alignment.CenterVertically
    ) {
        var monthExpanded by remember { mutableStateOf(false) }
        var yearExpanded by remember { mutableStateOf(false) }

        // Month Picker using ExposedDropdownMenuBox for better styling
        ExposedDropdownMenuBox(
            expanded = monthExpanded,
            onExpandedChange = { monthExpanded = !monthExpanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                // Or use TextField if you prefer standard Material look
                value = selectedMonth.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                onValueChange = {}, // Not editable
                readOnly = true,
                label = { Text("Month") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                modifier = Modifier.menuAnchor(
                    MenuAnchorType.PrimaryNotEditable,
                    true
                ) // Important for positioning the dropdown
            )
            ExposedDropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false }
            ) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month.getDisplayName(TextStyle.FULL, Locale.getDefault())) },
                        onClick = {
                            onMonthSelected(month)
                            monthExpanded = false
                        }
                    )
                }
            }
        } // End of Month ExposedDropdownMenuBox

        Spacer(modifier = Modifier.width(8.dp)) // Spacer between dropdowns

        // Year Picker using ExposedDropdownMenuBox
        ExposedDropdownMenuBox(
            expanded = yearExpanded,
            onExpandedChange = { yearExpanded = !yearExpanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                // Or use TextField
                value = selectedYear.toString(),
                onValueChange = {}, // Not editable
                readOnly = true,
                label = { Text("Year") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            )
            ExposedDropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false }
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            onYearSelected(year)
                            yearExpanded = false
                        }
                    )
                }
            }
        } // End of Year ExposedDropdownMenuBox
    } // End of Row
} // End of MonthYearPicker

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class) // For ExposedDropdownMenuBox
@Composable
fun YearPicker(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var yearExpanded by remember { mutableStateOf(false) }

    Box( // Box to center the single dropdown
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = yearExpanded,
            onExpandedChange = { yearExpanded = !yearExpanded },
            modifier = Modifier.fillMaxWidth(0.7f) // Make it a bit narrower than full width
        ) {
            OutlinedTextField(
                // Or use TextField
                value = selectedYear.toString(),
                onValueChange = {}, // Not editable
                readOnly = true,
                label = { Text("Year") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                modifier = Modifier.menuAnchor(
                    MenuAnchorType.PrimaryNotEditable,
                    true
                ) // Important for positioning the dropdown
            )
            ExposedDropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false }
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            onYearSelected(year)
                            yearExpanded = false
                        }
                    )
                }
            }
        }
    }
} // End of YearPicker


// Previews
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 420, name = "Dialog - Single Day")
@Composable
fun DateSelectionDialogPreviewSingleDay() {
    MaterialTheme { // Wrap with your app's theme or MaterialTheme
        DateSelectionDialog(onDismiss = {}, onConfirmSelection = { _, _, _ -> })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 420, heightDp = 750, name = "Dialog - Month/Year")
@Composable
fun DateSelectionDialogPreviewMonthYear() {
    // To see this specific state in preview, we might need to pass an initial
    // selectedType or have a way to set it. For now, you can click to it in interactive mode.
    // Let's make a state here to demonstrate for preview purposes.
    var selectedType by remember { mutableStateOf(DateSelectionType.MONTH) } // Default to MONTH for this preview
    val currentDateTime = LocalDate.now()
    var selectedMonthForMonthYearPicker by remember { mutableStateOf(currentDateTime.month) }
    var selectedYearForMonthYearPicker by remember { mutableStateOf(currentDateTime.year) }
    val yearRangeList = remember {
        ((currentDateTime.year - 100)..(currentDateTime.year + 10)).toList().sortedDescending()
    }
    val monthList = remember { Month.entries }


    MaterialTheme {
        // This is a simplified version of the main dialog just for previewing MonthYearPicker effectively
        AlertDialog(
            onDismissRequest = {},
            confirmButton = { TextButton(onClick = {}) { Text("OK") } },
            text = {
                Column {
                    DateSelectionType.entries.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedType = type }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type }
                            )
                            Text(
                                text = when (type) {
                                    DateSelectionType.SINGLE_DAY -> "Single Day"
                                    DateSelectionType.MONTH -> "Month & Year"
                                    DateSelectionType.YEAR -> "Year Only"
                                    DateSelectionType.DATE_RANGE -> "Date Range"
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    when (selectedType) {
                        DateSelectionType.MONTH -> {
                            MonthYearPicker(
                                months = monthList,
                                years = yearRangeList,
                                selectedMonth = selectedMonthForMonthYearPicker,
                                selectedYear = selectedYearForMonthYearPicker,
                                onMonthSelected = { selectedMonthForMonthYearPicker = it },
                                onYearSelected = { selectedYearForMonthYearPicker = it }
                            )
                        }

                        DateSelectionType.YEAR -> {
                            YearPicker(
                                years = yearRangeList,
                                selectedYear = selectedYearForMonthYearPicker, // can reuse
                                onYearSelected = { selectedYearForMonthYearPicker = it }
                            )
                        }

                        else -> {
                            Text("Other selection type active: $selectedType")
                        }
                    }
                }
            }
        )
    }
}