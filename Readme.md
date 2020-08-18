**lingbao项目**

**服务划分**
	
客户端： 
			
	新建集群
	新建集群分支
	对集群内文件的 添加 删除 修改 操作
	提交集群内文件
	分支合并
	将提交后的集群文件发布到服务端
	提供操作日志记录
			
		
服务端:
	
	新建集群接口
	新建集群分支接口
	接收客户端提供的发布接口
	接收日志接口
	产生文件发布索引记录
	产生文件发布推送消息
		
发布端:

	推拉模式 
	获取服务端的发布消息
	将该消息推送给应用
	提供应用根据获取的消息，下载配置


客户端实体

	项目: 含 appId 负责人 负责人 人员组 邮件  部门  下辖集群 以及IP地址  

	集群： 分为 主集群(不能删除) 兄弟集群 分支集群   下辖文件  以及IP地址  主集群在项目创建时就创建
		  分支集群由主集群新建，只能在主集群的基础上 新增文件，以及对新增文件的删除修改 可合并到主集群，也可以删除
		  兄弟集群间可合并 可删除 主集群可以合并兄弟集群 但反之则不行(合并文件行内容有冲突将冲突内容并列显示提示修改， 可以拷贝新建) 
		  可根据提交日志 进行向前 向后回滚 一旦回滚生效  那么 以该回滚为节点
			
		IP地址规则:
			分支集群IP集必须包含于 兄弟集群IP集 或者 主集群IP集  分支之间IP地址必须交集为空
			兄弟IP 和 主集群IP 包含于 项目IP集
			如果兄弟IP集有存于主集群，则兄弟IP优先于主IP
		
	文件: 可以 添加 修改 删除 
	日志: 产生提交日志 包括 集群日志  文件日志

服务端实体(面向客户端提供操作接口，数据传输， 面向发布端提供发布消息)

	项目: 含 负责人 负责人 人员组 邮件  部门  下辖集群
	集群: 主集群，兄弟集群 分支集群 下辖文件  接收客户端提交的 修改日志  以及集群操作日志 
		 可根据提交日志 进行向前 向后回滚 一旦回滚生效  那么 以该回滚为节点
	文件: 可以 添加 修改 删除 
	日志: 产生提交日志 包括 集群日志  文件日志
	消息实体: 需要对外发布的所有消息文件集合
	发布消息: 消息名称 以及消息实体索引

发布端:

	消息实体
	发布消息



	