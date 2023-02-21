package com.phonetaxx.ui.navigationdrawer.accountsetting

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonetaxx.R
import com.phonetaxx.adapter.FilterRecyclerView
import com.phonetaxx.listener.BaseRecyclerListener
import com.phonetaxx.model.NotificationSoundModel
import com.phonetaxx.ui.BaseActivity
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_frequent_number.*
import kotlinx.android.synthetic.main.no_content_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class NotificationSoundActivity : BaseActivity(), View.OnClickListener, BaseRecyclerListener<NotificationSoundModel> {

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, NotificationSoundActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_sound)
        initView()
        getCategorizedData()
    }

    lateinit var adapter: NotificationSoundAdapter
    private fun initView() {
        cardviewToolbar.setBackgroundResource(R.drawable.toolbar_card_background)
        tvToolbarTitle.setText(getString(R.string.notification_sound))
        ivBackToolbar.setOnClickListener(this);

        var recyclerView = findViewById<FilterRecyclerView>(R.id.recyclerView)
        adapter = NotificationSoundAdapter(this, this)
        recyclerView?.setEmptyMsgHolder(tvNoData!!)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter


    }

    private fun getCategorizedData() {

        if (PreferenceHelper.getInstance().notification_sound == 0) {
            PreferenceHelper.getInstance().notification_sound = R.raw.click
        }
        Log.v(TAG, "getCategorizedData : " + PreferenceHelper.getInstance().notification_sound)

        var arrayList: ArrayList<NotificationSoundModel?> = ArrayList()

        var notificationSoundModel = NotificationSoundModel()
        notificationSoundModel.name = "Click"
        notificationSoundModel.sound = R.raw.click
        notificationSoundModel.isSelected = isNotificationSelected(R.raw.click)

        arrayList.add(notificationSoundModel)

        var notificationSoundModel1 = NotificationSoundModel()
        notificationSoundModel1.name = "Jingle"
        notificationSoundModel1.sound = R.raw.jingle
        notificationSoundModel1.isSelected = isNotificationSelected(R.raw.jingle)

        arrayList.add(notificationSoundModel1)

        var notificationSoundModel2 = NotificationSoundModel()
        notificationSoundModel2.name = "Ding dong"
        notificationSoundModel2.sound = R.raw.dingdong
        notificationSoundModel2.isSelected = isNotificationSelected(R.raw.dingdong)

        arrayList.add(notificationSoundModel2)

        var notificationSoundModel3 = NotificationSoundModel()
        notificationSoundModel3.name = "Tap"
        notificationSoundModel3.sound = R.raw.tap
        notificationSoundModel3.isSelected = isNotificationSelected(R.raw.tap)

        arrayList.add(notificationSoundModel3)

        adapter.addItems(arrayList)
    }

    val TAG: String = "NotificationSound"
    private fun isNotificationSelected(sound: Int): Boolean {
        Log.v(TAG, "isNotificationSelected : " + sound)
        if (PreferenceHelper.getInstance().notification_sound == sound) {
            return true
        } else {
            return false
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackToolbar -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun showEmptyDataView(resId: Int) {
//        showToast("showEmptyDataView")
        recyclerView.showEmptyDataView(getString(resId))
    }

    var mp: MediaPlayer? = null
    override fun onRecyclerItemClick(view: View, position: Int, item: NotificationSoundModel?) {

        PreferenceHelper.getInstance().notification_sound = item?.sound

        Log.v(TAG, "onRecyclerItemClick : " + PreferenceHelper.getInstance().notification_sound)

        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (mp != null && mp?.isPlaying!!) {
            mp?.stop()
        }
        when (am.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                mp = MediaPlayer.create(this, item!!.sound)
                mp?.start()
            }
        }
    }

}
