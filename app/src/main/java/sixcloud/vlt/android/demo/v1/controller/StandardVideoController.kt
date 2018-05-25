package sixcloud.vlt.android.demo.v1.controller

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.support.annotation.AttrRes
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.dueeeke.videoplayer.controller.GestureVideoController
import com.dueeeke.videoplayer.player.IjkVideoView
import com.dueeeke.videoplayer.util.L
import com.dueeeke.videoplayer.util.WindowUtil
import kotlinx.android.synthetic.main.layout_standard_controller.view.*
import sixcloud.vlt.android.demo.v1.MarqueeTextView
import sixcloud.vlt.android.demo.v1.R
import sixcloud.vlt.android.demo.v1.util.BatteryReceiver

/**
 * Created by bhavdip on 5/24/18.
 */
open class StandardVideoController(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : GestureVideoController(context, attrs, defStyleAttr), View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    protected var totalTime: TextView? = null
    protected var currTime: TextView? = null
    protected lateinit var fullScreenButton: ImageView
    private lateinit var bottomContainer: LinearLayout
    private lateinit var topContainer: LinearLayout
    private var videoProgress: SeekBar? = null
    //    protected ImageView moreMenu;
    protected lateinit var backButton: ImageView
    protected lateinit var lock: ImageView
    protected var title: MarqueeTextView? = null
    private var isLive: Boolean = false
    private var isDragging: Boolean = false

    private var bottomProgress: ProgressBar? = null
    private var playButton: ImageView? = null
    private var startPlayButton: ImageView? = null
    private var loadingProgress: ProgressBar? = null
    var thumb: ImageView? = null
        private set
    private var completeContainer: LinearLayout? = null
    private var sysTime: TextView? = null//系统当前时间
    private lateinit var batteryLevel: ImageView
    private val showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_player_alpha_in)
    private val hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_player_alpha_out)
    private var mBatteryReceiver: BatteryReceiver? = null
    private lateinit var refresh: ImageView

    override fun getLayoutId(): Int {
        return R.layout.layout_standard_controller
    }

    override fun initView() {
        super.initView()
        //        moreMenu = controllerView.findViewById(R.id.more_menu);
        //        moreMenu.setOnClickListener(this);
        fullScreenButton = controllerView.findViewById(R.id.fullscreen)
        fullScreenButton.setOnClickListener(this)
        bottomContainer = controllerView.findViewById(R.id.bottom_container)
        topContainer = controllerView.findViewById(R.id.top_container)
        videoProgress = controllerView.findViewById(R.id.seekBar)
        videoProgress!!.setOnSeekBarChangeListener(this)
        totalTime = controllerView.findViewById(R.id.total_time)
        currTime = controllerView.findViewById(R.id.curr_time)
        backButton = controllerView.findViewById(R.id.back)
        backButton.setOnClickListener(this)
        lock = controllerView.findViewById(R.id.lock)
        lock.setOnClickListener(this)
        thumb = controllerView.findViewById(R.id.thumb)
        thumb!!.setOnClickListener(this)
        playButton = controllerView.findViewById(R.id.iv_play)
        playButton!!.setOnClickListener(this)
        startPlayButton = controllerView.findViewById(R.id.start_play)
        loadingProgress = controllerView.findViewById(R.id.loading)
        bottomProgress = controllerView.findViewById(R.id.bottom_progress)
        iv_replay.setOnClickListener(this)
        completeContainer = controllerView.findViewById(R.id.complete_container)
        completeContainer!!.setOnClickListener(this)
        title = controllerView.findViewById(R.id.title)
        sysTime = controllerView.findViewById(R.id.sys_time)
        batteryLevel = controllerView.findViewById(R.id.iv_battery)
        mBatteryReceiver = BatteryReceiver(batteryLevel)
        refresh = controllerView.findViewById(R.id.iv_refresh)
        refresh.setOnClickListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.unregisterReceiver(mBatteryReceiver)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        context.registerReceiver(mBatteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.fullscreen || i == R.id.back) {
            doStartStopFullScreen()
        } else if (i == R.id.lock) {
            doLockUnlock()
        } else if (i == R.id.iv_play || i == R.id.thumb) {
            doPauseResume()
        } else if (i == R.id.iv_replay) {
            mediaPlayer.retry()
        } else if (i == R.id.iv_refresh) {
            mediaPlayer.refresh()
        }
    }

    fun showTitle() {
        title!!.setVisibility(View.VISIBLE)
    }

    override fun setPlayerState(playerState: Int) {
        when (playerState) {
            IjkVideoView.PLAYER_NORMAL -> {
                L.e("PLAYER_NORMAL")
                if (isLocked) return
                layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                gestureEnabled = false
                fullScreenButton.isSelected = false
                backButton.visibility = View.GONE
                lock.visibility = View.GONE
                title!!.setVisibility(View.INVISIBLE)
                sysTime!!.visibility = View.GONE
                batteryLevel!!.visibility = View.GONE
                topContainer.visibility = View.GONE
            }
            IjkVideoView.PLAYER_FULL_SCREEN -> {
                L.e("PLAYER_FULL_SCREEN")
                if (isLocked) return
                gestureEnabled = true
                fullScreenButton.isSelected = true
                backButton.visibility = View.VISIBLE
                title!!.setVisibility(View.VISIBLE)
                sysTime!!.visibility = View.VISIBLE
                batteryLevel!!.visibility = View.VISIBLE
                if (mShowing) {
                    lock.visibility = View.VISIBLE
                    topContainer.visibility = View.VISIBLE
                } else {
                    lock.visibility = View.GONE
                }
            }
        }
    }

    override fun setPlayState(playState: Int) {
        super.setPlayState(playState)
        when (playState) {
            IjkVideoView.STATE_IDLE -> {
                L.e("STATE_IDLE")
                hide()
                isLocked = false
                lock.isSelected = false
                mediaPlayer.setLock(false)
                bottomProgress!!.progress = 0
                bottomProgress!!.secondaryProgress = 0
                videoProgress!!.progress = 0
                videoProgress!!.secondaryProgress = 0
                completeContainer!!.visibility = View.GONE
                bottomProgress!!.visibility = View.GONE
                loadingProgress!!.visibility = View.GONE
                startPlayButton!!.visibility = View.VISIBLE
                thumb!!.visibility = View.VISIBLE
            }
            IjkVideoView.STATE_PLAYING -> {
                L.e("STATE_PLAYING")
                post(mShowProgress)
                playButton!!.isSelected = true
                loadingProgress!!.visibility = View.GONE
                completeContainer!!.visibility = View.GONE
                thumb!!.visibility = View.GONE
                startPlayButton!!.visibility = View.GONE
            }
            IjkVideoView.STATE_PAUSED -> {
                L.e("STATE_PAUSED")
                playButton!!.isSelected = false
                startPlayButton!!.visibility = View.GONE
            }
            IjkVideoView.STATE_PREPARING -> {
                L.e("STATE_PREPARING")
                completeContainer!!.visibility = View.GONE
                startPlayButton!!.visibility = View.GONE
                loadingProgress!!.visibility = View.VISIBLE
                thumb!!.visibility = View.VISIBLE
            }
            IjkVideoView.STATE_PREPARED -> {
                L.e("STATE_PREPARED")
                if (!isLive) bottomProgress!!.visibility = View.VISIBLE
                //                loadingProgress.setVisibility(GONE);
                startPlayButton!!.visibility = View.GONE
            }
            IjkVideoView.STATE_ERROR -> {
                L.e("STATE_ERROR")
                startPlayButton!!.visibility = View.GONE
                loadingProgress!!.visibility = View.GONE
                thumb!!.visibility = View.GONE
                bottomProgress!!.visibility = View.GONE
                topContainer.visibility = View.GONE
            }
            IjkVideoView.STATE_BUFFERING -> {
                L.e("STATE_BUFFERING")
                startPlayButton!!.visibility = View.GONE
                loadingProgress!!.visibility = View.VISIBLE
                thumb!!.visibility = View.GONE
            }
            IjkVideoView.STATE_BUFFERED -> {
                loadingProgress!!.visibility = View.GONE
                startPlayButton!!.visibility = View.GONE
                thumb!!.visibility = View.GONE
                L.e("STATE_BUFFERED")
            }
            IjkVideoView.STATE_PLAYBACK_COMPLETED -> {
                L.e("STATE_PLAYBACK_COMPLETED")
                hide()
                removeCallbacks(mShowProgress)
                startPlayButton!!.visibility = View.GONE
                thumb!!.visibility = View.VISIBLE
                completeContainer!!.visibility = View.VISIBLE
                bottomProgress!!.progress = 0
                bottomProgress!!.secondaryProgress = 0
                isLocked = false
                mediaPlayer.setLock(false)
            }
        }
    }

    protected fun doLockUnlock() {
        if (isLocked) {
            isLocked = false
            mShowing = false
            gestureEnabled = true
            show()
            lock.isSelected = false
            Toast.makeText(context, R.string.unlocked, Toast.LENGTH_SHORT).show()
        } else {
            hide()
            isLocked = true
            gestureEnabled = false
            lock.isSelected = true
            Toast.makeText(context, R.string.locked, Toast.LENGTH_SHORT).show()
        }
        mediaPlayer.setLock(isLocked)
    }

    /**
     * 设置是否为直播视频
     */
    fun setLive() {
        isLive = true
        bottomProgress!!.visibility = View.GONE
        videoProgress!!.visibility = View.INVISIBLE
        totalTime!!.visibility = View.INVISIBLE
        currTime!!.visibility = View.INVISIBLE
        refresh.visibility = View.VISIBLE
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        isDragging = true
        removeCallbacks(mShowProgress)
        removeCallbacks(mFadeOut)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        val duration = mediaPlayer.duration
        val newPosition = duration * seekBar.progress / videoProgress!!.max
        mediaPlayer.seekTo(newPosition.toInt().toLong())
        isDragging = false
        post(mShowProgress)
        show()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        }

        val duration = mediaPlayer.duration
        val newPosition = duration * progress / videoProgress!!.max
        if (currTime != null)
            currTime!!.text = stringForTime(newPosition.toInt())
    }

    override fun hide() {
        if (mShowing) {
            if (mediaPlayer.isFullScreen) {
                lock.visibility = View.GONE
                if (!isLocked) {
                    hideAllViews()
                }
            } else {
                bottomContainer.visibility = View.GONE
                bottomContainer.startAnimation(hideAnim)
            }
            if (!isLive && !isLocked) {
                bottomProgress!!.visibility = View.VISIBLE
                bottomProgress!!.startAnimation(showAnim)
            }
            mShowing = false
        }
    }

    private fun hideAllViews() {
        topContainer.visibility = View.GONE
        topContainer.startAnimation(hideAnim)
        bottomContainer.visibility = View.GONE
        bottomContainer.startAnimation(hideAnim)
    }

    private fun show(timeout: Int) {
        if (!mShowing) {
            if (mediaPlayer.isFullScreen) {
                lock.visibility = View.VISIBLE
                if (!isLocked) {
                    showAllViews()
                }
            } else {
                bottomContainer.visibility = View.VISIBLE
                bottomContainer.startAnimation(showAnim)
            }
            if (!isLocked && !isLive) {
                bottomProgress!!.visibility = View.GONE
                bottomProgress!!.startAnimation(hideAnim)
            }
            mShowing = true
        }
        removeCallbacks(mFadeOut)
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout.toLong())
        }
    }

    private fun showAllViews() {
        bottomContainer.visibility = View.VISIBLE
        bottomContainer.startAnimation(showAnim)
        topContainer.visibility = View.VISIBLE
        topContainer.startAnimation(showAnim)
    }

    override fun show() {
        show(sDefaultTimeout)
    }

    override fun setProgress(): Int {
        if (mediaPlayer == null || isDragging) {
            return 0
        }

        if (sysTime != null)
            sysTime!!.text = currentSystemTime
        if (title != null && TextUtils.isEmpty(title!!.getText())) {
            title!!.setText(mediaPlayer.title)
        }

        if (isLive) return 0

        val position = mediaPlayer.currentPosition.toInt()
        val duration = mediaPlayer.duration.toInt()
        if (videoProgress != null) {
            if (duration > 0) {
                videoProgress!!.isEnabled = true
                val pos = (position * 1.0 / duration * videoProgress!!.max).toInt()
                videoProgress!!.progress = pos
                bottomProgress!!.progress = pos
            } else {
                videoProgress!!.isEnabled = false
            }
            val percent = mediaPlayer.bufferPercentage
            if (percent >= 95) { //修复第二进度不能100%问题
                videoProgress!!.secondaryProgress = videoProgress!!.max
                bottomProgress!!.secondaryProgress = bottomProgress!!.max
            } else {
                videoProgress!!.secondaryProgress = percent * 10
                bottomProgress!!.secondaryProgress = percent * 10
            }
        }

        if (totalTime != null)
            totalTime!!.text = stringForTime(duration)
        if (currTime != null)
            currTime!!.text = stringForTime(position)

        return position
    }


    override fun slideToChangePosition(deltaX: Float) {
        if (isLive) {
            mNeedSeek = false
        } else {
            super.slideToChangePosition(deltaX)
        }
    }

    override fun onBackPressed(): Boolean {
        if (isLocked) {
            show()
            Toast.makeText(context, R.string.lock_tip, Toast.LENGTH_SHORT).show()
            return true
        }
        if (mediaPlayer.isFullScreen) {
            WindowUtil.scanForActivity(context).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            mediaPlayer.stopFullScreen()
            return true
        }
        return super.onBackPressed()
    }
}