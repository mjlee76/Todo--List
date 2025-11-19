package com.hello.ToDoList.repository.member;

import com.hello.ToDoList.entity.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO member (id, name, password, email, created_at) VALUES (?, ?, ?, ?, now())";
        jdbcTemplate.update(sql, member.getId(), member.getName(), member.getPassword(), member.getEmail());
        return member;
    }

    @Override
    public Optional<Member> findById(String id) {
        List<Member> result = jdbcTemplate.query("SELECT * FROM member WHERE id = ?", memberRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("SELECT * FROM member", memberRowMapper());
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        List<Member> result = jdbcTemplate.query("SELECT * FROM member WHERE email = ?", memberRowMapper(), email);
        return result.stream().findAny();
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member WHERE email = ? AND id <> ?", Integer.class, email, id);
        return count != null && count > 0;
    }

    @Override
    public int updateName(String id, String name) {
        return jdbcTemplate.update("UPDATE member SET name = ?, updated_at = now() WHERE id = ?", name, id);
    }

    @Override
    public int updateEmail(String id, String email) {
        return jdbcTemplate.update("UPDATE member SET email = ?, updated_at = now() WHERE id = ?", email, id);
    }

    @Override
    public int updatePassword(String id, String password) {
        return jdbcTemplate.update("UPDATE member SET password = ?, updated_at = now() WHERE id = ?", password, id);
    }

    // RowMapper는 JDBC의 query()메서드가 반환한 ResultSet 한 행(row)을 → 도메인 객체(Member)로 변환하는 역할
    // RowMapper는 인터페이스이고 mapRow 메서드가 정의되어있음
    // 익명클래스를 바로 구현해서(mapRow 메서드는 Override) 사용함
    private RowMapper<Member> memberRowMapper() {
        return new RowMapper<Member>() {
            @Override
            public Member mapRow(ResultSet rs, int rowNum) throws SQLException{
                Member member = new Member();
                member.setId(rs.getString("id"));
                member.setName(rs.getString("name"));
                member.setPassword(rs.getString("password"));
                member.setEmail(rs.getString("email"));
                member.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                Timestamp updatedTs = rs.getTimestamp("updated_at");
                if (updatedTs != null) {
                    member.setUpdatedAt(updatedTs.toLocalDateTime());
                }
                return member;
            }
        };
    }
}
