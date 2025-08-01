package kg.abu.domain.usecase

import kg.abu.domain.model.Ad
import kg.abu.domain.repository.AdRepository
import javax.inject.Inject

class GetAdsUseCase @Inject constructor(private val repository: AdRepository) {
    operator fun invoke(): List<Ad> = repository.getAds()
}