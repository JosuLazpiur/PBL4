document.addEventListener('DOMContentLoaded', function() {
  // Solo procesa elementos que tienen valoración
  document.querySelectorAll('[data-rating]:not([data-rating="null"])').forEach(container => {
    const ratingValue = container.getAttribute('data-rating');
    const starElements = container.querySelectorAll('.star-inner');
    const ratingValueElement = container.querySelector('.rating-value');
    
    const rating = parseFloat(ratingValue);
    const fullStars = Math.floor(rating);
    const partialPercentage = (rating % 1) * 100;
    
    // Aplicar estilos a cada estrella
    starElements.forEach((star, index) => {
      if (index < fullStars) {
        star.style.width = '100%'; // Estrella completa
      } else if (index === fullStars) {
        star.style.width = `${partialPercentage}%`; // Estrella parcial
      } else {
        star.style.width = '0%'; // Estrella vacía
      }
    });
  });
});