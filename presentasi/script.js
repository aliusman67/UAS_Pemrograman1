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
  const timerButton = document.getElementById('timerButton');
  const timerValue = document.getElementById('timerValue');
  const overviewButton = document.getElementById('overviewButton');
  const closeOverviewButton = document.getElementById('closeOverviewButton');
  const slideOverview = document.getElementById('slideOverview');
  const overviewGrid = document.getElementById('overviewGrid');
  const pointerButton = document.getElementById('pointerButton');
  const laserPointer = document.getElementById('laserPointer');
  const progressTrack = document.getElementById('progressTrack');
  const imageDialog = document.getElementById('imageDialog');
  const dialogImage = document.getElementById('dialogImage');
  const dialogCaption = document.getElementById('dialogCaption');
  const closeImageDialog = document.getElementById('closeImageDialog');

  let current = 0;
  let touchStartX = 0;
  let touchStartY = 0;
  let toastTimer;
  let timerInterval;
  let timerStartedAt = 0;
  let timerElapsed = 0;
  let timerRunning = false;

  totalNumber.textContent = String(total).padStart(2, '0');
  progressTrack.setAttribute('aria-valuemax', String(total));

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

    const overviewItem = document.createElement('button');
    overviewItem.type = 'button';
    overviewItem.className = 'overview-card';
    overviewItem.innerHTML = `<span>${number}</span><strong>${title}</strong><small>${index === 0 ? 'Pembuka' : index === total - 1 ? 'Penutup' : 'Bagian presentasi'}</small>`;
    overviewItem.addEventListener('click', () => {
      goTo(index);
      toggleOverview(false);
    });
    overviewGrid.appendChild(overviewItem);
  });

  const dots = [...dotIndicator.children];
  const menuItems = [...slideList.children];
  const overviewItems = [...overviewGrid.children];

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
    progressTrack.setAttribute('aria-valuenow', String(current + 1));
    progressTrack.setAttribute('aria-valuetext', `Slide ${current + 1} dari ${total}: ${slide.dataset.title}`);
    notesContent.textContent = slide.dataset.notes || 'Tidak ada catatan untuk slide ini.';
    prevButton.disabled = current === 0;
    nextButton.disabled = current === total - 1;
    dots.forEach((dot, index) => dot.classList.toggle('active', index === current));
    menuItems.forEach((item, index) => item.classList.toggle('active', index === current));
    overviewItems.forEach((item, index) => item.classList.toggle('active', index === current));
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

  function toggleOverview(force) {
    const willOpen = typeof force === 'boolean' ? force : !slideOverview.classList.contains('open');
    slideOverview.classList.toggle('open', willOpen);
    slideOverview.setAttribute('aria-hidden', String(!willOpen));
    overviewButton.setAttribute('aria-expanded', String(willOpen));
    if (willOpen) {
      toggleMenu(false);
      toggleNotes(false);
      overviewItems[current].focus();
    } else if (document.activeElement?.classList.contains('overview-card')) {
      overviewButton.focus();
    }
  }

  function updateTimer() {
    const elapsed = timerElapsed + (timerRunning ? Date.now() - timerStartedAt : 0);
    const seconds = Math.floor(elapsed / 1000);
    const minutes = Math.floor(seconds / 60);
    timerValue.textContent = `${String(minutes).padStart(2, '0')}:${String(seconds % 60).padStart(2, '0')}`;
  }

  function toggleTimer() {
    if (timerRunning) {
      timerElapsed += Date.now() - timerStartedAt;
      window.clearInterval(timerInterval);
    } else {
      timerStartedAt = Date.now();
      timerInterval = window.setInterval(updateTimer, 250);
    }
    timerRunning = !timerRunning;
    timerButton.setAttribute('aria-pressed', String(timerRunning));
    timerButton.setAttribute('aria-label', timerRunning ? 'Jeda timer presentasi' : 'Mulai timer presentasi');
    updateTimer();
  }

  function resetTimer() {
    window.clearInterval(timerInterval);
    timerElapsed = 0;
    timerRunning = false;
    timerButton.setAttribute('aria-pressed', 'false');
    timerButton.setAttribute('aria-label', 'Mulai timer presentasi');
    updateTimer();
  }

  function togglePointer(force) {
    const willActivate = typeof force === 'boolean' ? force : !document.body.classList.contains('pointer-active');
    document.body.classList.toggle('pointer-active', willActivate);
    if (!willActivate) document.body.classList.remove('pointer-positioned', 'pointer-down');
    pointerButton.setAttribute('aria-pressed', String(willActivate));
    pointerButton.setAttribute('aria-label', willActivate ? 'Nonaktifkan pointer presentasi' : 'Aktifkan pointer presentasi');
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
  overviewButton.addEventListener('click', () => toggleOverview());
  closeOverviewButton.addEventListener('click', () => toggleOverview(false));
  overviewGrid.addEventListener('keydown', event => {
    if (!event.target.classList.contains('overview-card')) return;
    const index = overviewItems.indexOf(event.target);
    const moves = { ArrowRight: 1, ArrowLeft: -1, ArrowDown: 4, ArrowUp: -4, Home: -index, End: total - 1 - index };
    if (!(event.key in moves)) return;
    event.preventDefault();
    event.stopPropagation();
    overviewItems[Math.min(total - 1, Math.max(0, index + moves[event.key]))].focus();
  });
  pointerButton.addEventListener('click', () => togglePointer());
  timerButton.addEventListener('click', event => event.shiftKey ? resetTimer() : toggleTimer());
  timerButton.addEventListener('dblclick', resetTimer);

  progressTrack.addEventListener('click', event => {
    const bounds = progressTrack.getBoundingClientRect();
    const ratio = (event.clientX - bounds.left) / bounds.width;
    goTo(Math.min(total - 1, Math.floor(ratio * total)));
  });

  document.addEventListener('pointermove', event => {
    if (!document.body.classList.contains('pointer-active')) return;
    document.body.classList.add('pointer-positioned');
    laserPointer.style.left = `${event.clientX}px`;
    laserPointer.style.top = `${event.clientY}px`;
  });
  document.addEventListener('pointerdown', () => document.body.classList.add('pointer-down'));
  document.addEventListener('pointerup', () => document.body.classList.remove('pointer-down'));

  document.querySelectorAll('[data-go]').forEach(button => {
    button.addEventListener('click', () => {
      if (button.dataset.go === 'next') goTo(current + 1);
      if (button.dataset.go === 'first') goTo(0);
    });
  });

  document.addEventListener('keydown', event => {
    if (event.target.matches('input, textarea, select') && event.key !== 'Escape') return;
    if (event.target.matches('button, a') && ['Enter', ' ', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown'].includes(event.key)) return;
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
      Escape: () => { toggleOverview(false); toggleMenu(false); toggleNotes(false); if (imageDialog.open) imageDialog.close(); },
      n: () => toggleNotes(),
      N: () => toggleNotes(),
      o: () => toggleOverview(),
      O: () => toggleOverview(),
      l: () => togglePointer(),
      L: () => togglePointer(),
      t: () => toggleTimer(),
      T: () => toggleTimer(),
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
    if (slideOverview.classList.contains('open') || imageDialog.open) return;
    const dx = event.changedTouches[0].clientX - touchStartX;
    const dy = event.changedTouches[0].clientY - touchStartY;
    if (Math.abs(dx) > 55 && Math.abs(dx) > Math.abs(dy) * 1.35) {
      goTo(dx < 0 ? current + 1 : current - 1);
    }
  }, { passive: true });

  window.addEventListener('hashchange', () => goTo(indexFromHash(), false));

  // Slide 6: interactive technology stack
  const stackVisual = document.querySelector('.stack-visual');
  const stackCards = [...document.querySelectorAll('.stack-card')];
  stackCards.forEach(card => {
    card.tabIndex = 0;
    card.setAttribute('role', 'button');
    card.setAttribute('aria-pressed', 'false');
    const selectStack = () => {
      const wasSelected = card.classList.contains('is-focused');
      stackCards.forEach(item => {
        item.classList.remove('is-focused');
        item.setAttribute('aria-pressed', 'false');
      });
      card.classList.toggle('is-focused', !wasSelected);
      card.setAttribute('aria-pressed', String(!wasSelected));
      stackVisual.classList.toggle('has-focus', !wasSelected);
    };
    card.addEventListener('click', selectStack);
    card.addEventListener('keydown', event => {
      if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); selectStack(); }
    });
  });

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

  // Slide 9: replayable automation flow
  const automationTrigger = document.getElementById('automationTrigger');
  const automationMap = document.querySelector('.automation-map');
  let automationTimer;
  automationTrigger.addEventListener('click', () => {
    window.clearTimeout(automationTimer);
    automationMap.classList.remove('is-running');
    void automationMap.offsetWidth;
    automationMap.classList.add('is-running');
    automationTimer = window.setTimeout(() => automationMap.classList.remove('is-running'), 1700);
  });

  // Slide 10: focus individual database entities
  const erd = document.querySelector('.erd');
  const databaseTables = [...document.querySelectorAll('.db-table')];
  databaseTables.forEach(table => {
    table.tabIndex = 0;
    table.setAttribute('role', 'button');
    table.setAttribute('aria-pressed', 'false');
    const selectTable = () => {
      const wasSelected = table.classList.contains('is-selected');
      databaseTables.forEach(item => {
        item.classList.remove('is-selected');
        item.setAttribute('aria-pressed', 'false');
      });
      table.classList.toggle('is-selected', !wasSelected);
      table.setAttribute('aria-pressed', String(!wasSelected));
      erd.classList.toggle('has-selection', !wasSelected);
    };
    table.addEventListener('click', selectTable);
    table.addEventListener('keydown', event => {
      if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); selectTable(); }
    });
  });

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
  demoTabs.forEach((tab, index) => tab.addEventListener('keydown', event => {
    if (!['ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown'].includes(event.key)) return;
    event.preventDefault();
    const direction = ['ArrowRight', 'ArrowDown'].includes(event.key) ? 1 : -1;
    const nextTab = demoTabs[(index + direction + demoTabs.length) % demoTabs.length];
    nextTab.focus();
    nextTab.click();
  }));

  const demoZoomButton = document.getElementById('demoZoomButton');
  demoZoomButton.addEventListener('click', () => {
    dialogImage.src = demoImage.src;
    dialogImage.alt = demoImage.alt;
    dialogCaption.textContent = demoCaption.textContent;
    imageDialog.showModal();
  });
  closeImageDialog.addEventListener('click', () => imageDialog.close());
  imageDialog.addEventListener('click', event => {
    const bounds = imageDialog.getBoundingClientRect();
    const outside = event.clientX < bounds.left || event.clientX > bounds.right || event.clientY < bounds.top || event.clientY > bounds.bottom;
    if (outside) imageDialog.close();
  });

  // Slide 13: implementation checklist
  const setupChecks = [...document.querySelectorAll('.setup-check')];
  const setupProgressBar = document.getElementById('setupProgressBar');
  const setupProgressText = document.getElementById('setupProgressText');
  setupChecks.forEach(check => check.addEventListener('click', () => {
    const isDone = check.getAttribute('aria-pressed') !== 'true';
    check.setAttribute('aria-pressed', String(isDone));
    check.closest('li').classList.toggle('is-done', isDone);
    const complete = setupChecks.filter(item => item.getAttribute('aria-pressed') === 'true').length;
    setupProgressBar.style.width = `${(complete / setupChecks.length) * 100}%`;
    setupProgressText.textContent = complete === setupChecks.length ? 'Siap dijalankan!' : `${complete}/${setupChecks.length} siap`;
  }));

  slides.forEach(slide => slide.setAttribute('aria-hidden', 'true'));
  current = indexFromHash();
  slides.forEach((slide, index) => {
    slide.classList.toggle('is-active', index === current);
    slide.classList.toggle('was-active', index < current);
    slide.setAttribute('aria-hidden', index === current ? 'false' : 'true');
  });
  updateUi();
  if (current === 0) window.setTimeout(showHelp, 900);
})();
