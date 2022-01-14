package ru.smartro.worknote.util

object PhotoTypeEnum {
    //До обслуживания платформы
    const val forBeforeMedia = 0

    //После обслуживания платформы
    const val forAfterMedia = 1

    //Проблемы с платформами
    const val forPlatformProblem = 2

    //Проблемы с контейнерами
    const val forContainerProblem = 3

    //Крупногабаритные отходы.забрал
    const val forServedKGO = 4

    //Подбор
    const val forPlatformPickupVolume = 5

    //Вызвать
    const val forRemainingKGO = 6
}