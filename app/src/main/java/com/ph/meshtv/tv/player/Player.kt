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


fun Fragment.startVLC(source: String?, vlcLayout: VLCVideoLayout?, isLive: Boolean, onCompletionListener: () -> Unit) {
    val TAG = this::javaClass.name
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

                mMediaPlayer!!.attachViews(vlcLayout!!, null, true, false)

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
                            MediaPlayer.Event.MediaChanged -> Log.i(
                                TAG,
                                "@MediaChanged:  ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.Opening -> Log.i(
                                TAG,
                                "@Opening:  ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.Buffering -> Log.i(
                                TAG,
                                "@Buffering:  ${event.buffering.roundToInt().toFloat()}"
                            )
                            MediaPlayer.Event.Playing -> Log.i(
                                TAG,
                                "@Playing:  ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.Paused -> Log.i(
                                TAG,
                                "@Paused: ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.Stopped -> {
                                onCompletionListener.invoke()
                                if(mMediaPlayer.isPlaying)
                                    mMediaPlayer.stop()
                                mMediaPlayer.detachViews()
                                mMediaPlayer.release()
                                mLibVLC!!.release()
                                Log.i(TAG, "@OnEnd")
                            }
                            MediaPlayer.Event.EndReached -> Log.i(
                                TAG,
                                "@EndReached: ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.EncounteredError -> {
                                Log.i(TAG, "@EncounteredError:  $source")
                                if(mMediaPlayer.isPlaying)
                                    mMediaPlayer.stop()
                                mMediaPlayer.detachViews()
                                mMediaPlayer.release()
                                mLibVLC!!.release()

                            }
                            MediaPlayer.Event.TimeChanged -> Log.i(
                                TAG,
                                "@TimeChanged: position= ${event.timeChanged / 1000L}"
                            )
                            MediaPlayer.Event.PositionChanged -> Log.i(
                                TAG,
                                "@PositionChanged: ${Calendar.getInstance().time} +  =>  + ${source}, playing =${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.SeekableChanged -> Log.i(
                                TAG,
                                "@SeekableChanged: playing ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.Vout -> Log.i(
                                TAG,
                                "@Vout: playing = ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.ESDeleted -> Log.i(
                                TAG,
                                "@ESDeleted: playing =  ${mMediaPlayer.isPlaying}"
                            )
                            MediaPlayer.Event.ESSelected -> Log.i(
                                TAG,
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