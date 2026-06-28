(() => {
  'use strict';

  const slides = [...document.querySelectorAll('.slide')];
  const total = slides.length;
  const progressBar = document.getElementById('progressBar');
  const currentNumber = document.getElementById('currentNumber');
  const totalNumber = document.getElementById('totalNumber');
  const slideLabel = document.getElementById('slideLabel');
  const prevButton = document.getElementById('prevButton');
  const nextButton = document.getElementById('nextButton');
  const dotIndicator = document.getElementById('dotIndicator');
  const slideMenu = document.getElementById('slideMenu');
  const slideList = document.getElementById('slideList');
  const menuButton = document.getElementById('menuButton');
  const closeMenuButton = document.getElementById('closeMenuButton');
  const scrim = document.getElementById('scrim');
  const notes = document.getElementById('speakerNotes');
  const notesContent = document.getElementById('notesContent');
  const notesButton = document.getElementById('notesButton');
  const closeNotesButton = document.getElementById('closeNotesButton');
  const fullscreenButton = document.getElementById('fullscreenButton');
  const helpToast = document.getElementById('helpToast');

  let current = 0;
  let touchStartX = 0;
  let touchStartY = 0;
  let toastTimer;

  totalNumber.textContent = String(total).padStart(2, '0');

  slides.forEach((slide, index) => {
    const number = String(index + 1).padStart(2, '0');
    const title = slide.dataset.title || `Slide ${number}`;

    const dot = document.createElement('button');
    dot.type = 'button';
    dot.setAttribute('aria-label', `Ke slide ${index + 1}: ${title}`);
    dot.addEventListener('click', () => goTo(index));
    dotIndicator.appendChild(dot);

    const item = document.createElement('button');
    item.type = 'button';
    item.innerHTML = `<span>${number}</span><strong>${title}</strong>`;
    item.addEventListener('click', () => {
      goTo(index);
      toggleMenu(false);
    });
    slideList.appendChild(item);
  });

  const dots = [...dotIndicator.children];
  const menuItems = [...slideList.children];

  function indexFromHash() {
    const parsed = Number.parseInt(location.hash.replace('#', ''), 10);
    return Number.isFinite(parsed) ? Math.min(total - 1, Math.max(0, parsed - 1)) : 0;
  }

  function goTo(index, updateHash = true) {
    const next = Math.min(total - 1, Math.max(0, index));
    if (next === current && slides[current].classList.contains('is-active')) {
      updateUi();
      return;
    }

    slides.forEach((slide, i) => {
      slide.classList.toggle('was-active', i < next);
      slide.classList.toggle('is-active', i === next);
      slide.setAttribute('aria-hidden', i === next ? 'false' : 'true');
      if (i === next) slide.scrollTop = 0;
    });
    current = next;
    if (updateHash) history.replaceState(null, '', `#${current + 1}`);
    updateUi();
  }

  function updateUi() {
    const slide = slides[current];
    currentNumber.textContent = String(current + 1).padStart(2, '0');
    slideLabel.textContent = slide.dataset.title;
    progressBar.style.width = `${((current + 1) / total) * 100}%`;
    notesContent.textContent = slide.dataset.notes || 'Tidak ada catatan untuk slide ini.';
    prevButton.disabled = current === 0;
    nextButton.disabled = current === total - 1;
    dots.forEach((dot, index) => dot.classList.toggle('active', index === current));
    menuItems.forEach((item, index) => item.classList.toggle('active', index === current));
    document.title = `${String(current + 1).padStart(2, '0')} · ${slide.dataset.title} — Aplikasi Hampers`;
  }

  function toggleMenu(force) {
    const willOpen = typeof force === 'boolean' ? force : !slideMenu.classList.contains('open');
    slideMenu.classList.toggle('open', willOpen);
    slideMenu.setAttribute('aria-hidden', String(!willOpen));
    menuButton.setAttribute('aria-expanded', String(willOpen));
    scrim.hidden = !willOpen;
    if (willOpen) menuItems[current].focus();
  }

  function toggleNotes(force) {
    const willOpen = typeof force === 'boolean' ? force : !notes.classList.contains('open');
    notes.classList.toggle('open', willOpen);
    notes.setAttribute('aria-hidden', String(!willOpen));
  }

  function toggleFullscreen() {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen?.();
    } else {
      document.exitFullscreen?.();
    }
  }

  function showHelp() {
    clearTimeout(toastTimer);
    helpToast.classList.add('show');
    toastTimer = setTimeout(() => helpToast.classList.remove('show'), 4000);
  }

  prevButton.addEventListener('click', () => goTo(current - 1));
  nextButton.addEventListener('click', () => goTo(current + 1));
  menuButton.addEventListener('click', () => toggleMenu());
  closeMenuButton.addEventListener('click', () => toggleMenu(false));
  scrim.addEventListener('click', () => toggleMenu(false));
  notesButton.addEventListener('click', () => toggleNotes());
  closeNotesButton.addEventListener('click', () => toggleNotes(false));
  fullscreenButton.addEventListener('click', toggleFullscreen);

  document.querySelectorAll('[data-go]').forEach(button => {
    button.addEventListener('click', () => {
      if (button.dataset.go === 'next') goTo(current + 1);
      if (button.dataset.go === 'first') goTo(0);
    });
  });

  document.addEventListener('keydown', event => {
    if (event.target.matches('button, a, input, textarea, select') && !['Escape'].includes(event.key)) return;
    const actions = {
      ArrowRight: () => goTo(current + 1),
      ArrowDown: () => goTo(current + 1),
      PageDown: () => goTo(current + 1),
      ' ': () => goTo(current + 1),
      ArrowLeft: () => goTo(current - 1),
      ArrowUp: () => goTo(current - 1),
      PageUp: () => goTo(current - 1),
      Home: () => goTo(0),
      End: () => goTo(total - 1),
      Escape: () => { toggleMenu(false); toggleNotes(false); },
      n: () => toggleNotes(),
      N: () => toggleNotes(),
      f: toggleFullscreen,
      F: toggleFullscreen,
      '?': showHelp
    };
    if (actions[event.key]) {
      event.preventDefault();
      actions[event.key]();
    }
  });

  document.addEventListener('touchstart', event => {
    touchStartX = event.changedTouches[0].clientX;
    touchStartY = event.changedTouches[0].clientY;
  }, { passive: true });

  document.addEventListener('touchend', event => {
    const dx = event.changedTouches[0].clientX - touchStartX;
    const dy = event.changedTouches[0].clientY - touchStartY;
    if (Math.abs(dx) > 55 && Math.abs(dx) > Math.abs(dy) * 1.35) {
      goTo(dx < 0 ? current + 1 : current - 1);
    }
  }, { passive: true });

  window.addEventListener('hashchange', () => goTo(indexFromHash(), false));

  // Slide 4: feature explorer
  const featureCards = [...document.querySelectorAll('.feature-card')];
  const featureDetail = document.getElementById('featureDetail');
  featureCards.forEach(card => card.addEventListener('click', () => {
    featureCards.forEach(item => item.classList.remove('is-selected'));
    card.classList.add('is-selected');
    featureDetail.innerHTML = `<span>${card.querySelector('span').textContent}</span><p>${card.dataset.feature}</p>`;
  }));

  // Slide 5: role comparison
  const roleButtons = [...document.querySelectorAll('.role-toggle button')];
  const permissionPanel = document.getElementById('permissionPanel');
  const adminOnly = permissionPanel.querySelector('.admin-only');
  const roleName = permissionPanel.querySelector('.role-badge strong');
  const roleInitial = permissionPanel.querySelector('.role-badge > span');
  const roleAccess = permissionPanel.querySelector('.role-badge small');
  const roleNote = document.getElementById('roleNote');
  roleButtons.forEach(button => button.addEventListener('click', () => {
    const isAdmin = button.dataset.role === 'admin';
    roleButtons.forEach(item => item.classList.toggle('active', item === button));
    roleName.textContent = isAdmin ? 'Administrator' : 'Kasir';
    roleInitial.textContent = isAdmin ? 'A' : 'K';
    roleAccess.textContent = isAdmin ? 'Akses penuh' : 'Akses operasional';
    adminOnly.classList.toggle('denied', !isAdmin);
    roleNote.innerHTML = isAdmin
      ? '<svg><use href="#i-shield"/></svg> Admin dapat menambah akun kasir maupun admin baru.'
      : '<svg><use href="#i-shield"/></svg> Menu Tambah Pengguna disembunyikan untuk akun kasir.';
  }));

  // Slide 8: operational journey
  const journeySteps = [...document.querySelectorAll('.journey-step')];
  const journeyDetail = document.getElementById('journeyDetail');
  journeySteps.forEach((step, index) => step.addEventListener('click', () => {
    journeySteps.forEach(item => item.classList.remove('active'));
    step.classList.add('active');
    journeyDetail.innerHTML = `<span>${String(index + 1).padStart(2, '0')}</span><p>${step.dataset.detail}</p>`;
  }));

  // Slide 11: screenshot tabs
  const demoTabs = [...document.querySelectorAll('.demo-tabs button')];
  const demoImage = document.getElementById('demoImage');
  const demoCaption = document.getElementById('demoCaption');
  demoTabs.forEach((tab, index) => tab.addEventListener('click', () => {
    demoTabs.forEach(item => {
      item.classList.toggle('active', item === tab);
      item.setAttribute('aria-selected', String(item === tab));
    });
    demoImage.classList.add('changing');
    window.setTimeout(() => {
      demoImage.src = tab.dataset.image;
      demoImage.alt = `Tampilan modul ${tab.textContent} aplikasi penjualan hampers`;
      demoCaption.textContent = tab.dataset.caption;
      document.querySelector('.demo-caption span').textContent = String(index + 1).padStart(2, '0');
      demoImage.classList.remove('changing');
    }, 180);
  }));

  slides.forEach(slide => slide.setAttribute('aria-hidden', 'true'));
  current = indexFromHash();
  slides.forEach((slide, index) => {
    slide.classList.toggle('is-active', index === current);
    slide.classList.toggle('was-active', index < current);
    slide.setAttribute('aria-hidden', index === current ? 'false' : 'true');
  });
  updateUi();
  window.setTimeout(showHelp, 900);
})();
