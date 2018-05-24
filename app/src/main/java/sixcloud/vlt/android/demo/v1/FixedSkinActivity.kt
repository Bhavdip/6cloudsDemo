package sixcloud.vlt.android.demo.v1

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.KeyEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.aliyun.vodplayer.media.AliyunLocalSource
import com.aliyun.vodplayer.media.IAliyunVodPlayer
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView
import kotlinx.android.synthetic.main.activity_skin.*
import kotlinx.android.synthetic.main.activity_skin.view.*
import java.lang.ref.WeakReference

/**
 * Created by cs03 on 3/5/18.
 */
class FixedSkinActivity : BaseAppCompatActivity() {
  companion object {
    private val INTENT_URL = "url"
    fun newIntent(context: Context, url: String): Intent {
      val intent = Intent(context, FixedSkinActivity::class.java)
      intent.putExtra(INTENT_URL, url)
      return intent
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    setContentView(R.layout.activity_skin)
    //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


    val sdDir = Environment.getExternalStorageDirectory().absolutePath + "/test_save_cache"
    aliyunPlayerView.setPlayingCache(true, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/)
    aliyunPlayerView.setTheme(AliyunVodPlayerView.Theme.Orange)

    aliyunPlayerView.setOnPreparedListener(MyPrepareListener(this))
    aliyunPlayerView.setOnCompletionListener(MyCompletionListener(this))
    aliyunPlayerView.setOnFirstFrameStartListener(MyFirstFrameListener(this))
    aliyunPlayerView.setOnChangeQualityListener(MyChangeQualityListener(this))
    aliyunPlayerView.setOnStoppedListner(MyStoppedListener(this))
    aliyunPlayerView.enableNativeLog()
    fixSkin()
    setPlaySource()
  }

  private class MyPrepareListener(
      activity: FixedSkinActivity) : IAliyunVodPlayer.OnPreparedListener {

    val activityWeakReference: WeakReference<FixedSkinActivity> = WeakReference(activity)

    override fun onPrepared() {
      val activity = activityWeakReference.get()
      activity?.onPrepared()
    }
  }

  private fun onPrepared() {
    Toast.makeText(this@FixedSkinActivity.applicationContext, R.string.toast_prepare_success,
        Toast.LENGTH_SHORT).show()
  }

  private class MyCompletionListener(
      activity: FixedSkinActivity) : IAliyunVodPlayer.OnCompletionListener {

    val activityWeakReference: WeakReference<FixedSkinActivity> = WeakReference(activity)

    override fun onCompletion() {
      val activity = activityWeakReference.get()
      activity?.onCompletion()
    }
  }

  private fun onCompletion() {
    Toast.makeText(this@FixedSkinActivity.applicationContext, R.string.toast_play_compleion,
        Toast.LENGTH_SHORT).show()
  }

  private class MyFirstFrameListener(
      activity: FixedSkinActivity) : IAliyunVodPlayer.OnFirstFrameStartListener {

    val activityWeakReference: WeakReference<FixedSkinActivity> = WeakReference(activity)

    override fun onFirstFrameStart() {
      val activity = activityWeakReference.get()
      activity?.onFirstFrameStart()
    }
  }

  private fun onFirstFrameStart() {
    val debugInfo = aliyunPlayerView!!.allDebugInfo
    var createPts: Long = 0
    if (debugInfo["create_player"] != null) {
      val time = debugInfo["create_player"]
      createPts = java.lang.Double.parseDouble(time).toLong()
    }
    if (debugInfo["open-url"] != null) {
      val time = debugInfo["open-url"]
      val openPts = java.lang.Double.parseDouble(time).toLong() + createPts
    }
    if (debugInfo["find-stream"] != null) {
      val time = debugInfo["find-stream"]
      val findPts = java.lang.Double.parseDouble(time).toLong() + createPts
    }
    if (debugInfo["open-stream"] != null) {
      val time = debugInfo["open-stream"]
      val openPts = java.lang.Double.parseDouble(time).toLong() + createPts
    }
  }

  private class MyChangeQualityListener(
      activity: FixedSkinActivity) : IAliyunVodPlayer.OnChangeQualityListener {

    val activityWeakReference: WeakReference<FixedSkinActivity> = WeakReference(activity)

    override fun onChangeQualitySuccess(finalQuality: String) {
      val activity = activityWeakReference.get()
      activity?.onChangeQualitySuccess(finalQuality)

    }

    override fun onChangeQualityFail(code: Int, msg: String) {
      val activity = activityWeakReference.get()
      activity?.onChangeQualityFail(code, msg)
    }
  }


  private fun onChangeQualitySuccess(finalQuality: String) {
    Toast.makeText(this@FixedSkinActivity.applicationContext,
        getString(R.string.log_change_quality_success), Toast.LENGTH_SHORT).show()
  }

  private fun onChangeQualityFail(code: Int, msg: String) {
    Toast.makeText(this@FixedSkinActivity.applicationContext,
        getString(R.string.log_change_quality_fail), Toast.LENGTH_SHORT).show()
  }


  private class MyStoppedListener(
      activity: FixedSkinActivity) : IAliyunVodPlayer.OnStoppedListener {
    val activityWeakReference: WeakReference<FixedSkinActivity> = WeakReference(activity)
    override fun onStopped() {
      val activity = activityWeakReference.get()
      activity?.onStopped()
    }
  }

  private fun onStopped() {
    Toast.makeText(this@FixedSkinActivity.applicationContext, R.string.log_play_stopped,
        Toast.LENGTH_SHORT).show()
  }

  private fun fixSkin() {
    aliyunPlayerView!!.lockPortraitMode = IAliyunVodPlayer.LockPortraitListener { screenMode ->
      if (screenMode == 1)
      //跳到小屏
      {
        if (Build.DEVICE.equals("mx5", ignoreCase = true)
            || Build.DEVICE.equals("Redmi Note2", ignoreCase = true)
            || Build.DEVICE.equals("Z00A_1", ignoreCase = true)) {
          supportActionBar?.show()
        }
        this@FixedSkinActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
      } else {
        if (Build.DEVICE.equals("mx5", ignoreCase = true)
            || Build.DEVICE.equals("Redmi Note2", ignoreCase = true)
            || Build.DEVICE.equals("Z00A_1", ignoreCase = true)) {
          supportActionBar?.hide()
        } else if (!(Build.DEVICE.equals("V4", ignoreCase = true) && Build.MANUFACTURER.equals(
                "Meitu", ignoreCase = true))) {
          this@FixedSkinActivity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
              WindowManager.LayoutParams.FLAG_FULLSCREEN)
          aliyunPlayerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              or View.SYSTEM_UI_FLAG_FULLSCREEN
              or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
      }
    }
  }

  private fun setPlaySource() {
    var videoUrl = intent.getStringExtra("url")
    if (!TextUtils.isEmpty(videoUrl)) {
      val alsb = AliyunLocalSource.AliyunLocalSourceBuilder()
//      videoUrl = "https://devbuket.oss-cn-beijing.aliyuncs.com/VideoSources/testplayer.mp4"
      alsb.setSource(videoUrl)
      val localSource = alsb.build()
      aliyunPlayerView!!.setLocalSource(localSource)
      //Toast.makeText(this@FixedSkinActivity, videoUrl, Toast.LENGTH_SHORT).show()
      aliyunPlayerView.setAutoPlay(true)
    }
  }

  override fun onResume() {
    super.onResume()
    //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    if (aliyunPlayerView != null) {
      aliyunPlayerView!!.onResume()
    }
  }

  override fun onStop() {
    super.onStop()
    if (aliyunPlayerView != null) {
      aliyunPlayerView!!.onStop()
    }
  }

  override fun onDestroy() {
    if (aliyunPlayerView != null) {
      aliyunPlayerView!!.onDestroy()
    }
    super.onDestroy()
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    if (aliyunPlayerView != null) {
      val handler = aliyunPlayerView!!.onKeyDown(keyCode, event)
      if (!handler) {
        return false
      }
    }
    return super.onKeyDown(keyCode, event)
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
  }
}
