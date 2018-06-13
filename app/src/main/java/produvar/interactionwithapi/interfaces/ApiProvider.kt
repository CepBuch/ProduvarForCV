package produvar.interactionwithapi.interfaces

import produvar.interactionwithapi.enums.TagType
import produvar.interactionwithapi.models.BasicOrderViewDTO
import produvar.interactionwithapi.models.OrderDTO
import produvar.interactionwithapi.models.User
import produvar.interactionwithapi.models.UserDTO

interface ApiProvider {
    fun authenticate(authenticationTag: String, handler: (UserDTO?) -> Unit)

    fun login(username: String, password: String, handler: (UserDTO?) -> Unit)

    fun logout(user: User, handler: (Boolean) -> Unit)

    fun searchByScan(tagContent: String, tagType: TagType, handler: (BasicOrderViewDTO?) -> Unit)

    fun orderInfo(user: User, orderCode: String, handler: (OrderDTO?) -> Unit)

    fun orderStatusUpdate(user: User, orderCode: String, currentStatus: String,
                          newStatus: String, handler: (Boolean) -> Unit)

}