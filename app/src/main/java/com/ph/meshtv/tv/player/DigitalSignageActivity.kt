package com.ph.meshtv.tv.player


import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity

class DigitalSignageActivity : AppCompatActivity() {

    private val TAG = "DigitalSignageActivity"

    private var source1: String = "http://192.168.200.3:8080/media/videos_vod/full_lightsout.mp4"
    private var source2: String = "http://192.168.200.3:8080/media/videos_vod/full_secretlife.mp4"


    var fragment : DigitalSignageFragment = DigitalSignageFragment()



    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        KeyEvent.keyCodeToString(event!!.keyCode)
        when (event.keyCode) {
            KeyEvent.KEYCODE_PAGE_UP,
            KeyEvent.KEYCODE_CHANNEL_UP,
            KeyEvent.KEYCODE_DPAD_UP -> {
                fragment.play(source1)
                return true
            }
            KeyEvent.KEYCODE_PAGE_DOWN,
            KeyEvent.KEYCODE_CHANNEL_DOWN,
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                fragment.play(source2)
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        supportFragmentManager.beginTransaction()
            .add(R.id.player_layout,fragment,"player")
            .commit()

    }


}