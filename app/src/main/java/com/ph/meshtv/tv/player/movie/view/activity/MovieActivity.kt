package com.ph.meshtv.tv.player.movie.view.activity



import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.ph.meshtv.tv.player.movie.view.fragment.MovieFragment
import com.ph.meshtv.tv.player.R
import com.ph.meshtv.tv.player.control.onKeyUp


class MovieActivity : AppCompatActivity() {

    var movieFragment: MovieFragment = MovieFragment()


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        movieFragment.apply {
            onKeyUp(keyCode) {
                play(it,true)
                updateChannelNo()
            }
        }
        return super.onKeyUp(keyCode, event)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        supportFragmentManager.beginTransaction()
            .add(R.id.player_layout, movieFragment, "player")
            .commit()

    }

}