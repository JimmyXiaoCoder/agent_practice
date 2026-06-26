// ===== script.js =====
// 待办事项应用 - 原生 JavaScript 实现

document.addEventListener('DOMContentLoaded', () => {
  // ---------- DOM 元素 ----------
  const todoForm = document.getElementById('todo-form');
  const todoInput = document.getElementById('todo-input');
  const todoList = document.getElementById('todo-list');
  const taskCountSpan = document.querySelector('#task-count strong');
  const completedCountSpan = document.querySelector('#completed-count strong');
  const filterButtons = document.querySelectorAll('.filter-btn');
  const clearCompletedBtn = document.getElementById('clear-completed');
  const emptyMessage = document.querySelector('.empty-message');

  // ---------- 状态 ----------
  let tasks = [];                    // 主数据 [{ id, text, completed }]
  let currentFilter = 'all';        // 'all', 'active', 'completed'
  let nextId = Date.now();          // 简易ID生成

  // ---------- 初始化 ----------
  // 插入一些示例任务
  tasks = [
    { id: nextId++, text: '探索 Todo List 功能', completed: false },
    { id: nextId++, text: '双击编辑任务内容', completed: false },
    { id: nextId++, text: '尝试切换过滤视图', completed: true },
  ];
  render();

  // ---------- 核心函数 ----------

  // 渲染任务列表 (基于当前过滤)
  function render() {
    // 过滤
    let filteredTasks = tasks;
    if (currentFilter === 'active') {
      filteredTasks = tasks.filter(task => !task.completed);
    } else if (currentFilter === 'completed') {
      filteredTasks = tasks.filter(task => task.completed);
    }

    // 清空列表 (保留空状态占位)
    todoList.innerHTML = '';

    if (filteredTasks.length === 0) {
      // 显示空状态
      const li = document.createElement('li');
      li.className = 'empty-message';
      li.textContent = currentFilter === 'all' ? '还没有任务，添加一个吧 ✨' :
                       currentFilter === 'active' ? '没有未完成的任务 🎉' : '还没有已完成的任务 💪';
      todoList.appendChild(li);
    } else {
      // 渲染每个任务
      filteredTasks.forEach(task => {
        const li = document.createElement('li');
        li.className = 'todo-item' + (task.completed ? ' completed' : '');
        li.dataset.id = task.id;

        // 复选框
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'checkbox';
        checkbox.checked = task.completed;
        checkbox.setAttribute('aria-label', `标记任务 "${task.text}" 为${task.completed ? '未完成' : '已完成'}`);
        // 切换完成状态
        checkbox.addEventListener('change', () => {
          toggleTaskCompletion(task.id);
        });

        // 任务文本 (用于显示)
        const textSpan = document.createElement('span');
        textSpan.className = 'todo-text';
        textSpan.textContent = task.text;
        // 单击切换完成 (便捷)
        textSpan.addEventListener('click', () => {
          toggleTaskCompletion(task.id);
        });
        // 双击进入编辑模式
        textSpan.addEventListener('dblclick', () => {
          enableEditMode(li, task);
        });

        // 编辑输入框 (隐藏)
        const editInput = document.createElement('input');
        editInput.type = 'text';
        editInput.className = 'edit-input';
        editInput.value = task.text;
        editInput.setAttribute('aria-label', '编辑任务内容');
        // 失焦或回车保存编辑
        editInput.addEventListener('blur', () => {
          saveEdit(li, task, editInput);
        });
        editInput.addEventListener('keydown', (e) => {
          if (e.key === 'Enter') {
            e.preventDefault();
            editInput.blur();  // 触发blur保存
          } else if (e.key === 'Escape') {
            // 取消编辑
            cancelEdit(li);
          }
        });

        // 删除按钮
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'delete-btn';
        deleteBtn.textContent = '✕';
        deleteBtn.setAttribute('aria-label', `删除任务 "${task.text}"`);
        deleteBtn.addEventListener('click', () => {
          deleteTask(task.id);
        });

        // 组装
        li.appendChild(checkbox);
        li.appendChild(textSpan);
        li.appendChild(editInput);
        li.appendChild(deleteBtn);
        todoList.appendChild(li);
      });
    }

    // 更新统计
    updateStats();

    // 更新清除按钮状态
    updateClearButton();

    // 更新过滤按钮高亮 (确保当前过滤器active)
    filterButtons.forEach(btn => {
      btn.classList.toggle('active', btn.dataset.filter === currentFilter);
    });
  }

  // 更新统计数字
  function updateStats() {
    const total = tasks.length;
    const completed = tasks.filter(t => t.completed).length;
    taskCountSpan.textContent = total;
    completedCountSpan.textContent = completed;
  }

  // 更新清除已完成按钮
  function updateClearButton() {
    const hasCompleted = tasks.some(t => t.completed);
    clearCompletedBtn.disabled = !hasCompleted;
  }

  // 切换任务完成状态
  function toggleTaskCompletion(id) {
    const task = tasks.find(t => t.id === id);
    if (task) {
      task.completed = !task.completed;
      render();
    }
  }

  // 删除任务
  function deleteTask(id) {
    tasks = tasks.filter(t => t.id !== id);
    render();
  }

  // 启用编辑模式
  function enableEditMode(li, task) {
    // 如果已经在编辑则忽略
    if (li.classList.contains('editing')) return;
    
    // 关闭其他编辑
    document.querySelectorAll('.todo-item.editing').forEach(item => {
      cancelEdit(item);
    });

    li.classList.add('editing');
    const editInput = li.querySelector('.edit-input');
    if (editInput) {
      editInput.value = task.text;
      editInput.focus();
      editInput.select();
    }
  }

  // 保存编辑
  function saveEdit(li, task, editInput) {
    if (!li.classList.contains('editing')) return;
    const newText = editInput.value.trim();
    if (newText === '') {
      // 如果为空则取消编辑
      cancelEdit(li);
      return;
    }
    // 更新数据
    task.text = newText;
    li.classList.remove('editing');
    // 更新文本显示
    const textSpan = li.querySelector('.todo-text');
    if (textSpan) textSpan.textContent = newText;
    // 可选：重新渲染整个列表以确保一致性 (但略显浪费，直接更新UI更快)
    // 但我们为了确保过滤等一致，调用render
    render();
  }

  // 取消编辑
  function cancelEdit(li) {
    li.classList.remove('editing');
    // 复原输入框内容为当前任务文本 (可选)
    const taskId = li.dataset.id ? Number(li.dataset.id) : null;
    if (taskId) {
      const task = tasks.find(t => t.id === taskId);
      const editInput = li.querySelector('.edit-input');
      if (task && editInput) {
        editInput.value = task.text;
      }
    }
  }

  // 添加新任务
  function addTask(text) {
    const trimmed = text.trim();
    if (trimmed === '') return false;
    
    const newTask = {
      id: nextId++,
      text: trimmed,
      completed: false
    };
    tasks.push(newTask);
    render();
    return true;
  }

  // 清除已完成
  function clearCompleted() {
    tasks = tasks.filter(t => !t.completed);
    render();
  }

  // ---------- 事件监听 ----------

  // 表单提交
  todoForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const inputValue = todoInput.value;
    if (addTask(inputValue)) {
      todoInput.value = '';     // 清空输入
      todoInput.focus();
    } else {
      // 提示有效性
      todoInput.placeholder = '内容不能为空...';
      setTimeout(() => {
        todoInput.placeholder = '输入新任务...';
      }, 1500);
    }
  });

  // 过滤按钮点击
  filterButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const filter = btn.dataset.filter;
      if (filter) {
        currentFilter = filter;
        render();
      }
    });
  });

  // 清除已完成
  clearCompletedBtn.addEventListener('click', clearCompleted);

  // 点击页面空白处取消编辑 (辅助)
  document.addEventListener('click', (e) => {
    if (!e.target.closest('.todo-item.editing')) {
      document.querySelectorAll('.todo-item.editing').forEach(li => {
        cancelEdit(li);
      });
    }
  });

  // 额外：键盘支持 (Escape 取消已有编辑)
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      document.querySelectorAll('.todo-item.editing').forEach(li => {
        cancelEdit(li);
      });
    }
  });
});