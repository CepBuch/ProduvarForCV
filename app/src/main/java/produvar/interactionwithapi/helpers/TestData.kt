package produvar.interactionwithapi.helpers

import produvar.interactionwithapi.models.*

class TestData {
    companion object {
        val manufacturer = BasicManufacturerViewDTO("Evolve Productions B.V",
                "https://www.evolveproductions.nl", "+79256410156",
                "info@evolveproductions.nl")
        val orderItems = listOf<String>("Worktop X34 for Johnson family", "everything is added dynamically",
                "according to server response", "if server doesn't provide data for one or other card it won't be shown",
                "btw, not sure if icons in this card are ok").map { OrderItemDTO(it) }

        private val messages = listOf("2017-03-03 15:33 Pete Manson polished the blade",
                "2018-05-23 2:37 if it has date it will be highlighted", "otherwise, just shown as a string",
                "i'm also thinking of making every card expandable/collapsable",
                "2018-05-23 2:46 When opened, probably show ''expanded'' only Manufacturer and OrderDTO info and collapse others")
                .map { OrderProcessDTO(it, "", "") }.toMutableList()


        private fun getMessages(): List<OrderProcessDTO> {
            messages.addAll(listOf(OrderProcessDTO("", "2018-10-06", "Hey Sereja"),
                    OrderProcessDTO(null, "2018-23-23", "Papapapa")))
            return messages.toList()
        }

        private fun getWorkflowSteps(): List<WorkFlowStepDTO> {
            val workFlowSteps = listOf("1st previous step", "2nd step", "these", "are", "steps", "of execution")
                    .map { WorkFlowStepDTO(it, false, true) }.toMutableList()
            workFlowSteps.add(WorkFlowStepDTO("Current step", true, false))
            workFlowSteps.addAll(listOf("future step 1", "future step 2", "finish").map { WorkFlowStepDTO(it, false, false) })
            return workFlowSteps.toList()
        }

        val basicOrderView = BasicOrderViewDTO("2018004938", manufacturer)

        val order = OrderDTO(basicOrderView.ordercode, "Worktop X34 for Johnson family",
                "2018-07-29T09:12:33.001Z", orderItems, getMessages(), getWorkflowSteps())
    }
}