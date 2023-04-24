package com.example.customdatepickercompose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.days


private val defaultCardShape = RoundedCornerShape(8.dp)

/**
 * Represents the selected date for a horizontal date picker.
 *
 * @param selectedDay The selected day of the month.
 * @param selectedMonth The selected month (January is 0).
 * @param selectedYear The selected year.
 * @param autoScrollToSelectedDate Whether to scroll to the selected date automatically.
 */
class HorizontalDateState2(
     selectedDay: Int,
     selectedMonth: Int,
     selectedYear: Int,
    shouldScrollToSelectedDate: Boolean = true,
) {
    private var _selectedDay by mutableStateOf(selectedDay, structuralEqualityPolicy())
    private var _selectedMonth by mutableStateOf(selectedMonth, structuralEqualityPolicy())
    private var _selectedYear by mutableStateOf(selectedYear, structuralEqualityPolicy())


    val selectedDay: Int
        get() = _selectedDay

    val selectedMonth: Int
        get() = _selectedMonth

    val selectedYear: Int
        get() = _selectedYear

    fun increaseMonth() {
        _selectedMonth += 1
        if (_selectedMonth > Calendar.DECEMBER+1) {
            _selectedMonth = Calendar.JANUARY+1
            _selectedYear += 1
        }
    }

    fun setSelectedDay(day:Int) {
        _selectedDay =day

    }

    fun decreaseMonth() {
        _selectedMonth -= 1
        if (_selectedMonth < Calendar.JANUARY+1) {
            _selectedMonth = Calendar.DECEMBER+1
            _selectedYear -= 1
        }
    }

    fun increaseYear(){
        _selectedYear+=1
    }
    fun decreaseYear(){
        _selectedYear-=1
    }
    companion object {
        val Saver: Saver<HorizontalDateState2, *> = listSaver(
            save = {
                listOf(
                    it.selectedDay,
                    it.selectedMonth,
                    it.selectedYear,
                )
            },
            restore = {
                HorizontalDateState2(
                    selectedDay=it[0],
                    selectedMonth=it[1],
                    selectedYear=it[2],
                )
            }
        )
    }
}

@Composable
fun rememberHorizontalDatePickerState2(  initialCalendar: Calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())) =
    rememberSaveable(saver = HorizontalDateState2.Saver) { HorizontalDateState2(
        selectedDay = initialCalendar.get(Calendar.DAY_OF_MONTH),
        selectedMonth = initialCalendar.get(Calendar.MONTH)+1,
        selectedYear = initialCalendar.get(Calendar.YEAR)) }


@Composable
fun HorizontalDatePicker(state: HorizontalDateState2 = rememberHorizontalDatePickerState2()) {
    val locale = Locale.ENGLISH
    //month year
    Surface() {
        Column() {
            MonthYearPicker(
                modifier=Modifier.fillMaxWidth(),
                monthText = Month.of(state.selectedMonth).toString(),
                yearText = state.selectedYear,
                monthIncreased = {
                 state.increaseMonth()
                },
                monthDecreased = { state.decreaseMonth() },
                yearIncreased = {state.increaseYear() },
                yearDecreased = { state.decreaseYear() })
            //LOCALE SET TO ENGLISH
            DayPicker( selectedDay = state.selectedDay,
                year = state.selectedYear,
                month = state.selectedMonth,
                locale = locale,
                onDayClick = { day -> state.setSelectedDay(day) }
            )
        }
    }

}

fun daysOfMonth(year: Int, month: Int): List<LocalDate> {
    val start = LocalDate.of(year, month, 1)
    val end = start.plusMonths(1).minusDays(1)
    return (0 until end.dayOfMonth).map { start.plusDays(it.toLong()) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPicker(
    modifier:Modifier=Modifier,
    selectedDay: Int,
    year: Int, month: Int, locale: Locale = LocalConfiguration.current.locale,
    onDayClick: (Int) -> Unit,
    dayCardShape: Shape = defaultCardShape,
    dayCardBorder: BorderStroke = BorderStroke(1.dp, Color.Black.copy(0.1f)),
    dayCardBackgroundColor: Color = Color.White,
    dayCardTextColor: Color = Color.Black,
    selectedDayCardBackgroundColor: Color = Color.Black,
    selectedDayCardTextColor: Color = Color.White,
    dayOfWeekTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    dayOfMonthTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    dayOfWeekFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E", locale)
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedDay - 1,
        initialFirstVisibleItemScrollOffset = 0,
    )
    val days: List<LocalDate> = daysOfMonth(year, month)

    LazyRow(state = listState) {
        items(days) { day ->
            Card(
                modifier = modifier,
                border = dayCardBorder,
                shape = dayCardShape,
                onClick = { onDayClick(day.dayOfMonth) }
            ) {
                Column(
                    modifier = Modifier
                        .background(if (day.dayOfMonth == selectedDay) selectedDayCardBackgroundColor else dayCardBackgroundColor)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.format(dayOfWeekFormatter),
                        style = dayOfWeekTextStyle,

                        color=if (day.dayOfMonth == selectedDay) selectedDayCardTextColor else dayCardTextColor
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = day.dayOfMonth.toString(),
                        style = dayOfMonthTextStyle,
                        fontWeight = if (day.dayOfMonth == selectedDay) FontWeight.SemiBold else FontWeight.Normal,
                        color=if (day.dayOfMonth == selectedDay) selectedDayCardTextColor else dayCardTextColor

                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }

}

@Composable
fun MonthYearPicker(
    modifier:Modifier=Modifier,
    monthText: String,
    yearText: Int,
    monthIncreased: () -> Unit,
    monthDecreased: () -> Unit,
    yearDecreased: () -> Unit,
    yearIncreased: () -> Unit,
    monthTextStyle: TextStyle = TextStyle.Default.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.Default,
        color = Color.Black
    ),
    yearTextStyle: TextStyle = TextStyle.Default.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.Default,
        color = Color.Black
    )

) {
    Row(modifier=modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CardButton(
            onClick = yearDecreased,
            icon = R.drawable.double_arrow_left,
            backgroundColor = Color.White,
            borderColor = Color.Black.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        CardButton(
            onClick = monthDecreased,
            icon = R.drawable.arrow_left,
            backgroundColor = Color.White,
            borderColor = Color.Black.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = monthText, style = monthTextStyle)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = yearText.toString(), style = yearTextStyle)
        Spacer(modifier = Modifier.width(12.dp))
        CardButton(
            onClick = monthIncreased,
            icon = R.drawable.arrow_right,
            backgroundColor = Color.White,
            borderColor = Color.Black.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        CardButton(
            onClick = yearIncreased,
            icon = R.drawable.double_arrow_right,
            backgroundColor = Color.White,
            borderColor = Color.Black.copy(alpha = 0.1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    cardShape: Shape = defaultCardShape,
    borderColor: Color = MaterialTheme.colorScheme.primaryContainer,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    cardPadding: Dp = 8.dp
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        onClick = onClick,
        shape = cardShape,
        border = BorderStroke(1.dp, color = borderColor),

        ) {
        Box(
            modifier = modifier
                .background(color = backgroundColor)
                .padding(cardPadding)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "arrow",
                tint = iconTint
            )

        }
    }
}

