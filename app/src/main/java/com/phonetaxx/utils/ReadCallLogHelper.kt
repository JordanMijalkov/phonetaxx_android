package com.phonetaxx.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.provider.CallLog
import android.text.TextUtils
import android.util.Log
import com.phonetaxx.firebase.model.CallLogsDbModel
import java.lang.Long
import java.util.*
import kotlin.collections.ArrayList


class ReadCallLogHelper {
    val TAG: String = "ReadCallLogHelper"
    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: ReadCallLogHelper? = null
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        @JvmStatic
        fun getInstance(context: Context): ReadCallLogHelper {
            this.context = context;
            if (instance == null) {
                instance = ReadCallLogHelper()
            }
            return instance as ReadCallLogHelper
        }
    }


    public fun getCallDetailNew(lastSyncTime: kotlin.Long?, listener: ReadCallLogListener?) {
        MyAsyncTask(lastSyncTime, listener).execute()
    }

    public class MyAsyncTask() : AsyncTask<Void, Void, ArrayList<CallLogsDbModel>>() {
        var listener: ReadCallLogListener? = null
        var lastSyncTime: kotlin.Long? = null
        val TAG: String = "ReadCallLogHelper"

        constructor(lastSyncTime: kotlin.Long?, listener: ReadCallLogListener?) : this() {
            this.listener = listener
            this.lastSyncTime = lastSyncTime
        }

        private var callLogArrayList: ArrayList<CallLogsDbModel> = ArrayList()
        override fun doInBackground(vararg p0: Void?): ArrayList<CallLogsDbModel> {
            return getCallDetails()
        }

        override fun onPostExecute(result: ArrayList<CallLogsDbModel>?) {
            super.onPostExecute(result)
            listener?.onCompleted(result)
        }

        private fun getCallDetails(): ArrayList<CallLogsDbModel> {


            callLogArrayList.clear()
            var userUUID = ""
            val sb = StringBuffer()

            val mSelectionClause = CallLog.Calls.DATE + " >= ?"
            val mSelectionArgs = arrayOf<String>(lastSyncTime.toString())

            Log.v(TAG, "getCallDetails : " + lastSyncTime.toString())

            val managedCursor: Cursor =
                context?.contentResolver?.query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    mSelectionClause,
                    mSelectionArgs,
                    CallLog.Calls.DATE + " DESC"
                )!!
            val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
            val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
            val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
            val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
            val name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)

            sb.append("Call Details :")
            Log.v(TAG, "lastSyncCallTimeStamp : " + lastSyncTime.toString());

            while (managedCursor.moveToNext()) {
                val phNumber = managedCursor.getString(number)
                val callType = managedCursor.getString(type)
                val callDate = managedCursor.getString(date)
                val callDayTime = Date(Long.valueOf(callDate))
                val callDuration = managedCursor.getString(duration)
                val name = managedCursor.getString(name)
                var dir: String = ""
                val dircode = Integer.parseInt(callType)
                when (dircode) {
                    CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"

                    CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"

                    CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                }
                var callLog = "\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir +
                        " \nCall Date:--- " + DateTimeHelper.getInstance()
                    .getDisplayDateFormat(Long.parseLong(callDate)) +
                        " \nname :--- " + name +
                        " \ntimestamp :--- " + Long.parseLong(callDate) +
                        " \nCall Date time:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration

                Log.v("CALL_LOG", ": " + callLog)
                var callLogModel = CallLogsDbModel()
                callLogModel.uuId = userUUID
                callLogModel.userUuid = userUUID
                if (TextUtils.isEmpty(name)) {
                    callLogModel.name = phNumber

                } else {
                    callLogModel.name = name

                }
                callLogModel.phoneNumber = phNumber
                callLogModel.callType = dir
                callLogModel.callDateTimeUTC = Long.parseLong(callDate)
                callLogModel.callDateTimeLocal =
                    DateTimeHelper.getInstance().getUTCtoGMTtimestamp(Long.parseLong(callDate))
                callLogModel.callDurationInSecond = callDuration
                callLogModel.callCategory = Const.uncategorized
                callLogModel.callDate =
                    DateTimeHelper.getInstance().getDisplayDateFormat(Long.parseLong(callDate))
                callLogModel.callWeek =
                    DateTimeHelper.getInstance().getWeekDays(Long.parseLong(callDate))
                callLogModel.callMonth =
                    DateTimeHelper.getInstance().getDisplayMonthFormat(Long.parseLong(callDate))
                callLogModel.callYear =
                    DateTimeHelper.getInstance().getDisplayYearFormat(Long.parseLong(callDate))

                Log.v(
                    TAG,
                    ": Call Time " + callLogModel.callDateTimeLocal + " - last sync : " + PreferenceHelper.getInstance().lastSyncCallTimeStamp
                );
                if (callLogModel.callDateTimeLocal > lastSyncTime!! && callDuration.toInt() > 0) {
                    callLogArrayList.add(callLogModel)
                }

            }
            return callLogArrayList

        }

    }

    public interface ReadCallLogListener {
        fun onCompleted(result: ArrayList<CallLogsDbModel>?)
        fun onStart()
        fun onFail()
    }

}