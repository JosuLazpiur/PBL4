document.addEventListener('DOMContentLoaded', function () {
    const stars = document.querySelectorAll('.rating-star');
    const estrellasInput = document.getElementById('estrellas');

    stars.forEach(star => {
        // Efecto hover
        star.addEventListener('mouseover', function () {
            const value = parseInt(this.getAttribute('data-value'));
            highlightStars(value);
        });

        // Efecto click (selección o deselección)
        star.addEventListener('click', function () {
            const clickedValue = parseInt(this.getAttribute('data-value'));
            const currentValue = parseInt(estrellasInput.value) || 0;

            // Si se hace clic en la misma estrella que ya estaba seleccionada, deselecciona
            const newValue = (clickedValue === currentValue) ? 0 : clickedValue;
            estrellasInput.value = newValue;

            // Actualizar clases y estilos
            stars.forEach(s => {
                const val = parseInt(s.getAttribute('data-value'));
                s.classList.toggle('selected', val <= newValue);
            });

            highlightStars(newValue);
        });
    });

    // Restaurar estado al salir del contenedor
    document.getElementById('rating-stars').addEventListener('mouseleave', function () {
        const currentValue = parseInt(estrellasInput.value) || 0;
        highlightStars(currentValue);
    });

    function highlightStars(value) {
        stars.forEach(star => {
            const starValue = parseInt(star.getAttribute('data-value'));
            if (starValue <= value) {
                star.style.color = '#FFD700';
                star.style.transform = 'scale(1.1)';
            } else {
                star.style.color = '#e0e0e0';
                star.style.transform = 'scale(1)';
            }
        });
    }
});
