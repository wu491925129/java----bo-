package com.ws.tool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;

public class DbToPojo {
	public static void main(String[] args) {
		String tablename="tb_corp";
		NutIoc ioc=new NutIoc(new JsonLoader("ioc/dao.js"));
		Dao dao=ioc.get(Dao.class);
		String sqlstr = "SELECT t.COLUMN_NAME,t.DATA_TYPE,C.COMMENTS  FROM user_tab_columns t ,all_col_comments c"
				+ " where T.COLUMN_NAME = C.COLUMN_NAME and T.TABLE_NAME = C.TABLE_NAME "
				+ "and t.table_name = upper('" + tablename + "') order by t.column_id asc";  
		Sql sql=Sqls.create(sqlstr);
		sql.setCallback(new SqlCallback() {
			
			@Override
			public Object invoke(Connection conn, ResultSet rs, Sql sql)
					throws SQLException {
				StringBuffer result=new StringBuffer();
				while (rs.next()) {
					result.append("/**\n*"+rs.getString(3)+"\n**/");
					result.append("\n");
					result.append("@Column(\""+rs.getString(1)+"\")\n");
					result.append("private ");
					if(rs.getString(2).equalsIgnoreCase("VARCHAR2")||rs.getString(2).equalsIgnoreCase("CLOB")
							||rs.getString(2).equalsIgnoreCase("CHAR")){
						result.append("String ");
					}
					if(rs.getString(2).equalsIgnoreCase("NUMBER")||rs.getString(2).equalsIgnoreCase("INTEGER")){
						result.append("double ");
					}
					if(rs.getString(2).equalsIgnoreCase("DATE")){
						result.append("Date ");
					}
					result.append(rs.getString(1).toLowerCase().replace("_", "")+";");
					result.append("\n");
				}
				return result;
			}
		});
		dao.execute(sql);
		System.out.println(sql.getResult());
	}
}
