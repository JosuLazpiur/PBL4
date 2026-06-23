document.addEventListener("DOMContentLoaded", () => {
  if (typeof puestos !== "undefined" && typeof ventasEstimadas !== "undefined" && ventasEstimadas.length > 0) {
    const ctx = document.getElementById("regresionChart").getContext("2d");
    if (graficoCard) {
      graficoCard.style.display = "block"; 
    }
    new Chart(ctx, {
      type: 'line',
      data: {
        labels: puestos,
        datasets: [{
          label: 'Ventas estimadas',
          data: ventasEstimadas,
          borderColor: '#2e7d32',
          backgroundColor: 'rgba(76, 175, 80, 0.2)',
          fill: true,
          tension: 0.4,
          pointRadius: 4
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'top' },
          title: { display: true, text: 'Estimación por Puesto' }
        },
        scales: {
          x: { title: { display: true, text: 'Puesto' } },
          y: { title: { display: true, text: 'Ventas estimadas' } }
        }
      }
    });
  }
});
