package com.pluu.webtoon.ui.weekly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pluu.support.impl.AbstractWeekApi
import com.pluu.webtoon.db.RealmHelper
import com.pluu.webtoon.item.WebToonInfo
import com.pluu.webtoon.utils.withMainDispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

class WeekyViewModel(
    private val serviceApi: AbstractWeekApi,
    private val weekPos: Int,
    private val realmHelper: RealmHelper
) : ViewModel() {

    private val jobs = arrayListOf<Job>()

    private val _listEvent = MutableLiveData<List<WebToonInfo>>()
    val listEvent: LiveData<List<WebToonInfo>>
        get() = _listEvent

    private val _event = MutableLiveData<WeeklyEvent>()
    val event: LiveData<WeeklyEvent>
        get() = _event

    val naviItem = serviceApi.naviItem

    init {
        _event.value = WeeklyEvent.START
        jobs += GlobalScope.launch {
            var error: WeeklyEvent? = null
            val result = try {
                serviceApi.parseMain(weekPos)
                    .asSequence()
                    .map {
                        it.isFavorite = realmHelper.getFavoriteToon(serviceApi.naviItem, it.toonId)
                        it
                    }
                    .sortedWith(compareBy<WebToonInfo> { it.isFavorite }.thenBy { it.title })
                    .toList()
            } catch (e: Exception) {
                error = WeeklyEvent.ERROR(e.localizedMessage)
                emptyList<WebToonInfo>()
            }

            withMainDispatchers {
                _event.value = error ?: WeeklyEvent.LOADED
                _listEvent.value = result
            }
        }
    }

    override fun onCleared() {
        jobs.forEach { it.cancel() }
        super.onCleared()
    }
}

sealed class WeeklyEvent {
    object START : WeeklyEvent()
    object LOADED : WeeklyEvent()
    class ERROR(val message: String) : WeeklyEvent()
}