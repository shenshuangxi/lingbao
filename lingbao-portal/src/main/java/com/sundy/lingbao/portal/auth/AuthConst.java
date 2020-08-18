package com.sundy.lingbao.portal.auth;

public class AuthConst {

	/**
	 * @author Administrator
	 *
	 */
	public static enum AuthType {
		CREATE(1, "create"),
		UPDATE(2, "update"),
		DELETE(3, "create"),
		COMMITPUSH(4, "commit-push"),
		FIND(5, "find");
		
		private final Integer type;
		private final String name;

		private AuthType(Integer type, String name) {
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
	
	
	
	public static enum RoleType {
		Admin("admin");
		
		private final String name;

		private RoleType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	
}
