package com.sundy.lingbao.portal.repository.base;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sundy.lingbao.common.repository.LingbaoGenericalRepository;
import com.sundy.lingbao.portal.entity.base.UserEntity;

@Repository
public interface UserRepository extends LingbaoGenericalRepository<UserEntity, Long> {

	@Query(value=""
			+ "select new map(a.account as account, b.name as name, b.phone as phone,  b.email as email, a.roles as roles) from AccountEntity a "
			+ "left join UserEntity b on a.userId=b.id "
			+ "where "
			+ "a.account like CONCAT('%',?1,'%') or "
			+ "b.name like CONCAT('%',?1,'%') or "
			+ "b.phone like CONCAT('%',?1,'%') or "
			+ "b.email like CONCAT('%',?1,'%') or "
			+ "a.roles like CONCAT('%',?1,'%')")
	public List<Map<String, Object>> findUserInfo(String search);
	
	
//	select 
//	a.id, 
//	b.account, 
//	a.phone, 
//	a.`name`, 
//	a.email,
//	(case c.auth_type when null then 0 else 1 end) as permission 
//	from user_entity a 
//	left join account_entity b on a.id = b.user_id 
//	left join resource_entity c on a.id = c.user_id 
//	where c.auth_type=5 and c.`key`='1234567891+149cad91f0ad4e11ac8152ee2c63486b+' and c.key_type='com.sundy.lingbao.portal.entity.bussiness.EnvAppReleationEntity'

	
	@Query(value=""
			+ "select new map(b.id as id, a.account as account, b.name as name, b.phone as phone,  b.email as email, (case when c.authType is null then 0 else 1 end) as permission ) from AccountEntity a "
			+ "left join UserEntity b on a.userId=b.id "
			+ "left join ResourceEntity c on b.id=c.userId and c.key=?2 and c.keyType=?3 and c.authType=?4 "
			+ "where "
			+ "(a.account like CONCAT('%',?1,'%') or "
			+ "b.name like CONCAT('%',?1,'%') or "
			+ "b.phone like CONCAT('%',?1,'%') or "
			+ "b.email like CONCAT('%',?1,'%') or "
			+ "a.roles like CONCAT('%',?1,'%')) ")
	public Page<Map<String, Object>> findPermissionUser(String search, String key, String keyType, Integer authType, Pageable pageable);
	
}
