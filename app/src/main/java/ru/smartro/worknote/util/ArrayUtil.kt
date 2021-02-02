package ru.smartro.worknote.util

import ru.smartro.worknote.service.response.way_task.WayPoint

class ArrayUtil {
    fun getContainerFromArray(x: Double, y: Double, list: List<WayPoint>): WayPoint? {
        for (containerPoint in list) {
            if (x == containerPoint.coordinate[0] && y == containerPoint.coordinate[1]) {
                return containerPoint
            }
        }
        return null
    }

}