package ru.smartro.worknote.data.vehicle

import ru.smartro.worknote.data.Result
import ru.smartro.worknote.domain.models.VehicleModel
import ru.smartro.worknote.network.BearerToken
import ru.smartro.worknote.network.workNote.WnNetwork
import ru.smartro.worknote.network.workNote.responseDTO.VehiclesDTO

class VehicleNetworkDataSource {


    suspend fun getAllByOrganisation(
        organisationId: Int,
        token: String
    ): Result<List<VehicleModel>> {

        val resultModels = mutableListOf<VehicleModel>()
        val firstPage = 1
        val secondPage = 2

        val middleErrorOrMetaResult = processGetAllSinglePage(
            GetAllQueryData(
                organisationId = organisationId,
                token = token,
                page = firstPage
            ),
            carry = resultModels
        )
        if (middleErrorOrMetaResult.exception != null) {
            return Result.Error(middleErrorOrMetaResult.exception)
        }
        if (middleErrorOrMetaResult.meta == null) {
            throw Exception("only one of (meta, error) can be null")
        }

        val lastPage = middleErrorOrMetaResult.meta.lastPage
        val nthPagesMiddleResult = processGetAllNthPages(
            queryData = GetAllQueryData(
                organisationId = organisationId,
                token = token,
                page = secondPage
            ),
            lastPage = lastPage,
            carry = resultModels
        )

        if (nthPagesMiddleResult != null) {
            return nthPagesMiddleResult
        }
        return Result.Success(resultModels)
    }

    private suspend fun processGetAllNthPages(
        queryData: GetAllQueryData,
        lastPage: Int,
        carry: MutableList<VehicleModel>
    ): Result.Error? {
        for (nthPage in queryData.page..lastPage) {
            val middleResult = processGetAllSinglePage(
                queryData.apply { page = nthPage },
                carry
            )
            if (middleResult.exception !== null) {
                return Result.Error(middleResult.exception)
            }
        }
        return null
    }

    private suspend fun processGetAllSinglePage(
        queryData: GetAllQueryData,
        carry: MutableList<VehicleModel>
    ): GetAllByOrgIdMetaOrErrorResult {
        val firstQueryResult = getAllByOrganisationOnPage(queryData)
        if (firstQueryResult is Result.Error) {
            return GetAllByOrgIdMetaOrErrorResult(firstQueryResult.exception, null)
        }
        val vehiclesDTO = (firstQueryResult as Result.Success).data
        carry.addAll(vehiclesDTO.asDomainModel())

        return GetAllByOrgIdMetaOrErrorResult(null, vehiclesDTO.meta)
    }

    private suspend fun getAllByOrganisationOnPage(
        queryData: GetAllQueryData
    ): Result<VehiclesDTO> {
        val getDeferred = WnNetwork.VEHICLE_ENTRY_POINT.index(
            page = queryData.page,
            organisationId = queryData.organisationId,
            token = BearerToken(queryData.token)
        )
        return try {
            Result.Success(getDeferred.await())
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    data class GetAllQueryData(val organisationId: Int, val token: String, var page: Int)

    data class GetAllByOrgIdMetaOrErrorResult(
        val exception: Throwable?,
        val meta: VehiclesDTO.Meta?
    )
}