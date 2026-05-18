/**
 * News Module - Бесконечный скроллинг новостей
 * Требования: jQuery (если есть) или чистый JS
 */

(function() {
    'use strict';

    // Конфигурация
    const CONFIG = {
        pageSize: 9,
        scrollThreshold: 500,  // за сколько пикселей до конца начинать подгрузку
        containerId: 'news-container',
        loadingId: 'loading-indicator',
        endId: 'news-end-marker'
    };

    // Состояние
    let state = {
        currentPage: 0,
        isLoading: false,
        hasMore: true,
        totalLoaded: 0
    };

    // DOM элементы
    let elements = {};

    /**
     * Инициализация модуля
     */
    function init() {
        elements.container = document.getElementById(CONFIG.containerId);
        elements.loading = document.getElementById(CONFIG.loadingId);
        elements.end = document.getElementById(CONFIG.endId);

        if (!elements.container) {
            console.error('News container not found');
            return;
        }

        // Создаем элементы, если их нет
        if (!elements.loading) {
            elements.loading = createLoadingElement();
        }
        if (!elements.end) {
            elements.end = createEndElement();
        }

        // Загружаем первую порцию
        loadNews();

        // Добавляем слушатель скролла
        window.addEventListener('scroll', handleScroll);
    }

    /**
     * Создает элемент загрузки
     */
    function createLoadingElement() {
        const div = document.createElement('div');
        div.id = CONFIG.loadingId;
        div.className = 'news-loading';
        div.style.display = 'none';
        div.innerHTML = `
            <div class="news-loading-spinner"></div>
            <p>Загрузка новостей...</p>
        `;
        elements.container.parentNode.appendChild(div);
        return div;
    }

    /**
     * Создает элемент "конец ленты"
     */
    function createEndElement() {
        const div = document.createElement('div');
        div.id = CONFIG.endId;
        div.className = 'news-end';
        div.style.display = 'none';
        div.innerHTML = `
            <p>✨ Вы прочитали все свежие новости</p>
            <p style="font-size: 0.75rem;">Загляните позже — здесь появятся новые статьи</p>
        `;
        elements.container.parentNode.appendChild(div);
        return div;
    }

    /**
     * Экранирует HTML для безопасности
     */
    function escapeHtml(text) {
        if (!text) return '';
        return text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    /**
     * Форматирует дату
     */
    function formatDate(dateString) {
        if (!dateString) return '';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('ru-RU', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            });
        } catch (e) {
            return dateString;
        }
    }

    /**
     * Создает HTML карточки новости
     */
    function createNewsCard(item) {
        const card = document.createElement('div');
        card.className = 'news-card';

        const hasImage = item.imageUrl && item.imageUrl.trim() !== '';

        if (hasImage) {
            card.innerHTML = `
                <div class="news-card-image" style="background-image: url('${escapeHtml(item.imageUrl)}')"></div>
            `;
        } else {
            card.innerHTML = `
                <div class="news-card-icon">📰</div>
            `;
        }

        const content = document.createElement('div');
        content.className = 'news-card-content';
        content.innerHTML = `
            ${item.publishedAt ? `<div class="news-date">📅 ${escapeHtml(formatDate(item.publishedAt))}</div>` : ''}
            <h3 class="news-title">${escapeHtml(item.title)}</h3>
            <p class="news-description">${escapeHtml(item.description || '')}</p>
            <a href="${escapeHtml(item.url)}" target="_blank" class="news-link" rel="noopener noreferrer">
                Читать далее →
            </a>
        `;

        card.appendChild(content);
        return card;
    }

    /**
     * Отображает новости
     */
    function renderNews(newsItems) {
        if (state.currentPage === 0) {
            elements.container.innerHTML = '';
        }

        const fragment = document.createDocumentFragment();

        newsItems.forEach(item => {
            const card = createNewsCard(item);
            fragment.appendChild(card);
        });

        elements.container.appendChild(fragment);
        state.totalLoaded += newsItems.length;

        // Обновляем счетчик (если есть)
        updateCounter();
    }

    /**
     * Обновляет счетчик новостей
     */
    function updateCounter() {
        let counter = document.querySelector('.news-counter');
        if (!counter && state.totalLoaded > 0) {
            counter = document.createElement('div');
            counter.className = 'news-counter';
            elements.container.parentNode.insertBefore(counter, elements.container);
        }
        if (counter) {
            counter.textContent = `Показано ${state.totalLoaded} новостей`;
        }
    }

    /**
     * Показывает ошибку загрузки
     */
    function showError() {
        if (state.currentPage === 0 && elements.container.children.length === 0) {
            elements.container.innerHTML = `
                <div class="news-empty">
                    <div class="news-empty-icon">😔</div>
                    <h3>Новости временно недоступны</h3>
                    <p>Попробуйте обновить страницу позже</p>
                </div>
            `;
        }
    }

    /**
     * Загружает новости с сервера
     */
    async function loadNews() {
        if (state.isLoading || !state.hasMore) return;

        state.isLoading = true;
        showLoading(true);

        try {
            const contextPath = document.querySelector('meta[name="context-path"]')?.content || '';
            const url = `${contextPath}/api/news?page=${state.currentPage}&size=${CONFIG.pageSize}`;

            const response = await fetch(url);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();
            const news = data.news || [];
            const hasMore = data.hasMore === true;

            if (news.length > 0) {
                renderNews(news);
                state.currentPage++;
                state.hasMore = hasMore;
            } else {
                state.hasMore = false;
            }

            // Показываем конец ленты
            if (!state.hasMore && state.currentPage > 0) {
                showEndMarker();
            }

        } catch (error) {
            console.error('Ошибка загрузки новостей:', error);
            showError();
        } finally {
            state.isLoading = false;
            showLoading(false);
        }
    }

    /**
     * Управляет отображением индикатора загрузки
     */
    function showLoading(show) {
        if (elements.loading) {
            elements.loading.style.display = show ? 'block' : 'none';
        }
    }

    /**
     * Показывает маркер конца ленты
     */
    function showEndMarker() {
        if (elements.end) {
            elements.end.style.display = 'block';
        }
    }

    /**
     * Обработчик скролла (бесконечная подгрузка)
     */
    function handleScroll() {
        if (state.isLoading || !state.hasMore) return;

        const scrollPosition = window.innerHeight + window.scrollY;
        const threshold = document.body.offsetHeight - CONFIG.scrollThreshold;

        if (scrollPosition >= threshold) {
            loadNews();
        }
    }

    /**
     * Сбрасывает состояние и загружает заново (полезно при фильтрах)
     */
    function reset() {
        state = {
            currentPage: 0,
            isLoading: false,
            hasMore: true,
            totalLoaded: 0
        };
        elements.container.innerHTML = '';
        if (elements.end) elements.end.style.display = 'none';
        loadNews();
    }

    // Публичное API
    window.NewsModule = {
        init,
        reset,
        loadNews
    };

    // Автоматическая инициализация при загрузке DOM
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();