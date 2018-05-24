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

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests showing MongoDB's reactive driver behavior.
 *
 * @author Mark Paluch
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MongoDBIntegrationTests {

	private static final int ITEM_COUNT = 109;

	@Autowired ReactiveMongoOperations mongoOperations;

	@Before
	public void before() {

		Flux<Person> people = mongoOperations.dropCollection(Person.class)
				.thenMany(Flux.fromStream(People.stream()) //
						.buffer(20) //
						.flatMap(mongoOperations::insertAll));

		StepVerifier.create(people) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();
		System.out.println();
		System.out.println();
	}

	/**
	 * Read from {@link com.mongodb.reactivestreams.client.MongoCollection} with request(unbounded).
	 */
	@Test
	public void findRequestUnboundedFromMongoCollection() {

		log.info("findRequestUnboundedFromCollection: Find all using MongoCollection (Driver)");

		Publisher<Document> find = mongoOperations.getCollection("person").find();

		StepVerifier.create(find) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();
	}

	/**
	 * Read from {@link ReactiveMongoOperations#findAll(Class)} with request(unbounded).
	 */
	@Test
	public void findRequestUnbounded() {

		log.info("findRequestUnbounded: Find all via ReactiveMongoOperations (Spring Data)");

		Flux<Person> find = mongoOperations.findAll(Person.class);

		StepVerifier.create(find) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();
	}

	/**
	 * Read from {@link ReactiveMongoOperations#findAll(Class)} v
	 */
	@Test
	public void findRequestTen() {

		log.info("findRequestTen: Find all via ReactiveMongoOperations (Spring Data)");

		Flux<Person> find = mongoOperations.findAll(Person.class);

		StepVerifier.create(find, 10).expectNextCount(10) //
				.thenAwait()//
				.thenRequest(10).expectNextCount(10) //
				.thenCancel().verify();
	}

	/**
	 * Read from {@link ReactiveMongoOperations#findAll(Class)} with request(10) and a {@link Flux#filter(Predicate)}
	 * operator.
	 */
	@Test
	public void findRequestWithFilter() {

		log.info("findRequestWithFilter: Find all via findAll and filter(…) operator");

		Flux<Person> find = mongoOperations.findAll(Person.class) //
				.filter(p -> p.name.length() > 20) //
				.log("example.filter", Level.INFO, SignalType.REQUEST);

		StepVerifier.create(find, 10) //
				.expectNextCount(7) //
				.verifyComplete();
	}

	/**
	 * Read from {@link ReactiveMongoOperations#findAll(Class)} with request(10) and a {@link Flux#filter(Predicate)}
	 * operator. Additionally adding {@link Flux#limitRate(int)} before {@link Flux#filter(Predicate)} for asynchronous
	 * prefetching.
	 */
	@Test
	public void findRequestWithFilterAndPrefetch() {

		log.info("findRequestWithFilterAndPrefetch: Find all via findAll and filter(…) using limitRate(20)");

		Flux<Person> find = mongoOperations.findAll(Person.class) //
				.limitRate(20) //
				.filter(p -> p.name.length() > 20) //
				.log("example.filter", Level.INFO, SignalType.REQUEST);

		StepVerifier.create(find, 10) //
				.expectNextCount(7) //
				.verifyComplete();
	}

	/**
	 * Read from {@link ReactiveMongoOperations#findAll(Class)} with limitRate(20).
	 */
	@Test
	public void findRequestLimitRate() {

		log.info("findRequestLimitRate: Find all via findAll using limitRate(20)");

		Flux<Person> find = mongoOperations.findAll(Person.class);

		// limit rate reduces initial prefetch by prefetch >> 2 for smart pre-buffering

		// StepVerifier.create(find.limitRate(20)).expectNextCount(ITEM_COUNT).verifyComplete();

		StepVerifier.create(find.log("example.flux.find", Level.INFO, SignalType.REQUEST) //
				.limitRate(20)) //
				.expectNextCount(ITEM_COUNT) //
				.verifyComplete();
	}
}
