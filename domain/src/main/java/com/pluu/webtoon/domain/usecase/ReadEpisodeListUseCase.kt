package com.pluu.webtoon.domain.usecase

import com.pluu.webtoon.NAV_ITEM
import com.pluu.webtoon.data.db.IDBHelper
import com.pluu.webtoon.domain.moel.Episode

/**
 * EpisodeInfo Section Use Case
 */
class ReadEpisodeListUseCase(
    private val dbHelper: IDBHelper,
    private val naviItem: NAV_ITEM
) {
    /**
     * 이미 읽은 EpisodeInfo 취득
     * @param id EpisodeInfo ID
     * @return Read List
     */
    suspend operator fun invoke(id: String): List<Episode> =
        dbHelper.getEpisode(naviItem.name, id)
            .map {
                Episode(
                    service = it.service!!,
                    toonId = it.toonId!!,
                    episodeId = it.episodeId!!
                )
            }
}
