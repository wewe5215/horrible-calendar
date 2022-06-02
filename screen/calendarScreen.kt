package com.example.workordie.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workordie.ui.calendar.Calendar
import com.example.workordie.ui.calendar.CalendarViewModel
import com.example.workordie.ui.calendar.CalendarYear
import com.example.workordie.ui.calendar.model.CalendarDay
import com.example.workordie.ui.calendar.model.CalendarMonth
import com.example.workordie.ui.calendar.model.DaySelected
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsHeight

class calendarScreen {
    setContent {
        ProvideWindowInsets {
            CustomCalendarTheme {
                Screen(onBackPressed = { finish() })
            }
        }
    }
}
@Composable
fun Screen(
    onBackPressed: () -> Unit
) {
    val calendarViewModel: CalendarViewModel = viewModel()
    val calendarYear = calendarViewModel.calendarYear

    CalendarContent(
        selectedDates = calendarViewModel.datesSelected.toString(),
        calendarYear = calendarYear,
        onDayClicked = { calendarDay, calendarMonth ->
            calendarViewModel.onDaySelected(
                DaySelected(calendarDay.value.toInt(), calendarMonth, calendarYear)
            )
        },
        onBackPressed = onBackPressed
    )
}

@Composable
fun CalendarContent(
    selectedDates: String,
    calendarYear: CalendarYear,
    onDayClicked: (CalendarDay, CalendarMonth) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(
        backgroundColor = Color.White,
        topBar = {
            CalendarTopAppBar(selectedDates, onBackPressed)
        }
    ) {
        Calendar(calendarYear, onDayClicked)
    }
}

@Composable
fun CalendarTopAppBar(selectedDates: String, onBackPressed: () -> Unit) {
    Column {
        Spacer(modifier = Modifier
            .statusBarsHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colors.primaryVariant)
        )
        TopAppBar(
            title = {
                Text(
                    text = if (selectedDates.isEmpty()) "Select Dates"
                    else selectedDates,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            backgroundColor = MaterialTheme.colors.primaryVariant,
            elevation = 0.dp
        )
    }
}