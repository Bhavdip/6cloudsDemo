package sixcloud.vlt.android.demo.v1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dueeeke.videoplayer.player.IjkPlayer
import com.dueeeke.videoplayer.player.PlayerConfig
import kotlinx.android.synthetic.main.activity_definition_player.*
import sixcloud.vlt.android.demo.v1.controller.DefinitionController
import java.util.*

/**
 * Created by bhavdip on 5/24/18.
 */
class DefinitionPlayerActivity : AppCompatActivity() {

    companion object {
        private val INTENT_URL = "url"
        private val INTENT_TITLE = "title"
        fun newIntent(context: Context, url: String, title: String): Intent {
            val intent = Intent(context, DefinitionPlayerActivity::class.java)
            intent.putExtra(INTENT_URL, url)
            intent.putExtra(INTENT_TITLE, title)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_definition_player)
        val config = PlayerConfig.Builder().setCustomMediaPlayer(object : IjkPlayer(this) {

            override fun setOptions() {
                //mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
            }
        }).build()
        ijkVideoView.setPlayerConfig(config)

        val controller = DefinitionController(this)
        val videos = LinkedHashMap<String, String>()
        videos["Quality1"] = intent.getStringExtra(INTENT_URL)
        videos["Quality2"] = intent.getStringExtra(INTENT_URL)
        videos["Quality3"] = intent.getStringExtra(INTENT_URL)

        ijkVideoView.setDefinitionVideos(videos)
        ijkVideoView.setVideoController(controller)
        ijkVideoView.title = intent.getStringExtra(INTENT_TITLE)
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