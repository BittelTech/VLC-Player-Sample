package com.ph.meshtv.tv.player.tv.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ph.bittelasia.libvlc.control.listener.OnChangeListener
import com.ph.bittelasia.libvlc.control.listener.OnFragmentListener
import com.ph.bittelasia.libvlc.views.fragment.PlayerVLCFragment
import com.ph.meshtv.tv.player.R
import com.ph.meshtv.tv.player.tv.view.fragment.TVLivePlayerFragment
import com.ph.meshtv.tv.player.tv.view.fragment.TVPlayerFragment

class TVActivity : AppCompatActivity(), OnChangeListener, OnFragmentListener {

    private val TAG = "VLC-MainActivity"


    private var player : PlayerVLCFragment? = null
    private var isLive = false

    private var source = "http://192.168.200.3:8080/media/videos_vod/full_lightsout.mp4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        if(!isLive) {
            player = TVPlayerFragment.player(source) //use this if not live
        }else{
            player = TVLivePlayerFragment.player(source) //use this is live (eg. udp, rtsp, etc)
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.player_layout,player!!,"player")
            .commit()
    }


    //============================OnChangeListener==================================================
    override fun onBufferChanged(buffer: Float) {
        Log.i(TAG, "@onBufferChanged: $buffer")
    }

    override fun onChanging(seconds: Long) {
        Log.i(TAG, "@onChanging: $seconds")
    }

    override fun onChannelChanged(`object`: Any?) {
        Log.i(TAG, "@onChannelChanged: $`object`")
    }

    override fun onChannelIndex(channelIndex: Int) {
        Log.i(TAG, "@onChannelIndex: $channelIndex")
    }

    override fun onEnd() {
        Log.e(TAG, "@onEnd")
        if(player!=null){
           player!!.stop()
           player!!.play(source,true)
        }
    }

    override fun onError(msg: String?) {
        Log.e(TAG, "@onError: $msg")
    }

    override fun onStatus(message: String?, isPlaying: Boolean) {
        Log.i(TAG, "@onStatus: message=$message, isPlaying=$isPlaying " )
    }
    //==============================================================================================

    //================================OnFragmentListener============================================
    override fun onFragmentDetached(fragment: Fragment) {
        Log.i(TAG,"@onFragmentDetached: ${fragment::javaClass.name}")
    }
    //==============================================================================================


}