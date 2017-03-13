package com.krobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.common.base.Joiner;
import com.krobot.enums.RedisEnum;

@Service
public class RedisService extends BaseService {

	@SuppressWarnings("unused")
	private final static Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

	@Autowired
	private JedisPool jedisPool;

	public static String fullKey(RedisEnum redisEnum, Object... params) {
		if (null == params) {
			return redisEnum.getKey();
		}
		return Joiner.on("-").appendTo(new StringBuilder(redisEnum.getKey()), params).toString();
	}

	public String get(RedisEnum redisEnum, Object... keys) {
		return this.get(fullKey(redisEnum, keys));
	}

	public String get(final String key) {
		return execute(new JedisReturnTask<String>() {
			@Override
			public String run(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	public void set(RedisEnum redisEnum, Object key, String value) {
		this.set(fullKey(redisEnum, key), value, (int) redisEnum.getExpireTime());
	}

	public void set(final String key, final String value, final int expireTime) {
		execute(new JedisTask() {
			@Override
			public void run(Jedis jedis) {
				jedis.setex(key, expireTime, value);
			}
		});
	}

	public void del(RedisEnum redisEnum, final Object... keys) {
		this.del(fullKey(redisEnum, keys));
	}

	public Long del(final String key) {
		return execute(new JedisReturnTask<Long>() {
			@Override
			public Long run(Jedis jedis) {
				return jedis.del(key);
			}
		});
	}

	public void rpush(final RedisEnum redisEnum, final Object key, final String... values) {
		execute(new JedisTask() {
			@Override
			public void run(Jedis jedis) {
				String fullKey = fullKey(redisEnum, key);
				jedis.rpush(fullKey, values);
				jedis.expire(fullKey, (int) redisEnum.getExpireTime());
			}
		});
	}

	public String lpop(final RedisEnum redisEnum, final Object key) {
		return execute(new JedisReturnTask<String>() {
			@Override
			public String run(Jedis jedis) {
				return jedis.lpop(fullKey(redisEnum, key));
			}
		});
	}

	public void incr(final RedisEnum redisEnum, final Object... keys) {
		execute(new JedisTask() {
			@Override
			public void run(Jedis jedis) {
				String fullKey = fullKey(redisEnum, keys);
				jedis.incr(fullKey);
				jedis.expire(fullKey, (int) redisEnum.getExpireTime());
			}
		});
	}

	private interface JedisTask {
		void run(Jedis jedis);
	}

	private interface JedisReturnTask<T> {
		T run(Jedis jedis);
	}

	@SuppressWarnings("deprecation")
	private void execute(JedisTask jedisTask) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedisTask.run(jedis);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@SuppressWarnings("deprecation")
	private <T> T execute(JedisReturnTask<T> task) {
		Jedis jedis = jedisPool.getResource();
		try {
			return task.run(jedis);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
}
