package com.cloudstream.extensions

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class BlueseriesProvider : MainAPI() {
    override var name = "BlueSeries"
    override var mainUrl = "https://www.blueseries.cc"
    override var lang = "fr"
    override val hasMainPage = true

    override val supportedTypes = setOf(TvType.TvSeries, TvType.Movie)

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=$query"
        val doc = app.get(url).document
        val results = mutableListOf<SearchResponse>()

        doc.select("div.result-item").forEach { element ->
            val title = element.selectFirst("h3.title")?.text() ?: return@forEach
            val link = element.selectFirst("a")?.attr("href") ?: return@forEach
            val posterUrl = element.selectFirst("img")?.attr("src") ?: ""

            results.add(TvSeriesSearchResponse(
                title,
                link,
                this.name,
                TvType.TvSeries,
                posterUrl,
                null
            ))
        }
        return results
    }

    override suspend fun load(url: String): LoadResponse? {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1.entry-title")?.text() ?: return null
        val posterUrl = doc.selectFirst(".poster img")?.attr("src") ?: ""
        val episodes = mutableListOf<Episode>()

        doc.select("ul.episodios li").forEach { ep ->
            val epTitle = ep.selectFirst(".numerando")?.text() ?: "Épisode inconnu"
            val epUrl = ep.selectFirst("a")?.attr("href") ?: return@forEach
            episodes.add(Episode(epUrl, epTitle))
        }

        return TvSeriesLoadResponse(title, url, this.name, TvType.TvSeries, episodes, posterUrl)
    }

    override suspend fun loadLinks(url: String, isCasting: Boolean): List<ExtractorLink> {
        return BlueseriesExtractor().getUrl(url, mainUrl)
    }
}
