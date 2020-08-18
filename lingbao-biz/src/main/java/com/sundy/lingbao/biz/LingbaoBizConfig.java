package com.sundy.lingbao.biz;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sundy.lingbao.common.LingbaoCommonConfig;


@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses={LingbaoBizConfig.class, LingbaoCommonConfig.class})
public class LingbaoBizConfig {

}
