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

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;

/**
 * @author Mark Paluch
 */
@SpringBootApplication
public class UnderTheHoodReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnderTheHoodReactiveApplication.class, args);
	}

	/**
	 * Register a customizer that adds a {@link CommandListener} to log MongoDB commands.
	 *
	 * @return
	 */
	@Bean
	MongoClientSettingsBuilderCustomizer customizer() {

		Logger logger = LoggerFactory.getLogger("example.mongodb.listener");

		List<String> filter = Arrays.asList("insert", "drop", "endSessions");

		return clientSettingsBuilder -> {

			clientSettingsBuilder.addCommandListener(new CommandListener() {

				@Override
				public void commandStarted(CommandStartedEvent event) {

					if (filter.contains(event.getCommandName())) {
						return;
					}

					Document copy = Document.parse(event.getCommand().toJson());

					copy.remove("lsid");
					copy.remove("$readPreference");

					logger.info("|MongoDB| " + copy.toJson());

				}

				@Override
				public void commandSucceeded(CommandSucceededEvent event) {

				}

				@Override
				public void commandFailed(CommandFailedEvent event) {

				}
			});
		};
	}
}
