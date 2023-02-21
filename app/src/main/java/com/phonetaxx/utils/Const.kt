package com.phonetaxx.utils

class Const {
    companion object {

        const val USER_MODEL = "USER_MODEL"
        private var instance: Const? = null

        const val USER_TYPE_NORMAL = "Normal"
        const val USER_TYPE_GOOGLE = "Google"
        const val USER_TYPE_FACEBOOK = "Facebook"
        const val UUID = "UUID"

        const val uncategorized = "0"
        const val personal = "1"
        const val business = "2"
        const val LOADING_OFFSET = 5
        const val PAGINATION_ITEM_COUNT = 10
        const val ITEM_TYPE = 0
        const val LOADING_TYPE = 1
        const val TODAY = "Today"
        const val MONTHLY = "Monthly"
        const val WEEKLY = "Weekly"
        const val MONTHLY_SUBSCRIPTION_ID_1 = "phonetaxx_monthly_subscription_1"
        const val ANNUAL_SUBSCRIPTION_ID_1 = "annual_subscription_id_1"

        const val isSubScriptionAvailable = "isSubScriptionAvailable"
        const val SUB_PRICE_ID = "SUB_PRICE_ID"
        const val SUB_PRICE_TYPE = "SUB_PRICE_TYPE"

        const val MONTHLY_PLAN = "Monthly"
        const val ANNUAL_PLAN = "Yearly"
        const val BASIC_PLAN = "Free"

        const val NAME = "NAME"
        const val PHONE = "PHONE"
        const val IMAGE = "IMAGE"

        const val FROM_FORGOT = "FROM_FORGOT"


        const val CALL_LIMIT = 14


        fun getInstance(): Const {
            if (instance == null) {
                instance = Const()
            }
            return instance as Const
        }

    }
}