package ru.smartro.worknote.awORKOLDs.util

object PhotoTypeEnum {
    //После обслуживания платформы
    const val forAfterMedia = 1

    //Проблемы с платформами
    const val forPlatformProblem = 2

    //Проблемы с контейнерами
    const val forContainerFailure = 3

    //Проблемы с контейнерами
    const val forContainerBreakdown = 7

    //Крупногабаритные отходы.забрал
    const val forServedKGO = 4

    //Подбор
    const val forPlatformPickupVolume = 5

    //Вызвать
    const val forRemainingKGO = 6

    //Для упрощенного экрана
    const val forSimplifyServeBefore = 8
    const val forSimplifyServeAfter = 9
}