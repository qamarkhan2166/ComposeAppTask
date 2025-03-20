package com.example.composeapptask.feature.common.customComposableViews

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import com.example.composeapptask.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//################################################
//##### Interactive Task Manager Component #######
//################################################

object DatePickerUtils {

    fun showDatePickerDialog(
        context: Context,
        dateFormatPattern: String = "yyyy-MM-dd",
        initialDateValue: String? = null,
        onDatePicked: (String) -> Unit,
        isDisableFutureDate: Boolean = false
    ) {
        val mCalendar = Calendar.getInstance()
        initialDateValue?.let {
            convertTextToDate(
                inputDate = initialDateValue,
                dateFormatPattern = dateFormatPattern,
                locale = Locale.ENGLISH,
            )
        }?.let { date: Date ->
            mCalendar.time = date
        }

        val mYear = mCalendar.get(Calendar.YEAR)
        val mMonth = mCalendar.get(Calendar.MONTH)
        val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

        val mDatePickerDialog = DatePickerDialog(
            /* context = */ context,
            /* themeResId = */ R.style.MyDatePickerStyle,
            /* listener = */
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.ENGLISH)
                onDatePicked.invoke(dateFormat.format(calendar.time))
            },
            /* year = */ mYear,
            /* monthOfYear = */ mMonth,
            /* dayOfMonth = */ mDay,
        )

        if (isDisableFutureDate) {
            mDatePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }
        mDatePickerDialog.show()
    }
}

fun convertTextToDate(
    inputDate: String,
    dateFormatPattern: String = "yyyy-MM-dd",
    locale: Locale = Locale.ENGLISH
): Date? {
    return try {
        val inputFormat = SimpleDateFormat(dateFormatPattern, locale)
        // Parse the input date string into a Date object
        inputFormat.parse(inputDate)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}