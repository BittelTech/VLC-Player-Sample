package com.ph.meshtv.tv.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.videolan.libvlc.util.VLCVideoLayout

class DigitalSignageFragment : Fragment() {

    private val TAG = "DigitalSignageFragment"
    private var mVideoLayout: VLCVideoLayout? = null


    var source : String ="http://192.168.200.3:8080/media/videos_vod/full_lightsout.mp4"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.surface_layout,container,false)
    }


    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVideoLayout = view.findViewById(R.id.video_layout)
        startVLC(source, mVideoLayout, false) {
            Log.i(TAG, "stopped")
        }
    }
}