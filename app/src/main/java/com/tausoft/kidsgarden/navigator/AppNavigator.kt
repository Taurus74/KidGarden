package com.tausoft.kidsgarden.navigator

import android.os.Bundle

/*
  Экраны приложения
    SETTINGS        - настройки
    IMPORT          - импорт списка детей
    KIDS            - список детей
    EDIT_KID        - добавление/правка ребенка
    ABSENCES        - список периодов отсутствия ребенка
    EDIT_ABSENCE    - добавление/правка данных об отсутствии
    WORK_CALENDAR   - произв. календарь
 */
enum class Screens {
    SETTINGS,
    IMPORT,
    KIDS,
    EDIT_KID,
    ABSENCES,
    EDIT_ABSENCE,
    WORK_CALENDAR
}

interface AppNavigator {
    fun navigateTo(screen: Screens, params: Bundle? = null)
}