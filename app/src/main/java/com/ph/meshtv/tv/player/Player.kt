package com.ph.meshtv.tv.player

import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.Media
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt


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


fun Fragment.startVLC(source: String?, layout: VLCVideoLayout?, isLive: Boolean, autoRestart : Boolean, onCompletionListener: (Boolean, String) -> Unit) {
    val TAG = this::class.java.simpleName


    if (source != null)
        if (activity != null) {
            activity?.runOnUiThread {

                var mLibVLC: LibVLC? = null
                var mMediaPlayer: MediaPlayer? = null
                val media: Media?

                val map = if (isLive)
                    setVLCMediaPLayer(true)
                else
                    setVLCMediaPLayer(false)

                for (key in map.keys) {
                    mLibVLC = key
                    mMediaPlayer = map[key]
                }

                mMediaPlayer!!.attachViews(layout!!, null, true, false)

                media = Media(mLibVLC, Uri.parse(source))

                if (isLive) {
                    media.addOption(":network-caching=100")
                    media.addOption(":clock-jitter=0")
                    media.addOption(":clock-synchro=0")
                }

                mMediaPlayer.media = media
                mMediaPlayer.updateVideoSurfaces()
                mMediaPlayer.videoScale = MediaPlayer.ScaleType.SURFACE_FILL
                media.release()

                mMediaPlayer.setEventListener { event ->
                    try {
                        when (event.type) {
                            MediaPlayer.Event.MediaChanged ->onCompletionListener.invoke(false,"@MediaChanged")
                            MediaPlayer.Event.Opening -> onCompletionListener.invoke(false,"@Opening")
                            MediaPlayer.Event.Buffering -> onCompletionListener.invoke(false,"@Buffering")
                            MediaPlayer.Event.Playing ->onCompletionListener.invoke(false,"@Playing")
                            MediaPlayer.Event.Paused ->  onCompletionListener.invoke(false,"@Paused")
                            MediaPlayer.Event.Stopped -> {
                                onCompletionListener.invoke(true,"@Stopped")
                                if(autoRestart) {
                                    this.startVLC(source, layout, isLive,autoRestart,onCompletionListener)
                                }else{
                                    if(mMediaPlayer.isPlaying)
                                        mMediaPlayer.stop()
                                    mMediaPlayer.detachViews()
                                    mMediaPlayer.release()
                                    mLibVLC!!.release()
                                }
                            }
                            MediaPlayer.Event.EndReached -> onCompletionListener.invoke(true,"@EndReached")
                            MediaPlayer.Event.EncounteredError -> {
                                onCompletionListener.invoke(true,"@EncounteredError")
                                if(mMediaPlayer.isPlaying)
                                    mMediaPlayer.stop()
                                mMediaPlayer.detachViews()
                                mMediaPlayer.release()
                                mLibVLC!!.release()

                            }
                            MediaPlayer.Event.TimeChanged ->  onCompletionListener.invoke(false,
                                "@TimeChanged: position= ${event.timeChanged / 1000L}"
                            )
                            MediaPlayer.Event.PositionChanged -> onCompletionListener.invoke(false,
                                "@PositionChanged: ${Calendar.getInstance().time} +  =>  + ${source}, playing =${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.SeekableChanged ->  onCompletionListener.invoke(false,
                                "@SeekableChanged: playing ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.Vout ->  onCompletionListener.invoke(false,
                                "@Vout: playing = ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.ESDeleted ->  onCompletionListener.invoke(false,
                                "@ESDeleted: playing =  ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.ESSelected ->  onCompletionListener.invoke(false,
                                "@ESSelected:  playing = ${mMediaPlayer.isPlaying}"
                            )
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                mMediaPlayer.play()
            }
        }
}