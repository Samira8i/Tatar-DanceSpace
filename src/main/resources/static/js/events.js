//Events Module - Общие функции для страниц событий

// Форматирование даты
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;
    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Показ уведомления
function showNotification(message, type = 'success') {
    const notification = $('<div>')
        .addClass(`notification notification-${type}`)
        .text(message)
        .css({
            position: 'fixed',
            top: '20px',
            right: '20px',
            padding: '12px 20px',
            borderRadius: '8px',
            background: type === 'success' ? '#7A8450' : '#dc3545',
            color: 'white',
            zIndex: 9999,
            boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
            fontSize: '14px'
        });

    $('body').append(notification);
    setTimeout(() => notification.fadeOut(300, function() { $(this).remove(); }), 3000);
}

// Экранирование HTML
function escapeHtml(text) {
    if (!text) return '';
    return text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Глобальный доступ к утилитам
window.DanceSpace = {
    formatDate,
    showNotification,
    escapeHtml
};