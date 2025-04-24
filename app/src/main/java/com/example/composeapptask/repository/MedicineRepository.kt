package com.example.composeapptask.repository

import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminder
import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminderDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.SessionDao

class MedicineRepository(
    private val dao: MedicineReminderDao,
    private val sessionDao: SessionDao
) {
    /**region medicine Reminder starts here*/
    suspend fun addReminder(reminder: MedicineReminder) = dao.insert(reminder)
    fun getAllReminders() = dao.getAllReminders()
    suspend fun deleteReminder(reminder: MedicineReminder) = dao.delete(reminder)
    suspend fun deleteReminderById(id: Int) = dao.deleteReminderById(id)
    /**region medicine Reminder ends here*/

    fun getAllHistorySession() = sessionDao.getAllSessions()
}