export function renderFooter() {
  const footer = document.getElementById("footer");
  if (!footer) return;

  footer.innerHTML = `
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-logo">
          <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
        </div>
        <div class="footer-links">
          <div class="footer-column">
            <h4>Company</h4>
            <a href="#">About</a>
            <a href="#">Careers</a>
          </div>
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Help Center</a>
            <a href="#">Contact</a>
          </div>
          <div class="footer-column">
            <h4>Legals</h4>
            <a href="#">Privacy</a>
            <a href="#">Terms</a>
          </div>
        </div>
      </div>
    </footer>`;
}

window.renderFooter = renderFooter;
