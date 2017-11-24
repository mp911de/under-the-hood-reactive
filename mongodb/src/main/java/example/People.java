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

import java.util.stream.Stream;

/**
 * @author Mark Paluch
 */
public class People {

	public static Stream<Person> stream() {

		return Stream.of("Walter White", "Skyler White", "Jesse Pinkman", "Hank Schrader", "Marie Schrader",
				"Walter White, Jr.", "Saul Goodman", "Gustavo Fring", "Mike Ehrmantraut", "Lydia Rodarte-Quayle",
				"Todd Alquist", "Kimberly Wexler", "Howard Hamlin", "Ignacio 'Nacho' Varga", "Charles 'Chuck' McGill, Jr.",
				"Steven Gomez", "Skinny Pete", "Carmen Molina", "Tuco Salamanca", "Gretchen Schwartz", "Gonzo", "No-Doze",
				"Domingo 'Krazy-8' Molina", "Emilio Koyama", "Brandon 'Badger' Mayhew", "Christian 'Combo' Ortega",
				"Adam Pinkman", "Wendy", "Bogdan Wolynetz", "Elliott Schwartz", "Ken 'Ken Wins'", "Holly White", "Ted Beneke",
				"George Merkert", "Hector Salamanca", "Jane Margolis", "Donald Margolis", "Clovis", "SAC Ramey", "Victor",
				"Tomás Cantillo", "Francesca Liddy", "Cythia", "Tortuga", "Det. Tim Roberts", "Andrea Cantillo",
				"Brock Cantillo", "Gale Boetticher", "Leonel Salamanca", "Juan Bolsa", "Group Leader", "Kaylee Ehrmantraut",
				"Marco Salamanca", "Pamela", "Duane Chow", "Stacey Ehrmantraut", "Officer Saxton", "Huell Babineaux",
				"Patrick Kuby", "Chris Mara", "Tyrus Kitt", "Don Eladio Vuente", "Gaff", "Dennis Markowski", "Lawson",
				"Barry Goodman", "Detective Kalanchoe", "Detective Munn", "Nurse", "Stephanie Doswell", "Declan",
				"Ron Forenall", "Dan Wachsberger", "Jack Welker", "Kenny", "Frankie", "Fran", "Lester", "Matt", "Ernesto",
				"Mrs. Nguyen", "Rick Schweikart", "Dr. Caldera", "Bill Oakley", "Irene Landry", "Marco Pasternak",
				"Betsy Kettleman", "Craig Kettleman", "Detective Sanders", "Detective Abbasi", "Joey Dixon", "Sound Guy",
				"Daniel 'Pryce' Wormald", "Mrs. Strauss", "Brenda", "Dr. Laura Cruz", "Paige Novick", "Arturo",
				"Kevin Wachtell", "Erin Brill", "Ximenez Lecerda", "Clifford Main", "Omar", "Brian Archuleta",
				"Rebecca McGill née Bois", "Make-up Artist", "Captain Bauer", "Mr. Ughetta", "David Brightbill")
				.map(Person::new);
	}
}
