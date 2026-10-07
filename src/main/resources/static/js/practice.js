// 연습: 실시간 표시는 JS, 최종 결과는 서버(Java)에서 계산

// 주소창의 ?id=숫자에서 어떤 글을 연습할지 꺼냄
const id = new URLSearchParams(location.search).get('id');
const input = document.getElementById('input');
const originEl = document.getElementById('origin');

let origin = '';        // 연습할 원문 (서버에서 받아서 채움)
let startTime = null;   // 처음 글자를 친 시각
let composing = false;  // 한글 조합 중인지 (예: ㅎ→하→한, 글자가 완성되기 전 상태)
let timer = null;       // 경과 시간 표시용 타이머
let lastBad = 0;        // 직전 오타 개수 (새 오타가 생겼을 때만 흔들기 효과를 주려고)

// 서버에서 글을 받아와 화면에 표시. 기본 글/붙여넣은 글 모두 같은 API로 받는다.
async function loadText() {
  const res = await fetch(`/api/texts/${id}`);
  if (!res.ok) { originEl.textContent = '글을 찾을 수 없어요'; return; }
  const text = await res.json();
  origin = text.content;
  document.getElementById('title').textContent = text.title;
  render('');
}

// HTML에서 특별한 글자(<, >, &)를 안전하게 바꿔서 화면이 깨지는 것을 막음
const esc = c => c === '<' ? '&lt;' : c === '>' ? '&gt;' : c === '&' ? '&amp;' : c;

// 원문을 글자별로 맞음(ok)/틀림(bad) 색칠하고 진행도 바를 갱신 (typed: 지금까지 친 글)
function render(typed) {
  // 조합 중이면 마지막 글자는 아직 완성 전이므로 판정에서 뺌
  const judged = composing ? typed.slice(0, -1) : typed;
  let bad = 0;
  originEl.innerHTML = [...origin].map((ch, i) => {
    if (i >= judged.length) return esc(ch);
    const same = judged[i] === ch;
    if (!same) bad++;
    return `<span class="${same ? 'ok' : 'bad'}">${esc(ch)}</span>`;
  }).join('');
  // 오타가 늘었을 때만 카드를 흔듦
  if (bad > lastBad) {
    originEl.classList.remove('shake');
    void originEl.offsetWidth; // 강제로 화면을 다시 계산시켜야 같은 애니메이션이 다시 재생됨
    originEl.classList.add('shake');
  }
  lastBad = bad;
  const pct = origin.length ? Math.min(100, typed.length / origin.length * 100) : 0;
  document.getElementById('bar').style.width = pct + '%';
}

// 실시간 정확도/타수 표시 (대략치. 최종 결과는 서버 계산을 사용)
function updateLive(typed) {
  let correct = 0;
  for (let i = 0; i < typed.length && i < origin.length; i++) if (typed[i] === origin[i]) correct++;
  const acc = typed.length ? Math.round(correct / typed.length * 100) : 0;
  const min = (Date.now() - startTime) / 60000;
  document.getElementById('acc').textContent = acc;
  document.getElementById('cpm').textContent = min > 0 ? Math.round(correct / min) : 0;
}

// 첫 글자를 칠 때 시작 시각을 기록하고 경과 시간 표시를 시작
function startTimer() {
  startTime = Date.now();
  timer = setInterval(() => {
    document.getElementById('time').textContent = Math.floor((Date.now() - startTime) / 1000);
  }, 250);
}

// 한글 조합 시작/끝 추적: 조합이 끝나면 완성된 글자까지 포함해 다시 그림
input.addEventListener('compositionstart', () => composing = true);
input.addEventListener('compositionend', () => { composing = false; render(input.value); });

// 글자를 칠 때마다 색칠과 실시간 수치를 갱신
input.addEventListener('input', () => {
  if (!startTime) startTimer();
  render(input.value);
  updateLive(input.value);
});

// 원문 길이만큼 치고 Enter를 누르면 연습 종료
input.addEventListener('keydown', e => {
  if (e.key === 'Enter') {
    e.preventDefault();
    if (input.value.length >= origin.length) finish();
  }
});

// 연습을 끝내고 서버에 채점을 요청해 결과를 보여줌
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

// 숫자가 0에서 목표값까지 올라가는 효과 (처음엔 빠르고 끝에서 느려짐)
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

// 정확도와 타수로 등급(S~D)과 응원 메시지를 정함. S/A면 폭죽
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

// 폭죽: 캔버스에 색종이 140개를 랜덤 속도로 쏘아 올려 중력으로 떨어지게 그림 (약 2.5초)
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
