package com.ph.meshtv.tv.player.tv.view.fragment

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
class TVPlayerFragment : PlayerVLCFragment() {

    var source : String? =null

    companion object{

        var player : Fragment?=null

        @JvmStatic
        fun player(@NonNull source : String?) : TVPlayerFragment
        {
            player = player ?: TVPlayerFragment()

            if(player is TVPlayerFragment){
                (player as TVPlayerFragment).source = source
            }

           return player as TVPlayerFragment
        }
    }

    override fun getPath(): String {
        return source!!
    }

}