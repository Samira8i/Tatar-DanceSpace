// News Module - Бесконечный скроллинг новостей

(function() {
    'use strict';

    const CONFIG = {
        pageSize: 9,
        scrollThreshold: 500,
        containerId: 'news-container',
        loadingId: 'loading-indicator',
        endId: 'news-end-marker'
    };

    let state = {
        currentPage: 0,
        isLoading: false,
        hasMore: true,
        totalLoaded: 0
    };

    let elements = {};

    function getContextPath() {
        return window.CONTEXT_PATH || document.querySelector('meta[name="context-path"]')?.getAttribute('content') || '';
    }

    function init() {
        elements.container = document.getElementById(CONFIG.containerId);
        if (!elements.container) {
            console.error('News container not found');
            return;
        }
        elements.loading = document.getElementById(CONFIG.loadingId) || createLoadingElement();
        elements.end = document.getElementById(CONFIG.endId) || createEndElement();

        loadNews();
        window.addEventListener('scroll', handleScroll);
    }

    function createLoadingElement() {
        const div = document.createElement('div');
        div.id = CONFIG.loadingId;
        div.className = 'news-loading';
        div.style.display = 'none';
        div.innerHTML = '<div class="news-loading-spinner"></div><p>Загрузка новостей...</p>';
        elements.container.parentNode.appendChild(div);
        return div;
    }

    function createEndElement() {
        const div = document.createElement('div');
        div.id = CONFIG.endId;
        div.className = 'news-end';
        div.style.display = 'none';
        div.innerHTML = '<p>✨ Вы прочитали все свежие новости</p>';
        elements.container.parentNode.appendChild(div);
        return div;
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text.replace(/[&<>]/g, m => {
            if (m === '&') return '&amp;';
            if (m === '<') return '&lt;';
            if (m === '>') return '&gt;';
            return m;
        });
    }

    function createNewsCard(item) {
        const card = document.createElement('div');
        card.className = 'news-card';

        const hasImage = item.imageUrl && item.imageUrl.trim() !== '';

        if (hasImage) {
            card.innerHTML = `<div class="news-card-image" style="background-image: url('${escapeHtml(item.imageUrl)}');"></div>`;
        } else {
            card.innerHTML = `<div class="news-card-icon">📰</div>`;
        }

        const content = document.createElement('div');
        content.className = 'news-card-content';
        content.innerHTML = `
            ${item.publishedAt ? `<span class="news-date"> ${escapeHtml(item.publishedAt)}</span>` : ''}
            <h3 class="news-title">${escapeHtml(item.title)}</h3>
            <p class="news-description">${escapeHtml(item.description || '')}</p>
            <a href="${escapeHtml(item.url)}" target="_blank" class="news-link" rel="noopener noreferrer">Читать далее →</a>
        `;

        card.appendChild(content);
        return card;
    }

    function renderNews(newsItems) {
        if (state.currentPage === 0) elements.container.innerHTML = '';
        const fragment = document.createDocumentFragment();
        newsItems.forEach(item => fragment.appendChild(createNewsCard(item)));
        elements.container.appendChild(fragment);
        state.totalLoaded += newsItems.length;
        updateCounter();
    }

    function updateCounter() {
        let counter = document.querySelector('.news-counter');
        if (!counter && state.totalLoaded > 0) {
            counter = document.createElement('div');
            counter.className = 'news-counter';
            elements.container.parentNode.insertBefore(counter, elements.container);
        }
        if (counter) counter.textContent = `Показано ${state.totalLoaded} новостей`;
    }

    function showError() {
        if (state.currentPage === 0 && elements.container.children.length === 0) {
            elements.container.innerHTML = '<div class="news-empty"><div class="news-empty-icon">😔</div><h3>Новости временно недоступны</h3><p>Попробуйте обновить страницу позже</p></div>';
        }
    }

    async function loadNews() {
        if (state.isLoading || !state.hasMore) return;
        state.isLoading = true;
        if (elements.loading) elements.loading.style.display = 'block';

        try {
            const url = `${getContextPath().replace(/\/$/, '')}/api/news?page=${state.currentPage}&size=${CONFIG.pageSize}`;
            const response = await fetch(url);
            if (!response.ok) throw new Error(`HTTP ${response.status}`);

            const data = await response.json();
            const news = data.news || [];

            if (news.length > 0) {
                renderNews(news);
                state.currentPage++;
                state.hasMore = data.hasMore === true;
            } else {
                state.hasMore = false;
            }

            if (!state.hasMore && state.currentPage > 0 && elements.end) elements.end.style.display = 'block';
        } catch (error) {
            console.error('Ошибка загрузки новостей:', error);
            showError();
        } finally {
            state.isLoading = false;
            if (elements.loading) elements.loading.style.display = 'none';
        }
    }

    function handleScroll() {
        if (state.isLoading || !state.hasMore) return;
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - CONFIG.scrollThreshold) {
            loadNews();
        }
    }

    window.NewsModule = { init, reset: () => { reset(); }, loadNews };
    function reset() {
        state = { currentPage: 0, isLoading: false, hasMore: true, totalLoaded: 0 };
        elements.container.innerHTML = '';
        if (elements.end) elements.end.style.display = 'none';
        loadNews();
    }

    document.readyState === 'loading' ? document.addEventListener('DOMContentLoaded', init) : init();
})();