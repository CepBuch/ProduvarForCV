package produvar.interactionwithapi.model

data class BasicOrderView(val orderCode: String?, val manufacturer: BasicManufacturerView)

data class BasicManufacturerView(val name: String?, val website: String?,
                                 val phoneNumber: String?, val email: String?)

data class Order(val code: String?, val label: String?, val dueDate: String?,
                 val items: List<OrderItem>, val process: List<OrderProcess>,
                 val statusFlow: List<WorkFlowStep>)

data class OrderItem(val label: String?)

data class WorkFlowStep(val status: String?)

data class OrderProcess(val label: String?)


