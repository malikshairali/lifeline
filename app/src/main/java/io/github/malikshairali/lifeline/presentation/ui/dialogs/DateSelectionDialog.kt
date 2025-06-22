package io.github.malikshairali.lifeline.presentation.ui.dialogs

import android.content.Context
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DateSelectionDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirmSelection: (selectionType: DateSelectionType, startDate: Long, endDate: Long) -> Unit
) {
    var selectedType by remember { mutableStateOf(DateSelectionType.SINGLE_DAY) }
    val currentDateTime = LocalDate.now()

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

    var selectedMonthForMonthYearPicker by remember { mutableStateOf(currentDateTime.month) }
    var selectedYearForMonthYearPicker by remember { mutableIntStateOf(currentDateTime.year) }
    var selectedYearForYearPicker by remember { mutableIntStateOf(currentDateTime.year) }

    val yearRangeList = remember {
        ((currentDateTime.year - 20)..(currentDateTime.year)).toList().sortedDescending()
    }
    val monthList = remember { Month.entries }

    val albumName = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        Scaffold(
            modifier = modifier
                .imePadding().hideKeyboardOnTap(),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onDismiss) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                        }
                    },
                    title = {
                        Text(
                            text = "Create New Album",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = albumName.value,
                    onValueChange = {
                        albumName.value = it
                    },
                    label = { Text("Album title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Import photos based on a:",
                    style = MaterialTheme.typography.titleMedium
                )

                DateSelectionType.entries.forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
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
                            modifier = Modifier.heightIn(max = 500.dp),
                            showModeToggle = false
                        )
                    }

                    DateSelectionType.DATE_RANGE -> {
                        DateRangePicker(
                            state = dateRangePickerState,
                            modifier = Modifier.heightIn(max = 500.dp),
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

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    onClick = {
                        val today = LocalDate.now(ZoneId.systemDefault())
                        when (selectedType) {
                            DateSelectionType.SINGLE_DAY -> {
                                val selectedDate = singleDatePickerState.selectedDateMillis
                                    ?: today.toEpochMillisEndOfDay()
                                onConfirmSelection(
                                    DateSelectionType.SINGLE_DAY,
                                    selectedDate,
                                    selectedDate
                                )
                            }

                            DateSelectionType.DATE_RANGE -> {
                                val startDate =
                                    dateRangePickerState.selectedStartDateMillis
                                        ?: today.toEpochMillisStartOfDay()
                                val endDate =
                                    dateRangePickerState.selectedEndDateMillis
                                        ?: today.toEpochMillisEndOfDay()
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
                                val startDate = yearMonth.atDay(1).toEpochMillisStartOfDay()
                                val endDate = yearMonth.atEndOfMonth().toEpochMillisEndOfDay()
                                onConfirmSelection(DateSelectionType.MONTH, startDate, endDate)
                            }

                            DateSelectionType.YEAR -> {
                                val startDate =
                                    LocalDate.of(selectedYearForYearPicker, Month.JANUARY, 1)
                                        .toEpochMillisStartOfDay()
                                val endDate =
                                    LocalDate.of(selectedYearForYearPicker, Month.DECEMBER, 31)
                                        .toEpochMillisEndOfDay()
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
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
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var monthExpanded by remember { mutableStateOf(false) }
        var yearExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = monthExpanded,
            onExpandedChange = { monthExpanded = !monthExpanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedMonth.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                onValueChange = {},
                readOnly = true,
                label = { Text("Month") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                modifier = Modifier.menuAnchor(
                    MenuAnchorType.PrimaryNotEditable,
                    true
                )
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
        }

        Spacer(modifier = Modifier.width(8.dp))

        ExposedDropdownMenuBox(
            expanded = yearExpanded,
            onExpandedChange = { yearExpanded = !yearExpanded },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = selectedYear.toString(),
                onValueChange = {},
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
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearPicker(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var yearExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = yearExpanded,
            onExpandedChange = { yearExpanded = !yearExpanded },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            OutlinedTextField(
                value = selectedYear.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Year") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                modifier = Modifier.menuAnchor(
                    MenuAnchorType.PrimaryNotEditable,
                    true
                )
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
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 420, name = "Dialog - Single Day")
@Composable
fun DateSelectionDialogPreviewSingleDay() {
    MaterialTheme {
        DateSelectionDialog(onDismiss = {}, onConfirmSelection = { _, _, _ -> })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 420, heightDp = 750, name = "Dialog - Month/Year")
@Composable
fun DateSelectionDialogPreviewMonthYear() {
    var selectedType by remember { mutableStateOf(DateSelectionType.MONTH) }
    val currentDateTime = LocalDate.now()
    var selectedMonthForMonthYearPicker by remember { mutableStateOf(currentDateTime.month) }
    var selectedYearForMonthYearPicker by remember { mutableStateOf(currentDateTime.year) }
    val yearRangeList = remember {
        ((currentDateTime.year - 100)..(currentDateTime.year + 10)).toList().sortedDescending()
    }
    val monthList = remember { Month.entries }

    MaterialTheme {
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

fun Modifier.hideKeyboardOnTap(): Modifier = composed {
    val context = LocalContext.current
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    this.pointerInput(Unit) {
        detectTapGestures {
            view.clearFocus()
            focusManager.clearFocus()
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toEpochMillisStartOfDay(): Long {
    return this.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toEpochMillisEndOfDay(): Long {
    return this.plusDays(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli() - 1
}