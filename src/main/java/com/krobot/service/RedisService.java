package com.krobot.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import com.krobot.enums.RedisEnum;
import com.krobot.service.BaseService;

@Service
public class RedisService extends BaseService implements InitializingBean {

	@SuppressWarnings("unused")
	private final static Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

	private JedisPool jedisPool;

	@Override
	public void afterPropertiesSet() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(10);
		poolConfig.setMaxTotal(500);
		jedisPool = new JedisPool(poolConfig, "10.163.18.111", 7135, 10000, "qazxcvbnmklpoiuytrew");
		// jedisPool = new JedisPool(poolConfig, "112.126.79.155", 7135, 10000,
		// "qazxcvbnmklpoiuytrew");
	}

	public String fullKey(RedisEnum redisEnum, Object... args) {
		StringBuilder sb = new StringBuilder(redisEnum.getKey());
		if (null == args) {
			return sb.toString();
		}
		for (Object arg : args) {
			if (null == arg) {
				continue;
			}
			sb.append(HYPHEN_LINE).append(arg);
		}
		return sb.toString();
	}

	public String get(RedisEnum redisEnum, Object... keys) {
		return this.get(fullKey(redisEnum, keys));
	}

	public String get(final String key) {
		return execute(new JedisResultTask<String>() {
			@Override
			protected String run(Jedis jedis) {
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
			protected void run(Jedis jedis) {
				jedis.setex(key, expireTime, value);
			}
		});
	}

	public void del(final RedisEnum redisEnum, final Object... keys) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.del(fullKey(redisEnum, keys));
			}
		});
	}

	public Long del(final String key) {
		return execute(new JedisResultTask<Long>() {
			@Override
			protected Long run(Jedis jedis) {
				return jedis.del(key);
			}
		});
	}

	public void incrBy(RedisEnum redisEnum, Object... keys) {
		incrBy(redisEnum, DEFAULT_REDIS_STEP, keys);
	}

	public void incrBy(final RedisEnum redisEnum, final int step, final Object... keys) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				String fullKey = fullKey(redisEnum, keys);
				jedis.incrBy(fullKey, step);
				jedis.expire(fullKey, (int) redisEnum.getExpireTime());
			}
		});
	}

	public void decrBy(RedisEnum redisEnum, Object... keys) {
		decrBy(redisEnum, DEFAULT_REDIS_STEP, keys);
	}

	public void decrBy(final RedisEnum redisEnum, final int step, final Object... keys) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				String fullKey = fullKey(redisEnum, keys);
				jedis.decrBy(fullKey, step);
				jedis.expire(fullKey, (int) redisEnum.getExpireTime());
			}
		});
	}

	public Long setnx(final RedisEnum redisEnum, final Object key, final String value) {
		return execute(new JedisResultTask<Long>() {
			@Override
			protected Long run(Jedis jedis) {
				String fullKey = fullKey(redisEnum, key);
				long result = jedis.setnx(fullKey, value);
				jedis.expire(fullKey, (int) redisEnum.getExpireTime());
				return result;
			}
		});
	}

	public Set<String> keys(final String pattern) {
		return execute(new JedisResultTask<Set<String>>() {
			@Override
			protected Set<String> run(Jedis jedis) {
				return jedis.keys(pattern);
			}
		});
	}

	// public String getset(final RedisEnum redisEnum, final Object key, final
	// String value) {
	// return execute(new JedisResultTask<String>() {
	// @Override
	// protected String run(Jedis jedis) {
	// return jedis.getSet(fullKey(redisEnum, key), value);
	// }
	// });
	// }

	public void rpush(final RedisEnum redisEnum, final Object key, final String... values) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.rpush(fullKey(redisEnum, key), values);
			}
		});
	}

	public void lpush(final RedisEnum redisEnum, final Object key, final String... values) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.lpush(fullKey(redisEnum, key), values);
			}
		});
	}

	public String lpop(final RedisEnum redisEnum, final Object key) {
		return execute(new JedisResultTask<String>() {
			@Override
			protected String run(Jedis jedis) {
				return jedis.lpop(fullKey(redisEnum, key));
			}
		});
	}

	// public List<String> lrange(RedisEnum redisEnum, Object key) {
	// return lrange(redisEnum, key, DEFAULT_L_REDIS_START, Long.MAX_VALUE);
	// }
	//
	// public List<String> lrange(final RedisEnum redisEnum, final Object key,
	// final long start, final long end) {
	// return execute(new JedisResultTask<List<String>>() {
	// @Override
	// protected List<String> run(Jedis jedis) {
	// return jedis.lrange(fullKey(redisEnum, key), start, end);
	// }
	// });
	// }
	//
	// public void lrem(final RedisEnum redisEnum, final Object key, final
	// String value) {
	// execute(new JedisTask() {
	// @Override
	// protected void run(Jedis jedis) {
	// jedis.lrem(fullKey(redisEnum, key), 0, value);
	// }
	// });
	// }
	//
	// public void ltrem(final RedisEnum redisEnum, final Object key, final int
	// start, final int end) {
	// execute(new JedisTask() {
	// @Override
	// protected void run(Jedis jedis) {
	// jedis.ltrim(fullKey(redisEnum, key), start, end);
	// }
	// });
	// }

	// public Long llen(final RedisEnum redisEnum, final Object key) {
	// return execute(new JedisResultTask<Long>() {
	// @Override
	// protected Long run(Jedis jedis) {
	// return jedis.llen(fullKey(redisEnum, key));
	// }
	// });
	// }

	public void sadd(final RedisEnum redisEnum, final Object key, final String... values) {
		sadd(fullKey(redisEnum, key), values);
	}

	public void sadd(final String key, final String... values) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.sadd(key, values);
			}
		});
	}

	public void srem(final RedisEnum redisEnum, final Object key, final String... members) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.srem(fullKey(redisEnum, key), members);
			}
		});
	}

	public Set<String> smembers(RedisEnum redisEnum, Object key) {
		return this.smembers(fullKey(redisEnum, key));
	}

	public Set<String> smembers(final String key) {
		return execute(new JedisResultTask<Set<String>>() {
			@Override
			protected Set<String> run(Jedis jedis) {
				return jedis.smembers(key);
			}
		});
	}

	// public boolean sismember(final RedisEnum redisEnum, final Object key,
	// final String member) {
	// return execute(new JedisResultTask<Boolean>() {
	// @Override
	// protected Boolean run(Jedis jedis) {
	// return jedis.sismember(fullKey(redisEnum, key), member);
	// }
	// });
	// }

	public void zadd(RedisEnum redisEnum, Object key, long score, long member) {
		zadd(redisEnum, key, score, String.valueOf(member));
	}

	public void zadd(RedisEnum redisEnum, Object key, long score, String member) {
		zadd(fullKey(redisEnum, key), score, member);
	}

	public void zadd(String fullKey, long score, long member) {
		zadd(fullKey, score, String.valueOf(member));
	}

	public void zadd(final String fullKey, final long score, final String member) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.zadd(fullKey, score, member);
			}
		});
	}

	// public void zadd(final RedisEnum redisEnum, final Object key, final
	// Map<String, Double> scoreMembers) {
	// execute(new JedisTask() {
	// @Override
	// protected void run(Jedis jedis) {
	// jedis.zadd(fullKey(redisEnum, key), scoreMembers);
	// }
	// });
	// }

	public void zrem(RedisEnum redisEnum, Object key, long member) {
		this.zrem(redisEnum, key, String.valueOf(member));
	}

	public void zrem(RedisEnum redisEnum, Object key, String member) {
		this.zrem(fullKey(redisEnum, key), member);
	}

	public void zrem(String key, long member) {
		this.zrem(key, String.valueOf(member));
	}

	public void zrem(final String key, final String member) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.zrem(key, member);
			}
		});
	}

	public Double zscore(RedisEnum redisEnum, Object key, long member) {
		return zscore(redisEnum, key, String.valueOf(member));
	}

	public Double zscore(RedisEnum redisEnum, Object key, String member) {
		return zscore(fullKey(redisEnum, key), String.valueOf(member));
	}

	public Double zscore(String key, long member) {
		return zscore(key, String.valueOf(member));
	}

	public Double zscore(final String key, final String member) {
		return execute(new JedisResultTask<Double>() {
			@Override
			protected Double run(Jedis jedis) {
				return jedis.zscore(key, member);
			}
		});
	}

	public Set<String> zrange(final String key, final int offset, final int count) {
		return execute(new JedisResultTask<Set<String>>() {
			@Override
			protected Set<String> run(Jedis jedis) {
				return jedis.zrange(key, offset, count);
			}
		});
	}

	public Set<String> zrange(final RedisEnum redisEnum, final Object key, final int offset, final int count) {
		return this.zrange(fullKey(redisEnum, key), offset, count);
	}

	public Set<Tuple> zrangeWithScores(final String key, final int offset, final int count) {
		return execute(new JedisResultTask<Set<Tuple>>() {
			@Override
			protected Set<Tuple> run(Jedis jedis) {
				return jedis.zrangeWithScores(key, offset, count);
			}
		});
	}

	public Set<Tuple> zrangeWithScores(final RedisEnum redisEnum, final Object key, final int offset, final int count) {
		return this.zrangeWithScores(fullKey(redisEnum, key), offset, count);
	}

	//
	// public Set<String> zrangeByScore(final RedisEnum redisEnum, final Object
	// key, final long min, final long max) {
	//
	// return this.zrangeByScore(redisEnum, key, min, max, DEFAULT_REDIS_START,
	// Integer.MAX_VALUE);
	//
	// }
	//
	// public Set<String> zrangeByScore(final RedisEnum redisEnum, final Object
	// key, final long min, final long max,
	// final int offset, final int count) {
	// return execute(new JedisResultTask<Set<String>>() {
	// @Override
	// protected Set<String> run(Jedis jedis) {
	// return jedis.zrangeByScore(fullKey(redisEnum, key), min, max, offset,
	// count);
	// }
	// });
	// }
	//
	// public Set<String> zrevrangeByScore(final RedisEnum redisEnum, final
	// Object key, final int offset, final int count) {
	// return execute(new JedisResultTask<Set<String>>() {
	// @Override
	// protected Set<String> run(Jedis jedis) {
	// return jedis.zrevrangeByScore(fullKey(redisEnum, key), Long.MAX_VALUE, 0,
	// offset, count);
	// }
	// });
	// }
	//
	// public Set<String> zrevrangeByScore(final RedisEnum redisEnum, final
	// Object key, final long max, final long min,
	// final int offset, final int count) {
	// return execute(new JedisResultTask<Set<String>>() {
	// @Override
	// protected Set<String> run(Jedis jedis) {
	// return jedis.zrevrangeByScore(fullKey(redisEnum, key), max, min, offset,
	// count);
	// }
	// });
	// }

	public Set<Tuple> zrangeByScoreWithScores(RedisEnum redisEnum, long key, long min, long max) {
		return this.zrangeByScoreWithScores(redisEnum, String.valueOf(key), min, max);
	}

	public Set<Tuple> zrangeByScoreWithScores(RedisEnum redisEnum, Object key, long min, long max) {
		return this.zrangeByScoreWithScores(fullKey(redisEnum, key), min, max);
	}

	public Set<Tuple> zrangeByScoreWithScores(String key, long min, long max) {
		return this.zrangeByScoreWithScores(key, min, max, DEFAULT_I_REDIS_START, Integer.MAX_VALUE);
	}

	public Set<Tuple> zrangeByScoreWithScores(RedisEnum redisEnum, long key, long min, long max, int offset, int count) {
		return this.zrevrangeByScoreWithScores(redisEnum, String.valueOf(key), min, max, offset, count);
	}

	public Set<Tuple> zrangeByScoreWithScores(RedisEnum redisEnum, Object key, long min, long max, int offset, int count) {
		return this.zrevrangeByScoreWithScores(fullKey(redisEnum, key), min, max, offset, count);
	}

	public Set<Tuple> zrangeByScoreWithScores(final String key, final long min, final long max, final int offset,
			final int count) {
		return execute(new JedisResultTask<Set<Tuple>>() {
			@Override
			protected Set<Tuple> run(Jedis jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(RedisEnum redisEnum, long key, long max, long min) {
		return this.zrevrangeByScoreWithScores(redisEnum, String.valueOf(key), max, min);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(RedisEnum redisEnum, Object key, long max, long min) {
		return this.zrevrangeByScoreWithScores(fullKey(redisEnum, key), max, min);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(String key, long max, long min) {
		return this.zrevrangeByScoreWithScores(key, max, min, DEFAULT_I_REDIS_START, Integer.MAX_VALUE);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(RedisEnum redisEnum, long key, long max, long min, int offset,
			int count) {
		return this.zrevrangeByScoreWithScores(redisEnum, String.valueOf(key), max, min, offset, count);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(RedisEnum redisEnum, Object key, long max, long min, int offset,
			int count) {
		return this.zrevrangeByScoreWithScores(fullKey(redisEnum, key), max, min, offset, count);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final long max, final long min, final int offset,
			final int count) {
		return execute(new JedisResultTask<Set<Tuple>>() {
			@Override
			protected Set<Tuple> run(Jedis jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
			}
		});
	}

	public long zcount(RedisEnum redisEnum, long key) {
		return this.zcount(fullKey(redisEnum, key), Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public long zcount(RedisEnum redisEnum, long key, long min, long max) {
		return this.zcount(fullKey(redisEnum, key), min, max);
	}

	public long zcount(String key) {
		return this.zcount(key, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public long zcount(final String key, final long min, final long max) {
		return execute(new JedisResultTask<Long>() {
			@Override
			protected Long run(Jedis jedis) {
				return jedis.zcount(key, min, max);
			}
		});
	}

	// public Long zrevrank(final RedisEnum redisEnum, final Object key, final
	// String member) {
	// return execute(new JedisResultTask<Long>() {
	// @Override
	// protected Long run(Jedis jedis) {
	// return jedis.zrevrank(fullKey(redisEnum, key), member);
	// }
	// });
	// }

	public String hget(RedisEnum redisEnum, String field) {
		return this.hget(fullKey(redisEnum), field);
	}

	public String hget(final String key, final String field) {
		return execute(new JedisResultTask<String>() {
			@Override
			protected String run(Jedis jedis) {
				return jedis.hget(key, field);
			}
		});
	}

	public Map<String, String> hgetAll(RedisEnum redisEnum) {
		return this.hgetAll(fullKey(redisEnum));
	}

	public Map<String, String> hgetAll(final String key) {
		return execute(new JedisResultTask<Map<String, String>>() {
			@Override
			protected Map<String, String> run(Jedis jedis) {
				return jedis.hgetAll(key);
			}
		});
	}

	public void hset(RedisEnum redisEnum, String field, String value) {
		this.hset(fullKey(redisEnum), field, value);
	}

	public void hset(final String key, final String field, final String value) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.hset(key, field, value);
			}
		});
	}

	public void hdel(RedisEnum redisEnum, String field) {
		this.hdel(fullKey(redisEnum), field);
	}

	public void hdel(final String key, final String field) {
		execute(new JedisTask() {
			@Override
			protected void run(Jedis jedis) {
				jedis.hdel(key, field);
			}
		});
	}

	private abstract class JedisTask {
		protected abstract void run(Jedis jedis);
	}

	private abstract class JedisResultTask<T> {
		protected abstract T run(Jedis jedis);
	}

	private void execute(JedisTask jedisTask) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedisTask.run(jedis);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	private <T> T execute(JedisResultTask<T> task) {
		Jedis jedis = jedisPool.getResource();
		try {
			return task.run(jedis);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public <T> T getDataObject(String fullKey, Class<T> c) {
		String json = this.get(fullKey);
		if (null == json) {
			return null;
		}
		return this.fromGson(json, c);
	}

	public <T> T getDataObject(RedisEnum redisEnum, Object key, Class<T> c) {
		String json = this.get(redisEnum, key);
		if (null == json) {
			return null;
		}
		return this.fromGson(json, c);
	}

	public <T> List<T> getDataObjects(String fullKey, Type t) {
		String json = this.get(fullKey);
		if (null == json) {
			return null;
		}
		return this.fromGson(json, t);
	}

	public <T> List<T> getDataObjects(RedisEnum redisEnum, Object key, Type t) {
		String json = this.get(redisEnum, key);
		if (null == json) {
			return null;
		}
		return this.fromGson(json, t);
	}

	public static void main(String[] args) {
		RedisService rs = new RedisService();
		rs.afterPropertiesSet();

		int index = 1;

		Set<String> keys = rs.keys("video_files04-*");
		for (String key : keys) {
			Set<Tuple> tuples = rs.zrevrangeByScoreWithScores(key, Long.MAX_VALUE, Long.MIN_VALUE);
			for (Tuple tuple : tuples) {
				System.out.println(index++ + "," + tuple.getElement());
			}
		}

	}
}
