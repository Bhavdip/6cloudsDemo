package sixcloud.vlt.android.demo.v1

import sixcloud.vlt.android.demo.v1.rest.Model

interface VideoHandler{
  fun onItemVideoClick(videoId: Int, videoInfo: Model.Data)
}

