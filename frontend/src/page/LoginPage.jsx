import { useState } from 'react';
import { login, fetchMe } from '../api/auth';
import { useNavigate, Link } from 'react-router-dom';
import { LogIn, User, Lock } from 'lucide-react';

export default function LoginPage() {
    const [username, setU] = useState('');
    const [password, setP] = useState('');
    const [error, setError] = useState('');
    const nav = useNavigate();

    const onSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await login({ username, password });
            const me = await fetchMe();
            if (me.authenticated) nav('/', { replace: true });
            else setError('로그인 상태 확인 실패');
        } catch {
            setError('아이디/비밀번호를 확인하세요.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-500 to-pink-500">
            <div className="bg-white/10 backdrop-blur-md p-8 rounded-2xl shadow-lg w-full max-w-md">
                {/* 헤더 */}
                <div className="flex items-center justify-center mb-6">
                    <LogIn className="w-8 h-8 text-white mr-2" />
                    <h2 className="text-2xl font-bold text-white">로그인</h2>
                </div>

                {/* 로그인 폼 */}
                <form onSubmit={onSubmit} className="space-y-4">
                    <div className="flex items-center bg-white/20 rounded-xl px-3 py-2">
                        <User className="w-5 h-5 text-white/80 mr-2" />
                        <input
                            type="text"
                            placeholder="아이디"
                            value={username}
                            onChange={(e) => setU(e.target.value)}
                            className="bg-transparent flex-1 text-white placeholder-white/70 focus:outline-none"
                        />
                    </div>

                    <div className="flex items-center bg-white/20 rounded-xl px-3 py-2">
                        <Lock className="w-5 h-5 text-white/80 mr-2" />
                        <input
                            type="password"
                            placeholder="비밀번호"
                            value={password}
                            onChange={(e) => setP(e.target.value)}
                            className="bg-transparent flex-1 text-white placeholder-white/70 focus:outline-none"
                        />
                    </div>

                    {error && (
                        <div className="text-pink-200 text-sm text-center">{error}</div>
                    )}

                    <button
                        type="submit"
                        className="w-full bg-gradient-to-r from-indigo-500 to-pink-500 text-white py-2 rounded-xl font-semibold shadow-md hover:opacity-90 transition"
                    >
                        로그인
                    </button>
                </form>

                {/* 회원가입 링크 */}
                <div className="mt-6 text-center">
                    <span className="text-white/80">계정이 없으신가요? </span>
                    <Link to="/signup" className="text-pink-200 font-semibold hover:underline">
                        회원가입
                    </Link>
                </div>
            </div>
        </div>
    );
}