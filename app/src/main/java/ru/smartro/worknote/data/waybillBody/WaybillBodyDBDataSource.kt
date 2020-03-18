package ru.smartro.worknote.data.waybillBody

import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.database.entities.*
import ru.smartro.worknote.domain.models.complex.SrpContainerWithRelations
import ru.smartro.worknote.domain.models.complex.WaybillWithRelations

class WaybillBodyDBDataSource(
    val dataBase: DataBase,
    val dbWaybillWIthRelationsConverter: DbWaybillWIthRelationsConverter
) {

    fun insert(waybillWithRelations: WaybillWithRelations) {
        val entities = makeEntities(waybillWithRelations)
        dataBase.runInTransaction {
            dataBase.wayBillBodyDAO.insert(entities.waybillBody)
            dataBase.workOrderDAO.insertAll(entities.workOrderEntities)
            dataBase.srpPlatformDao.insertAll(entities.platformEntities)
            dataBase.srpContainerDao.insertAll(entities.containerEntities)
            dataBase.srpContainerTypeDao.insertAll(entities.containerTypeEntities)
        }
    }

    private fun makeEntities(waybillWithRelations: WaybillWithRelations): WaybillSeparateEntities {
        val waybillEntity =
            dbWaybillWIthRelationsConverter.makeWaybillBodyEntity(waybillWithRelations)
        val workOrderEntities = waybillWithRelations.workOrders.map {
            dbWaybillWIthRelationsConverter.makeWorkOrderEntity(it, waybillWithRelations.srpId)
        }
        val platformEntities = waybillWithRelations.workOrders
            .fold(mutableListOf<SrpPlatformEntity>()) { acc, workOrderWithRelations ->
                acc.addAll(workOrderWithRelations.platforms.map {
                    dbWaybillWIthRelationsConverter.makePlatformEntity(
                        it,
                        workOrderWithRelations.srpId
                    )
                })

                return@fold acc
            }
        val containerEntities =
            flatMapContainers(waybillWithRelations) { container: SrpContainerWithRelations, platformId: Int ->
                dbWaybillWIthRelationsConverter.makeContainerEntity(container, platformId)
            }

        val containerTypeEntities = flatMapContainers(waybillWithRelations) { container, _ ->
            container.srpType.asDataBaseModel()
        }.distinctBy { it.srpId }

        return WaybillSeparateEntities(
            waybillBody = waybillEntity,
            workOrderEntities = workOrderEntities,
            platformEntities = platformEntities,
            containerEntities = containerEntities,
            containerTypeEntities = containerTypeEntities
        )

    }

    private fun <ACC_ITEM> flatMapContainers(
        waybillWithRelations: WaybillWithRelations,
        op: (container: SrpContainerWithRelations, platformId: Int) -> ACC_ITEM
    ): MutableList<ACC_ITEM> {
        return waybillWithRelations.workOrders
            .fold(mutableListOf<ACC_ITEM>()) { acc, workOrderWithRelations ->
                acc.addAll(workOrderWithRelations.platforms.fold(mutableListOf<ACC_ITEM>()) { accSecond, srpPlatformWithRelations ->
                    accSecond.addAll(srpPlatformWithRelations.containers.map {
                        op(it, srpPlatformWithRelations.srpId)
                    })
                    accSecond
                })

                acc
            }
    }

    data class WaybillSeparateEntities(
        val waybillBody: WayBillBodyEntity,
        val workOrderEntities: List<WorkOrderEntity>,
        val platformEntities: List<SrpPlatformEntity>,
        val containerEntities: List<SrpContainerEntity>,
        val containerTypeEntities: List<SrpContainerTypeEntity>
    )

}