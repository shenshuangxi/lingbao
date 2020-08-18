package com.sundy.lingbao.common.consts;

public interface Consts {

	public final String CLUSTER = "cluster";
	public final String MASTER_CLUSTER = "master";
	
	
	public static enum OperateType{
		
		CREATE(0,"create"),
		DELETE(1, "delete"),
		UPDATE(2, "update"),
		PUSH(3, "push"),
		PULL(4, "pull"),
		SAVE(5, "save"),
		SYNC(6, "sync");
		
		private final Integer type;
		private final String name;

		private OperateType(Integer type, String name) {
			this.type = type;
			this.name = name;
		}

		public Integer getType() {
			return type;
		}

		public String getName() {
			return name;
		}
		
	}
	
	public static enum Topic {
		RELEASE_MESSAGE(1, "releaseMessage");

		private final int code;
		private final String name;

		private Topic(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}
	}

	public static enum State {
		DELETE(-1, "删除"), ABANDON(0, "废弃"), ACTIVE(1, "有效");

		private final int code;
		private final String name;

		private State(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

	}

	public static enum ConfigFileOutputFormat {
		PROPERTIES("properties"), JSON("json");

		private String value;

		ConfigFileOutputFormat(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

}
