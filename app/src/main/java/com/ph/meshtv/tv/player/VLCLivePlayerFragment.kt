package com.ph.meshtv.tv.player

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.ph.bittelasia.libvlc.control.annotation.PlayerSettings
import com.ph.bittelasia.libvlc.model.ScaleType
import com.ph.bittelasia.libvlc.views.fragment.PlayerVLCFragment


@PlayerSettings(
    scaleType = ScaleType.SURFACE_FILL, //Video Scale
    preventDeadLock = true, //required
    enableDelay = false, //false if live, true if demo
    showStatus = true // show vlc logs
)
class VLCLivePlayerFragment : PlayerVLCFragment() {

    var source : String? =null

    companion object{

        var player : Fragment?=null

        @JvmStatic
        fun player(@NonNull source : String?) : VLCLivePlayerFragment
        {
            player = player?:VLCLivePlayerFragment()

            if(player is VLCLivePlayerFragment){
                (player as VLCLivePlayerFragment).source = source
            }

            return player as VLCLivePlayerFragment
        }
    }

    override fun getPath(): String {
        return source!!
    }

}