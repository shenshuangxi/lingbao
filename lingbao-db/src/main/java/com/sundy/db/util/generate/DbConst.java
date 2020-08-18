package com.sundy.db.util.generate;

import java.math.BigDecimal;
import java.util.Date;

public interface DbConst {
	
	public static enum DbType{
		INTEGER("integer"),
		SMALLINTEGER("smallint"),
		FLOAT("float"),
		STRING("varchar(128)"),
		CHARACTER("char(2)"),
		DATE("datetime"),
		CHAR("char(1)"),
		BYTEARRAY("longblob"),
		BIGINT("bigint"),
		DOUBLE("double");
		
		private String dbType;
		private DbType(String dbType) {
			this.dbType = dbType;
		}
		
		public static DbType getDbTypeByClass(Class<?> clazz){
			if(Integer.class==clazz||int.class==clazz){
				return DbType.INTEGER;
			} else if(Boolean.class==clazz||boolean.class==clazz){
				return DbType.SMALLINTEGER;
			} else if(Float.class==clazz||float.class==clazz){
				return DbType.FLOAT;
			} else if(String.class==clazz){
				return DbType.STRING;
			} else if(Character.class==clazz){
				return DbType.CHARACTER;
			} else if(Date.class==clazz){
				return DbType.DATE;
			} else if(char.class==clazz){
				return DbType.CHAR;
			} else if(byte[].class==clazz||Byte[].class==clazz){
				return DbType.BYTEARRAY;
			} else if(BigDecimal.class==clazz||Long.class==clazz||long.class==clazz){
				return DbType.BIGINT;
			} else if(double.class==clazz||Double.class==clazz){
				return DbType.DOUBLE;
			}
			System.out.println(clazz);
			return null;
		}

		public String getDbType() {
			return dbType;
		}
		
	}
	
}
