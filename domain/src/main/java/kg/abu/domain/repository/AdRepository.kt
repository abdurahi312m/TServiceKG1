package kg.abu.domain.repository

import kg.abu.domain.model.Ad

interface AdRepository {
    fun getAds(): List<Ad>
}