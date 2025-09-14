export async function signup({ id, password, name, email }) {
    const r = await fetch('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ id, password, name, email })
    });
    if (!r.ok) throw new Error('signup failed');
    return r.json();
}

export async function login({ username, password }) {
    const params = new URLSearchParams({ username, password });
    const r = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        credentials: 'include',
        body: params
    });
    if (!r.ok) throw new Error('login failed');
    return r.json();
}

export async function logout() {
    const r = await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
    if (!r.ok) throw new Error('logout failed');
    return r.json();
}

export async function fetchMe() {
    const r = await fetch('/api/auth/me', { credentials: 'include' });
    if (r.status === 401) return { authenticated: false };
    if (!r.ok) throw new Error('me failed');
    return r.json();
}