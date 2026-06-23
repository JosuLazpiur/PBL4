document.addEventListener("DOMContentLoaded", function () {
  const sidebar = document.getElementById("mobileSidebar");
  const overlay = document.getElementById("sidebarOverlay");

  function toggleSidebar() {
    sidebar.classList.toggle("active");
    overlay.classList.toggle("active");
  }

  window.toggleSidebar = toggleSidebar;

  document.querySelector(".hamburger-btn").onclick = function (e) {
    e.stopPropagation();
    sidebar.classList.toggle("active");
    overlay.classList.toggle("active");
  };

  overlay.onclick = function () {
    sidebar.classList.remove("active");
    overlay.classList.remove("active");
  };

  document.querySelectorAll(".mobile-sidebar .dropdown > a").forEach(toggle => {
    toggle.addEventListener("click", function (e) {
      e.preventDefault();
      const parent = toggle.closest(".dropdown");
      parent.classList.toggle("open");
    });
  });

  document.querySelector(".profile-dropdown .dropdown").onclick = function (e) {
    e.stopPropagation();
    const menu = this.querySelector(".dropdown-menu");
    menu.style.display = menu.style.display === "block" ? "none" : "block";
  };

  document.addEventListener("click", function () {
    document.querySelectorAll(".mobile-sidebar .dropdown").forEach(dropdown => dropdown.classList.remove("open"));
    document.querySelectorAll(".profile-dropdown .dropdown-menu").forEach(menu => menu.style.display = "none");
  });
});
