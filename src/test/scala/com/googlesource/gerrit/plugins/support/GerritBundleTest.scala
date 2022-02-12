/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlesource.gerrit.plugins.support

import java.util.zip.ZipFile

import com.google.gson.{Gson, JsonPrimitive}
import com.googlesource.gerrit.plugins.support.FileMatchers._
import com.googlesource.gerrit.plugins.support.GerritSupportCommand.{CommandResult, JsonResult}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

class GerritBundleTest extends FlatSpec with Matchers
  with JsonMatcher with TmpPath {
  implicit val gson = new Gson

  "Bundle builder" should "create an output zip file" in {
    val zipFile = new SupportBundleBuilder(tmpPath.toPath, gson).build(Seq())

    zipFile should (be a 'file
      and endWithExtension("zip"))
  }

  it should "create a one entry in the output zip file" in {
    val file = new SupportBundleBuilder(tmpPath.toPath, gson)
      .build(Seq(CommandResult("foo", JsonResult(new JsonPrimitive("bar")))))

    val zipEntries = new ZipFile(file).entries.asScala
    zipEntries should have size (1)
  }

  it should "add the Json primitive into the zip entry" in {
    val file = new SupportBundleBuilder(tmpPath.toPath, gson)
      .build(Seq(CommandResult("entry-name", JsonResult(new JsonPrimitive("foo")))))

    val zipEntries = new ZipFile(file).entries.asScala.toSeq
    zipEntries.map(_.getName) should contain("entry-name")
  }

}

