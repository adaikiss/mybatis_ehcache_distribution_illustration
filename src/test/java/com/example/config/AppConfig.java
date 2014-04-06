/**
 * 
 */
package com.example.config;

import java.util.Properties;

import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.example.mapper.MapperPackage;

/**
 * @author hlw
 * 
 */
@Configuration
@MapperScan(basePackageClasses = MapperPackage.class)
public class AppConfig {
	@Bean
	public ResourceLoader resourceLoader() {
		return new DefaultResourceLoader();
	}

	@Bean
	public CacheManager ehcacheManager() {
		EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
		factory.setConfigLocation(resourceLoader().getResource(
				"classpath:ehcache.xml"));
		factory.setShared(true);
		return factory.getObject();
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.addDefaultScripts().build();
	}

	@Bean
	@DependsOn("ehcacheManager")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		Properties props = new Properties();
		props.setProperty("cacheEnabled", "true");
		sessionFactory.setConfigurationProperties(props);
		return sessionFactory.getObject();
	}
}
