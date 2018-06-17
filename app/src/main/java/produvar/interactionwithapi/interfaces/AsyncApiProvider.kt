package produvar.interactionwithapi.interfaces

import kotlinx.coroutines.experimental.Deferred
import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.models.BasicOrderViewDTO
import produvar.interactionwithapi.models.OrderDTO
import produvar.interactionwithapi.models.User
import produvar.interactionwithapi.models.UserDTO

interface AsyncApiProvider {
    fun authenticate(authenticationTag: String): Deferred<Pair<UserDTO?, ErrorType?>>

    fun login(username: String, password: String): Deferred<Pair<UserDTO?, ErrorType?>>

    fun logout(user: User): Deferred<ErrorType?>

    fun searchByScan(tagContent: String): Deferred<Pair<BasicOrderViewDTO?, ErrorType?>>

    fun orderInfo(user: User, orderCode: String): Deferred<Pair<OrderDTO?, ErrorType?>>

    fun orderStatusUpdate(user: User, orderCode: String, currentStatus: String, newStatus: String,
                          description: String, location: String): Deferred<ErrorType?>
}