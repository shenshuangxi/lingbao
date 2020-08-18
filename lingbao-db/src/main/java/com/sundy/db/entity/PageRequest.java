package com.sundy.db.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
public class PageRequest {

	private Integer limit;
	private Integer offset;
	private Sort[] sorts;
	
	public PageRequest(int pageNumber, int pageSize, Sort... sorts) {
		this.limit = pageSize;
		this.offset = pageNumber * pageSize;
		this.sorts = sorts;
	}
	
	public static void main(String[] args) {
		PageRequest pageRequest = new PageRequest(1, 10);
		System.out.println(pageRequest.getPageRequest());
		pageRequest = new PageRequest(1, 10, new Sort[]{new Sort("ab", Diretion.ASC), new Sort("cd")});
		System.out.println(pageRequest.getPageRequest());
	}
	
	public String getPageRequest() {
		StringBuilder builder = new StringBuilder();
		if (sorts!=null && sorts.length>0) {
			builder.append(" order by ");
			for (Sort sort : sorts) {
				builder.append(" "+sort.getSort()+" "+sort.getDiretion()+", ");
			}
			builder.deleteCharAt(builder.lastIndexOf(","));
		}
		if (limit!=null && offset!=null) {
			builder.append(" limit "+limit+" offset "+offset);
		}
		return builder.toString();
	}
	
	@Getter
	public static class Sort {
		private String sort;
		private Diretion diretion = Diretion.DESC;
		public Sort(String sort, Diretion diretion) {
			this.sort = sort;
			this.diretion = diretion;
		}
		public Sort(String sort) {
			this.sort = sort;
		}
	}
	
	public static enum Diretion {
		ASC("asc"), DESC("desc");
		
		private final String direction;

		private Diretion(String direction) {
			this.direction = direction;
		}

		public String getDirection() {
			return direction;
		}
		
	}
	
	
}
