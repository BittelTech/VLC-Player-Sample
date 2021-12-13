package com.ph.meshtv.tv.player.tv.view.fragment

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
class TVLivePlayerFragment : PlayerVLCFragment() {

    var source : String? =null

    companion object{

        var player : Fragment?=null

        @JvmStatic
        fun player(@NonNull source : String?) : TVLivePlayerFragment
        {
            player = player ?: TVLivePlayerFragment()

            if(player is TVLivePlayerFragment){
                (player as TVLivePlayerFragment).source = source
            }

            return player as TVLivePlayerFragment
        }
    }

    override fun getPath(): String {
        return source!!
    }

}