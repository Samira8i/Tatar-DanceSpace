package com.tatardancespace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tatardancespace.client.NewsApiClient;
import com.tatardancespace.dto.response.NewsResponse;
import com.tatardancespace.mapper.NewsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);
    private final NewsApiClient newsApiClient;
    private final NewsMapper newsMapper;

    public NewsService(NewsApiClient newsApiClient, NewsMapper newsMapper) {
        this.newsApiClient = newsApiClient;
        this.newsMapper = newsMapper;
    }

    /**
     * Получить первые 6 новостей для главной страницы
     */
    public List<NewsResponse> getDanceNews() {
        return getDanceNews(0, 6);
    }

    /**
     * Получить новости с пагинацией
     */
    public List<NewsResponse> getDanceNews(int page, int pageSize) {
        JsonNode response = newsApiClient.fetchNews(page, pageSize);

        if (response == null || !"ok".equals(response.has("status") ? response.get("status").asText() : "")) {
            log.warn("NewsAPI недоступен, возвращаем fallback-новости");
            return getFallbackNews(page, pageSize);
        }

        List<NewsResponse> newsList = newsMapper.mapToNewsResponses(response);
        return newsList.isEmpty() ? getFallbackNews(page, pageSize) : newsList;
    }

    /**
     * Проверяет, есть ли ещё новости (для бесконечного скроллинга)
     */
    public boolean hasMoreNews(int page, int pageSize) {
        JsonNode response = newsApiClient.fetchNews(page, pageSize);
        if (response == null || !response.has("articles")) {
            return false;
        }
        JsonNode articles = response.get("articles");
        return articles != null && articles.isArray() && articles.size() == pageSize;
    }

    private List<NewsResponse> getFallbackNews(int page, int pageSize) {
        List<NewsResponse> allFallback = getAllFallbackNews();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allFallback.size());

        if (start >= allFallback.size()) {
            return new ArrayList<>();
        }
        return allFallback.subList(start, end);
    }

    private List<NewsResponse> getAllFallbackNews() {
        return List.of(
                new NewsResponse("Международный день танца", "Ежегодно 29 апреля по всему миру отмечают Международный день танца. Праздник учреждён ЮНЕСКО в 1982 году.", "#", "", "29 апреля"),
                new NewsResponse("Танцы помогают здоровью", "Исследования показывают, что регулярные танцевальные тренировки улучшают память, координацию и настроение.", "#", "", "Актуально"),
                new NewsResponse("Новый танцевальный стиль", "Хореографы со всего мира создают новые направления, смешивая классику с уличными стилями.", "#", "", "Тренды"),
                new NewsResponse("Балет в Большом театре", "Знаменитый балет 'Лебединое озеро' возвращается на сцену после реставрации.", "#", "", "Скоро"),
                new NewsResponse("Танцевальный лагерь", "Летний танцевальный лагерь для детей и взрослых открывает набор участников.", "#", "", "Лето 2026"),
                new NewsResponse("Хип-хоп баттл", "Международный чемпионат по хип-хопу пройдёт в Москве в июне.", "#", "", "Июнь 2026"),
                new NewsResponse("Современная хореография", "Фестиваль современного танца соберёт лучшие коллективы со всей России.", "#", "", "Июль 2026"),
                new NewsResponse("Танцы для здоровья", "Врачи подтверждают: танцы помогают при стрессе и улучшают работу сердца.", "#", "", "Популярно"),
                new NewsResponse("Онлайн-марафон", "Бесплатный онлайн-марафон по танцам стартует в первых числах мая.", "#", "", "Май 2026"),
                new NewsResponse("Новая студия", "В Казани открылась новая танцевальная студия с профессиональным покрытием.", "#", "", "Новости Казани")
        );
    }
}