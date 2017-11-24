/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.core.cql.generator.CreateKeyspaceCqlGenerator;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.LatencyTracker;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

/**
 * @author Mark Paluch
 */
@SpringBootApplication
public class UnderTheHoodReactiveApplication {

	@Autowired Cluster cluster;

	public static void main(String[] args) {
		SpringApplication.run(UnderTheHoodReactiveApplication.class, args);
	}

	/**
	 * Register a customizer that adds a {@link LatencyTracker} to log Cassandra statements.
	 *
	 * @return
	 */
	@PostConstruct
	private void postConstruct() {

		Logger logger = LoggerFactory.getLogger("example.cassandra.listener");

		List<String> filter = Arrays.asList("INSERT", "TRUNCATE", "DROP", "CREATE");

		cluster.register(new LatencyTracker() {

			@Override
			public void update(Host host, Statement statement, Exception e, long l) {

				String cql = statement.toString();

				if (filter.stream().anyMatch(cql::contains)) {
					return;
				}

				logger.info("|Cassandra| " + cql + "|fetchSize: " + statement.getFetchSize());
			}

			@Override
			public void onRegister(Cluster cluster) {

			}

			@Override
			public void onUnregister(Cluster cluster) {

			}
		});

		Session system = cluster.connect();

		CreateKeyspaceSpecification createKeyspace = CreateKeyspaceSpecification.createKeyspace("example").ifNotExists()
				.withSimpleReplication();
		system.execute(CreateKeyspaceCqlGenerator.toCql(createKeyspace));
		system.close();
	}
}
