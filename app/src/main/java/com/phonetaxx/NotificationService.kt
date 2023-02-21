package com.phonetaxx

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.phonetaxx.firebase.FirebaseDatabaseListener
import com.phonetaxx.firebase.helper.FireBaseCallLogHelper
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.DateTimeHelper
import com.phonetaxx.utils.NotificationUtil
import com.phonetaxx.utils.PreferenceHelper
import java.util.*

public class NotificationService : JobIntentService() {
    val TAG: String = "NotificationService"

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, NotificationService::class.java, 101, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (PreferenceHelper.getInstance().isLogin!! && PreferenceHelper.getInstance().getProfileData()!!.pushNotification == 1) {

            if (!PreferenceHelper.getInstance().last_daily_notification_date.equals(
                    DateTimeHelper.getInstance().getAppDateFormat(
                        System.currentTimeMillis()
                    )
                )
            ) {
                getTodayUncategorizedCall()
            }

            if ((!PreferenceHelper.getInstance().last_weekly_notification_week.equals(
                    DateTimeHelper.getInstance().getWeekDays(
                        DateTimeHelper.getInstance().currentTimeStamp
                    )
                ))
                && DateTimeHelper.getInstance().getLastDayDateOfTheWeek(System.currentTimeMillis()).equals(
                    DateTimeHelper.getInstance().getAppDateFormat(System.currentTimeMillis())
                )
            ) {
                getWeeklyUncategorizedCall()
            }

            val cal = Calendar.getInstance()
            Log.v(TAG, "onHandleWork : ")
            if ((!PreferenceHelper.getInstance().last_monthly_notification_month.equals(
                    DateTimeHelper.getInstance().getDisplayMonthFormat(
                        DateTimeHelper.getInstance().currentTimeStamp
                    )
                ))
                && cal.getActualMaximum(Calendar.DAY_OF_MONTH).equals(cal.get(Calendar.DATE))
            ) {
                Log.v(TAG, "onHandleWork 12: ")
                getMonthlyUncategorizedCall()
            }
        }

    }

    private fun getTodayUncategorizedCall() {
        FireBaseCallLogHelper.getInstance().getAllCallLogByCategoryAndFilterType(
            Const.uncategorized, Const.TODAY,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {

                    var intent = Intent()
                    var size = data.size
                    if (size > 0) {
                        val notificationUtil = NotificationUtil(
                            applicationContext,
                            "Today",
                            "Today " + getString(R.string.app_name),
                            "You have " + size + " uncategorized call available for today.",
                            intent,
                            1
                        )

                        PreferenceHelper.getInstance().last_monthly_notification_month =
                            DateTimeHelper.getInstance()
                                .getDisplayMonthFormat(DateTimeHelper.getInstance().currentTimeStamp)
                        notificationUtil.show()
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {

                }
            })

    }

    private fun getWeeklyUncategorizedCall() {
        FireBaseCallLogHelper.getInstance().getAllCallLogByCategoryAndFilterType(
            Const.uncategorized, Const.WEEKLY,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {

                    var intent = Intent()
                    var size = data.size
                    if (size > 0) {
                        val notificationUtil = NotificationUtil(
                            applicationContext,
                            "Weekly",
                            "Weekly " + getString(R.string.app_name),
                            "You have " + size + " uncategorized call available for this week.",
                            intent,
                            2
                        )

                        PreferenceHelper.getInstance().last_weekly_notification_week =
                            DateTimeHelper.getInstance().getWeekDays(DateTimeHelper.getInstance().currentTimeStamp)
                        notificationUtil.show()
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {

                }
            })

    }

    private fun getMonthlyUncategorizedCall() {
        FireBaseCallLogHelper.getInstance().getAllCallLogByCategoryAndFilterType(
            Const.uncategorized, Const.MONTHLY,
            object : FirebaseDatabaseListener<ArrayList<CallLogsDbModel?>> {
                override fun onSuccess(data: ArrayList<CallLogsDbModel?>) {

                    var intent = Intent()
                    var size = data.size
                    if (size > 0) {
                        val notificationUtil = NotificationUtil(
                            applicationContext,
                            "Weekly",
                            "Monthly " + getString(R.string.app_name),
                            "You have " + size + " uncategorized call available in " + DateTimeHelper.getInstance().getDisplayMonthFormat(
                                DateTimeHelper.getInstance().currentTimeStamp
                            ),
                            intent,
                            3
                        )

                        PreferenceHelper.getInstance().last_weekly_notification_week =
                            DateTimeHelper.getInstance().getWeekDays(DateTimeHelper.getInstance().currentTimeStamp)
                        notificationUtil.show()
                    }
                }

                override fun onFail(errorMessage: String, exception: Exception?) {

                }
            })

    }

}