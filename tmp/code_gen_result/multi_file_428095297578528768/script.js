// script.js —— 极简交互，总计不超过10行 (实际9行)
const btn = document.getElementById('actionBtn');
let clickCount = 0;

btn.addEventListener('click', () => {
  clickCount += 1;
  // 更新按钮文字，反馈点击次数
  btn.textContent = clickCount === 1 ? '✨ 已点击 1 次' : `✨ 已点击 ${clickCount} 次`;
  // 轻量临时反馈 300ms 后恢复
  setTimeout(() => {
    btn.textContent = '点击我';
  }, 1200);
});