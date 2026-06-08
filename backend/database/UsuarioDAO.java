package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import exception.DatabaseException;
import model.Administrador;
import model.Anfitriao;
import model.Hospede;
import model.TipoConta;
import model.Usuario;

public class UsuarioDAO implements DAO<Usuario> {

    @Override
    public List<Usuario> getAll() throws DatabaseException {
        List<Usuario> list = new ArrayList<>();
        String sql = "SELECT id, nome, email, senha, tipo FROM usuarios ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar todos os usuarios no banco", e);
        }
        return list;
    }

    @Override
    public Usuario getById(int id) throws DatabaseException {
        String sql = "SELECT id, nome, email, senha, tipo FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuario por ID: " + id, e);
        }
        return null;
    }

    public Usuario getByEmail(String email) throws DatabaseException {
        String sql = "SELECT id, nome, email, senha, tipo FROM usuarios WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuario por email: " + email, e);
        }
        return null;
    }

    @Override
    public void save(Usuario u) throws DatabaseException {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getTipo().name());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    u.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar usuario no banco de dados", e);
        }
    }

    @Override
    public void update(Usuario u) throws DatabaseException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ?, tipo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getTipo().name());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar usuario de ID: " + u.getId(), e);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar usuario de ID: " + id, e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String senha = rs.getString("senha");
        String tipoStr = rs.getString("tipo");
        TipoConta tipo = TipoConta.obterPorDescricao(tipoStr);

        switch (tipo) {
            case ADMINISTRADOR:
                return new Administrador(id, nome, email, senha);
            case ANFITRIAO:
                return new Anfitriao(id, nome, email, senha);
            case HOSPEDE:
            default:
                return new Hospede(id, nome, email, senha);
        }
    }
}
