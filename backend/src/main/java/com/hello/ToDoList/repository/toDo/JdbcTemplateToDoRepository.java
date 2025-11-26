package com.hello.ToDoList.repository.toDo;

import com.hello.ToDoList.entity.ToDo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTemplateToDoRepository implements ToDoRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateToDoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ToDo save(ToDo todo) {
        String sql = """
                    INSERT INTO to_do (member_id, task, completed, to_do_date, created_at)
                    VALUES (?, ?, ?, ?, now())
                    RETURNING id
                    """;
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                todo.getMemberId(),
                todo.getTask(),
                todo.isCompleted(),
                todo.getToDoDate());
        todo.setId(id);
        return todo;
    }

    /*@Override
    public List<ToDo> findByMemberAndDate(String memberId, LocalDate date) {
        String sql = """
                SELECT id, member_id, task, completed, to_do_date, created_at, updated_at
                FROM to_do
                WHERE member_id = ? AND DATE(to_do_date) = ?
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, toDoRowMapper(), memberId, date);
    }*/
    //DATE()함수 제거 -> 인덱스 타도록 Range Query 적용
    @Override
    public List<ToDo> findByMemberAndDate(String memberId, LocalDate date) {
        LocalDate nextDate = date.plusDays(1);
        String sql = """
                SELECT id, member_id, task, completed, to_do_date, created_at, updated_at
                FROM to_do
                WHERE member_id = ? 
                    AND to_do_date >= ?
                    AND to_do_date < ?
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, toDoRowMapper(), memberId, date, nextDate);
    }

    /*@Override
    public int countByMemberAndDate(String memberId, LocalDate date) {
        String sql = """
                SELECT COUNT(*)
                FROM to_do
                WHERE member_id = ? AND DATE(to_do_date) = ?
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class, memberId, date); //Integer.class: 결과값을 Int 타입으로 매핑
    }*/
    //DATE()함수 제거 -> 인덱스 타도록 Range Query 적용
    @Override
    public int countByMemberAndDate(String memberId, LocalDate date) {
        LocalDate nextDate = date.plusDays(1);
        String sql = """
                SELECT COUNT(*)
                FROM to_do
                WHERE member_id = ? 
                    AND to_do_date = ?
                    AND to_do_date < ?
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class, memberId, date, nextDate); //Integer.class: 결과값을 Int 타입으로 매핑
    }

    @Override
    public boolean toggleCompleted(String memberId, Long todoId) {
        String sql = """
                UPDATE to_do 
                SET completed = NOT completed
                WHERE member_id = ? AND id = ?
                """;
        int result = jdbcTemplate.update(sql, memberId, todoId); //result: 변환된 행 개수
        return result > 0; //0보다 크면 true
    }

    @Override
    public int delete(String memberId, Long todoId) {
        String sql = """
                DELETE FROM to_do
                WHERE member_id = ? AND id = ?
                """;
        int result = jdbcTemplate.update(sql, memberId, todoId);
        return result;
    }

    @Override
    public Map<String, Integer> getStatistics(String memberId) {
        String sql = """
                SELECT COUNT(*) AS total_todos,
                COUNT(*) FILTER (WHERE completed = true) AS completed_todos,
                COUNT(*) FILTER (WHERE to_do_date = CURRENT_DATE) AS today_todos,
                COUNT(*) FILTER (WHERE to_do_date = CURRENT_DATE AND completed = true) AS completed_today
                FROM to_do
                WHERE member_id = ?
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("totalTodos", rs.getInt("total_todos"));
            stats.put("completedTodos", rs.getInt("completed_todos"));
            stats.put("todayTodos", rs.getInt("today_todos"));
            stats.put("completedToday", rs.getInt("completed_today"));
            return stats;
        }, memberId);
    }

    // 'DB 결과 한 줄' → 'ToDo 객체 하나'로 변환해주는 매퍼 함수
    private RowMapper<ToDo> toDoRowMapper() {
        return new RowMapper<ToDo>() {
            @Override
            public ToDo mapRow(ResultSet rs, int rowNum) throws SQLException{
                ToDo todo = new ToDo();
                todo.setId(rs.getLong("id"));
                todo.setMemberId(rs.getString("member_id"));
                todo.setTask(rs.getString("task"));
                todo.setCompleted(rs.getBoolean("completed"));
                todo.setToDoDate(rs.getObject("to_do_date",  LocalDate.class));
                todo.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                if (rs.getTimestamp("updated_at") != null) {
                    todo.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                return todo;
            }
        };
    }
}
