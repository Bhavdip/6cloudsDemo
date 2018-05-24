package sixcloud.vlt.android.demo.v1.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sixcloud.vlt.android.demo.v1.R
import sixcloud.vlt.android.demo.v1.VideoHandler
import sixcloud.vlt.android.demo.v1.databinding.ItemVideoBinding
import sixcloud.vlt.android.demo.v1.rest.Model

/**
 * Created by cs03 on 16/5/18.
 */
class VideoAdapter(val videoHandler: VideoHandler, val videos: ArrayList<Model.Data>,
    val context: Context) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

  override fun getItemCount(): Int {
    return videos.size
  }

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VideoViewHolder {
    return VideoViewHolder(
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_video, parent, false))
  }

  override fun onBindViewHolder(holder: VideoViewHolder?, position: Int) {
    holder?.bindView(position, videoInfo = videos[position])
  }

  inner class VideoViewHolder(view: ItemVideoBinding) : RecyclerView.ViewHolder(view.root) {
    val videoTitle = view.textViewTitle
    val watchVideo = view.btnWatch
    fun bindView(position: Int, videoInfo: Model.Data) {
      videoTitle?.text = videoInfo.video_title
      watchVideo.setOnClickListener(
          { view: View? -> videoHandler.onItemVideoClick(position, videoInfo) })
    }
  }
}