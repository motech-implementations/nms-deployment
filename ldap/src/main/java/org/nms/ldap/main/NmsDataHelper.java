package org.nms.ldap.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to get NMS data required for LDAP integration.
 *
 */
public class NmsDataHelper {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(NmsDataHelper.class);

    static Map<String, List<String>> getStateDistrictData(Resource resource) {
        Map<String, List<String>> dataMap = new HashMap<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String connectionUrl = "jdbc:mysql://" + resource.getNmsServerUrl()
                + ":" + resource.getNmsServerPort() + "/"
                + resource.getNmsDataBase();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionUrl,
                    resource.getNmsUser(), resource.getNmsPassword());
            StringBuilder query = new StringBuilder();
            query.append(" SELECT sd.State_Name,dd.district_name FROM district_dimension dd ");
            query.append(" INNER JOIN state_dimension sd");
            query.append(" WHERE sd.State_ID=dd.State_ID");
            query.append(" ORDER BY sd.State_Name,dd.district_name");
            stmt = connection.prepareStatement(query.toString());
            rs = stmt.executeQuery();
            String state = null;
            List<String> districtList = null;
            while (rs.next()) {
                if (rs.getString("State_Name").equalsIgnoreCase(state)) {
                    districtList.add(rs.getString("district_name"));
                } else {
                    if (state != null) {
                        dataMap.put(state, districtList);
                    }
                    state = rs.getString("State_Name");
                    districtList = new ArrayList<>();
                    districtList.add(rs.getString("district_name"));
                }
            }
            if (state != null) {
                dataMap.put(state, districtList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return dataMap;
    }
}
