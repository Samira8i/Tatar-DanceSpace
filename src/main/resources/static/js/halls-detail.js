(function() {
    'use strict';

    const hallId = window.hallId;

    if (!hallId) return;

    function updateHallStats() {
        fetch('/tatardancespace/api/halls/' + hallId + '/stats')
            .then(res => res.json())
            .then(data => {
                const avgRating = document.querySelector('.avg-rating');
                if (avgRating) avgRating.textContent = data.averageRating.toFixed(1);

                const reviewsCount = document.querySelectorAll('.reviews-count, .reviews-count-display');
                reviewsCount.forEach(el => el.textContent = data.reviewsCount);
            })
            .catch(error => console.error('Error updating stats:', error));
    }

    const submitReview = document.getElementById('submit-review');
    if (submitReview) {
        submitReview.addEventListener('click', () => {
            const ratingSelect = document.getElementById('review-rating');
            const rating = ratingSelect ? ratingSelect.value : null;
            const textarea = document.getElementById('review-text');
            const text = textarea ? textarea.value : '';

            if (!rating) return;

            fetch('/tatardancespace/api/halls/' + hallId + '/review?rating=' + rating + '&text=' + encodeURIComponent(text), { method: 'POST' })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        prependReview(data);
                        if (textarea) textarea.value = '';
                        updateHallStats();
                        showNotification('Отзыв добавлен', 'success');
                    }
                })
                .catch(error => console.error('Error posting review:', error));
        });
    }

    function prependReview(data) {
        const reviewsList = document.getElementById('reviews-list');
        if (!reviewsList) return;

        const stars = '★'.repeat(data.rating) + '☆'.repeat(5 - data.rating);
        const newReview = document.createElement('div');
        newReview.className = 'review';
        newReview.setAttribute('data-review-id', data.id);
        newReview.innerHTML = `
            <div class="review-header">
                <div>
                    <strong>${escapeHtml(data.author)}</strong>
                    <span class="stars">${stars}</span>
                </div>
                <div>
                    <span class="review-date">${data.createdAt}</span>
                    <button class="delete-review-btn" data-review-id="${data.id}">Удалить</button>
                </div>
            </div>
            <p>${escapeHtml(data.text || '')}</p>
        `;
        reviewsList.prepend(newReview);
    }

    document.addEventListener('click', (e) => {
        const deleteBtn = e.target.closest('.delete-review-btn');
        if (!deleteBtn) return;

        const reviewId = deleteBtn.getAttribute('data-review-id');
        if (!reviewId || !confirm('Удалить этот отзыв?')) return;

        fetch('/tatardancespace/api/halls/reviews/' + reviewId, { method: 'DELETE' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    const reviewDiv = deleteBtn.closest('.review');
                    if (reviewDiv) reviewDiv.remove();
                    updateHallStats();
                    showNotification('Отзыв удалён', 'success');
                }
            })
            .catch(error => console.error('Error deleting review:', error));
    });

    function escapeHtml(text) {
        if (!text) return '';
        return text.replace(/[&<>]/g, m => {
            if (m === '&') return '&amp;';
            if (m === '<') return '&lt;';
            if (m === '>') return '&gt;';
            return m;
        });
    }

    function showNotification(message, type) {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed; top: 20px; right: 20px; padding: 12px 20px;
            border-radius: 8px; background: ${type === 'success' ? '#7A8450' : '#dc3545'};
            color: white; z-index: 9999;
        `;
        document.body.appendChild(notification);
        setTimeout(() => notification.remove(), 3000);
    }

    updateHallStats();
})();