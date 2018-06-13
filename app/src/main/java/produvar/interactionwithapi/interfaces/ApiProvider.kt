package produvar.interactionwithapi.interfaces

import produvar.interactionwithapi.enums.ErrorType
import produvar.interactionwithapi.enums.TagType
import produvar.interactionwithapi.models.BasicOrderViewDTO
import produvar.interactionwithapi.models.OrderDTO
import produvar.interactionwithapi.models.User
import produvar.interactionwithapi.models.UserDTO

interface ApiProvider {
    fun authenticate(authenticationTag: String, success: (UserDTO) -> Unit, failure: (ErrorType) -> Unit)

    fun login(username: String, password: String, success: (UserDTO) -> Unit, failure: (ErrorType) -> Unit)

    fun logout(user: User, success: (Boolean) -> Unit, failure: (ErrorType) -> Unit)

    fun searchByScan(tagContent: String, tagType: TagType, success: (BasicOrderViewDTO) -> Unit, failure: (ErrorType) -> Unit)

    fun orderInfo(user: User, orderCode: String, success: (OrderDTO) -> Unit, failure: (ErrorType) -> Unit)

    fun orderStatusUpdate(user: User, orderCode: String, currentStatus: String,
                          newStatus: String, success: (Boolean) -> Unit, failure: (ErrorType) -> Unit)

}