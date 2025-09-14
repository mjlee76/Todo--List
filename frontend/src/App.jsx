import { useEffect, useState } from 'react';
import { fetchMe } from './api/auth';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import TodoPage from './page/TodoPage';
import LoginPage from './page/LoginPage';
import SignupPage from './page/SignupPage';

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage/>} />
                <Route path="/signup" element={<SignupPage/>} />
                <Route path="/" element={<Protected><TodoPage/></Protected>} />
                <Route path="*" element={<Navigate to="/" replace/>} />
            </Routes>
        </BrowserRouter>
    );
}

// 간단 보호 래퍼
function Protected({ children }) {
    const [state, setState] = useState({ loading: true, ok: false });
    useEffect(() => {
        (async () => {
            try {
                const me = await fetchMe();
                setState({ loading: false, ok: !!me.authenticated });
            } catch {
                setState({ loading: false, ok: false });
            }
        })();
    }, []);
    if (state.loading) return <div style={{padding:'2rem'}}>로딩 중...</div>;
    if (!state.ok) return <Navigate to="/login" replace/>;
    return children;
}
