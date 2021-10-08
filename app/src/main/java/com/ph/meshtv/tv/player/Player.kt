package com.ph.meshtv.tv.player

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.Media
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.math.roundToInt


/**
 * Mapping the vlc player as mutable
 */
val playerMap = mutableMapOf<Fragment, HashMap<LibVLC,MediaPlayer>>()
var Fragment.vlcPlayer: HashMap<LibVLC,MediaPlayer>?
    get() = playerMap[this]?: hashMapOf()
    set(value) {
        playerMap[this] = value!!
    }


fun Fragment.stopVLCPlayer(playerMap : HashMap<LibVLC, MediaPlayer>)
{
    for(key in playerMap.keys)
    {
        if (playerMap[key]!!.isPlaying) {
            playerMap[key]!!.stop()
            playerMap[key]!!.detachViews()
            playerMap[key]!!.release()
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
    map[libVlC] = MediaPlayer(LibVLC(requireContext(), args))
    return map
}


fun Fragment.stopVLC(){
    vlcPlayer?.let {
        if(it.size > 0)
          stopVLCPlayer(it)

    }
}


@SuppressLint("SdCardPath")
fun Fragment.startVLC(
    source: String?,
    layout: VLCVideoLayout?,
    isLive: Boolean,
    autoRestart: Boolean,
    onCompletionListener: (String, Int) -> Unit
)  {
    val TAG = this::class.java.simpleName
    var mLibVLC: LibVLC? = null
    var mMediaPlayer: MediaPlayer? = null

    Log.i(TAG, "source = $source")
    Log.i(TAG, "layout = $layout")
    Log.i(TAG, "isLive = $isLive")
    Log.i(TAG, "auto restart = $autoRestart")

    if(source!=null)
    if (activity != null) {
        activity?.runOnUiThread {
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
                        media.setHWDecoderEnabled(true, true)
                        media.addOption(":network-caching=100")
                        media.addOption(":clock-jitter=0")
                        media.addOption(":clock-synchro=0")
                    }


                    this.media = media
                    updateVideoSurfaces()
                    videoScale = MediaPlayer.ScaleType.SURFACE_FILL
                    media.release()
                    setEventListener { event ->
                        try {
                            when (event.type) {
                                MediaPlayer.Event.MediaChanged -> onCompletionListener.invoke(
                                    "@MediaChanged",
                                    event.type
                                )
                                MediaPlayer.Event.Opening -> onCompletionListener.invoke(
                                    "@Opening $event.",
                                    event.type
                                )
                                MediaPlayer.Event.Buffering -> onCompletionListener.invoke(
                                    "@Buffering ${event.buffering.roundToInt().toFloat()}",
                                    event.type
                                )
                                MediaPlayer.Event.Playing -> onCompletionListener.invoke(
                                    "@Playing",
                                    event.type
                                )
                                MediaPlayer.Event.Paused -> onCompletionListener.invoke(
                                    "@Paused",
                                    event.type
                                )
                                MediaPlayer.Event.Stopped -> {
                                    onCompletionListener.invoke("@Stopped", event.type)
                                    if (autoRestart) {
                                        startVLC(source, layout, isLive, autoRestart, onCompletionListener)
                                    } else {
                                        if (isPlaying)
                                            stop()
                                        detachViews()
                                        release()
                                        mLibVLC!!.release()
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
                                    "@TimeChanged:  ${TimeUnit.MILLISECONDS.toMinutes(event.timeChanged) }", event.type
                                )
                                MediaPlayer.Event.PositionChanged -> onCompletionListener.invoke(
                                    "@PositionChanged:  ${event.positionChanged}  =>  + ${source}, playing =$isPlaying",
                                    event.type
                                )
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

        }
        vlcPlayer = hashMapOf(mLibVLC!! to mMediaPlayer!!)
    }
}

