const id = new URLSearchParams(location.search).get('id');
const input = document.getElementById('input');
const originEl = document.getElementById('origin');

let origin = '';
let startTime = null;
let composing = false;
let timer = null;
let lastBad = 0;

async function loadText() {
  const res = await fetch(`/api/texts/${id}`);
  if (!res.ok) { originEl.textContent = '글을 찾을 수 없어요'; return; }
  const text = await res.json();
  origin = text.content;
  document.getElementById('title').textContent = text.title;
  render('');
}

const esc = c => c === '<' ? '&lt;' : c === '>' ? '&gt;' : c === '&' ? '&amp;' : c;

function render(typed) {
  const judged = composing ? typed.slice(0, -1) : typed;
  let bad = 0;
  const j = [...judged];
  originEl.innerHTML = [...origin].map((ch, i) => {
    if (i >= j.length) return esc(ch);
    const same = j[i] === ch;
    if (!same) bad++;
    return `<span class="${same ? 'ok' : 'bad'}">${esc(ch)}</span>`;
  }).join('');
  if (bad > lastBad) {
    originEl.classList.remove('shake');
    void originEl.offsetWidth;
    originEl.classList.add('shake');
  }
  lastBad = bad;
  const cur = originEl.children[j.length - 1];
  if (cur) originEl.scrollTop = cur.offsetTop - originEl.clientHeight / 2;
  else originEl.scrollTop = 0;
  const pct = origin.length ? Math.min(100, typed.length / origin.length * 100) : 0;
  document.getElementById('bar').style.width = pct + '%';
}

function updateLive(typed) {
  let correct = 0;
  for (let i = 0; i < typed.length && i < origin.length; i++) if (typed[i] === origin[i]) correct++;
  const acc = typed.length ? Math.round(correct / typed.length * 100) : 0;
  const min = (Date.now() - startTime) / 60000;
  document.getElementById('acc').textContent = acc;
  document.getElementById('cpm').textContent = min > 0 ? Math.round(correct / min) : 0;
}

function startTimer() {
  startTime = Date.now();
  timer = setInterval(() => {
    document.getElementById('time').textContent = Math.floor((Date.now() - startTime) / 1000);
  }, 250);
}

input.addEventListener('compositionstart', () => composing = true);
input.addEventListener('compositionend', () => { composing = false; render(input.value); });

input.addEventListener('input', () => {
  if (!startTime) startTimer();
  render(input.value);
  updateLive(input.value);
});

input.addEventListener('keydown', e => {
  if (e.key === 'Enter') {
    e.preventDefault();
    if (input.value.length >= origin.length) finish();
  }
});

async function finish() {
  input.disabled = true;
  clearInterval(timer);
  const res = await fetch('/api/results', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ textId: Number(id), typed: input.value, elapsedMs: Date.now() - startTime })
  });
  const r = await res.json();
  document.getElementById('result').hidden = false;
  countUp('r-acc', r.accuracy);
  countUp('r-cpm', r.cpm);
  countUp('r-typo', r.typoCount);
  showGrade(r.accuracy, r.cpm);
  document.getElementById('result').scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function countUp(elId, to, ms = 900) {
  const el = document.getElementById(elId);
  const end = Number(to) || 0;
  const t0 = performance.now();
  (function step(now) {
    const p = Math.min(1, (now - t0) / ms);
    el.textContent = Math.round(end * (1 - Math.pow(1 - p, 3)));
    if (p < 1) requestAnimationFrame(step);
  })(t0);
}

function showGrade(acc, cpm) {
  let g = 'D', msg = '천천히 정확하게부터 다시 가봐요!';
  if (acc >= 98 && cpm >= 300) { g = 'S'; msg = '전설의 손가락이에요 🔥'; }
  else if (acc >= 95) { g = 'A'; msg = '아주 훌륭해요!'; }
  else if (acc >= 90) { g = 'B'; msg = '좋아요, 조금만 더!'; }
  else if (acc >= 80) { g = 'C'; msg = '오타만 줄이면 금방 늘어요.'; }
  document.getElementById('grade').textContent = g;
  document.getElementById('grade-msg').textContent = msg;
  if (g === 'S' || g === 'A') confetti();
}

function confetti() {
  const cv = document.getElementById('confetti');
  const ctx = cv.getContext('2d');
  cv.width = innerWidth; cv.height = innerHeight;
  const colors = ['#8b7dff', '#ff6ec7', '#4fe3ff', '#3be08f', '#ffd166'];
  const ps = Array.from({ length: 140 }, () => ({
    x: innerWidth / 2, y: innerHeight * 0.6,
    vx: (Math.random() - .5) * 18, vy: -Math.random() * 18 - 4,
    s: Math.random() * 7 + 4, r: Math.random() * 6, vr: (Math.random() - .5) * .4,
    c: colors[Math.floor(Math.random() * colors.length)]
  }));
  let frames = 0;
  (function step() {
    ctx.clearRect(0, 0, cv.width, cv.height);
    ps.forEach(p => {
      p.vy += .35; p.x += p.vx; p.y += p.vy; p.r += p.vr;
      ctx.save();
      ctx.translate(p.x, p.y); ctx.rotate(p.r);
      ctx.fillStyle = p.c; ctx.fillRect(-p.s / 2, -p.s / 2, p.s, p.s * .6);
      ctx.restore();
    });
    if (++frames < 150) requestAnimationFrame(step);
    else ctx.clearRect(0, 0, cv.width, cv.height);
  })();
}

loadText();
