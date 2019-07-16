package com.pluu.webtoon.ui.weekly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pluu.webtoon.item.Result
import com.pluu.webtoon.item.ToonInfo
import com.pluu.webtoon.usecase.HasFavoriteUseCase
import com.pluu.webtoon.usecase.WeeklyUseCase
import com.pluu.webtoon.utils.AppCoroutineDispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeekyViewModel(
    private val dispatchers: AppCoroutineDispatchers,
    private val weekPos: Int,
    private val weeklyUseCase: WeeklyUseCase,
    private val hasFavoriteUseCase: HasFavoriteUseCase
) : ViewModel() {

    private val _listEvent = MutableLiveData<List<ToonInfo>>()
    val listEvent: LiveData<List<ToonInfo>>
        get() = _listEvent

    private val _event = MutableLiveData<WeeklyEvent>()
    val event: LiveData<WeeklyEvent>
        get() = _event

    init {
        viewModelScope.launch {
            _event.value = WeeklyEvent.START
            val result = withContext(dispatchers.computation) {
                runCatching {
                    getWeekLoad()
                }
            }
            _listEvent.value = result.getOrDefault(emptyList())
            _event.value = WeeklyEvent.LOADED
        }
    }

    private suspend fun getWeekLoad(): List<ToonInfo> = coroutineScope {
        val apiResult = weeklyUseCase(weekPos)
        if (apiResult is Result.Success) {
            apiResult.data.asSequence()
                .map {
                    it.isFavorite = hasFavoriteUseCase(it.id)
                    it
                }
                .sortedWith(compareBy<ToonInfo> {
                    !it.isFavorite
                }.thenBy {
                    it.title
                })
                .toList()
        } else {
            emptyList()
        }
    }
}

sealed class WeeklyEvent {
    object START : WeeklyEvent()
    object LOADED : WeeklyEvent()
    class ERROR(val message: String) : WeeklyEvent()
}
