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

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.test.StepVerifier;

import java.util.function.Predicate;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.cql.QueryOptions;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;

/**
 * Integration tests showing Cassandra's bridged driver behavior.
 *
 * @author Mark Paluch
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CassandraIntegrationTests {

	private static final int ITEM_COUNT = 109;

	@Autowired ReactiveCassandraOperations cassandraOperations;

	@Before
	public void before() {

		Flux<Person> people = cassandraOperations.truncate(Person.class)
				.thenMany(Flux.fromStream(People.stream()) //
						.flatMap(cassandraOperations::insert));

		StepVerifier.create(people).expectNextCount(ITEM_COUNT).verifyComplete();
		System.out.println();
		System.out.println();
	}

	/**
	 * Read from {@link org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate} with request(unbounded).
	 */
	@Test
	public void findRequestUnboundedFromTable() {

		log.info("findRequestUnboundedFromTable: Find all using CQL Template (ReactiveCqlOperations)");

		Flux<Row> find = cassandraOperations.getReactiveCqlOperations().queryForRows("SELECT * FROM person");

		StepVerifier.create(find) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();
	}

	/**
	 * Read from {@link ReactiveCassandraOperations#select(Query, Class)} with request(unbounded).
	 */
	@Test
	public void findRequestUnbounded() {

		log.info("findRequestUnbounded: Find all via ReactiveCassandraOperations (Spring Data)");

		Flux<Person> find = cassandraOperations.select(Query.empty(), Person.class);

		StepVerifier.create(find) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();
	}

	/**
	 * Read from {@link ReactiveCassandraOperations#select(Query, Class)} with request(unbounded).
	 */
	@Test
	public void findRequestPaged() {

		log.info("findRequestPaged: Find all via ReactiveCassandraOperations (Spring Data)");

		QueryOptions queryOptions = QueryOptions.builder().fetchSize(10).build();

		Flux<Person> find = cassandraOperations.select(Query.empty().queryOptions(queryOptions), Person.class);

		StepVerifier.create(find) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();
	}

	/**
	 * Read from {@link ReactiveCassandraOperations#select(Query, Class)} with request(10) and a
	 * {@link Flux#filter(Predicate)} operator.
	 */
	@Test
	public void findRequestWithFilter() {

		log.info("findRequestWithFilter: Find all via findAll and filter(…) operator");

		QueryOptions queryOptions = QueryOptions.builder() //
				.fetchSize(10).build();

		Flux<Person> find = cassandraOperations.select(Query.empty().queryOptions(queryOptions), Person.class) //
				.filter(p -> p.name.length() > 20) //
				.log("example.filter", Level.INFO, SignalType.REQUEST);

		StepVerifier.create(find, 10) //
				.expectNextCount(7) //
				.verifyComplete();
	}

	/**
	 * Read from {@link org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate#queryForRows(Statement)} with
	 * limitRate(20).
	 */
	@Test
	public void findRequestLimitRate() {

		log.info("findRequestLimitRate: Find all via findAll using limitRate(20)");

		Statement statement = new SimpleStatement("SELECT * FROM person");
		statement.setFetchSize(20);

		Flux<Row> find = Flux.from(cassandraOperations.getReactiveCqlOperations().queryForRows(statement));

		// limit rate reduces initial prefetch by prefetch >> 2 for smart pre-buffering

		StepVerifier.create(find.limitRate(20)).expectNextCount(ITEM_COUNT).verifyComplete();

		/*StepVerifier.create(find.log("example.flux.find", Level.INFO, SignalType.REQUEST) //
				.limitRate(20)) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();*/
	}
}
