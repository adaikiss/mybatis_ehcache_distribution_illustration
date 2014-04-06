/**
 * 
 */
package com.example.functional;

import net.sf.ehcache.CacheManager;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.example.config.AppConfig;
import com.example.mapper.FooMapper;
import com.example.model.FooExample;

/**
 * @author hlw
 * 
 */
@ContextConfiguration(classes = AppConfig.class)
public class EhcacheDistributionTest extends AbstractJUnit4SpringContextTests{
	@Autowired
	SqlSessionFactory sqlSessionFactory;
	@Autowired
	ResourceLoader resourceLoader;

	CacheManager cacheManagerA;
	CacheManager cacheManagerB;
	CacheManager cacheManagerC;
	CacheManager cacheManagerD;
	String cacheName;

	@Before
	public void setUp() throws Exception {
		cacheManagerA = CacheManager.newInstance(resourceLoader.getResource(
				"ehcache-a.xml").getURL());
		cacheManagerB = CacheManager.newInstance(resourceLoader.getResource(
				"ehcache-b.xml").getURL());
		cacheManagerC = CacheManager.newInstance(resourceLoader.getResource(
				"ehcache-c.xml").getURL());
		cacheManagerD = CacheManager.newInstance(resourceLoader.getResource(
				"ehcache-d.xml").getURL());
		cacheName = FooMapper.class.getName();
	}

	private void verifyCache(int size) {
		Assert.assertEquals(size, cacheManagerA.getCache(cacheName).getSize());
		Assert.assertEquals(size, cacheManagerB.getCache(cacheName).getSize());
		Assert.assertEquals(size, cacheManagerC.getCache(cacheName).getSize());
		Assert.assertEquals(size, cacheManagerD.getCache(cacheName).getSize());
	}

	@Test
	public void test() {
		FooExample example = new FooExample();
		SqlSession session;
		FooMapper mapper;
		long start;

		// initial cache size is 0.
		verifyCache(0);
		session = sqlSessionFactory.openSession();
		mapper = session.getMapper(FooMapper.class);
		start = System.nanoTime();
		mapper.selectByExample(example);
		long cost1 = System.nanoTime() - start;
		session.close();
		// cache size becomes 1 after the query.
		verifyCache(1);

		// query use cache.
		session = sqlSessionFactory.openSession();
		mapper = session.getMapper(FooMapper.class);
		start = System.nanoTime();
		mapper.selectByExample(example);
		long cost2 = System.nanoTime() - start;
		session.close();
		// clear cache in cacheManagerA, replicates in distribution.
		cacheManagerA.getCache(cacheName).removeAll();
		// cache size becomes 0.
		verifyCache(0);

		session = sqlSessionFactory.openSession();
		mapper = session.getMapper(FooMapper.class);
		start = System.nanoTime();
		mapper.selectByExample(example);
		long cost3 = System.nanoTime() - start;
		session.close();
		// cache size becomes 1 again after the query.
		verifyCache(1);

		System.out.println("cost-1:" + cost1);
		System.out.println("cost-2:" + cost2);
		System.out.println("cost-3:" + cost3);
		Assert.assertTrue(
				"query-1 querys directly from db should cost more time than query-2 uses cache.",
				cost1 > cost2);
		Assert.assertTrue(
				"query-3 querys directly from db should cost more time than query-2 uses cache.",
				cost3 > cost2);
	}
}
