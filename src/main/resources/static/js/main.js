// 메인: 글 목록 불러오기 + 내 글 추가

// 서버에서 글 목록을 받아와 카드 형태로 화면에 그린다.
async function loadTexts() {
  const res = await fetch('/api/texts');
  const texts = await res.json();
  const list = document.getElementById('text-list');
  list.innerHTML = '';
  if (!texts.length) list.innerHTML = '<li class="empty">아직 글이 없어요. 아래에서 추가해보세요!</li>';
  texts.forEach((t, i) => {
    // category는 서버의 SongText/CustomText가 정해서 내려준 값 → 뱃지 구분에 사용
    const custom = t.category === 'CUSTOM';
    const li = document.createElement('li');
    // 카드가 순서대로 차례차례 나타나는 효과용 값 (CSS의 --i)
    li.style.setProperty('--i', i);
    const a = document.createElement('a');
    a.className = 'text-item';
    // 클릭하면 이 글의 연습 페이지로 이동 (id를 주소로 전달)
    a.href = `/practice.html?id=${t.id}`;
    const name = document.createElement('span');
    name.className = 'name';
    // innerHTML 대신 textContent를 써서 제목에 HTML이 있어도 안전하게 표시
    name.textContent = t.title;
    const badge = document.createElement('span');
    badge.className = 'badge' + (custom ? ' custom' : '');
    badge.textContent = custom ? '내 글' : '기본';
    const arrow = document.createElement('span');
    arrow.className = 'arrow';
    arrow.textContent = '→';
    const foot = document.createElement('span');
    foot.className = 'foot';
    foot.append(badge, arrow);
    a.append(name, foot);
    li.appendChild(a);
    list.appendChild(li);
  });
}

// "추가하고 연습하기": 붙여넣은 글을 서버에 저장하고 바로 연습 페이지로 이동
document.getElementById('add-btn').addEventListener('click', async () => {
  const title = document.getElementById('title').value;
  const content = document.getElementById('content').value;
  if (!content.trim()) return alert('글을 붙여넣어 주세요');

  const res = await fetch('/api/texts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title, content })
  });
  const saved = await res.json();
  location.href = `/practice.html?id=${saved.id}`;
});

// 마우스를 따라다니는 빛 (카드/글 항목): 마우스 위치를 CSS 변수로 넘기면 CSS가 그 자리에 빛을 그림
document.addEventListener('pointermove', e => {
  const el = e.target.closest('.card, .text-item');
  if (!el) return;
  const r = el.getBoundingClientRect();
  el.style.setProperty('--mx', (e.clientX - r.left) + 'px');
  el.style.setProperty('--my', (e.clientY - r.top) + 'px');
});

// 히어로 문구 타자 효과: 문구를 한 글자씩 치고 지우며 순환 (함수를 만들자마자 바로 실행)
(function typeTagline() {
  const lines = ['한 글자씩, 더 빠르고 정확하게.', '붙여넣은 내 글이 곧 연습장.', '오늘도 타자가자!'];
  const el = document.getElementById('tagline');
  // li: 몇 번째 문구 / ci: 몇 글자까지 보여줄지 / del: 지우는 중인지
  let li = 0, ci = 0, del = false;
  function tick() {
    const s = lines[li];
    el.textContent = s.slice(0, ci);
    let wait = del ? 28 : 70;
    // 다 쳤으면 1.6초 멈춘 뒤 지우기 시작, 다 지웠으면 다음 문구로 넘어감
    if (!del && ci === s.length) { del = true; wait = 1600; }
    else if (del && ci === 0) { del = false; li = (li + 1) % lines.length; wait = 350; }
    else ci += del ? -1 : 1;
    setTimeout(tick, wait);
  }
  tick();
})();

loadTexts();
