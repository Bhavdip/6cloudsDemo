package sixcloud.vlt.android.demo.v1

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sixcloud_player.*
import sixcloud.vlt.android.demo.v1.adapter.VideoAdapter
import sixcloud.vlt.android.demo.v1.databinding.ActivitySixcloudPlayerBinding
import sixcloud.vlt.android.demo.v1.rest.Model
import sixcloud.vlt.android.demo.v1.rest.VLTService


class SixcloudPlayer : AppCompatActivity() {
    private var activityPlayerBinding: ActivitySixcloudPlayerBinding? = null
    private var videoItemHandler = videoClickHandler()
    private var disposable: Disposable? = null
    private val vltVideoApiService by lazy {
        VLTService.create()
    }
    private lateinit var videoAdapter: VideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPlayerBinding = DataBindingUtil.setContentView(this, R.layout.activity_sixcloud_player)
        fetchVideosList()
        refreshListener()
    }

    private fun fetchVideosList() {
        //show only loading bar
        loadingView.visibility = View.VISIBLE
        //hide the Recycler view to visible until data received
        videosList.visibility = View.INVISIBLE
        vltVideoApiService.getVideos().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ result -> resultHandle(result) },
                { error -> handleError(error) })
    }

    private fun resultHandle(result: Model.Response) {
        if (result.code == 200) {
            videoAdapter = VideoAdapter(videoItemHandler, result.data, this)
            videosList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            videosList.adapter = videoAdapter
        }
        //hide only loading bar
        loadingView.visibility = View.INVISIBLE
        //hide the Recycler view to visible until data received
        videosList.visibility = View.VISIBLE
        // after refresh is done, remember to call is Refreshing false
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        loadingView.visibility = View.INVISIBLE
        // after refresh is done, remember to call the following code
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refreshListener() {
        swipeRefreshLayout.setOnRefreshListener({
            //do something here
            fetchVideosList()
        })
    }

    private fun videoClickHandler(): VideoHandler {
        return object : VideoHandler {
            override fun onItemVideoClick(videoId: Int, videoInfo: Model.Data) {
                Toast.makeText(this@SixcloudPlayer, videoInfo.url, Toast.LENGTH_SHORT).show()

//        val intent = FixedSkinActivity.newIntent(this@SixcloudPlayer, videoInfo.url)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                val intent = DefinitionPlayerActivity.newIntent(this@SixcloudPlayer, videoInfo.url)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
