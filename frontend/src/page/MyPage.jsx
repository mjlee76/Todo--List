import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
    User,
    ArrowLeft,
    Camera,
    Save,
    Eye,
    EyeOff,
    Mail,
    Lock,
    BarChart3,
    Edit3,
    Check,
    X,
    Calendar,
    Trophy,
    Target,
    TrendingUp
} from 'lucide-react'

export default function MyPage() {
    const navigate = useNavigate()
    const fileInputRef = useRef(null)

    // 탭 상태
    const [activeTab, setActiveTab] = useState('profile')

    // 사용자 정보 상태 (초기값, 실제로는 API에서 가져와야 함)
    const [userInfo, setUserInfo] = useState({
        id: '',
        name: '',
        email: '',
        profileImage: null,
        joinDate: '',
        totalTodos: 0,
        completedTodos: 0,
        todayTodos: 0,
        completedToday: 0
    })

    // 폼 상태들
    const [profileForm, setProfileForm] = useState({
        name: userInfo.name,
        email: userInfo.email
    })

    const [passwordForm, setPasswordForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    })

    const [showPasswords, setShowPasswords] = useState({
        current: false,
        new: false,
        confirm: false
    })

    const [isEditing, setIsEditing] = useState(false)
    const [loading, setLoading] = useState(false)
    const [message, setMessage] = useState('')

    const [meLoading, setMeLoading] = useState(true)

    // 현재 로그인 사용자 정보 조회
    const fetchMe = async () => {
        try {
            const res = await fetch('/api/me', {
                method: 'GET',
                credentials: 'include'
            })

            if (res.status === 401) {
                setMessage('로그인이 필요합니다. 로그인 페이지로 이동합니다.')
                navigate('/login')
                return
            }

            if (!res.ok) throw new Error('me 조회 실패')

            const data = await res.json()
            // 백엔드 MeResponseDto 필드에 맞춰 안전하게 매핑
            setUserInfo(prev => ({
                ...prev,
                id: data.id ?? data.username ?? '',
                name: data.name ?? '',
                email: data.email ?? '',
                joinDate: data.createdAt ? data.createdAt.split('T')[0] : '',
                totalTodos: data.totalTodos ?? 0,
                completedTodos: data.completedTodos ?? 0,
                todayTodos: data.todayTodos ?? 0,
                completedToday: data.completedToday ?? 0
            }))
        } catch (e) {
            setMessage('내 정보 조회에 실패했습니다.')
        } finally {
            setMeLoading(false)
        }
    }

    // 처음 마운트 시 내 정보 불러오기
    useEffect(() => {
        fetchMe()
    }, [])

    // userInfo 갱신 시 프로필 폼 초기화 동기화
    useEffect(() => {
        setProfileForm({ name: userInfo.name, email: userInfo.email })
    }, [userInfo.name, userInfo.email])

    // 프로필 이미지 업로드
    const handleImageUpload = (e) => {
        const file = e.target.files[0]
        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader()
            reader.onload = (e) => {
                setUserInfo(prev => ({
                    ...prev,
                    profileImage: e.target.result
                }))
            }
            reader.readAsDataURL(file)
        }
    }

    // 프로필 정보 저장
    const handleProfileSave = async () => {
        setLoading(true)
        try {
            // 변경된 필드만 전송 (백엔드 ProfilePatchRequest와 일치)
            const body = {}
            if (profileForm.name !== userInfo.name) body.name = profileForm.name
            if (profileForm.email !== userInfo.email) body.email = profileForm.email

            const res = await fetch('/api/me', {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(body)
            })

            if (res.status === 401) {
                setMessage('인증이 만료되었습니다. 다시 로그인 해주세요.')
                navigate('/login')
                return
            }

            if (!res.ok) throw new Error('프로필 업데이트 실패')

            const updated = await res.json()
            setUserInfo(prev => ({
                ...prev,
                name: updated.name ?? body.name ?? prev.name,
                email: updated.email ?? body.email ?? prev.email
            }))
            setIsEditing(false)
            setMessage('프로필이 성공적으로 업데이트되었습니다.')
        } catch (error) {
            setMessage('업데이트에 실패했습니다.')
        } finally {
            setLoading(false)
        }
    }

    // 비밀번호 변경
    const handlePasswordChange = async (e) => {
        e.preventDefault()
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            setMessage('새 비밀번호가 일치하지 않습니다.')
            return
        }

        setLoading(true)
        try {
            // 여기에 실제 API 호출 로직
            const body = {
                currentPassword: passwordForm.currentPassword,
                newPassword: passwordForm.newPassword,
                repeatPassword: passwordForm.confirmPassword
            }

            const headers = { 'Content-Type': 'application/json' }
            // CSRF를 사용 중이면 쿠키에서 토큰을 꺼내 헤더로 함께 보냅니다.
            // const xsrf = getCookie('XSRF-TOKEN') || getCookie('XSRF_TOKEN')
            // if (xsrf) headers['X-XSRF-TOKEN'] = xsrf

            const res = await fetch('/api/me', {
                method: 'POST',
                headers,
                credentials: 'include',   // 세션(JSESSIONID) 전송
                body: JSON.stringify(body)
            })

            if (res.status === 401) {
                setMessage('세션이 만료되었습니다. 다시 로그인 해주세요.')
                navigate('/login', { replace: true })
                return
            }

            const data = await res.json()

            if (!res.ok || data.success !== true) {
                setMessage(data.message || '비밀번호 변경에 실패했습니다.')
                return
            }

            // 서버가 relogin: true를 주면 즉시 로그인 페이지로 보냅니다.
            if (data.relogin) {
                setMessage('비밀번호가 변경되어 다시 로그인합니다.')
                navigate('/login', { replace: true })
                return
            }

            setPasswordForm({
                currentPassword: '',
                newPassword: '',
                confirmPassword: ''
            })
            setMessage('비밀번호가 성공적으로 변경되었습니다.')
        } catch (error) {
            setMessage('비밀번호 변경에 실패했습니다.')
        } finally {
            setLoading(false)
        }
    }

    const tabs = [
        { id: 'profile', name: '내 정보', icon: User },
        { id: 'edit', name: '정보 수정', icon: Edit3 },
        { id: 'password', name: '비밀번호 변경', icon: Lock },
        { id: 'stats', name: '통계', icon: BarChart3 }
    ]

    const completionRate = userInfo.totalTodos > 0 ? Math.round((userInfo.completedTodos / userInfo.totalTodos) * 100) : 0
    const todayCompletionRate = userInfo.todayTodos > 0 ? Math.round((userInfo.completedToday / userInfo.todayTodos) * 100) : 0

    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-600 via-purple-600 to-pink-500">
            {/* 배경 효과 */}
            <div className="absolute inset-0 overflow-hidden pointer-events-none">
                <div className="absolute -top-40 -right-40 w-80 h-80 bg-white/10 rounded-full blur-3xl animate-pulse" />
                <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-white/10 rounded-full blur-3xl animate-pulse delay-1000" />
            </div>

            {/* 헤더 */}
            <header className="relative z-10 bg-white/10 backdrop-blur-2xl border-b border-white/20">
                <div className="max-w-6xl mx-auto px-6 py-4 flex items-center text-white">
                    <button
                        onClick={() => navigate('/todo')}
                        className="flex items-center gap-2 px-4 py-2 bg-white/20 hover:bg-white/30 rounded-2xl border border-white/20 transition mr-6"
                    >
                        <ArrowLeft className="w-5 h-5" />
                        돌아가기
                    </button>
                    <div className="flex items-center gap-3">
                        <div className="p-2 bg-white/20 rounded-xl">
                            <User className="w-7 h-7" />
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold">마이페이지</h1>
                            <p className="text-white/70 text-sm">내 정보를 관리하세요</p>
                        </div>
                    </div>
                </div>
            </header>

            <main className="relative z-10 max-w-6xl mx-auto px-6 py-8">
                <div className="flex gap-8 items-start">
                    {/* 왼쪽 사이드바 */}
                    <aside className="bg-white/90 text-gray-800 backdrop-blur-xl rounded-3xl p-6 shadow-2xl border border-white/30 sticky top-20 w-80">
                        {/* 프로필 이미지 */}
                        <div className="text-center mb-6">
                            <div className="relative inline-block">
                                <div className="w-24 h-24 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4 overflow-hidden">
                                    {userInfo.profileImage ? (
                                        <img
                                            src={userInfo.profileImage}
                                            alt="프로필"
                                            className="w-full h-full object-cover"
                                        />
                                    ) : (
                                        userInfo.name.charAt(0)
                                    )}
                                </div>
                                <button
                                    onClick={() => fileInputRef.current?.click()}
                                    className="absolute -bottom-1 -right-1 p-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 transition"
                                >
                                    <Camera className="w-4 h-4" />
                                </button>
                                <input
                                    ref={fileInputRef}
                                    type="file"
                                    accept="image/*"
                                    onChange={handleImageUpload}
                                    className="hidden"
                                />
                            </div>
                            <h3 className="text-xl font-bold text-gray-800">{userInfo.name}</h3>
                            <p className="text-gray-600">{userInfo.email}</p>
                        </div>

                        {/* 탭 메뉴 */}
                        <nav className="space-y-2">
                            {tabs.map(tab => {
                                const Icon = tab.icon
                                return (
                                    <button
                                        key={tab.id}
                                        onClick={() => setActiveTab(tab.id)}
                                        className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition ${
                                            activeTab === tab.id
                                                ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white shadow-lg'
                                                : 'text-gray-600 hover:bg-gray-100'
                                        }`}
                                    >
                                        <Icon className="w-5 h-5" />
                                        {tab.name}
                                    </button>
                                )
                            })}
                        </nav>
                    </aside>

                    {/* 메인 콘텐츠 */}
                    <section className="flex-1">
                        <div className="bg-white/95 text-gray-800 backdrop-blur-2xl rounded-3xl shadow-2xl border border-white/30 overflow-hidden">
                            {message && (
                                <div className="bg-blue-50 border-b border-blue-200 p-4">
                                    <p className="text-blue-800 text-center">{message}</p>
                                </div>
                            )}

                            {/* 내 정보 탭 */}
                            {activeTab === 'profile' && (
                                <div className="p-8">
                                    <div className="bg-gradient-to-r from-blue-500 to-purple-600 rounded-2xl p-6 text-white mb-8">
                                        <h2 className="text-2xl font-bold mb-4">내 정보</h2>
                                        <div className="grid grid-cols-2 gap-6">
                                            <div>
                                                <p className="text-white/80 mb-1">사용자 ID</p>
                                                <p className="text-xl font-semibold">{meLoading ? '로딩중...' : (userInfo.id || '-')}</p>
                                            </div>
                                            <div>
                                                <p className="text-white/80 mb-1">가입일</p>
                                                <p className="text-xl font-semibold">{meLoading ? '로딩중...' : (userInfo.joinDate || '-')}</p>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-2 gap-6">
                                        <div className="bg-gray-50 rounded-2xl p-6">
                                            <div className="flex items-center gap-3 mb-3">
                                                <Mail className="w-6 h-6 text-blue-600" />
                                                <h3 className="text-lg font-semibold">이메일</h3>
                                            </div>
                                            <p className="text-2xl font-bold text-gray-800">{meLoading ? '로딩중...' : (userInfo.email || '-')}</p>
                                        </div>
                                        <div className="bg-gray-50 rounded-2xl p-6">
                                            <div className="flex items-center gap-3 mb-3">
                                                <User className="w-6 h-6 text-purple-600" />
                                                <h3 className="text-lg font-semibold">이름</h3>
                                            </div>
                                            <p className="text-2xl font-bold text-gray-800">{meLoading ? '로딩중...' : (userInfo.name || '-')}</p>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* 정보 수정 탭 */}
                            {activeTab === 'edit' && (
                                <div className="p-8">
                                    <div className="bg-gradient-to-r from-green-500 to-blue-500 rounded-2xl p-6 text-white mb-8">
                                        <h2 className="text-2xl font-bold">정보 수정</h2>
                                        <p className="text-white/80">이름과 이메일을 변경할 수 있습니다</p>
                                    </div>

                                    <div className="space-y-6">
                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-2">이름</label>
                                            <div className="relative">
                                                <User className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                                                <input
                                                    type="text"
                                                    value={profileForm.name}
                                                    onChange={(e) => setProfileForm(prev => ({...prev, name: e.target.value}))}
                                                    disabled={!isEditing}
                                                    className={`w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:outline-none transition ${
                                                        isEditing
                                                            ? 'border-gray-200 focus:border-blue-500 bg-white'
                                                            : 'border-gray-100 bg-gray-50 text-gray-500'
                                                    }`}
                                                />
                                            </div>
                                        </div>

                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-2">이메일</label>
                                            <div className="relative">
                                                <Mail className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                                                <input
                                                    type="email"
                                                    value={profileForm.email}
                                                    onChange={(e) => setProfileForm(prev => ({...prev, email: e.target.value}))}
                                                    disabled={!isEditing}
                                                    className={`w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:outline-none transition ${
                                                        isEditing
                                                            ? 'border-gray-200 focus:border-blue-500 bg-white'
                                                            : 'border-gray-100 bg-gray-50 text-gray-500'
                                                    }`}
                                                />
                                            </div>
                                        </div>

                                        <div className="flex gap-4">
                                            {!isEditing ? (
                                                <button
                                                    onClick={() => setIsEditing(true)}
                                                    className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-xl font-medium hover:shadow-lg transition"
                                                >
                                                    <Edit3 className="w-5 h-5" />
                                                    수정하기
                                                </button>
                                            ) : (
                                                <>
                                                    <button
                                                        onClick={handleProfileSave}
                                                        disabled={loading}
                                                        className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-green-500 to-blue-500 text-white rounded-xl font-medium hover:shadow-lg transition disabled:opacity-50"
                                                    >
                                                        {loading ? (
                                                            <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                                                        ) : (
                                                            <Save className="w-5 h-5" />
                                                        )}
                                                        저장하기
                                                    </button>
                                                    <button
                                                        onClick={() => {
                                                            setIsEditing(false)
                                                            setProfileForm({
                                                                name: userInfo.name,
                                                                email: userInfo.email
                                                            })
                                                        }}
                                                        className="flex items-center gap-2 px-6 py-3 bg-gray-500 text-white rounded-xl font-medium hover:bg-gray-600 transition"
                                                    >
                                                        <X className="w-5 h-5" />
                                                        취소
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* 비밀번호 변경 탭 */}
                            {activeTab === 'password' && (
                                <div className="p-8">
                                    <div className="bg-gradient-to-r from-red-500 to-pink-500 rounded-2xl p-6 text-white mb-8">
                                        <h2 className="text-2xl font-bold">비밀번호 변경</h2>
                                        <p className="text-white/80">보안을 위해 정기적으로 비밀번호를 변경하세요</p>
                                    </div>

                                    <form onSubmit={handlePasswordChange} className="space-y-6">
                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-2">현재 비밀번호</label>
                                            <div className="relative">
                                                <Lock className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                                                <input
                                                    type={showPasswords.current ? "text" : "password"}
                                                    value={passwordForm.currentPassword}
                                                    onChange={(e) => setPasswordForm(prev => ({...prev, currentPassword: e.target.value}))}
                                                    className="w-full pl-12 pr-12 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-blue-500 transition"
                                                    required
                                                />
                                                <button
                                                    type="button"
                                                    onClick={() => setShowPasswords(prev => ({...prev, current: !prev.current}))}
                                                    className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                                >
                                                    {showPasswords.current ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                                </button>
                                            </div>
                                        </div>

                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-2">새 비밀번호</label>
                                            <div className="relative">
                                                <Lock className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                                                <input
                                                    type={showPasswords.new ? "text" : "password"}
                                                    value={passwordForm.newPassword}
                                                    onChange={(e) => setPasswordForm(prev => ({...prev, newPassword: e.target.value}))}
                                                    className="w-full pl-12 pr-12 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-blue-500 transition"
                                                    required
                                                />
                                                <button
                                                    type="button"
                                                    onClick={() => setShowPasswords(prev => ({...prev, new: !prev.new}))}
                                                    className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                                >
                                                    {showPasswords.new ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                                </button>
                                            </div>
                                        </div>

                                        <div>
                                            <label className="block text-sm font-medium text-gray-700 mb-2">새 비밀번호 확인</label>
                                            <div className="relative">
                                                <Lock className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                                                <input
                                                    type={showPasswords.confirm ? "text" : "password"}
                                                    value={passwordForm.confirmPassword}
                                                    onChange={(e) => setPasswordForm(prev => ({...prev, confirmPassword: e.target.value}))}
                                                    className="w-full pl-12 pr-12 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-blue-500 transition"
                                                    required
                                                />
                                                <button
                                                    type="button"
                                                    onClick={() => setShowPasswords(prev => ({...prev, confirm: !prev.confirm}))}
                                                    className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                                >
                                                    {showPasswords.confirm ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                                </button>
                                            </div>
                                        </div>

                                        <button
                                            type="submit"
                                            disabled={loading}
                                            className="w-full py-3 bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-xl font-medium hover:shadow-lg transition disabled:opacity-50 flex items-center justify-center gap-2"
                                        >
                                            {loading ? (
                                                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                                            ) : (
                                                <Lock className="w-5 h-5" />
                                            )}
                                            비밀번호 변경
                                        </button>
                                    </form>
                                </div>
                            )}

                            {/* 통계 탭 */}
                            {activeTab === 'stats' && (
                                <div className="p-8">
                                    <div className="bg-gradient-to-r from-purple-500 to-indigo-500 rounded-2xl p-6 text-white mb-8">
                                        <h2 className="text-2xl font-bold">내 통계</h2>
                                        <p className="text-white/80">할 일 완수율과 활동 내역을 확인하세요</p>
                                    </div>

                                    {/* 전체 통계 */}
                                    <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mb-8">
                                        <div className="bg-blue-50 rounded-2xl p-6 text-center">
                                            <Target className="w-8 h-8 text-blue-600 mx-auto mb-3" />
                                            <div className="text-2xl font-bold text-blue-800">{userInfo.totalTodos}</div>
                                            <div className="text-blue-600 text-sm">전체 할 일</div>
                                        </div>
                                        <div className="bg-green-50 rounded-2xl p-6 text-center">
                                            <Check className="w-8 h-8 text-green-600 mx-auto mb-3" />
                                            <div className="text-2xl font-bold text-green-800">{userInfo.completedTodos}</div>
                                            <div className="text-green-600 text-sm">완료한 일</div>
                                        </div>
                                        <div className="bg-purple-50 rounded-2xl p-6 text-center">
                                            <Trophy className="w-8 h-8 text-purple-600 mx-auto mb-3" />
                                            <div className="text-2xl font-bold text-purple-800">{completionRate}%</div>
                                            <div className="text-purple-600 text-sm">완수율</div>
                                        </div>
                                        <div className="bg-orange-50 rounded-2xl p-6 text-center">
                                            <Calendar className="w-8 h-8 text-orange-600 mx-auto mb-3" />
                                            <div className="text-2xl font-bold text-orange-800">{userInfo.todayTodos}</div>
                                            <div className="text-orange-600 text-sm">오늘 할 일</div>
                                        </div>
                                    </div>

                                    {/* 진행률 바 */}
                                    <div className="bg-gray-50 rounded-2xl p-6 mb-6">
                                        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
                                            <TrendingUp className="w-5 h-5 text-blue-600" />
                                            전체 진행률
                                        </h3>
                                        <div className="w-full bg-gray-200 rounded-full h-4 mb-2">
                                            <div
                                                className="bg-gradient-to-r from-blue-500 to-purple-600 h-4 rounded-full transition-all duration-1000"
                                                style={{ width: `${completionRate}%` }}
                                            ></div>
                                        </div>
                                        <div className="flex justify-between text-sm text-gray-600">
                                            <span>완료: {userInfo.completedTodos}</span>
                                            <span>{completionRate}%</span>
                                            <span>전체: {userInfo.totalTodos}</span>
                                        </div>
                                    </div>

                                    {/* 오늘 진행률 */}
                                    <div className="bg-gray-50 rounded-2xl p-6">
                                        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
                                            <Calendar className="w-5 h-5 text-green-600" />
                                            오늘 진행률
                                        </h3>
                                        <div className="w-full bg-gray-200 rounded-full h-4 mb-2">
                                            <div
                                                className="bg-gradient-to-r from-green-500 to-emerald-600 h-4 rounded-full transition-all duration-1000"
                                                style={{ width: `${todayCompletionRate}%` }}
                                            ></div>
                                        </div>
                                        <div className="flex justify-between text-sm text-gray-600">
                                            <span>완료: {userInfo.completedToday}</span>
                                            <span>{todayCompletionRate}%</span>
                                            <span>전체: {userInfo.todayTodos}</span>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </section>
                </div>
            </main>
        </div>
    )
}