package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;
import org.apache.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO post(name, text, link, created) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            if (post.getTime() != null) {
                ps.setTimestamp(4, new Timestamp(post.getTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        String sql = "SELECT * FROM post";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                result.add(toPost(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM post WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(toPost(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.empty();
    }

    private Post toPost(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("created");
        Long time = null;
        if (timestamp != null) {
            time = timestamp.getTime();
        }

        return new Post(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("link"),
                rs.getString("text"),
                time);

    }
}