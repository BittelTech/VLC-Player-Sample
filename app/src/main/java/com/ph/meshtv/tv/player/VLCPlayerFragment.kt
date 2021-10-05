package com.ph.meshtv.tv.player

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.ph.bittelasia.libvlc.control.annotation.PlayerSettings
import com.ph.bittelasia.libvlc.model.ScaleType
import com.ph.bittelasia.libvlc.views.fragment.PlayerVLCFragment


@PlayerSettings(
    scaleType = ScaleType.SURFACE_FILL, //Video Scale
    preventDeadLock = false, //required
    enableDelay = false, //false if live, true if demo
    showStatus = true // show vlc logs
)
class VLCPlayerFragment : PlayerVLCFragment() {

    var source : String? =null

    companion object{

        var player : Fragment?=null

        @JvmStatic
        fun player(@NonNull source : String?) : VLCPlayerFragment
        {
            player = player?:VLCPlayerFragment()

            if(player is VLCPlayerFragment){
                (player as VLCPlayerFragment).source = source
            }

           return player as VLCPlayerFragment
        }
    }

    override fun getPath(): String {
        return source!!
    }

}