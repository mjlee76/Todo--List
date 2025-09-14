// 할 일 목록 조회
export async function fetchTodos(date) {
    const res = await fetch(`/api/todos?date=${date}`, {
        credentials: 'include', // 세션 쿠키(JSESSIONID) 포함
    })
    if (!res.ok) throw new Error(`fetch failed: ${res.status}`)
    return res.json()
}

// 할 일 추가
export async function addTodo(task, date) {
    const res = await fetch('/api/todos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ task, toDoDate: date })
    })
    if (!res.ok) throw new Error(`add failed: ${res.status}`)
    return res.json()
}

// 완료 상태 토글
export async function toggleTodo(id) {
    const res = await fetch(`/api/todos/${id}/toggle`, {
        method: 'PATCH',
        credentials: 'include',
    })
    if (!res.ok) throw new Error(`toggle failed: ${res.status}`)
}

// 삭제
export async function deleteTodo(id) {
    const res = await fetch(`/api/todos/${id}`, {
        method: 'DELETE',
        credentials: 'include',
    })
    if (!res.ok) throw new Error(`delete failed: ${res.status}`)
}