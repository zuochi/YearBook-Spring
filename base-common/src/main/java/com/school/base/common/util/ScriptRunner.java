package com.school.base.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author work
 */
public class ScriptRunner {

    public void exccute(Connection conn, InputStream inputStream) throws IOException, SQLException {
        List<String> lines = IOUtils.readLines(inputStream, "UTF-8");
        conn.setAutoCommit(false);
        Statement statement = conn.createStatement();
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith("/*")) {
                continue;
            } else if (line.trim().endsWith(";")) {
                builder.append(" ").append(line.trim());
                String sql = builder.toString().substring(0, builder.length() - 1);
                builder.setLength(0);
                try {
                    statement.execute(sql);
                } catch (SQLException e) {
                    //
                }
            } else {
                builder.append(" ").append(line).append(" ");
            }
        }
    }
}
