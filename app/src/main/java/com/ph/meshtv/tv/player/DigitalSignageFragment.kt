package com.ph.meshtv.tv.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.*
import kotlin.math.roundToInt

class DigitalSignageFragment : Fragment() {

    private val TAG = "DigitalSignageFragment"
    private var mVideoLayout: VLCVideoLayout? = null


    var source: String = "http://192.168.200.3:8080/media/videos_vod/full_lightsout.mp4"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.surface_layout, container, false)
    }


    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mVideoLayout = view.findViewById(R.id.video_layout)

        /**
         * source = the source to play
         * layout = org.videolan.libvlc.util.VLCVideoLayout
         * isLive = TRUE - if live source (eg. udp, rtsp, etc), FALSE if not
         * autoRestart = TRUE - restart forever, FALSE = play once
         */

        startVLC(source = source, layout = mVideoLayout, isLive = false, autoRestart = false) { completed, status ->
            if (completed) {
                Log.i(TAG, "@video completed")
            }
            Log.v(TAG, status)
        }

    }
}