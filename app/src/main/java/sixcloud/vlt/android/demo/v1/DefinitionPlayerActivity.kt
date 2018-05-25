package sixcloud.vlt.android.demo.v1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dueeeke.videoplayer.player.IjkPlayer
import com.dueeeke.videoplayer.player.PlayerConfig
import kotlinx.android.synthetic.main.activity_definition_player.*
import sixcloud.vlt.android.demo.v1.controller.DefinitionController
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.util.*

/**
 * Created by bhavdip on 5/24/18.
 */
class DefinitionPlayerActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_definition_player)
        val config = PlayerConfig.Builder().setCustomMediaPlayer(object : IjkPlayer(this) {

            override fun setOptions() {
                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
            }
        }).build()
        val controller = DefinitionController(this)

        ijkVideoView.setPlayerConfig(config)
        val videos = LinkedHashMap<String, String>()
        videos.put("Quality1", intent.getStringExtra("url"))
        videos.put("Quality2", intent.getStringExtra("url"))
        videos.put("Quality3", intent.getStringExtra("url"))

        ijkVideoView.setDefinitionVideos(videos)
        ijkVideoView.setVideoController(controller)
        ijkVideoView.title = "韩雪：积极的悲观主义者"
        ijkVideoView.start()
    }

    override fun onPause() {
        super.onPause()
        ijkVideoView.pause()
    }

    override fun onResume() {
        super.onResume()
        ijkVideoView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        ijkVideoView.release()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}