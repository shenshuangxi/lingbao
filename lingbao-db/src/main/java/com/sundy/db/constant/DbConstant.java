package com.sundy.db.constant;

public interface DbConstant {

	public static enum UnitOfWorkType{
		STORE(1,"间接存储"), SESSION(2,"直接存储");
		
		private final int code;
		private final String name;
		
		private UnitOfWorkType(int code, String name) {
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
	
	public static enum DbTransactionIsolationLevel {
		NONE(0, "空"),
		/**
		 * 1)A修改事务级别并开始事务，对表做一次查询
		 * 2)B更新一条记录
		 * 3）此时B事务还未提交，A在事务内做一次查询，发现查询结果已经改变
		 * 4）B进行事务回滚
		 * 5）A再做一次查询，查询结果又变回去了
		 * 6）A表对表数据进行修改
		 * 7）B表重新开始事务后，对表记录进行修改，修改被挂起，直至超时，但是对另一条数据的修改成功，说明A的修改对表的数据行加行共享锁(因为可以使用select)
		 * READ-UNCOMMITTED隔离级别，当两个事务同时进行时，即使事务没有提交，所做的修改也会对事务内的查询做出影响，这种级别显然很不安全。但是在表对某行进行修改时，会对该行加上行共享锁
		 */
		READ_UNCOMMITTED(1, "读取未提交内容"),
		/**
		 * 1）设置A的事务隔离级别，并进入事务做一次查询
		 * 2）B开始事务，并对记录进行修改
		 * 3）A再对表进行查询，发现记录没有受到影响
		 * 4）B提交事务
		 * 5）A再对表查询，发现记录被修改
		 * 6）A对表进行修改
		 * 7）B重新开始事务，并对表同一条进行修改，发现修改被挂起，直到超时，但对另一条记录修改，却是成功，说明A的修改对表加上了行共享锁(因为可以select) 
		 * READ-COMMITTED事务隔离级别，只有在事务提交后，才会对另一个事务产生影响，并且在对表进行修改时，会对表数据行加上行共享锁
		 */
		READ_COMMITTED(2, "读取提交内容"),
		/**
		 * 1）A设置事务隔离级别，进入事务后查询一次
		 * 2）B开始事务，并对表进行修改
		 * 3）A查看表数据，数据未发生改变
		 * 4）B提交事务
		 * 5）A再进行一次查询，结果还是没有变化
		 * 6）A提交事务后，再查看结果，结果已经更新
		 * 7）A重新开始事务，并对表进行修改
		 * 8）B表重新开始事务，并对表进行修改，修改被挂起，直到超时，对另一条记录修改却成功，说明A对表进行修改时加了行共享锁(可以select) 
		 * REPEATABLE-READ事务隔离级别，当两个事务同时进行时，其中一个事务修改数据对另一个事务不会造成影响，即使修改的事务已经提交也不会对另一个事务造成影响。在事务中对某条记录修改，会对记录加上行共享锁，直到事务结束才会释放。
		 */
		REPEATABLE_READ(4, "可重读"),
		/**
		 * 1）修改A的事务隔离级别，并作一次查询
		 * 2）B对表进行查询，正常得出结果，可知对user表的查询是可以进行的
		 * 3）B开始事务，并对记录做修改，因为A事务未提交，所以B的修改处于等待状态，等待A事务结束，最后超时，说明A在对表做查询操作后，对表加上了共享锁
		 * SERIALIZABLE事务隔离级别最严厉，在进行查询时就会对表或行加上共享锁，其他事务对该表将只能进行读操作，而不能进行写操作
		 */
		SERIALIZABLE(8, "可串行化");
	
		private final int level;
		private final String name;
	
		private DbTransactionIsolationLevel(int level, String name) {
		    this.level = level;
		    this.name = name;
		}
	
		public int getLevel() {
		    return level;
		}

		public String getName() {
			return name;
		}
		  
	}
	
}
