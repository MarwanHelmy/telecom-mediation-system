package RULE_PK;

import DB_PK.DB_Local;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RULE {
    // =========================================================
    // ATTRIBUTES
    // =========================================================

    private int ID;

    private int SOURCE_NODE_ID;

    private int DESTINATION_NODE_ID;

    private boolean IS_ACTIVE;

    private Timestamp CREATED_AT;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================
    public RULE() {

    }

    public RULE(
            int ID,
            int SOURCE_NODE_ID,
            int DESTINATION_NODE_ID,
            boolean IS_ACTIVE,
            Timestamp CREATED_AT
    ) {
        this.ID = ID;

        this.SOURCE_NODE_ID = SOURCE_NODE_ID;

        this.DESTINATION_NODE_ID = DESTINATION_NODE_ID;

        this.IS_ACTIVE = IS_ACTIVE;

        this.CREATED_AT = CREATED_AT;
    }

    // =========================================================
    // GETTERS
    // =========================================================
    public int getID() {
        return ID;
    }

    public int getSOURCE_NODE_ID() {
        return SOURCE_NODE_ID;
    }

    public int getDESTINATION_NODE_ID() {
        return DESTINATION_NODE_ID;
    }

    public boolean isIS_ACTIVE() {
        return IS_ACTIVE;
    }

    public Timestamp getCREATED_AT() {
        return CREATED_AT;
    }

    // =========================================================
    // SETTERS
    // =========================================================
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setSOURCE_NODE_ID(int SOURCE_NODE_ID) {
        this.SOURCE_NODE_ID = SOURCE_NODE_ID;
    }

    public void setDESTINATION_NODE_ID(int DESTINATION_NODE_ID) {
        this.DESTINATION_NODE_ID = DESTINATION_NODE_ID;
    }

    public void setIS_ACTIVE(boolean IS_ACTIVE) {
        this.IS_ACTIVE = IS_ACTIVE;
    }

    public void setCREATED_AT(Timestamp CREATED_AT) {
        this.CREATED_AT = CREATED_AT;
    }

    // =========================================================
    // TO STRING
    // =========================================================
    @Override
    public String toString() {
        return "RULE{"
                + "ID="
                + ID
                + ", SOURCE_NODE_ID="
                + SOURCE_NODE_ID
                + ", DESTINATION_NODE_ID="
                + DESTINATION_NODE_ID
                + ", IS_ACTIVE="
                + IS_ACTIVE
                + ", CREATED_AT="
                + CREATED_AT
                + '}';
    }

    public static boolean deleteRule(int ruleId) {

        String sql = "{ ? = call delete_rule(?) }";

        try (Connection conn = DB_Local.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.BOOLEAN);

            stmt.setInt(2, ruleId);

            stmt.execute();

            return stmt.getBoolean(1);

        } catch (SQLException e) {
            System.err.println("Error deleting rule with ID: " + ruleId);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkDuplicateRule(int sourceNodeId, int destinationNodeId) {
        String sql = "SELECT check_duplicate_rule(?, ?)";

        try (Connection conn = DB_Local.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sourceNodeId);
            stmt.setInt(2, destinationNodeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error checking duplicate rule");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertRule(int sourceNodeId, int destinationNodeId, boolean isActive) {
        String sql = "SELECT insert_rule(?, ?, ?)";

        try (Connection conn = DB_Local.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sourceNodeId);
            stmt.setInt(2, destinationNodeId);
            stmt.setBoolean(3, isActive);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error inserting rule");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRule(int ruleId, int sourceNodeId, int destinationNodeId, boolean isActive) {
        String sql = "SELECT update_rule(?, ?, ?, ?)";

        try (Connection conn = DB_Local.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ruleId);
            stmt.setInt(2, sourceNodeId);
            stmt.setInt(3, destinationNodeId);
            stmt.setBoolean(4, isActive);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error updating rule with ID: " + ruleId);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkDuplicateRuleExcludeId(int sourceNodeId, int destinationNodeId, int excludeId) {
        String sql = "SELECT check_duplicate_rule_exclude_id(?, ?, ?)";

        try (Connection conn = DB_Local.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sourceNodeId);
            stmt.setInt(2, destinationNodeId);
            stmt.setInt(3, excludeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error checking duplicate rule with exclude");
            e.printStackTrace();
            return false;
        }
    }

    public static List<RULE> getAllRules() {
        List<RULE> rules = new ArrayList<>();
        String sql = "SELECT * FROM get_all_rules()";

        try (Connection conn = DB_Local.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RULE rule = new RULE();
                rule.setID(rs.getInt("id"));
                rule.setSOURCE_NODE_ID(rs.getInt("source_node_id"));
                rule.setDESTINATION_NODE_ID(rs.getInt("destination_node_id"));
                rule.setIS_ACTIVE(rs.getBoolean("is_active"));
                rule.setCREATED_AT(rs.getTimestamp("created_at"));
                rules.add(rule);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all rules");
            e.printStackTrace();
        }

        return rules;
    }
}
