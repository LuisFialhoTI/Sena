package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import exception.DatabaseException;
import model.Avaliacao;
import model.Imovel;

public class ImovelDAO implements DAO<Imovel> {

    @Override
    public List<Imovel> getAll() throws DatabaseException {
        List<Imovel> list = new ArrayList<>();
        String sql = "SELECT id, titulo, localizacao, preco, imagem, detalhes, rating, badges, estrelas, " +
                     "endereco, descricao, comodidades, scores, proprietario FROM imoveis ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Imovel im = mapearImovel(rs);
                im.setAvaliacoes(getAvaliacoesPorImovel(im.getId()));
                list.add(im);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar todos os imoveis no banco", e);
        }
        return list;
    }

    @Override
    public Imovel getById(int id) throws DatabaseException {
        String sql = "SELECT id, titulo, localizacao, preco, imagem, detalhes, rating, badges, estrelas, " +
                     "endereco, descricao, comodidades, scores, proprietario FROM imoveis WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Imovel im = mapearImovel(rs);
                    im.setAvaliacoes(getAvaliacoesPorImovel(im.getId()));
                    return im;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar imovel por ID: " + id, e);
        }
        return null;
    }

    @Override
    public void save(Imovel im) throws DatabaseException {
        String sql = "INSERT INTO imoveis (titulo, localizacao, preco, imagem, detalhes, rating, badges, estrelas, " +
                     "endereco, descricao, comodidades, scores, proprietario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, im.getTitulo());
            ps.setString(2, im.getLocalizacao());
            ps.setString(3, im.getPreco());
            ps.setString(4, im.getImagem());
            ps.setString(5, im.getDetalhes());
            ps.setString(6, im.getRating());
            ps.setString(7, im.getBadges());
            ps.setString(8, im.getEstrelas());
            ps.setString(9, im.getEndereco());
            ps.setString(10, im.getDescricao());
            ps.setString(11, im.getComodidades());
            ps.setString(12, im.getScores());
            ps.setString(13, im.getProprietario());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    im.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar imovel no banco de dados", e);
        }
    }

    @Override
    public void update(Imovel im) throws DatabaseException {
        String sql = "UPDATE imoveis SET titulo = ?, localizacao = ?, preco = ?, imagem = ?, detalhes = ?, " +
                     "rating = ?, badges = ?, estrelas = ?, endereco = ?, descricao = ?, comodidades = ?, " +
                     "scores = ?, proprietario = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, im.getTitulo());
            ps.setString(2, im.getLocalizacao());
            ps.setString(3, im.getPreco());
            ps.setString(4, im.getImagem());
            ps.setString(5, im.getDetalhes());
            ps.setString(6, im.getRating());
            ps.setString(7, im.getBadges());
            ps.setString(8, im.getEstrelas());
            ps.setString(9, im.getEndereco());
            ps.setString(10, im.getDescricao());
            ps.setString(11, im.getComodidades());
            ps.setString(12, im.getScores());
            ps.setString(13, im.getProprietario());
            ps.setInt(14, im.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar imovel de ID: " + im.getId(), e);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM imoveis WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar imovel de ID: " + id, e);
        }
    }

    // Lista as avaliações de um imóvel específico
    public List<Avaliacao> getAvaliacoesPorImovel(int imovelId) throws DatabaseException {
        List<Avaliacao> list = new ArrayList<>();
        String sql = "SELECT id, imovel_id, avaliacao_geral, seguranca_bairro, seguranca_rua, comodidade, " +
                     "localizacao, tags, comentario, TO_CHAR(data_criacao, 'DD/MM/YYYY') as data_formatada FROM avaliacoes WHERE imovel_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, imovelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Avaliacao(
                        rs.getInt("id"),
                        rs.getInt("imovel_id"),
                        rs.getInt("avaliacao_geral"),
                        rs.getInt("seguranca_bairro"),
                        rs.getInt("seguranca_rua"),
                        rs.getInt("comodidade"),
                        rs.getInt("localizacao"),
                        rs.getString("tags"),
                        rs.getString("comentario"),
                        rs.getString("data_formatada")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar avaliacoes para o imovel ID: " + imovelId, e);
        }
        return list;
    }

    // Salva uma nova avaliação de moradia
    public void saveAvaliacao(Avaliacao av) throws DatabaseException {
        String sql = "INSERT INTO avaliacoes (imovel_id, avaliacao_geral, seguranca_bairro, seguranca_rua, " +
                     "comodidade, localizacao, tags, comentario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, av.getImovelId());
            ps.setInt(2, av.getAvaliacaoGeral());
            ps.setInt(3, av.getSegurancaBairro());
            ps.setInt(4, av.getSegurancaRua());
            ps.setInt(5, av.getComodidade());
            ps.setInt(6, av.getLocalizacao());
            ps.setString(7, av.getTags());
            ps.setString(8, av.getComentario());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    av.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar avaliacao no banco", e);
        }
    }

    private Imovel mapearImovel(ResultSet rs) throws SQLException {
        return new Imovel(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("localizacao"),
            rs.getString("preco"),
            rs.getString("imagem"),
            rs.getString("detalhes"),
            rs.getString("rating"),
            rs.getString("badges"),
            rs.getString("estrelas"),
            rs.getString("endereco"),
            rs.getString("descricao"),
            rs.getString("comodidades"),
            rs.getString("scores"),
            rs.getString("proprietario")
        );
    }
}
