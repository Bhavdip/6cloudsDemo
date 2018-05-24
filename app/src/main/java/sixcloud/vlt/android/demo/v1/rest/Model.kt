package sixcloud.vlt.android.demo.v1.rest

object Model {
    data class Response(val status: Int, val message : String,val code: Int, val data: ArrayList<Data>)
    data class Data(val id: Int, val video_title: String,val url: String)
}
