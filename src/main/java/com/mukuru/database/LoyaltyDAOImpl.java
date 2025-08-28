package com.mukuru.database;

import com.mukuru.model.Reward;
import com.mukuru.model.Transaction;
import com.mukuru.model.User;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoyaltyDAOImpl implements LoyaltyDAO {

    public static final String DISK_DB_URL_PREFIX = "jdbc:sqlite:";
    public static final String SEPARATOR = "\t";
    private String dbUrl = null;

    public static void main( String[] args ) {
        final LoyaltyDAOImpl app = new LoyaltyDAOImpl(args);
    }


    public LoyaltyDAOImpl(String[] args) {
        processCmdLineArgs( args );
    }

    @Override
    public User getOrCreateUser(String phone, String name) throws SQLException {

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ins = conn.prepareStatement(
                     "INSERT INTO users(phone, name) VALUES(?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, phone);
            ins.setString(2, (name == null || name.isBlank()) ? "Customer" : name);
            int count = ins.executeUpdate();
            if (count != 1) throw new SQLException("Expected 1 row inserted into users, got " + count);

            try (ResultSet keys = ins.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No generated key for users insert.");
                long id = keys.getLong(1);
                User u = new User(id, phone, (name == null || name.isBlank()) ? "Customer" : name);
                u.setPoints(0);
                u.setTier("Bronze");
                return u;
            }
        }
    }

    
    @Override
    public int addTransaction(long userId, double amount) throws SQLException {
        int pts = LoyaltyDAO.calculatePoints(amount);
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO transactions(user_id, amount, points) VALUES(?,?,?)");
                 PreparedStatement upd = conn.prepareStatement(
                         "UPDATE users SET points = points + ? WHERE id = ?");
                 PreparedStatement sel = conn.prepareStatement(
                         "SELECT points FROM users WHERE id = ?");
                 PreparedStatement tier = conn.prepareStatement(
                         "UPDATE users SET tier = ? WHERE id = ?")
            ) {
                ins.setLong(1, userId);
                ins.setDouble(2, amount);
                ins.setInt(3, pts);
                ins.executeUpdate();

                upd.setInt(1, pts);
                upd.setLong(2, userId);
                upd.executeUpdate();

                int newPts = 0;
                sel.setLong(1, userId);
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) newPts = rs.getInt(1);
                }
                tier.setString(1, tierFor(newPts));
                tier.setLong(2, userId);
                tier.executeUpdate();

                conn.commit();
                return pts;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    @Override
    public int getBalance(long userId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement("SELECT points FROM users WHERE id=?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    @Override
    public String getTier(long userId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement("SELECT tier FROM users WHERE id=?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString(1) : "Bronze"; }
        }
    }

    @Override
    public List<Transaction> getTransactions(long userId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, user_id, amount, points, created_at FROM transactions WHERE user_id=? ORDER BY id DESC")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getDouble("amount"),
                            rs.getInt("points")
                    ));
                }
            }
        }
        return list;
    }


    @Override
    public List<Reward> listRewards() throws SQLException {
        List<Reward> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, name, cost, description, icon, stock FROM rewards WHERE stock > 0 ORDER BY cost ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Reward(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("cost"),
                        rs.getString("description"),
                        rs.getString("icon"),
                        rs.getInt("stock")
                ));
            }
        }
        return list;
    }

    @Override
    public String redeem(long userId, long rewardId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.setAutoCommit(false);
            try (PreparedStatement getR = conn.prepareStatement(
                    "SELECT id, name, cost, icon, stock FROM rewards WHERE id=?");
                 PreparedStatement getP = conn.prepareStatement(
                         "SELECT points FROM users WHERE id=?");
                 PreparedStatement updU = conn.prepareStatement(
                         "UPDATE users SET points = points - ? WHERE id=?");
                 PreparedStatement updR = conn.prepareStatement(
                         "UPDATE rewards SET stock = stock - 1 WHERE id=?");
                 PreparedStatement ins  = conn.prepareStatement(
                         "INSERT INTO redemptions(user_id, reward_id) VALUES(?,?)");
                 PreparedStatement sel  = conn.prepareStatement(
                         "SELECT points FROM users WHERE id=?");
                 PreparedStatement tier = conn.prepareStatement(
                         "UPDATE users SET tier=? WHERE id=?")
            ){
                // reward
                getR.setLong(1, rewardId);
                String name = null, icon = "🪙"; int cost = 0, stock = 0;
                try (ResultSet rs = getR.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Reward not found");
                    name  = rs.getString("name");
                    icon  = rs.getString("icon");
                    cost  = rs.getInt("cost");
                    stock = rs.getInt("stock");
                }
                if (stock <= 0) throw new SQLException("Reward not available");

                // user points
                getP.setLong(1, userId);
                int pts = 0; try (ResultSet rs = getP.executeQuery()) { if (rs.next()) pts = rs.getInt(1); }
                if (pts < cost) throw new SQLException("Not enough points");

                // deduct & record
                updU.setInt(1, cost); updU.setLong(2, userId); updU.executeUpdate();
                updR.setLong(1, rewardId); updR.executeUpdate();
                ins.setLong(1, userId); ins.setLong(2, rewardId); ins.executeUpdate();

                // recalc tier
                sel.setLong(1, userId);
                int newPts = 0; try (ResultSet rs = sel.executeQuery()) { if (rs.next()) newPts = rs.getInt(1); }
                tier.setString(1, tierFor(newPts)); tier.setLong(2, userId); tier.executeUpdate();

                conn.commit();
                return "Unlocked " + icon + " " + name + "!";
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
    
    private static String tierFor(int pts){
        if (pts >= 1000) return "Platinum";
        if (pts >= 500)  return "Gold";
        if (pts >= 200)  return "Silver";
        return "Bronze";
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        User u = new User(rs.getLong("id"), rs.getString("phone"), rs.getString("name"));
        u.setPoints(rs.getInt("points"));
        u.setTier(rs.getString("tier"));
        return u;
    }


    private void processCmdLineArgs(String[] args){
        if (args.length == 2 && args[0].equals("-f")) {
            final File dbFile = new File(args[1]);
            if (dbFile.exists()) {
                String dbUrl = DISK_DB_URL_PREFIX + args[1];
            } else {
                throw new IllegalArgumentException("Database file " + dbFile.getAbsolutePath() + " not found.");
            }
        } else {
            throw new RuntimeException("Expected arguments '-f <filename>' but didn't find it.");
        }
    }
}
