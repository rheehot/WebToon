package com.pluu.support.ktoon

import android.net.Uri
import com.pluu.kotlin.asSequence
import com.pluu.support.impl.AbstractDetailApi
import com.pluu.support.impl.REQUEST_METHOD
import com.pluu.webtoon.di.NetworkUseCase
import com.pluu.webtoon.item.*
import com.pluu.webtoon.utils.buildRequest
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup

/**
 * 올레 웹툰 상세 API
 * Created by pluu on 2017-04-22.
 */
class OllehDetailApi(
    networkUseCase: NetworkUseCase
) : AbstractDetailApi(networkUseCase) {

    private lateinit var wettonId: String
    private lateinit var timesseq: String

    override fun parseDetail(episode: IEpisode): DetailResult {
        this.wettonId = episode.webToonId
        this.timesseq = episode.episodeId

        val ret = DetailResult.Detail(
            webtoonId = episode.webToonId,
            episodeId = episode.episodeId
        ).apply {
            title = episode.episodeTitle
        }

        val array: JSONArray = try {
            JSONObject(requestApi()).optJSONArray("response")
        } catch (e: Exception) {
            e.printStackTrace()
            return ret
        }
        ret.list = parserToon(array)
        parsePrevNext().let {
            ret.prevLink = it.first
            ret.nextLink = it.second
        }
        return ret
    }

    private fun parserToon(array: JSONArray): List<DetailView> {
        return array.asSequence()
            .map {
                DetailView(it.optString("imagepath"))
            }
            .toList()
    }

    private fun parsePrevNext(): Pair<String?, String?> {
        val request = buildRequest {
            val url =
                Uri.Builder().encodedPath("https://www.myktoon.com/mw/works/viewer.kt").apply {
                    appendQueryParameter("timesseq", timesseq)
                }.build().toString()
            url(url)
        }
        val pagingWrap = Jsoup.parse(requestApi(request)).select(".paging_wrap")
        return Pair(
            pagingWrap.select("a[class=btn_prev moveViewerBtn]").attr("data-seq"),
            pagingWrap.select("a[class=btn_next moveViewerBtn]").attr("data-seq")
        )
    }

    override fun getDetailShare(episode: Episode, detail: DetailResult.Detail) = ShareItem(
        title = "${episode.title} / ${detail.title}",
        url = "https://v2.myktoon.com/mw/works/viewer.kt?timesseq=${detail.episodeId}"
    )

    override val url = "https://v2.myktoon.com/web/works/times_image_list_ajax.kt"

    override val method: REQUEST_METHOD = REQUEST_METHOD.POST

    override val params: Map<String, String>
        get() = hashMapOf("timesseq" to timesseq)
}
