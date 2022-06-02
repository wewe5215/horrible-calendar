package com.example.workordie.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.workordie.R
import com.example.workordie.ui.calendar.model.CalendarDay
import com.example.workordie.ui.calendar.model.CalendarMonth
import com.example.workordie.ui.calendar.model.DayOfWeek
import com.example.workordie.ui.calendar.model.DaySelectedStatus
import com.google.android.gms.maps.model.Circle


typealias CalendarWeek = List<CalendarDay>

@Composable
fun Calendar(
    calendarYear: CalendarYear,
    onDayClicked: (CalendarDay, CalendarMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item { Spacer(Modifier.height(32.dp)) }
        for (month in calendarYear) {
            itemsCalendarMonth(month = month, onDayClicked = onDayClicked)
            item {
                Spacer(Modifier.height(32.dp))
            }
        }
        item { Spacer(modifier = Modifier.wrapContentHeight()) }
    }
}

@Composable
private fun MonthHeader(modifier: Modifier = Modifier, month: String, year: String) {
    Row(modifier = modifier.clearAndSetSemantics { }) {
        Text(
            modifier = Modifier.weight(1f),
            text = month,
            style = MaterialTheme.typography.h6
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = year,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
private fun Week(
    modifier: Modifier = Modifier,
    month: CalendarMonth,
    week: CalendarWeek,
    onDayClicked: (CalendarDay) -> Unit
) {
    val (leftFillColor, rightFillColor) = getLeftRightWeekColors(week, month)

    Row(modifier = modifier) {
        val spaceModifiers = Modifier
            .weight(1f)
            .heightIn(max = CELL_SIZE)
        Surface(modifier = spaceModifiers, color = leftFillColor) {
            Spacer(Modifier.fillMaxHeight())
        }
        for (day in week) {
            Day(
                day,
                onDayClicked,
                month
            )
        }
        Surface(modifier = spaceModifiers, color = rightFillColor) {
            Spacer(Modifier.fillMaxHeight())
        }
    }
}

@Composable
private fun DaysOfWeek(modifier: Modifier = Modifier) {
    Row(modifier = modifier.clearAndSetSemantics { }) {
        for (day in DayOfWeek.values()) {
            Day(name = day.name.take(1))
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    onDayClicked: (CalendarDay) -> Unit,
    month: CalendarMonth,
    modifier: Modifier = Modifier
) {
    val enabled = day.status != DaySelectedStatus.NonClickable
    DayContainer(
        modifier = modifier.semantics {
            if (enabled) text = AnnotatedString("${month.name} ${day.value} ${month.year}")
            dayStatusProperty = day.status
        },
        selected = day.status != DaySelectedStatus.NoSelected,
        onClick = { onDayClicked(day) },
        onClickEnabled = enabled,
        backgroundColor = day.status.color(MaterialTheme.colors),
        onClickLabel = stringResource(id = R.string.click_label_select)
    ) {
        DayStatusContainer(status = day.status) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    // Parent will handle semantics
                    .clearAndSetSemantics {},
                text = day.value,
                style = MaterialTheme.typography.body1.copy(color = Color.Black)
            )
        }
    }
}

@Composable
private fun Day(name: String) {
    DayContainer {
        Text(
            modifier = Modifier.wrapContentSize(Alignment.Center),
            text = name,
            style = MaterialTheme.typography.caption.copy(Color.Black.copy(alpha = 0.6f))
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DayContainer(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = { },
    onClickEnabled: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    onClickLabel: String? = null,
    content: @Composable () -> Unit
) {
    val stateDescriptionLabel = stringResource(
        if (selected) R.string.state_descr_selected else R.string.state_descr_not_selected
    )
    Surface(
        modifier = modifier
            .size(width = CELL_SIZE, height = CELL_SIZE)
            .then(
                if (onClickEnabled) {
                    modifier.semantics {
                        stateDescription = stateDescriptionLabel
                    }
                } else {
                    modifier.clearAndSetSemantics { }
                }
            ),
        onClick = onClick,
        enabled = onClickEnabled,
        color = backgroundColor,
        onClickLabel = onClickLabel
    ) {
        content()
    }
}

@Composable
private fun DayStatusContainer(
    status: DaySelectedStatus,
    content: @Composable () -> Unit
) {
    if (status.isMarked()) {
        Box {
            val color = MaterialTheme.colors.secondary
            Circle(color = color)
            if (status == DaySelectedStatus.FirstDay) {
                SemiRect(color = color, lookingLeft = false)
            } else if (status == DaySelectedStatus.LastDay) {
                SemiRect(color = color, lookingLeft = true)
            }
            content()
        }
    } else {
        content()
    }
}

private fun LazyListScope.itemsCalendarMonth(
    month: CalendarMonth,
    onDayClicked: (CalendarDay, CalendarMonth) -> Unit
) {
    item {
        MonthHeader(
            modifier = Modifier.padding(horizontal = 32.dp),
            month = month.name,
            year = month.year
        )
    }

    val contentModifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
    item {
        DaysOfWeek(modifier = contentModifier)
    }

    month.weeks.value.forEachIndexed { index, week ->
        item(key = "${month.year}/${month.monthNumber}/${index + 1}") {
            Week(
                modifier = contentModifier,
                week = week,
                month = month,
                onDayClicked = { day ->
                    onDayClicked(day, month)
                }
            )
        }
        item {
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun DaySelectedStatus.color(theme: Colors): Color = when (this) {
    DaySelectedStatus.Selected -> theme.secondary
    else -> Color.Transparent
}

@Composable
private fun getLeftRightWeekColors(week: CalendarWeek, month: CalendarMonth): Pair<Color, Color> {
    val materialColors = MaterialTheme.colors

    val firstDayOfTheWeek = week[0].value
    val leftFillColor = if (firstDayOfTheWeek.isNotEmpty()) {
        val lastDayPreviousWeek = month.getPreviousDay(firstDayOfTheWeek.toInt())
        if (lastDayPreviousWeek?.status?.isMarked() == true && week[0].status.isMarked()) {
            materialColors.secondary
        } else {
            Color.Transparent
        }
    } else {
        Color.Transparent
    }

    val lastDayOfTheWeek = week[6].value
    val rightFillColor = if (lastDayOfTheWeek.isNotEmpty()) {
        val firstDayNextWeek = month.getNextDay(lastDayOfTheWeek.toInt())
        if (firstDayNextWeek?.status?.isMarked() == true && week[6].status.isMarked()) {
            materialColors.secondary
        } else {
            Color.Transparent
        }
    } else {
        Color.Transparent
    }

    return leftFillColor to rightFillColor
}

private fun DaySelectedStatus.isMarked(): Boolean {
    return when (this) {
        DaySelectedStatus.Selected -> true
        DaySelectedStatus.FirstDay -> true
        DaySelectedStatus.LastDay -> true
        DaySelectedStatus.FirstLastDay -> true
        else -> false
    }
}

private val CELL_SIZE = 48.dp
val DayStatusKey = SemanticsPropertyKey<DaySelectedStatus>("DayStatusKey")
var SemanticsPropertyReceiver.dayStatusProperty by DayStatusKey