package ru.smartro.worknote.ac.exception

//class THR(msg : String) : Throwable(msg)
//package ru.smartro.worknote.awORKOLDs.service.network.exception

//sealed class BadRequestException(var msg: String) :  {
//    class login(val msg2: String) : BadRequestException(msg2)
////    class login(val msg2: String) : BadRequestException(msg2)
//    class owner : BadRequestException("owner")
//    //    object Fetching : SealedClass()
////    object Fetched : SealedClass()
////    object NotFetched : SealedClass()
//    fun getThrowable(e: BadRequestException): Throwable =
//        when (e) {
//            is BadRequestException.login -> Throwable(msg)
//            is BadRequestException.owner -> e.x + e.y
//           else
//        }
//}


/**
sealed interface Error // имеет реализации только в том же пакете и модуле

sealed class IOError(): Error // расширяется только в том же пакете и модуле
open class CustomError(): Error // может быть расширен везде, где виден
 */