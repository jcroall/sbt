// Copyright (C) 2017 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.support

import java.io.File

import com.google.gson.{Gson, JsonElement, JsonObject}
import com.google.inject.{Inject, Injector, Singleton}

import scala.collection.JavaConverters._
import scala.util.Try

@Singleton
class RequestProcessor @Inject()(val zipped: SupportBundleBuilder,
                                 gson: Gson,
                                 commandFactory: GerritSupportCommandFactory) {

  def processRequest(body: String): Try[File] = {
    Try {
      val requestJson = gson.fromJson(body, classOf[JsonElement]).getAsJsonObject
      zipped.build {
        requestJson
          .entrySet().asScala
          .filter(_.getValue.getAsBoolean)
          .map(_.getKey)
          .map(commandFactory.apply)
          .flatMap(_.execute)
      }
    }
  }
}
