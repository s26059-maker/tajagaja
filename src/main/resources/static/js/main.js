async function loadTexts() {
  const res = await fetch('/api/texts');
  const texts = await res.json();
  const list = document.getElementById('text-list');
  list.innerHTML = '';
  if (!texts.length) list.innerHTML = '<li class="empty">아직 글이 없어요. 아래에서 추가해보세요!</li>';
  texts.forEach((t, i) => {
    const custom = t.category === 'CUSTOM';
    const li = document.createElement('li');
    li.style.setProperty('--i', i);
    const a = document.createElement('a');
    a.className = 'text-item';
    a.href = `/practice.html?id=${t.id}`;
    const name = document.createElement('span');
    name.className = 'name';
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
    if (custom) {
      const del = document.createElement('button');
      del.type = 'button';
      del.className = 'del-btn';
      del.title = '삭제';
      del.textContent = '✕';
      del.addEventListener('click', async () => {
        if (!confirm(`"${t.title}" 글을 삭제할까요?`)) return;
        const r = await fetch(`/api/texts/${t.id}`, { method: 'DELETE' });
        if (!r.ok && r.status !== 404) return alert('삭제하지 못했어요');
        loadTexts();
      });
      li.appendChild(del);
    }
    list.appendChild(li);
  });
}

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

document.addEventListener('pointermove', e => {
  const el = e.target.closest('.card, .text-item');
  if (!el) return;
  const r = el.getBoundingClientRect();
  el.style.setProperty('--mx', (e.clientX - r.left) + 'px');
  el.style.setProperty('--my', (e.clientY - r.top) + 'px');
});

(function typeTagline() {
  const lines = ['한 글자씩, 더 빠르고 정확하게.', '붙여넣은 내 글이 곧 연습장.', '오늘도 타자가자!'];
  const el = document.getElementById('tagline');
  let li = 0, ci = 0, del = false;
  function tick() {
    const s = lines[li];
    el.textContent = s.slice(0, ci);
    let wait = del ? 28 : 70;
    if (!del && ci === s.length) { del = true; wait = 1600; }
    else if (del && ci === 0) { del = false; li = (li + 1) % lines.length; wait = 350; }
    else ci += del ? -1 : 1;
    setTimeout(tick, wait);
  }
  tick();
})();

loadTexts();
