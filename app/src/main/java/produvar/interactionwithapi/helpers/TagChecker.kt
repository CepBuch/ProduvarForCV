package produvar.interactionwithapi.helpers

import produvar.interactionwithapi.enums.TagType

class TagChecker {
    companion object {
        fun classify(tagContent: String): TagType {
            return when {
                isBarcode(tagContent) -> TagType.CODE
                isUrl(tagContent) -> TagType.URL
                else -> TagType.UNDEFINED
            }
        }

        fun isOrderTagValid(tagContent: String): Boolean {
            // Expected to be more detailed
            return isUrl(tagContent) || isBarcode(tagContent)
        }

        fun isAuthorizationTagValid(tagContent: String): Boolean {
            // Expected to be more detailed
            return isUrl(tagContent) || isBarcode(tagContent)
        }

        private fun isUrl(tagContent: String): Boolean {
            return tagContent.startsWith("https://") || tagContent.startsWith("http://")
        }

        private fun isBarcode(tagContent: String): Boolean {
            return tagContent.all {
                it.isDigit()
            }
        }
    }
}