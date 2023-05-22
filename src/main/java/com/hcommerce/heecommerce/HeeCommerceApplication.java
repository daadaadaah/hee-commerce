package com.hcommerce.heecommerce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStreamCommands;
import java.util.Collections;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeeCommerceApplication {

	public static void main(String[] args) {

		RedisClient client = RedisClient.create("redis://localhost");
		StatefulRedisConnection<String, String> connection = client.connect();
		RedisStreamCommands<String, String> commands = connection.sync();

		Map<String, String> body = Collections.singletonMap("value", "Hello World");

		commands.xadd("my_stream", body);

		SpringApplication.run(HeeCommerceApplication.class, args);
	}
}
