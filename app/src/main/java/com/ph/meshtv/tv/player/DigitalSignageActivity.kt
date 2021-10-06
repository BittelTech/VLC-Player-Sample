package com.ph.meshtv.tv.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DigitalSignageActivity : AppCompatActivity() {

    private val fragment by lazy{
        DigitalSignageFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        supportFragmentManager.beginTransaction()
            .add(R.id.player_layout,fragment,"player")
            .commit()

    }


}