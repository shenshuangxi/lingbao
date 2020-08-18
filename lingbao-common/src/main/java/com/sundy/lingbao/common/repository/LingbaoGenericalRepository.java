package com.sundy.lingbao.common.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface LingbaoGenericalRepository<T, ID> extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T> {

}
