// Общие функции для страниц событий
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

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
            zIndex: 9999
        });

    $('body').append(notification);
    setTimeout(() => notification.remove(), 3000);
}