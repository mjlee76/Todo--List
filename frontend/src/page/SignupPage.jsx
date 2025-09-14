// src/pages/SignupPage.jsx
import { useState } from 'react';
import { signup } from '../api/auth';
import { useNavigate, Link } from 'react-router-dom';
import { UserPlus, User, AtSign, KeyRound, IdCard } from 'lucide-react';

export default function SignupPage() {
    const [form, setForm] = useState({ id: '', password: '', name: '', email: '' });
    const [msg, setMsg] = useState('');
    const nav = useNavigate();

    const onChange = (k) => (e) => setForm((p) => ({ ...p, [k]: e.target.value }));

    const onSubmit = async (e) => {
        e.preventDefault();
        setMsg('');
        try {
            await signup(form);
            setMsg('가입 완료! 로그인 해주세요.');
            setTimeout(() => nav('/login', { replace: true }), 700);
        } catch (e) {
            const m = e?.message || '가입 실패';
            if (m.includes('이미 존재하는 ID입니다.')) setMsg('가입 실패: 중복 아이디입니다.');
            else setMsg('가입 실패: 입력 오류');
        }
    };

    const isSuccess = msg.startsWith('가입 완료');

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-500 to-pink-500">
            <div className="bg-white/10 backdrop-blur-md p-8 rounded-2xl shadow-lg w-full max-w-md">
                {/* 헤더 */}
                <div className="flex items-center justify-center mb-6">
                    <UserPlus className="w-8 h-8 text-white mr-2" />
                    <h2 className="text-2xl font-bold text-white">회원가입</h2>
                </div>

                {/* 폼 */}
                <form onSubmit={onSubmit} className="space-y-4">
                    <div className="flex items-center bg-white/20 rounded-xl px-3 py-2">
                        <IdCard className="w-5 h-5 text-white/80 mr-2" />
                        <input
                            aria-label="아이디"
                            placeholder="아이디"
                            value={form.id}
                            onChange={onChange('id')}
                            className="bg-transparent flex-1 text-white placeholder-white/70 focus:outline-none"
                        />
                    </div>

                    <div className="flex items-center bg-white/20 rounded-xl px-3 py-2">
                        <User className="w-5 h-5 text-white/80 mr-2" />
                        <input
                            aria-label="이름"
                            placeholder="이름"
                            value={form.name}
                            onChange={onChange('name')}
                            className="bg-transparent flex-1 text-white placeholder-white/70 focus:outline-none"
                        />
                    </div>

                    <div className="flex items-center bg-white/20 rounded-xl px-3 py-2">
                        <AtSign className="w-5 h-5 text-white/80 mr-2" />
                        <input
                            aria-label="이메일"
                            placeholder="이메일"
                            value={form.email}
                            onChange={onChange('email')}
                            className="bg-transparent flex-1 text-white placeholder-white/70 focus:outline-none"
                        />
                    </div>

                    <div className="flex items-center bg-white/20 rounded-xl px-3 py-2">
                        <KeyRound className="w-5 h-5 text-white/80 mr-2" />
                        <input
                            type="password"
                            aria-label="비밀번호"
                            placeholder="비밀번호"
                            value={form.password}
                            onChange={onChange('password')}
                            className="bg-transparent flex-1 text-white placeholder-white/70 focus:outline-none"
                        />
                    </div>

                    {msg && (
                        <div
                            className={`text-sm text-center ${
                                isSuccess ? 'text-emerald-200' : 'text-pink-200'
                            }`}
                        >
                            {msg}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="w-full bg-gradient-to-r from-indigo-500 to-pink-500 text-white py-2 rounded-xl font-semibold shadow-md hover:opacity-90 transition"
                    >
                        가입
                    </button>
                </form>

                {/* 이동 링크 */}
                <div className="mt-6 text-center">
                    <span className="text-white/80">이미 계정이 있으신가요? </span>
                    <Link to="/login" className="text-pink-200 font-semibold hover:underline">
                        로그인으로
                    </Link>
                </div>
            </div>
        </div>
    );
}