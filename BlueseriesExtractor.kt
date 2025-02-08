package com.cloudstream.extensions.extractors

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class BlueseriesExtractor : ExtractorAPI() {
    override var name = "BlueseriesExtractor"
    override var mainUrl = "https://www.blueseries.cc"
    
    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink> {
        val links = mutableListOf<ExtractorLink>()

        try {
            val doc = app.get(url).document

            doc.select("iframe").forEach { iframe ->
                val src = iframe.attr("src")

                if (src.isNotEmpty()) {
                    links.add(
                        ExtractorLink(
                            name = name,
                            source = name,
                            url = src,
                            referer = referer ?: mainUrl,
                            quality = Qualities.Unknown.value
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return links
    }
}
