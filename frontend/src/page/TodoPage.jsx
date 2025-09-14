import { useEffect, useRef, useState } from 'react'
import { fetchTodos, addTodo, toggleTodo, deleteTodo } from '../api/todo'
import FullCalendar from '@fullcalendar/react'
import dayGridPlugin from '@fullcalendar/daygrid'
import interactionPlugin from '@fullcalendar/interaction'
import koLocale from '@fullcalendar/core/locales/ko'
import {
    Calendar as CalendarIcon,
    CalendarDays,
    Plus,
    Trash2,
    Check,
    Circle,
    LogOut,
    ListTodo,
    Clock,
    ChevronLeft,
    ChevronRight
} from 'lucide-react'

function todayLocal() {
    const d = new Date()
    const yyyy = d.getFullYear()
    const mm = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd}`
}

export default function TodoPage() {
    // === 기존 상태/로직 그대로 유지 ===
    const [date, setDate] = useState(todayLocal())
    const [todos, setTodos] = useState([])
    const [text, setText] = useState('')
    const [filter, setFilter] = useState('all')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const load = async () => {
        try {
            setLoading(true)
            setError('')
            const data = await fetchTodos(date)
            setTodos(data)
        } catch (e) {
            setError('목록을 불러오지 못했습니다.')
            console.error(e)
        } finally {
            setLoading(false)
        }
    }
    useEffect(() => { load() }, [date])

    const onAdd = async (e) => {
        e.preventDefault()
        if (!text.trim()) return
        try {
            await addTodo(text.trim(), date)
            setText('')
            await load()
        } catch (e) {
            setError('추가에 실패했습니다.')
            console.error(e)
        }
    }
    const onToggle = async (id) => {
        try { await toggleTodo(id); await load() }
        catch (e) { setError('상태 변경에 실패했습니다.'); console.error(e) }
    }
    const onDelete = async (id) => {
        try { await deleteTodo(id); await load() }
        catch (e) { setError('삭제에 실패했습니다.'); console.error(e) }
    }

    const stats = {
        total: todos.length,
        completed: todos.filter(t => t.completed).length,
        remaining: todos.filter(t => !t.completed).length
    }
    const filtered = todos.filter(t => {
        if (filter === 'active') return !t.completed
        if (filter === 'completed') return t.completed
        return true
    })

    // ----- 참고 디자인 요소: 커스텀 달력 헤더 -----
    const calRef = useRef(null)
    const [monthLabel, setMonthLabel] = useState('')
    const formatMonth = (d) => `${d.getFullYear()}년 ${d.getMonth() + 1}월`
    const onDatesSet = (arg) => setMonthLabel(formatMonth(arg.start))
    const goToday = () => { calRef.current?.getApi().today(); setDate(todayLocal()); setMonthLabel(formatMonth(new Date())) }

    const filterButtons = [
        { key: 'all', label: '전체', icon: ListTodo, color: 'from-gray-500 to-gray-600' },
        { key: 'active', label: '진행중', icon: Clock, color: 'from-orange-500 to-red-500' },
        { key: 'completed', label: '완료', icon: Check, color: 'from-green-500 to-emerald-500' }
    ]

    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-600 via-purple-600 to-pink-500">
            {/* 부드러운 글래스 효과 배경 */}
            <div className="absolute inset-0 overflow-hidden pointer-events-none">
                <div className="absolute -top-40 -right-40 w-80 h-80 bg-white/10 rounded-full blur-3xl animate-pulse" />
                <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-white/10 rounded-full blur-3xl animate-pulse delay-1000" />
            </div>

            {/* 헤더 */}
            <header className="relative z-10 bg-white/10 backdrop-blur-2xl border-b border-white/20">
                <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between text-white">
                    <div className="flex items-center gap-3">
                        <div className="p-2 bg-white/20 rounded-xl">
                            <CalendarIcon className="w-7 h-7" />
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold">My Todo</h1>
                            <p className="text-white/70 text-sm">오늘도 화이팅! 💪</p>
                        </div>
                    </div>
                    <form action="/logout" method="post">
                        <button className="flex items-center gap-2 px-4 py-2 bg-white/20 hover:bg-white/30 rounded-2xl border border-white/20 transition">
                            <LogOut className="w-5 h-5" />
                            로그아웃
                        </button>
                    </form>
                </div>
            </header>

            {/* 메인 */}
            <main className="relative z-10 max-w-6xl mx-auto px-6 py-8 text-white">
                <div className="flex gap-8 items-start">
                    {/* 왼쪽: 달력 카드 (FullCalendar 유지) */}
                    <section className="bg-white/90 text-gray-800 backdrop-blur-xl rounded-3xl p-6 shadow-2xl border border-white/30 sticky top-20 w-[22rem]">
                        {/* 커스텀 달력 헤더 */}
                        <div className="flex items-center justify-between mb-4">
                            <button
                                onClick={() => { calRef.current?.getApi().prev(); setMonthLabel(formatMonth(calRef.current.getApi().getDate())) }}
                                className="p-2 rounded-xl hover:bg-gray-100 transition hover:scale-110"
                            >
                                <ChevronLeft className="w-5 h-5 text-gray-600" />
                            </button>
                            <h3 className="text-xl font-bold flex items-center gap-2">
                                <CalendarIcon className="w-5 h-5 text-blue-600" />
                                {monthLabel || formatMonth(new Date())}
                            </h3>
                            <button
                                onClick={() => { calRef.current?.getApi().next(); setMonthLabel(formatMonth(calRef.current.getApi().getDate())) }}
                                className="p-2 rounded-xl hover:bg-gray-100 transition hover:scale-110"
                            >
                                <ChevronRight className="w-5 h-5 text-gray-600" />
                            </button>
                        </div>

                        {/* 오늘로 이동 */}
                        <button
                            onClick={goToday}
                            className="w-full mb-3 px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-xl font-medium hover:shadow-lg transition"
                        >
                            오늘로 이동
                        </button>

                        {/* FullCalendar */}
                        <div className="[&_.fc]:text-sm [&_.fc-toolbar]:hidden">
                            <FullCalendar
                                ref={calRef}
                                plugins={[dayGridPlugin, interactionPlugin]}
                                initialView="dayGridMonth"
                                height="auto"
                                locale={koLocale}
                                headerToolbar={false}
                                fixedWeekCount={false}
                                showNonCurrentDates={false}
                                dateClick={(info) => setDate(info.dateStr)}
                                datesSet={onDatesSet}
                                dayCellDidMount={(arg) => {
                                    // 선택 날짜 하이라이트
                                    const ymd = arg.date.toISOString().slice(0,10)
                                    if (ymd === date) arg.el.classList.add('ring-2','ring-indigo-500','rounded-lg')
                                }}
                            />
                        </div>
                    </section>

                    {/* 오른쪽: 할 일 카드 */}
                    <section className="flex-1 max-w-3xl">
                        <div className="bg-white/95 text-gray-800 backdrop-blur-2xl rounded-3xl shadow-2xl border border-white/30 overflow-hidden">
                            {/* 카드 헤더 (그라데이션) */}
                            <div className="bg-gradient-to-r from-blue-500 to-purple-600 p-8 text-white">
                                <div className="flex items-center justify-between mb-4">
                                    <div className="flex items-center gap-3">
                                        <CalendarDays className="w-8 h-8" />
                                        <div>
                                            <h2 className="text-2xl font-bold">할 일 관리</h2>
                                            <p className="text-white/80">{date}</p>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <div className="text-3xl font-bold">{stats.total}</div>
                                        <div className="text-white/80 text-sm">전체 작업</div>
                                    </div>
                                </div>

                                {/* 통계 칩 */}
                                <div className="grid grid-cols-3 gap-4">
                                    <div className="bg-white/20 rounded-2xl p-4 text-center">
                                        <ListTodo className="w-6 h-6 mx-auto mb-2" />
                                        <div className="text-xl font-bold">{stats.total}</div>
                                        <div className="text-white/80 text-sm">전체</div>
                                    </div>
                                    <div className="bg-white/20 rounded-2xl p-4 text-center">
                                        <Clock className="w-6 h-6 mx-auto mb-2" />
                                        <div className="text-xl font-bold">{stats.remaining}</div>
                                        <div className="text-white/80 text-sm">남은 일</div>
                                    </div>
                                    <div className="bg-white/20 rounded-2xl p-4 text-center">
                                        <Check className="w-6 h-6 mx-auto mb-2" />
                                        <div className="text-xl font-bold">{stats.completed}</div>
                                        <div className="text-white/80 text-sm">완료</div>
                                    </div>
                                </div>
                            </div>

                            {/* 내용 */}
                            <div className="p-8">
                                {/* 입력 */}
                                <form onSubmit={onAdd} className="flex gap-3 mb-6">
                                    <input
                                        className="flex-1 px-6 py-4 bg-gray-50 border-2 border-gray-200 rounded-2xl focus:outline-none focus:border-blue-500 focus:bg-white transition text-lg"
                                        value={text}
                                        onChange={e => setText(e.target.value)}
                                        placeholder="새로운 할 일을 입력하세요..."
                                        required
                                    />
                                    <button
                                        type="submit"
                                        disabled={!text.trim()}
                                        className="px-8 py-4 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-2xl font-semibold hover:shadow-xl hover:scale-105 transition flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
                                    >
                                        <Plus className="w-5 h-5" />
                                        추가
                                    </button>
                                </form>
                                {error && (
                                    <div className="mt-3 p-4 bg-red-50 border border-red-200 text-red-700 rounded-xl">
                                        {error}
                                    </div>
                                )}

                                {/* 필터 */}
                                <div className="flex justify-center gap-3 mb-6">
                                    {filterButtons.map(({ key, label, icon: Icon, color }) => (
                                        <button
                                            key={key}
                                            onClick={() => setFilter(key)}
                                            type="button"
                                            className={`flex items-center gap-2 px-6 py-3 rounded-2xl font-medium transition
                        ${filter === key
                                                ? `bg-gradient-to-r ${color} text-white shadow-lg scale-105`
                                                : 'bg-gray-100 text-gray-600 hover:bg-gray-200 hover:scale-105'}`}
                                        >
                                            <Icon className="w-5 h-5" />
                                            {label}
                                        </button>
                                    ))}
                                </div>

                                {/* 리스트 */}
                                <div className="space-y-3 max-h-96 overflow-y-auto pr-2">
                                    {loading ? (
                                        <div className="text-center py-12">
                                            <div className="w-12 h-12 border-4 border-blue-200 border-t-blue-500 rounded-full animate-spin mx-auto mb-4" />
                                            <p className="text-gray-600 text-lg">불러오는 중...</p>
                                        </div>
                                    ) : filtered.length === 0 ? (
                                        <div className="text-center py-16">
                                            <div className="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                                <CalendarDays className="w-10 h-10 text-gray-400" />
                                            </div>
                                            <h3 className="text-xl font-semibold text-gray-600 mb-2">할 일이 없습니다</h3>
                                            <p className="text-gray-400">새로운 할 일을 추가해보세요!</p>
                                        </div>
                                    ) : (
                                        filtered.map((t) => (
                                            <div
                                                key={t.id}
                                                className={`group flex items-center gap-4 p-5 rounded-2xl border transition hover:shadow-lg hover:scale-[1.01]
                          ${t.completed ? 'bg-green-50 border-green-200' : 'bg-white border-gray-200 hover:border-blue-300'}`}
                                            >
                                                <button
                                                    onClick={() => onToggle(t.id)}
                                                    className={`p-1 rounded-full
                            ${t.completed ? 'text-green-600 bg-green-100' : 'text-gray-400 hover:text-blue-600 hover:bg-blue-50'}`}
                                                    type="button"
                                                >
                                                    {t.completed
                                                        ? <div className="w-6 h-6 bg-green-500 rounded-full flex items-center justify-center">
                                                            <Check className="w-4 h-4 text-white" />
                                                        </div>
                                                        : <Circle className="w-6 h-6" />}
                                                </button>

                                                <span className={`flex-1 text-lg ${t.completed ? 'text-gray-500 line-through' : 'text-gray-800'}`}>
                          {t.task}
                        </span>

                                                <button
                                                    onClick={() => onDelete(t.id)}
                                                    className="opacity-60 hover:opacity-100 p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-xl transition"
                                                    type="button"
                                                >
                                                    <Trash2 className="w-5 h-5" />
                                                </button>
                                            </div>
                                        ))
                                    )}
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
            </main>
        </div>
    )
}