package kg.abu.data.repository

import kg.abu.domain.model.Ad
import kg.abu.domain.repository.AdRepository

class FakeAdRepository : AdRepository {

    override fun getAds(): List<Ad> {
        return listOf(
            Ad(1, "Кирпичи", "Качественные кирпичи", "150 сом/шт", "https://via.placeholder.com/150"),
            Ad(2, "Уборка дома", "Профессиональная уборка", "500 сом/час", "https://via.placeholder.com/150"),
            Ad(3, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(4, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(5, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(6, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(7, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(8, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(9, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(10, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(11, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(12, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
            Ad(13, "Ремонт ноутбуков", "Чиним быстро и качественно", "1000 сом", "https://via.placeholder.com/150"),
        )
    }

}