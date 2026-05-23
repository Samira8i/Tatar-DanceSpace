(function() {
    'use strict';

    const eventId = window.eventId;

    if (!eventId) return;

    function updateStats() {
        fetch('/tatardancespace/api/events/' + eventId + '/stats')
            .then(res => res.json())
            .then(data => {
                updateElements('.like-count', data.likesCount);
                updateElements('.favorite-count', data.favoritesCount);
                updateElements('.comment-count, .comment-count-display', data.commentsCount);
                updateIconColor('.like-icon', data.liked, '#dc3545');
                updateIconColor('.favorite-icon', data.isFavorited, '#f5a623');
            })
            .catch(error => console.error('Error updating stats:', error));
    }

    function updateElements(selector, value) {
        document.querySelectorAll(selector).forEach(el => el.textContent = value);
    }

    function updateIconColor(selector, isActive, color) {
        document.querySelectorAll(selector).forEach(icon => {
            icon.style.color = isActive ? color : '';
        });
    }

    function sendRequest(url, onSuccess) {
        fetch(url, { method: 'POST' })
            .then(res => res.json())
            .then(onSuccess)
            .catch(error => console.error('Error:', error));
    }

    const likeBtn = document.querySelector('.like-btn');
    if (likeBtn) {
        likeBtn.addEventListener('click', () => {
            sendRequest('/tatardancespace/api/events/' + eventId + '/like', data => {
                updateElements('.like-count', data.likesCount);
                updateIconColor('.like-icon', data.liked, '#dc3545');
            });
        });
    }

    const favoriteBtn = document.querySelector('.favorite-btn');
    if (favoriteBtn) {
        favoriteBtn.addEventListener('click', () => {
            sendRequest('/tatardancespace/api/events/' + eventId + '/favorite', data => {
                updateElements('.favorite-count', data.favoritesCount);
                updateIconColor('.favorite-icon', data.isFavorited, '#f5a623');
            });
        });
    }

    const submitComment = document.getElementById('submit-comment');
    if (submitComment) {
        submitComment.addEventListener('click', () => {
            const textarea = document.getElementById('comment-text');
            const text = textarea ? textarea.value.trim() : '';
            if (!text) return;

            fetch('/tatardancespace/api/events/' + eventId + '/comment?text=' + encodeURIComponent(text), { method: 'POST' })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        if (textarea) textarea.value = '';
                        updateStats();
                        appendComment(data);
                        showNotification('Комментарий добавлен', 'success');
                    }
                })
                .catch(error => console.error('Error posting comment:', error));
        });
    }

    function appendComment(data) {
        const commentsList = document.getElementById('comments-list');
        if (!commentsList) return;

        const newComment = document.createElement('div');
        newComment.className = 'comment';
        newComment.setAttribute('data-comment-id', data.commentId);
        newComment.innerHTML = `
            <div class="comment-header">
                <div>
                    <strong>${escapeHtml(data.author)}</strong>
                    <span class="comment-date">${data.createdAt}</span>
                </div>
                <button class="delete-comment-btn" data-comment-id="${data.commentId}">Удалить</button>
            </div>
            <p>${escapeHtml(data.text)}</p>
        `;
        commentsList.appendChild(newComment);
    }

    document.addEventListener('click', (e) => {
        const deleteBtn = e.target.closest('.delete-comment-btn');
        if (!deleteBtn) return;

        const commentId = deleteBtn.getAttribute('data-comment-id');
        if (!commentId || !confirm('Удалить этот комментарий?')) return;

        fetch('/tatardancespace/api/events/comments/' + commentId, { method: 'DELETE' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    const commentDiv = deleteBtn.closest('.comment');
                    if (commentDiv) commentDiv.remove();
                    updateStats();
                    showNotification('Комментарий удалён', 'success');
                }
            })
            .catch(error => console.error('Error deleting comment:', error));
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

    updateStats();
})();