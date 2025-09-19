// src/api/myPage.js

// 프로필 조회
export async function getMe() {
    const res = await fetch('/api/me', {
        credentials: 'include',
    });
    return await res.json();
}

// 이름/이메일 수정 (둘 중 필요한 값만 전달)
export async function patchProfile({ name, email }) {
    const payload = {};
    if (name) payload.name = name;
    if (email) payload.email = email;

    const res = await fetch('/api/me', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(payload),
    });
    return await res.json();
}

// 비밀번호 변경
export async function changePassword({ currentPassword, newPassword, confirmNewPassword }) {
    const payload = { currentPassword, newPassword, confirmNewPassword };
    const res = await fetch('/api/me/password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(payload),
    });
    return await res.json();
}