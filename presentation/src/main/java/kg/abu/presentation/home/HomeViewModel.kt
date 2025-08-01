package kg.abu.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.abu.domain.model.Ad
import kg.abu.domain.usecase.GetAdsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAdsUseCase: GetAdsUseCase
) : ViewModel() {

    private val _ads = MutableStateFlow<List<Ad>>(emptyList())
    val ads: StateFlow<List<Ad>> = _ads

    init {
        loadAds()
    }

    private fun loadAds() {
        _ads.value = getAdsUseCase()
    }
}