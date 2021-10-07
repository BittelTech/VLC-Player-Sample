package com.ph.meshtv.tv.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout


class DigitalSignageFragment : Fragment() {

    private val TAG = "DigitalSignageFragment"
    private var mVideoLayout: VLCVideoLayout? = null

    private val source = "file:///storage/emulated/0/Android/full_always_be_my_maybe.mp4"


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

        play(source)
    }


   fun play(source: String?)
   {
       /**
        * source = the source to play
        * layout = org.videolan.libvlc.util.VLCVideoLayout
        * isLive = TRUE - if live source (eg. udp, rtsp, etc), FALSE if not
        * autoRestart = TRUE - restart forever, FALSE = play once
        */

       startVLC(source, layout = mVideoLayout, isLive = false, autoRestart = true) { status,event ->
           when(event){
               MediaPlayer.Event.Stopped -> run{
                   Log.i(TAG, "@video completed")
               }
               MediaPlayer.Event.EncounteredError -> run{
                   Log.i(TAG, "@video encountered error")
               }
           }
           Log.v(TAG, status)
       }
   }

}