package com.ph.meshtv.tv.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import kotlin.math.roundToInt


class DigitalSignageFragment : Fragment() {



     val TAG = "DigitalSignageFragment"

     var mVideoLayout: VLCVideoLayout? = null
     var progressFrame: View? = null

     val source1 = "file:///storage/emulated/0/Android/trailer_always_be_my_maybe.mp4"
     val source2 = "file:///storage/emulated/0/Android/full_always_be_my_maybe.mp4"

     val source3 = "file:///storage/emulated/0/Android/trailer_best_seller.mp4"
     val source4 = "file:///storage/emulated/0/Android/full_best_seller.mp4"

     val source5 = "file:///storage/emulated/0/Android/trailer_bird_box.mp4"
     val source6 = "file:///storage/emulated/0/Android/full_bird_box.mp4"

     val source7 =  "file:///storage/emulated/0/Android/trailer_black_widow.mp4"
     val source8 =  "file:///storage/emulated/0/Android/full_black_widow.mp4"

     val source9 =  "file:///storage/emulated/0/Android/trailer_boss_level.mp4"
     val source10 =  "file:///storage/emulated/0/Android/full_boss_level.mp4"

     val source11 = "file:///storage/emulated/0/Android/trailer_cruella.mp4"
     val source12 = "file:///storage/emulated/0/Android/full_cruella.mp4"

     val source13 = "file:///storage/emulated/0/Android/trailer_dont_breath.mp4"
     val source14 = "file:///storage/emulated/0/Android/full_dont_breath.mp4"

     val source15 = "file:///storage/emulated/0/Android/trailer_ex_machina.mp4"
     val source16 = "file:///storage/emulated/0/Android/full_ex_machina.mp4"

     val source17 = "file:///storage/emulated/0/Android/trailer_fast_and_furios.mp4"
     val source18 = "file:///storage/emulated/0/Android/full_fast_and_furios.mp4"

     val source19 = "file:///storage/emulated/0/Android/trailer_free_guy.mp4"
     val source20 = "file:///storage/emulated/0/Android/full_free_guy.mp4"

     val source21 = "file:///storage/emulated/0/Android/trailer_godzilla.mp4"
     val source22 = "file:///storage/emulated/0/Android/full_godzilla.mp4"

     val source23 = "file:///storage/emulated/0/Android/trailer_green_knight.mp4"
     val source24 = "file:///storage/emulated/0/Android/full_green_knight.mp4"

     val source25 = "file:///storage/emulated/0/Android/trailer_invisible_man.mp4"
     val source26 = "file:///storage/emulated/0/Android/full_invisible_man.mp4"

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
        progressFrame = view.findViewById(R.id.progress_frame)
        //play(source23)
    }


   fun play(source: String?)
   {
       /**
        * source = the source to play
        * layout = org.videolan.libvlc.util.VLCVideoLayout
        * isLive = TRUE - if live source (eg. udp, rtsp, etc), FALSE if not
        * autoRestart = TRUE - restart forever, FALSE = play once
        */
        startVLC(source=source, layout = mVideoLayout, progressFrame =progressFrame, isLive = false, autoRestart = true) { status,event ->
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


    fun stop(){
        /**
         * Stop VLC Player
         */
        stopVLC()
    }

}