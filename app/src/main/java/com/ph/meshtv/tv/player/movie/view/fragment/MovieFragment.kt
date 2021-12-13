package com.ph.meshtv.tv.player.movie.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.ph.meshtv.tv.player.R
import com.ph.meshtv.tv.player.control.*
import com.ph.meshtv.tv.player.movie.model.MoviesItem
import com.ph.meshtv.tv.player.util.Utils
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.*


class MovieFragment : Fragment() {


    val TAG = "DigitalSignageFragment"

    var timer: Timer? = null

    var surfaceFrame: VLCVideoLayout? = null
    var progressFrame: View? = null
    var statusFrame: View? = null
    var tvChannelNo: TextView? = null
    var tvChannelTitle: TextView? = null
    var tvDescription: TextView? = null
    var ivIcon: ImageView? = null

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

        surfaceFrame = view.findViewById(R.id.video_layout)
        statusFrame = view.findViewById(R.id.status_frame)
        progressFrame = view.findViewById(R.id.progress_frame)
        tvChannelNo = view.findViewById(R.id.tv_channel_no)
        tvChannelTitle = view.findViewById(R.id.tv_channel_name)
        tvDescription = view.findViewById(R.id.tv_description)
        ivIcon = view.findViewById(R.id.iv_icon)

        context?.apply {
            vlcMedia = Utils.getMoviesAPI(this)
        }

        vlcMedia?.apply {
            play(this[0])
            //play(MoviesItem("","udp://@239.0.0.5:6010",0,",",5,"","","")

        }
//        startVLC(
//            "udp://@239.0.0.5:6010" ,
//            surfaceFrame!!,
//            progressFrame!!,
//            withController = true,
//            isLive = true,
//            autoRestart = true
//        ) { status, event ->
//            Log.i(this.tag, status)
//        }
//        hideVLCUIStatus(80000)

    }

    override fun onStop() {
        super.onStop()
        stopVLC()
    }

    override fun onDestroy() {
        super.onDestroy()
        detachVLC()
    }

    fun play(movie: MoviesItem?, isTrailer: Boolean = false, withController: Boolean = true) {
        detachVLC()
        movie?.let {
            startVLC(
                if (isTrailer) it.trailer else it.full,
                surfaceFrame!!,
                progressFrame!!,
                withController,
                autoRestart = true
            ) { status, event ->
                Log.i(this.tag, status)
            }

            tvChannelNo?.text = it.movie_id.toString()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvChannelTitle?.text = Html.fromHtml(it.title, Html.FROM_HTML_MODE_COMPACT)
                tvDescription?.text = Html.fromHtml(Utils.readFile(it.description), Html.FROM_HTML_MODE_COMPACT)
            } else {
                tvChannelTitle?.text = Html.fromHtml(it.title)
                tvDescription?.text  = Html.fromHtml(Utils.readFile(it.description))
            }

            ivIcon?.load(it.poster)
        }

        hideVLCUIStatus(80000)
    }

    fun updateChannelNo()
    {
        vlcMedia?.apply {
            tvChannelNo?.text = this[channelIndex].movie_id.toString()
        }
    }

    private fun hideVLCUIStatus(delay: Long = 10000) {
        timer?.cancel()
        timer?.purge()
        timer = null
        timer = timer ?: Timer()
        statusFrame!!.visibility = View.VISIBLE
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    statusFrame!!.visibility = View.GONE
                }
            }
        }, delay)
    }

}