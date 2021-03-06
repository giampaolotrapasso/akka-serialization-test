/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.domain

import akka.persistence.inmemory.query.journal.scaladsl.InMemoryReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.testkit.scaladsl.TestSink
import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Person._
import com.github.dnvriend.generator.PersonCommandGenerator
import com.github.dnvriend.repository.PersonRepository

class PersonTest extends TestSpec {
  lazy val queries = PersistenceQuery(system).readJournalFor[InMemoryReadJournal](InMemoryReadJournal.Identifier)

  def eventsForPersistenceIdSource(id: String) =
    queries.currentEventsByPersistenceId(id, 0L, Long.MaxValue).map(_.event)

  "Person" should "register a name" in {
    val person = PersonRepository.forId("person-1")
    val xs = PersonCommandGenerator.registerNameCommands
    xs foreach (person ! _)

    eventually {
      eventsForPersistenceIdSource("person-1")
        .runWith(TestSink.probe[Any])
        .request(Int.MaxValue)
        .expectNextN(xs.map(cmd ⇒ NameRegistered(cmd.name, cmd.surname)))
        .expectComplete()
    }

    cleanup(person)
  }

  it should "update its name and surname" in {
    val person = PersonRepository.forId("person-2")
    val xs = PersonCommandGenerator.personCommands
    xs foreach (person ! _)

    eventually {
      eventsForPersistenceIdSource("person-2")
        .runWith(TestSink.probe[Any])
        .request(Int.MaxValue)
        .expectNextN(xs.map {
          case RegisterName(name, surname) ⇒ NameRegistered(name, surname)
          case ChangeName(name)            ⇒ NameChanged(name)
          case ChangeSurname(surname)      ⇒ SurnameChanged(surname)
        })
        .expectComplete()
    }
  }
}
