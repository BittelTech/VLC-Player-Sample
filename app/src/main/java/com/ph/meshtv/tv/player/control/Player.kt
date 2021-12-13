package com.ph.meshtv.tv.player.control


import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ph.bittelasia.libvlc.control.annotation.AttachPlayerFragment
import com.ph.bittelasia.libvlc.control.annotation.PlayerActivityLayout
import com.ph.meshtv.tv.player.movie.model.MoviesItem
import kotlinx.coroutines.*
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.Media
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/**
 * Mapping the vlc player as mutable
 */
val playerMap = mutableMapOf<Fragment, HashMap<LibVLC, MediaPlayer>>()
var Fragment.vlcPlayer: HashMap<LibVLC, MediaPlayer>?
    get() = playerMap[this] ?: hashMapOf()
    set(value) {
        playerMap[this] = value!!
    }
val channelNoMap = mutableMapOf<Fragment, String>()
var Fragment.channelNo: String?
    get() = channelNoMap[this] ?: ""
    set(value) {
        channelNoMap[this] = value!!
    }

val playerTaskMap = mutableMapOf<Fragment, CoroutineScope>()
var Fragment.playerTask: CoroutineScope
    get() = (playerTaskMap[this]
        ?: CoroutineScope(Dispatchers.Main).launchPeriodicAsync(100) {}) as CoroutineScope
    set(value) {
        playerTaskMap[this] = value
    }

val playerJobMap = mutableMapOf<Fragment, HashMap<String, Job>>()
var Fragment.playerJob: HashMap<String, Job>
    get() = playerJobMap[this]
        ?: hashMapOf("player" to CoroutineScope(Dispatchers.Main).launchPeriodicAsync(100) {})
    set(value) {
        playerJobMap[this] = value
    }


fun Fragment.stopVLCPlayer(playerMap: HashMap<LibVLC, MediaPlayer>) {
    vlcPlayer?.let { map ->
        map.keys.elementAt(0).let { libVLC ->
            map[libVLC]?.let { mediaPlayer ->
                mediaPlayer.let {
                    it.apply {
                        try {
                            if(vlcVout.areViewsAttached())
                            if ( isPlaying) {
                                stop()
                                detachViews()
                                release()
                            }
                        }catch (e : Exception){
                            e.printStackTrace()
                        }
                    }

                }
            }
        }

    }
}

fun Fragment.setVLCMediaPLayer(preventDeadLock: Boolean): HashMap<LibVLC, MediaPlayer> {
    val args = ArrayList<String>()
    val map = hashMapOf<LibVLC, MediaPlayer>()
    if (preventDeadLock) {
        args.add("--no-sub-autodetect-file")
        args.add("--swscale-mode=0")
        args.add("--network-caching=100")
        args.add("--no-drop-late-frames")
        args.add("--no-skip-frames")
    }
    args.add("-vvv")
    val libVlC = LibVLC(requireContext(), args)
    map[libVlC] = MediaPlayer(libVlC)
    return map
}


fun Fragment.stopVLC() {
    vlcPlayer?.let {
        if (it.size > 0)
            stopVLCPlayer(it)
        detachVLC()
    }
}

fun Fragment.detachVLC() {
    vlcPlayer?.let {
        if (it.size > 0) {
            it.keys.elementAt(0).release()
        }

    }
}


