package ru.smartro.worknote.data

import ru.smartro.worknote.database.DataBase
import ru.smartro.worknote.database.entities.UserEntity
import ru.smartro.worknote.domain.models.UserModel

class DbLoginDataSource(private val dataBase: DataBase) {

    fun insertOrUpdateUser(userModel: UserModel) {
        val dbModel = userModel.asDataBaseModel()
        dataBase.userDao.insert(dbModel)
    }

    fun updateCurrentOrganisation(userId: Int, organisationId: Int) {
        dataBase.userDao.setCurrentOrganisationId(userId, organisationId)
    }

    fun updateToken(userModel: UserModel) {

    }

    fun login(userId: Int) {
        dataBase.userDao.login(userId)
    }

    fun logOutAll() {
        dataBase.userDao.logOutAll()
    }

    fun getLoggedIn(): UserEntity? {
        return dataBase.userDao.getLoggedIn()
    }
}