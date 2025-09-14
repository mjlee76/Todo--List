package com.hello.ToDoList.repository.toDo;

import com.hello.ToDoList.entity.ToDo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class JdbcTemplateToDoRepository implements ToDoRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateToDoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ToDo save(ToDo todo) {
        String sql = """
                    INSERT INTO todo (member_id, task, completed, to_do_date, created_at, updated_at) 
                    VALUES (?, ?, ?, ?, now(), now()) 
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

    @Override
    public List<ToDo> findByMemberAndDate(String memberId, LocalDate date) {
        String sql = """
                SELECT id, member_id, task, completed, to_do_date, created_at, updated_at
                FROM todo
                WHERE member_id = ? AND DATE(to_do_date) = ?
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, toDoRowMapper(), memberId, date);
    }

    @Override
    public int countByMemberAndDate(String memberId, LocalDate date) {
        String sql = """
                SELECT COUNT(*)
                FROM todo
                WHERE member_id = ? AND DATE(to_do_date) = ?
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class, memberId, date); //Integer.class: 결과값을 Int 타입으로 매핑
    }

    @Override
    public boolean toggleCompleted(String memberId, Long todoId) {
        String sql = """
                UPDATE todo 
                SET completed = NOT completed
                WHERE member_id = ? AND id = ?
                """;
        int result = jdbcTemplate.update(sql, memberId, todoId); //result: 변환된 행 개수
        return result > 0; //0보다 크면 true
    }

    @Override
    public int delete(String memberId, Long todoId) {
        String sql = """
                DELETE FROM todo
                WHERE member_id = ? AND id = ?
                """;
        int result = jdbcTemplate.update(sql, memberId, todoId);
        return result;
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
