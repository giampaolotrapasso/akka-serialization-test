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

package com.github.dnvriend.serializer.avro4s

class AvroSENoOldClassesTest extends AvroTestSpec {

  "String" should "encode to base64" in {
    val encoder = implicitly[Encoder[Array[Byte], String]]
    val base64String = encoder.encode("Hello World!".getBytes())
    base64String shouldBe "SGVsbG8gV29ybGQh"
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, Array[Byte]]]
    new String(decoder.decode("SGVsbG8gV29ybGQh")) shouldBe "Hello World!"
  }

  "MovieChangedV1" should "encode to base64" in {
    val event = MovieChangedV1("foo", 1990)
    val encoder = implicitly[Encoder[MovieChangedV1, String]]
    val base64String = encoder.encode(event)
    base64String shouldBe "BmZvb4wf"
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, MovieChangedV1]]
    decoder.decode("BmZvb4wf") shouldBe MovieChangedV1("foo", 1990)
  }

  "MovieChangedV2" should "encode to base64" in {
    val event = MovieChangedV2("foo", 1990, "bar")
    val encoder = implicitly[Encoder[MovieChangedV2, String]]
    val base64String = encoder.encode(event)
    base64String shouldBe "BmZvb4wfBmJhcg=="
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, MovieChangedV2]]
    decoder.decode("BmZvb4wfBmJhcg==") shouldBe MovieChangedV2("foo", 1990, "bar")
  }

  "MovieChangedV3" should "encode to base64" in {
    val event = MovieChangedV3("foo", 1990, "bar")
    val encoder = implicitly[Encoder[MovieChangedV3, String]]
    val base64String = encoder.encode(event)
    base64String shouldBe "BmZvb4wfBmJhcg=="
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, MovieChangedV3]]
    decoder.decode("BmZvb4wfBmJhcg==") shouldBe MovieChangedV3("foo", 1990, "bar")
  }

  "MovieChangedV4" should "encode to base64" in {
    val event = MovieChangedV4("foo", "bar", 1)
    val encoder = implicitly[Encoder[MovieChangedV4, String]]
    val base64String = encoder.encode(event)
    base64String shouldBe "BmZvbwZiYXIC"
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, MovieChangedV4]]
    decoder.decode("BmZvbwZiYXIC") shouldBe MovieChangedV4("foo", "bar", 1)
  }

  "v1 to v2" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroEvolution, MovieChangedV2]]
    decoder.decode(AvroEvolution(
      "BmZvb4wf",
      oldSchema = SchemaRegistry.registry(1).toSchema,
      newSchema = SchemaRegistry.registry(2).toSchema
    )) shouldBe MovieChangedV2("foo", 1990, "unknown")
  }

  "v2 to v3" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroEvolution, MovieChangedV3]]
    decoder.decode(AvroEvolution(
      "BmZvb4wfBmJhcg==",
      oldSchema = SchemaRegistry.registry(2).toSchema,
      newSchema = SchemaRegistry.registry(3).toSchema
    )) shouldBe MovieChangedV3("foo", 1990, "bar")
  }

  "v1 to v3" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroEvolution, MovieChangedV3]]
    decoder.decode(AvroEvolution(
      "BmZvb4wf",
      oldSchema = SchemaRegistry.registry(1).toSchema,
      newSchema = SchemaRegistry.registry(3).toSchema
    )) shouldBe MovieChangedV3("foo", 1990, "unknown")
  }

  "v1 to v4" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroEvolution, MovieChangedV4]]
    decoder.decode(AvroEvolution(
      "BmZvb4wf",
      oldSchema = SchemaRegistry.registry(1).toSchema,
      newSchema = SchemaRegistry.registry(4).toSchema
    )) shouldBe MovieChangedV4("foo", "unknown", 0)
  }

}
