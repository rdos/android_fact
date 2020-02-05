package ru.smartro.worknote.database.dao

import androidx.room.*
import ru.smartro.worknote.database.entities.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userEntity: UserEntity)

    @Update
    fun update(userEntity: UserEntity)

    @Query("UPDATE users SET currentOrganisationId = :organisationId WHERE id = :userId")
    fun setCurrentOrganisationId(userId: Int, organisationId: Int)

    @Query("SELECT EXISTS(SELECT *  FROM users WHERE email = :email AND password = :password LIMIT 1)")
    fun checkAuth(email: String, password: String): Boolean

    @Query("SELECT * FROM users WHERE id = :key")
    fun get(key: Int): UserEntity

    @Query("UPDATE users SET isLoggedIn = 0")
    fun logOutAll()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id=:key")
    fun login(key: Int)

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedIn(): UserEntity?
}