fun Fragment.startVLC(
    source: String?,
    layout: VLCVideoLayout?,
    progressFrame: View?,
    withController: Boolean = false,
    isLive: Boolean = false,
    autoRestart: Boolean = false,
    onCompletionListener: (String, Int) -> Unit
) {
    val TAG = this::class.java.simpleName
    var mLibVLC: LibVLC? = null
    var mMediaPlayer: MediaPlayer? = null
    var mMediaController: MediaController? = null

    Log.i(TAG, "source = $source")
    Log.i(TAG, "layout = $layout")
    Log.i(TAG, "isLive = $isLive")
    Log.i(TAG, "auto restart = $autoRestart")

    if (source != null)
        if (activity != null) {
            playerJob["player"]?.cancel()
            playerJob["player"] = playerTask.launchPeriodicAsync(300) {
                stopVLC()
                val media: Media?
                val map = setVLCMediaPLayer(isLive)
                for (key in map.keys) {
                    mLibVLC = key
                    mMediaPlayer = map[key]!!
                }

                mMediaPlayer?.let {
                    it.apply {
                        attachViews(layout!!, null, true, false)

                        media = Media(mLibVLC, Uri.parse(source))

                        if (isLive) {
                            media.addOption(":network-caching=100")
                            media.addOption(":clock-jitter=0")
                            media.addOption(":clock-synchro=0")
                        }


                        media.setHWDecoderEnabled(true, true)
                        this.media = media
                        videoScale = MediaPlayer.ScaleType.SURFACE_FIT_SCREEN
                        media.release()
                        setEventListener { event ->
                            try {
                                when (event.type) {
                                    MediaPlayer.Event.MediaChanged -> onCompletionListener.invoke(
                                        "@MediaChanged: ${media.uri}",
                                        event.type
                                    )
                                    MediaPlayer.Event.Opening -> onCompletionListener.invoke(
                                        "@Opening: ${media.uri}",
                                        event.type
                                    )
                                    MediaPlayer.Event.Buffering -> {
                                        onCompletionListener.invoke(
                                            "@Buffering: ${event.buffering.roundToInt().toFloat()}",
                                            event.type
                                        )
                                        if (progressFrame != null) {
                                            if (event.buffering.roundToInt() >= 100)
                                                progressFrame.visibility = View.GONE
                                            else
                                                progressFrame.visibility = View.VISIBLE
                                        }
                                        updateVideoSurfaces()
                                    }
                                    MediaPlayer.Event.Playing -> onCompletionListener.invoke(
                                        "@Playing: ${media.uri}",
                                        event.type
                                    )
                                    MediaPlayer.Event.Paused -> onCompletionListener.invoke(
                                        "@Paused",
                                        event.type
                                    )
                                    MediaPlayer.Event.Stopped -> {
                                        onCompletionListener.invoke("@Stopped", event.type)
                                        if (autoRestart) {
                                            play(media.uri)
                                        } else {
                                            if (isPlaying) {
                                                stopVLC()
                                            }
                                        }
                                    }
                                    MediaPlayer.Event.EndReached -> onCompletionListener.invoke(
                                        "@EndReached",
                                        event.type
                                    )
                                    MediaPlayer.Event.EncounteredError -> {
                                        onCompletionListener.invoke("@EncounteredError", event.type)
                                        if (isPlaying)
                                            stop()
                                        detachViews()
                                        release()
                                        mLibVLC!!.release()
                                    }
                                    MediaPlayer.Event.TimeChanged -> onCompletionListener.invoke(
                                        "@TimeChanged:  ${TimeUnit.MILLISECONDS.toHours(event.timeChanged)}:${
                                            TimeUnit.MILLISECONDS.toMinutes(
                                                event.timeChanged
                                            )
                                        }:${TimeUnit.MILLISECONDS.toSeconds(event.timeChanged)}",
                                        event.type
                                    )
                                    MediaPlayer.Event.PositionChanged -> {
                                        onCompletionListener.invoke(
                                            "@PositionChanged:  ${event.positionChanged}  =>  + $source, playing =$isPlaying",
                                            event.type
                                        )
                                        updateVideoSurfaces()
                                    }
                                    MediaPlayer.Event.SeekableChanged -> onCompletionListener.invoke(
                                        "@SeekableChanged: playing $isPlaying", event.type
                                    )
                                    MediaPlayer.Event.Vout -> onCompletionListener.invoke(
                                        "@Vout: playing = $isPlaying", event.type
                                    )
                                    MediaPlayer.Event.ESDeleted -> onCompletionListener.invoke(
                                        "@ESDeleted: playing =  $isPlaying", event.type
                                    )
                                    MediaPlayer.Event.ESSelected -> onCompletionListener.invoke(
                                        "@ESSelected:  playing = $isPlaying", event.type
                                    )
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        play()
                    }
                }

                if (withController) {
                    mMediaController = MediaController(requireContext())
                    mMediaController?.setMediaPlayer(object : MediaController.MediaPlayerControl {
                        override fun start() {
                            mMediaPlayer!!.play()
                        }

                        override fun pause() {
                            mMediaPlayer!!.pause()
                        }

                        override fun getDuration(): Int {
                            return mMediaPlayer!!.length.toInt()
                        }

                        override fun getCurrentPosition(): Int {
                            val pos = mMediaPlayer!!.position
                            return (pos * duration).toInt()
                        }

                        override fun seekTo(p0: Int) {
                            mMediaPlayer!!.position = p0.toFloat() / duration
                        }

                        override fun isPlaying(): Boolean {
                            return mMediaPlayer!!.isPlaying()
                        }

                        override fun getBufferPercentage(): Int {
                            return 0
                        }

                        override fun canPause(): Boolean {
                            return true
                        }

                        override fun canSeekBackward(): Boolean {
                            return true
                        }

                        override fun canSeekForward(): Boolean {
                            return true
                        }

                        override fun getAudioSessionId(): Int {
                            return 0
                        }


                    })
                    mMediaController?.setAnchorView(layout)
                    layout?.setOnClickListener {
                        mMediaController?.show(10000)
                    }
                }

                vlcPlayer = hashMapOf(mLibVLC!! to mMediaPlayer!!)
            }
        }
}


fun FragmentActivity.attachPlayer() {

    this::class.java.declaredFields.forEach {
        it.isAccessible = true
        if (it.isAnnotationPresent(AttachPlayerFragment::class.java)) {
            val id =
                Objects.requireNonNull(it.getAnnotation(AttachPlayerFragment::class.java)).containerID
            val t = supportFragmentManager.beginTransaction()
            if (it[this] is Fragment) {
                t.replace(id, it[this] as Fragment, it[this]::javaClass.name)
                t.commitAllowingStateLoss()
            }
        }
    }
}

fun FragmentActivity.attachLayout(): Int {
    if (!this::class.java.isAnnotationPresent(PlayerActivityLayout::class.java))
        throw RuntimeException("PlayerActivityLayout annotation is not declared")
    return Objects.requireNonNull(this::class.java.getAnnotation(PlayerActivityLayout::class.java)).value
}

fun Fragment.onKeyUp(code: Int, onChannelNo: (MoviesItem?) -> Unit) {
    when (code) {
        KeyEvent.KEYCODE_DPAD_UP,
        KeyEvent.KEYCODE_PAGE_UP,
        KeyEvent.KEYCODE_CHANNEL_UP -> {
            channelIndex += 1
            channelNo = channelIndex.toString()
            vlcMedia?.get(channelIndex)?.let { onChannelNo.invoke(it) }
        }
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_PAGE_DOWN,
        KeyEvent.KEYCODE_CHANNEL_DOWN -> {
            channelIndex -= 1
            channelNo = channelIndex.toString()
            vlcMedia?.get(channelIndex)?.let { onChannelNo.invoke(it) }
        }
        else -> {
            getChannelNo(code) {
                if (it >= 0) {
                    channelIndex = it
                    vlcMedia?.get(channelIndex)?.apply { onChannelNo.invoke(this) }
                } else {
                    onChannelNo.invoke(null)
                }
            }
        }
    }
}


fun CoroutineScope.launchPeriodicAsync(millis: Long, action: () -> Unit) = this.async {
    if (millis > 0) {
        if (isActive) {
            delay(millis)
            action()
        }
    } else {
        action()
    }
}

fun Fragment.getChannelNo(code: Int, onIndex: (Int) -> Unit) {
    var channel = ""
    when (code) {
        KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> channel += 0
        KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> channel += 1
        KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> channel += 2
        KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> channel += 3
        KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> channel += 4
        KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> channel += 5
        KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> channel += 6
        KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> channel += 7
        KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> channel += 8
        KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> channel += 9
    }
    channelNo += channel
    onIndex.invoke(-1)
    playerJob["channel"]?.cancel()
    vlcMedia!!.forEachIndexed { index, videoInfo ->
        if (videoInfo.movie_id.toString() == channelNo) {
            playerJob["channel"] = playerTask.launchPeriodicAsync(1500) {
                onIndex.invoke(index)
                channelNo = ""
            }
        }

    }
}