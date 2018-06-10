package produvar.interactionwithapi.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson


data class UserDTO(val bearer: String, val username: String?, val name: String?, val role: String?) {
    class Deserializer : ResponseDeserializable<UserDTO> {
        override fun deserialize(content: String): UserDTO? = Gson().fromJson(content, UserDTO::class.java)
    }

    fun convertToModel(loginType: LoginType): User {
        return User(loginType, bearer, username, name, role)
    }
}


data class BasicOrderViewDTO(val ordercode: String?, val manufacturer: BasicManufacturerViewDTO?) {
    class Deserializer : ResponseDeserializable<BasicOrderViewDTO> {
        override fun deserialize(content: String): BasicOrderViewDTO? = Gson().fromJson(content, BasicOrderViewDTO::class.java)
    }

    fun convertToModel(): Pair<String?, Manufacturer?> {
        return Pair(ordercode,
                if (manufacturer != null) {
                    Manufacturer(manufacturer.name, manufacturer.website, manufacturer.phoneNumber, manufacturer.email)
                } else null)
    }
}

data class BasicManufacturerViewDTO(val name: String?, val website: String?,
                                    val phoneNumber: String?, val email: String?)

data class OrderDTO(val code: String?, val label: String?, val dueDate: String?,
                    val items: List<OrderItemDTO?>?, val process: List<OrderProcessDTO?>?,
                    val statusFlow: List<WorkFlowStepDTO?>?
) {

    class Deserializer : ResponseDeserializable<OrderDTO> {
        override fun deserialize(content: String): OrderDTO? {
            val arrayOfOrders = Gson().fromJson(content, Array<OrderDTO>::class.java)
            return if (arrayOfOrders.isNotEmpty()) {
                arrayOfOrders.first()
            } else null
        }
    }

    fun convertToModel(): Order {
        return Order(code, label, dueDate, filterOrderItems(), filterOrderProcesses(), filterStatusFlow())
    }

    private fun filterOrderItems(): List<Item> {
        return items?.filterNotNull()?.filterNot { it.label.isNullOrBlank() }?.map { Item(it.label!!) }
                ?: emptyList()
    }

    private fun filterStatusFlow(): List<WorkflowStep> {
        return statusFlow?.filterNotNull()?.filterNot {
            it.status.isNullOrBlank() ||
                    it.iscurrent == null || it.isfinished == null
        }?.map { WorkflowStep(it.status!!, it.iscurrent!!, it.isfinished!!) }
                ?: emptyList()
    }

    private fun filterOrderProcesses(): List<ProcessStep> {
        return process?.filterNotNull()?.filterNot {
            it.label.isNullOrBlank() &&
                    it.`when`.isNullOrBlank() && it.description.isNullOrBlank()
        }?.map { ProcessStep(it.label, it.`when`, it.description) } ?: emptyList()
    }
}

data class OrderItemDTO(val label: String?)

data class WorkFlowStepDTO(val status: String?, val iscurrent: Boolean?, val isfinished: Boolean?)

data class OrderProcessDTO(val label: String?, val `when`: String?, val description: String?)


